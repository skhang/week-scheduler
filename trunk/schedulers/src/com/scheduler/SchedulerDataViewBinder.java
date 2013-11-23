package com.scheduler;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.View;
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
	private String currentData;
	private SchedulerDBAdapter dbAdapter;
	private Cursor mainCursor;
	
	public SchedulerDataViewBinder(Context context, Cursor c, SchedulerDBAdapter dbAdapter) {
		this.context = context;
		this.mainCursor = c;
		this.dbAdapter = dbAdapter;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		this.mainCursor = cursor;
		
		if (view.getId() == R.id.text_scheduler_name) {
			
			// Bind text scheduler name
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			currentData = cursor.getString(columnId);
			
			int columnName = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
			String name = cursor.getString(columnName);
			
			TextView textSchedulerName = (TextView) view;
			textSchedulerName.setText(name);
			textSchedulerName.setTag(currentData);
			//textSchedulerName.setBackgroundResource(R.drawable.rounded_corners);

			textSchedulerName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(context, DragActivity.class);
					// Primary key of current scheduler as extra field
					i.putExtra(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, arg0.getTag().toString());
					context.startActivity(i);
				}
			});
			return true;
		}

		if (view.getId() == R.id.img_scheduler) {
			
			// Bind picture of scheduler
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			currentData = cursor.getString(columnId);
			
			ImageView imageScheduler = (ImageView) view;
			imageScheduler.setTag(currentData);
			imageScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(context, DragActivity.class);
					// Primary key of current scheduler as extra field
					i.putExtra(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, arg0.getTag().toString());
					context.startActivity(i);
				}
			});
			return true;
		}
		
		if (view.getId() == R.id.img_edit_scheduler) {
			// Bind edit Imagebutton
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			currentData = cursor.getString(columnId);

			ImageButton editScheduler = (ImageButton) view;
			editScheduler.setTag(currentData);
			editScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Toast.makeText(context, "EDIT " + currentData, Toast.LENGTH_SHORT).show();
				}
			});
			return true;
		}
		
		if (view.getId() == R.id.img_delete_scheduler) {
			// Bind delete Imagebutton
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			currentData = cursor.getString(columnId);
			
			ImageButton deleteScheduler = (ImageButton) view;
			deleteScheduler.setTag(currentData);
			deleteScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					dbAdapter.deleteScheduler(Long.valueOf(currentData));
					mainCursor.requery();
					
					Toast.makeText(context, R.string.scheduler_deleted + " (" + currentData + ")", Toast.LENGTH_SHORT).show();
				}
			});
			return true;
		}
		
		return false;
	}
}