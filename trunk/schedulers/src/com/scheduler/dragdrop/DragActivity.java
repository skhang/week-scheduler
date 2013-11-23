package com.scheduler.dragdrop;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.scheduler.R;
import com.scheduler.db.SchedulerDBAdapter;

/**
 * This activity presents a screen with a grid on which images can be added and
 * moved around. It also defines areas on the screen where the dragged views can
 * be dropped. Feedback is provided to the user as the objects are dragged over
 * these drop zones.
 * 
 * <p>
 * Like the DragActivity in the previous version of the DragView example
 * application, the code here is derived from the Android Launcher code.
 * 
 * <p>
 * The original Launcher code required a long click (press) to initiate a
 * drag-drop sequence. If you want to see that behavior, set the variable
 * mLongClickStartsDrag to true. It is set to false below, which means that any
 * touch event starts a drag-drop.
 * 
 */
public class DragActivity extends Activity implements View.OnLongClickListener, View.OnClickListener, View.OnTouchListener {
	
	private static final int HIDE_TRASHCAN_MENU_ID = Menu.FIRST;
	private static final int SHOW_TRASHCAN_MENU_ID = Menu.FIRST + 1;
	private static final int CHANGE_TOUCH_MODE_MENU_ID = Menu.FIRST + 2;

	public static final boolean Debugging = false; // Use this to see extra toast messages.
	
	private DragController mDragController; // Object that handles a drag-drop sequence. It intersacts with DragSource and DropTarget objects.
	private DragLayer mDragLayer; // The ViewGroup within which an object can be dragged.
	private DeleteZone mDeleteZone; // A drop target that is used to remove objects from the screen.
	private int mImageCount = 0; // The number of images that have been added to screen.
	private ImageCell mLastNewCell = null; // The last ImageCell added to the screen when Add Image is clicked.
	private boolean mLongClickStartsDrag = true; // If true, it takes a long click to start the drag operation. Otherwise, any touch event starts a  drag.
	
	private GridView gridView;
	private SchedulerDBAdapter dbAdapter;
	private LinearLayout myGallery;
	private String schedulerId;

