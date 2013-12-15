package com.scheduler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.scheduler.db.SchedulerDBAdapter;
import com.scheduler.dragdrop.DragActivity;

/**
 * This class draws elements of the list
 */
public class SchedulerDataViewBinder implements SimpleCursorAdapter.ViewBinder {

	private Context context;
	//private String currentData;
	private SchedulerDBAdapter dbAdapter;
	private Cursor mainCursor;
	private AlertDialog cameraDialog;
	
	public SchedulerDataViewBinder(Context context, Cursor c, SchedulerDBAdapter dbAdapter, AlertDialog cameraDialog) {
		this.context = context;
		this.mainCursor = c;
		this.dbAdapter = dbAdapter;
		this.cameraDialog = cameraDialog;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		this.mainCursor = cursor;
		
		if (view.getId() == R.id.text_scheduler_name) {
			
			// Bind text scheduler name
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			int columnName = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
			String name = cursor.getString(columnName);
			
			TextView textSchedulerName = (TextView) view;
			textSchedulerName.setText(name);
			textSchedulerName.setTag(schedulerId + "#" + name );

			textSchedulerName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(context, DragActivity.class);
					
					String args[] = arg0.getTag().toString().split("#");
					
					// Primary key and name of current scheduler as extra field
					Bundle extras = new Bundle();
					extras.putString(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, args[0]);
					extras.putString(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME, args[1]);
					i.putExtras(extras);
					
					context.startActivity(i);
				}
			});
			return true;
		}

		if (view.getId() == R.id.img_scheduler) {
			
			// Bind picture of scheduler
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			int columnName = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
			String name = cursor.getString(columnName);
			
			int columnImage = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_IMAGE);
			byte[] imageBytes = cursor.getBlob(columnImage);
			
			ImageView imageScheduler = (ImageView) view;
			imageScheduler.setTag(schedulerId + "#" + name);
			if (imageBytes != null && imageBytes.length > 0) {
				Bitmap b = BitmapFactory.decodeByteArray(imageBytes , 0, imageBytes .length);
				imageScheduler.setImageBitmap(b);
			}
			
			imageScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(context, DragActivity.class);
					
					String args[] = arg0.getTag().toString().split("#");
					
					// Primary key and name of current scheduler as extra field
					Bundle extras = new Bundle();
					extras.putString(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, args[0]);
					extras.putString(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME, args[1]);
					i.putExtras(extras);
					
					context.startActivity(i);
				}
			});
			return true;
		}
		
		if (view.getId() == R.id.img_edit_scheduler) {
			// Bind edit Imagebutton
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			int columnName = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
			String name = cursor.getString(columnName);
			
			ImageButton editScheduler = (ImageButton) view;
			editScheduler.setTag(schedulerId + "#" + name);
			
			editScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					
					final Dialog dialog = new Dialog(arg0.getContext());
					dialog.setContentView(R.layout.edit_scheduler_dialog);
					dialog.setTitle(R.string.edit_plan);
					dialog.show();

					ImageButton editScheduler = (ImageButton) arg0;
					
					String[] data = editScheduler.getTag().toString().split("#");
					final String schedulerId = data[0];
					final String schedulerName = data[1];
					EditText editText = (EditText) dialog.findViewById(R.id.text_view_scheduler);
					editText.setText(schedulerName);
					editText.setTag(schedulerId);
					
					Button buttonOK = (Button) dialog.findViewById(R.id.button_ok);
					buttonOK.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EditText editText = (EditText) dialog.findViewById(R.id.text_view_scheduler);
							String newName = editText.getText().toString();
							if (!"".equals(newName)) {
								dbAdapter.updateScheduler(Integer.valueOf(schedulerId), editText.getText().toString());
								mainCursor.requery();
								dialog.dismiss();
							}
						}
					});

					Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
					buttonCancel.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Close dialog
							dialog.dismiss();
						}
					});
					
					Button buttonPicture = (Button) dialog.findViewById(R.id.button_picture);
					buttonPicture.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Open camera/galley dialog
							cameraDialog.show();
							
							// Resize AlertDialog
							DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
							WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
							windowManager.getDefaultDisplay().getMetrics(displayMetrics);
							int mwidth = displayMetrics.heightPixels;
							int mheight = displayMetrics.widthPixels;
							WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
							lp.copyFrom(cameraDialog.getWindow().getAttributes());
							lp.width = mwidth/2;
							lp.height = mheight;

							//set the dim level of the background
							lp.dimAmount=0.1f;
							cameraDialog.getWindow().setAttributes(lp);
							//add a blur/dim flags
							cameraDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND | WindowManager.LayoutParams.FLAG_DIM_BEHIND);
						}
					});
					
				}
			});
			return true;
		}
		
		if (view.getId() == R.id.img_delete_scheduler) {
			// Bind delete Imagebutton
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			ImageButton deleteScheduler = (ImageButton) view;
			deleteScheduler.setTag(schedulerId);
			deleteScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					ImageButton deleteScheduler = (ImageButton) arg0;
					String schedulerId = deleteScheduler.getTag().toString();
					dbAdapter.deleteScheduler(Long.valueOf(schedulerId));
					mainCursor.requery();
					
					Toast.makeText(context, R.string.scheduler_deleted, Toast.LENGTH_SHORT).show();
				}
			});
			return true;
		}
		
		return false;
	}
}