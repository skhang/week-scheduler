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

import java.io.File;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.smartweeks.db.SchedulerDBAdapter;

public class ImportImagesTask extends AsyncTask<String, Void, Boolean> {

	// Directory that files are to be read from and written to
	private static final File DATABASE_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "smartweeks");
	private static final String DB_BAKCUP_NAME = "smart_weeks_backup.db";
	private static final File IMPORT_FILE = new File(DATABASE_DIRECTORY, DB_BAKCUP_NAME);

	private ProgressDialog dialogo;
	private Context context;
	private SchedulerDBAdapter dbAdapter;
	private String message;

	public ImportImagesTask(Context context, SchedulerDBAdapter dbAdapter) {
		dialogo = new ProgressDialog(context);
		this.context = context;
		this.dbAdapter = dbAdapter;
		this.message = context.getResources().getString(R.string.pref_task_imported);
	}

	protected void onPreExecute() {
		this.dialogo.setMessage(context.getResources().getString(R.string.pref_task_importing));
		this.dialogo.show();
	}

	/**
	 * Import images from external data base.
	 * 
	 * @return true if images were imported, else false
	 */
	private boolean importDataBase() {

		boolean result = false;
		
		if (!isPresentSD()) {
			message = context.getResources().getString(R.string.msg_no_sdcard_found);
			return false;
		}

		File importFile = IMPORT_FILE;
		SQLiteDatabase sqlDb = null;
		Cursor cursor = null;

		try {
			// Remove all tasks
			dbAdapter.deleteAllTasks();
			// Remove all images
			dbAdapter.deleteAllImages();
			
			// Insert all images in cursor to current database
			sqlDb = SQLiteDatabase.openDatabase(importFile.getPath(), null, SQLiteDatabase.OPEN_READONLY);
			cursor = sqlDb.query(true, SchedulerDBAdapter.IMAGES_TABLE_NAME, null, null, null, null, null, null, null);
			int columnImage = cursor.getColumnIndex(SchedulerDBAdapter.IMAGES_COLUMN_IMAGE);
			cursor.moveToPosition(-1);
			while (cursor.moveToNext()) {
				byte[] imageBytes = cursor.getBlob(columnImage);
				dbAdapter.insertImage(imageBytes);
			}
			
			result = true;
					
		} catch (Exception e) {
			message = context.getResources().getString(R.string.msg_error_importing_database);
			e.printStackTrace();
			result = false;
		}

		finally {
			if (sqlDb != null && sqlDb.isOpen()) {
				sqlDb.close();
			}
			if (cursor != null) {
				cursor.close();
			}
		}

		return result;
	}

	/**
	 * Check if is present SD card.
	 * 
	 * @return tru if SD card is present, else false
	 */
	private boolean isPresentSD() {
		return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
	}

	protected Boolean doInBackground(final String... args) {

		return importDataBase();
	}

	protected void onPostExecute(final Boolean success) {
		if (this.dialogo.isShowing()) {
			this.dialogo.dismiss();
		}

		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
