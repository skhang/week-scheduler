package com.smartweeks;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.widget.SimpleCursorAdapter;

import com.smartweeks.db.SchedulerDBAdapter;

class SchedulerCursorAdapter extends SimpleCursorAdapter {
	
	public SchedulerCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, SchedulerDBAdapter dbAdapter, AlertDialog cameraDialog, Dialog schedulerDialog) {
		super(context, layout, c, from, to);
		setViewBinder(new SchedulerDataViewBinder(context, c, dbAdapter, cameraDialog, schedulerDialog));
	}
}
