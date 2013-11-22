package com.scheduler;

import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import com.scheduler.db.SchedulerDBAdapter;

class SchedulerCursorAdapter extends SimpleCursorAdapter {
	
	public SchedulerCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, SchedulerDBAdapter dbAdapter) {
		super(context, layout, c, from, to);
		setViewBinder(new SchedulerDataViewBinder(context, c, dbAdapter));
	}
}