	static final String[] DAYS_OF_WEEK = new String[] {"L", "M", "X", "J", "V", "S", "D"};
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.grid_scheduler);
		
		Map<Integer,Integer> taskMap = loadSchedulerTasks();
		
		gridView = (GridView) findViewById(R.id.image_grid_view);
		gridView.setAdapter(new ImageCellAdapter(this, taskMap));
		// gridView.setOnItemClickListener (this);

		mDragController = new DragController(this);
		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mDragLayer.setDragController(mDragController);
		mDragLayer.setGridView(gridView);
		
		mDragController.setDragListener(mDragLayer);
		// mDragController.addDropTarget (mDragLayer);

		mDeleteZone = (DeleteZone) findViewById(R.id.delete_zone_view);

		// TODO: Give the user a little guidance.
		//Toast.makeText(getApplicationContext(), getResources().getString(R.string.instructions), Toast.LENGTH_LONG).show();
		
		loadAllTasks();
		
		GridView gridViewHeader = (GridView) findViewById(R.id.grid_header);
		 
		//ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DAYS_OF_WEEK);
		gridViewHeader.setAdapter(new TextAdapter(this));
	}
	
	private Map<Integer,Integer> loadSchedulerTasks() {
		
		// Map of cellId and imageId
		Map<Integer,Integer> taskMap = new HashMap<Integer, Integer>();
		
		Bundle extras = getIntent().getExtras();
		this.schedulerId = extras.getString(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
		
		dbAdapter = SchedulerDBAdapter.getInstace(this);
		dbAdapter.open();
		Cursor cursor = dbAdapter.getTaskByIdScheduler(Long.valueOf(this.schedulerId));
		if (cursor != null && cursor.moveToFirst()) {
			do {
				int columnId = cursor.getColumnIndex(SchedulerDBAdapter.TASK_PRIMARY_KEY);
				String taskPrimaryKey = cursor.getString(columnId);
				
				int columnCellNumber = cursor.getColumnIndex(SchedulerDBAdapter.TASK_COLUMN_CELL_NUMBER);
				String cellNumber = cursor.getString(columnCellNumber);
				
				int columnIdImage = cursor.getColumnIndex(SchedulerDBAdapter.TASK_COLUMN_ID_IMAGE);
				String idImage = cursor.getString(columnIdImage);
				
				//Toast.makeText(getApplicationContext(), taskPrimaryKey + " " + cellNumber + " " + idImage, Toast.LENGTH_LONG).show();
				
				taskMap.put(Integer.valueOf(cellNumber), Integer.valueOf(idImage));
				
		    } while (cursor.moveToNext());
		}
		
		return taskMap;
	}
	
	private void loadAllTasks() {
		
		myGallery = (LinearLayout) findViewById(R.id.mygallery);

		int[] iconIds = {R.drawable.ballet_icon, R.drawable.basketball_icon, R.drawable.beach_icon,
				R.drawable.bike_icon, R.drawable.birthday_icon, R.drawable.blackboard_icon,
				R.drawable.bouncycastle_icon, R.drawable.cinema_icon, R.drawable.doctor_icon,
				R.drawable.english_icon, R.drawable.football_icon, R.drawable.swimming_icon};
		
		for (int iconId : iconIds) {
			ImageCell newView = new ImageCell(this);
			newView.setTag(iconId);
			newView.setImageResource(iconId);
			newView.setLayoutParams(new LayoutParams(70, 70));
			newView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			newView.mEmpty = false;
			newView.mCellNumber = -1;
			mLastNewCell = newView;
			newView.setOnClickListener(this);
			newView.setOnLongClickListener(this);
			newView.setOnTouchListener(this);
			mImageCount++;
			myGallery.addView(newView);
		}
	}

	/**
	 * Handle a click on a view.
	 * 
	 */
	@Override
	public void onClick(View v) {
		if (mLongClickStartsDrag) {
			// Tell the user that it takes a long click to start dragging.
			toast("Press and hold to drag an image.");
		}
	}

	/**
	 * Build a menu for the activity.
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);

		menu.add(0, HIDE_TRASHCAN_MENU_ID, 0, "Hide Trashcan").setShortcut('1', 'c');
		menu.add(0, SHOW_TRASHCAN_MENU_ID, 0, "Show Trashcan").setShortcut('2', 'c');
		menu.add(0, CHANGE_TOUCH_MODE_MENU_ID, 0, "Change Touch Mode");

		return true;
	}

	/**
	 * Handle a click of an item in the grid of cells.
	 * 
	 */
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		ImageCell i = (ImageCell) v;
		trace("onItemClick in view: " + i.mCellNumber);
	}

	/**
	 * Handle a long click. If mLongClick only is true, this will be the only
	 * way to start a drag operation.
	 * 
	 * @param v View
	 * @return boolean - true indicates that the event was handled
	 */
	@Override
	public boolean onLongClick(View v) {
		if (mLongClickStartsDrag) {
			if (!v.isInTouchMode()) {
				toast("isInTouchMode returned false. Try touching the view again.");
				return false;
			}
			return startDrag(v);
		}
		return false;
	}

	/**
	 * Perform an action in response to a menu item being clicked.
	 * 
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case HIDE_TRASHCAN_MENU_ID:
			if (mDeleteZone != null)
				mDeleteZone.setVisibility(View.INVISIBLE);
			return true;
		case SHOW_TRASHCAN_MENU_ID:
			if (mDeleteZone != null)
				mDeleteZone.setVisibility(View.VISIBLE);
			return true;
		case CHANGE_TOUCH_MODE_MENU_ID:
			mLongClickStartsDrag = !mLongClickStartsDrag;
			String message = mLongClickStartsDrag ? "Changed touch mode. Drag now starts on long touch (click)." : "Changed touch mode. Drag now starts on touch (click).";
			Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * This is the starting point for a drag operation if mLongClickStartsDrag
	 * is false. It looks for the down event that gets generated when a user
	 * touches the screen. Only that initiates the drag-drop sequence.
	 * 
	 */
	@Override
	public boolean onTouch(View v, MotionEvent ev) {
		// If we are configured to start only on a long click, we are not going to handle any events here.
		if (mLongClickStartsDrag) {
			return false;
		}

		boolean handledHere = false;
		final int action = ev.getAction();
		// In the situation where a long click is not needed to initiate a drag, simply start on the down event.
		if (action == MotionEvent.ACTION_DOWN) {
			handledHere = startDrag(v);
		}

		return handledHere;
	}

	/**
	 * Start dragging a view.
	 * 
	 */
	public boolean startDrag(View v) {
		DragSource dragSource = (DragSource) v;

		// We are starting a drag. Let the DragController handle it.
		mDragController.startDrag(v, dragSource, dragSource, DragController.DRAG_ACTION_COPY);
		return true;
	}

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg
	 *            String
	 * @return void
	 */

	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Send a message to the debug log. Also display it using Toast if Debugging is true.
	 */
	public void trace(String msg) {
		Log.d("DragActivity", msg);
		if (!Debugging) {
			return;
		} else {
			toast(msg);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		// Clear tasks of current scheduler
		long idScheduler = Long.valueOf(schedulerId);
		dbAdapter.deleteTasks(idScheduler);
		
		// Save selected tasks of current scheduler
		if (gridView != null) {
			int numVisibleChildren = gridView.getChildCount();
			for (int i = 0; i < numVisibleChildren; i++) {
				ImageCell view = (ImageCell) gridView.getChildAt(i);
				if (!view.mEmpty) {
					String idImage = view.getTag().toString();
					dbAdapter.insertTask(idScheduler, i, Long.valueOf(idImage));
				}
			}
		}
	}

}
