package com.scheduler.dragdrop;

/*
 * This file is part of Smart weeks project.
 * 
 * Smart weeks is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Smart weeks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Smart weeks.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Sources: https://code.google.com/p/week-scheduler/
 * Copyright 2013 Iker Canarias.
 */

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
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
	
	// Font path
	private static final String fontPath = "fonts/SQUAREKI.TTF";
    
	public static final boolean Debugging = false; // Use this to see extra toast messages.
	
	private DragController mDragController; // Object that handles a drag-drop sequence. It intersacts with DragSource and DropTarget objects.
	private DragLayer mDragLayer; // The ViewGroup within which an object can be dragged.
	private DeleteZone mDeleteZone; // A drop target that is used to remove objects from the screen.
	private boolean mLongClickStartsDrag = true; // If true, it takes a long click to start the drag operation. Otherwise, any touch event starts a  drag.
	
	private GridView gridView;
	private SchedulerDBAdapter dbAdapter;
	private LinearLayout myGallery;
	private String schedulerId;
	
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);

		setContentView(R.layout.grid_scheduler);
		
		Map<Integer,Integer> taskMap = loadSchedulerTasks();
		gridView = (GridView) findViewById(R.id.image_grid_view);
		gridView.setAdapter(new ImageCellAdapter(this, taskMap));

		mDragController = new DragController(this);
		mDragLayer = (DragLayer) findViewById(R.id.drag_layer);
		mDragLayer.setDragController(mDragController);
		mDragLayer.setGridView(gridView);
		
		mDragController.setDragListener(mDragLayer);

		mDeleteZone = (DeleteZone) findViewById(R.id.delete_zone_view);

		loadAllTasks();
		
		loadHeader();
		
		ImageButton buttonHome = (ImageButton) findViewById(R.id.button_home);
		buttonHome.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				onBackPressed();
			}
		});
	}
	
	private void loadHeader() {
		
		GridView gridViewHeader = (GridView) findViewById(R.id.grid_header);
		gridViewHeader.setAdapter(new TextAdapter(this));
		// Vertical scrolling disablled on header
		gridViewHeader.setOnTouchListener(new OnTouchListener(){
		    @Override
		    public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_MOVE){
		            return true;
		        }
		        return false;
		    }
		});
	}
	
	@SuppressLint("UseSparseArrays")
	private Map<Integer,Integer> loadSchedulerTasks() {
		
		// Map of cellId and imageId
		Map<Integer,Integer> taskMap = new HashMap<Integer, Integer>();
		
		Bundle extras = getIntent().getExtras();
		this.schedulerId = extras.getString(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
		String schedulerName = extras.getString(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
		
		// Loading Font Face
        Typeface tf = Typeface.createFromAsset(getAssets(), fontPath);
        
		TextView textViewName = (TextView) findViewById(R.id.textView_name);
		textViewName.setTypeface(tf);
		textViewName.setText(schedulerName);
		textViewName.setTextSize((float)(getFontSize(this)*1.7));
		
		TextView textViewDate = (TextView) findViewById(R.id.textView_date);
		textViewDate.setTypeface(tf);
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, getResources().getConfiguration().locale);
		String currentDate = dateFormat.format(new Date());
		currentDate = currentDate.replaceAll("/", " ");
		textViewDate.setText(currentDate);
		textViewDate.setTextSize(getFontSize(this));
		
		dbAdapter = SchedulerDBAdapter.getInstace(this);
		dbAdapter.open();
		Cursor cursor = dbAdapter.getTaskByIdScheduler(Long.valueOf(this.schedulerId));
		if (cursor != null && cursor.moveToFirst()) {
			do {				
				int columnCellNumber = cursor.getColumnIndex(SchedulerDBAdapter.TASK_COLUMN_CELL_NUMBER);
				String cellNumber = cursor.getString(columnCellNumber);
				
				int columnIdImage = cursor.getColumnIndex(SchedulerDBAdapter.TASK_COLUMN_ID_IMAGE);
				String idImage = cursor.getString(columnIdImage);
				
				taskMap.put(Integer.valueOf(cellNumber), Integer.valueOf(idImage));
				
		    } while (cursor.moveToNext());
		}
		cursor.close();
		return taskMap;
	}
	
	public int getFontSize (Activity activity) { 

	    DisplayMetrics dMetrics = new DisplayMetrics();
	    activity.getWindowManager().getDefaultDisplay().getMetrics(dMetrics);

	    // lets try to get them back a font size realtive to the pixel width of the screen
	    final float WIDE = activity.getResources().getDisplayMetrics().widthPixels;
	    int valueWide = (int)(WIDE / 32.0f / (dMetrics.scaledDensity));
	    return valueWide;
	}
	
	private void loadAllTasks() {
		
		myGallery = (LinearLayout) findViewById(R.id.mygallery);

		int[] iconIds = {R.drawable.blackboard_icon, R.drawable.homework_icon, R.drawable.games_icon, R.drawable.paint_icon,
				R.drawable.music_icon, R.drawable.guitar_icon, R.drawable.piano_icon, R.drawable.drums_icon, 
				R.drawable.beach_icon, R.drawable.birthday_icon, R.drawable.museum_icon, R.drawable.laptop_icon,
				R.drawable.doctor_icon, R.drawable.bouncycastle_icon, R.drawable.swing_icon, R.drawable.cinema_icon, R.drawable.theatre_icon, R.drawable.grandparents_icon,
				R.drawable.english_icon, R.drawable.football_icon, R.drawable.ballet_icon, R.drawable.basketball_icon, R.drawable.swimming_icon, 
				R.drawable.bike_icon, R.drawable.bowling_icon, R.drawable.karate_icon, R.drawable.rugby_icon, R.drawable.tennis_icon, 
				R.drawable.present_icon, R.drawable.tree_icon};
		
		for (int iconId : iconIds) {
			ImageCell newView = new ImageCell(this);
			newView.setTag(iconId);
			newView.setImageResource(iconId);
			
			newView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
			newView.setScaleType(ScaleType.FIT_XY);
			newView.setAdjustViewBounds(true);
			
			newView.mEmpty = false;
			newView.mCellNumber = -1;
			newView.setOnClickListener(this);
			newView.setOnLongClickListener(this);
			newView.setOnTouchListener(this);
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
			toast(R.string.instructions);
		}
	}

	/**
	 * Build a menu for the activity.
	 * 
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		return false;
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
				//toast("isInTouchMode returned false. Try touching the view again.");
				return false;
			}
			return startDrag(v);
		}
		return false;
	}

	/**
	 * This is the starting point for a drag operation if mLongClickStartsDrag is false. 
	 * It looks for the down event that gets generated when a user touches the screen. Only that initiates the drag-drop sequence.
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
	 * @param msg String
	 */
	public void toast(String msg) {
		Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
	}

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msgId resource id of messsage
	 */
	public void toast(int msgId) {
		Toast.makeText(getApplicationContext(), msgId, Toast.LENGTH_SHORT).show();
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
				if (!view.mEmpty && view.getTag() != null) {
					String idImage = view.getTag().toString();
					dbAdapter.insertTask(idScheduler, i, Long.valueOf(idImage));
				}
			}
		}
	}

}
