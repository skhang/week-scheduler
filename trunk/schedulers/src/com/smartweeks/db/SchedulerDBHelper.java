package com.smartweeks.db;

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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchedulerDBHelper extends SQLiteOpenHelper {
	
	// DB name and version
	private static final String DB_NAME = "smart_week.db";
	private static final int DB_VERSION = 4;
	
	private boolean firstTime = false;

	// SQL Create table
	private static final String CREATE_SCHEDULERS_TABLE = "CREATE TABLE " + SchedulerDBAdapter.SCHEDULER_TABLE_NAME + 
			" (_id integer primary key autoincrement, " +
			" name text not null, image blob);";

	private static final String CREATE_TASK_TABLE = "CREATE TABLE " + SchedulerDBAdapter.TASK_TABLE_NAME + 
			" (_id integer primary key autoincrement, " +
			SchedulerDBAdapter.TASK_COLUMN_FK_SCHEDULERS + " integer, " +
			SchedulerDBAdapter.TASK_COLUMN_CELL_NUMBER + " integer, " +
			SchedulerDBAdapter.TASK_COLUMN_ID_IMAGE + " integer, " +
			" FOREIGN KEY(" + SchedulerDBAdapter.TASK_COLUMN_FK_SCHEDULERS + ") REFERENCES " + SchedulerDBAdapter.SCHEDULER_TABLE_NAME + "(_id));";
	
	private static final String CREATE_IMAGES_TABLE = "CREATE TABLE " + SchedulerDBAdapter.IMAGES_TABLE_NAME + 
			" (_id integer primary key autoincrement, " +
			" image blob not null);";
	
	public SchedulerDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		firstTime = true;
		database.execSQL(CREATE_SCHEDULERS_TABLE);
		database.execSQL(CREATE_TASK_TABLE);
		database.execSQL(CREATE_IMAGES_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + SchedulerDBAdapter.SCHEDULER_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + SchedulerDBAdapter.TASK_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + SchedulerDBAdapter.IMAGES_TABLE_NAME);
		onCreate(database);
	}

	public boolean isFirstTime() {
		return firstTime;
	}

	public void setFirstTime(boolean firstTime) {
		this.firstTime = firstTime;
	}
}