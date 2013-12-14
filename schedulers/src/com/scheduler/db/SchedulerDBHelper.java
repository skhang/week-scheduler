package com.scheduler.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SchedulerDBHelper extends SQLiteOpenHelper {
	
	// DB name and version
	private static final String DB_NAME = "smart_week.db";
	private static final int DB_VERSION = 3;

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
	
	public SchedulerDBHelper(Context context) {
		super(context, DB_NAME, null, DB_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(CREATE_SCHEDULERS_TABLE);
		database.execSQL(CREATE_TASK_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL("DROP TABLE IF EXISTS " + SchedulerDBAdapter.SCHEDULER_TABLE_NAME);
		database.execSQL("DROP TABLE IF EXISTS " + SchedulerDBAdapter.TASK_TABLE_NAME);
		onCreate(database);
	}
}