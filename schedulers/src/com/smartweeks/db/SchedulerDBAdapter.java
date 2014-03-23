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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Singleton class for database access.
 */
public class SchedulerDBAdapter {

	public static final String SCHEDULER_TABLE_NAME = "schedulers";
	public static final String SCHEDULER_PRIMARY_KEY = "_id";
	public static final String SCHEDULER_COLUMN_NAME = "name";
	public static final String SCHEDULER_COLUMN_IMAGE = "image";
	
	public static final String TASK_TABLE_NAME = "tasks";
	public static final String TASK_PRIMARY_KEY = "_id";
	public static final String TASK_COLUMN_FK_SCHEDULERS = "id_scheduler";
	public static final String TASK_COLUMN_CELL_NUMBER = "cell_number";
	public static final String TASK_COLUMN_ID_IMAGE = "id_image";
	
	public static final String IMAGES_TABLE_NAME = "images";
	public static final String IMAGES_PRIMARY_KEY = "_id";
	public static final String IMAGES_COLUMN_IMAGE = "image";
	
	private Context context;
	private SQLiteDatabase database;
	private SchedulerDBHelper dbHelper;

	private static SchedulerDBAdapter INSTANCE = null;
	
	private SchedulerDBAdapter(Context context) {
		this.context = context;
	}
	
	public static SchedulerDBAdapter getInstace(Context context) {
		
		if (INSTANCE == null) {
            synchronized(SchedulerDBAdapter.class) {
                if (INSTANCE == null) { 
                    INSTANCE = new SchedulerDBAdapter(context);
                }
            }
        }
		
		return INSTANCE;
	}

	public SQLiteDatabase getBD() {
		return database;
	}

	public SchedulerDBAdapter open() throws SQLException {
		
		if (dbHelper == null) {
			dbHelper = new SchedulerDBHelper(context);
			database = dbHelper.getWritableDatabase();
		}
		return this;
	}

	/**
	 * Close database.
	 */
	public void close() {
		
		if (database != null) {
			database.close();
		}
		
		if (dbHelper != null) {
			dbHelper.close();
			dbHelper = null;
		}
	}

	/**
	 * Insert a new scheduler.
	 * 
	 * @param name Name of scheduer
	 * 
	 * @return id of new scheduler
	 */
	public long insertScheduler(String name) {
		ContentValues initialValues = createSchedulerContentValues(name);
		return database.insert(SCHEDULER_TABLE_NAME, null, initialValues);
	}
	
	/**
	 * Insert a new scheduler.
	 * 
	 * @param name Name of scheduer
	 * @param imageBytes Image for current scheduler
	 * 
	 * @return id of new scheduler
	 */
	public long insertScheduler(String name, byte[] imageBytes) {
		ContentValues initialValues = createSchedulerContentValues(name, imageBytes);
		return database.insert(SCHEDULER_TABLE_NAME, null, initialValues);
	}

	/**
	 * Update a new scheduler.
	 * 
	 * @param name Name of scheduer
	 * 
	 * @return id of new scheduler
	 */
	public boolean updateScheduler(long id, String name) {
		return updateScheduler(id, name, null);
	}
	
	/**
	 * Update a new scheduler.
	 * 
	 * @param name Name of scheduer
	 * @param imageBytes Image for current scheduler
	 * 
	 * @return id of new scheduler
	 */
	public boolean updateScheduler(long id, String name, byte[] imageBytes) {
		ContentValues updateValues = createSchedulerContentValues(name, imageBytes);
		return database.update(SCHEDULER_TABLE_NAME, updateValues, SCHEDULER_PRIMARY_KEY + "=" + id, null) > 0;
	}

	/**
	 * Delete all schedulers. 
	 * 
	 * @return true if all schedulers have deleted, else false
	 */
	public boolean deleteAllSchedulers() {
		return database.delete(SCHEDULER_TABLE_NAME, null , null) > 0;
	}

	/**
	 * Delete a scheduler and each tasks.
	 * 
	 * @param id Identifier of scheduler
	 * 
	 * @return true if scheduler has deleted, else false
	 */
	public boolean deleteScheduler(long id) {
		deleteTasks(id);
		return database.delete(SCHEDULER_TABLE_NAME, SCHEDULER_PRIMARY_KEY + "=" + id, null) > 0;
	}

	/**
	 * Load all schedulers from database.
	 * 
	 * @param sort Sorting field 
	 * 
	 * @return Cursor with all schedulers of database
	 */
	public Cursor loadSchedulers(String sort) {
		return database.query(SCHEDULER_TABLE_NAME, new String[] { SCHEDULER_PRIMARY_KEY, SCHEDULER_COLUMN_NAME, SCHEDULER_COLUMN_IMAGE },
				null, null, null, null, sort);
	}

	/**
	 * Load all schedulers from database backup.
	 * 
	 * @param sort Sorting field 
	 * 
	 * @return Cursor with all schedulers of database backup
	 */	
	public Cursor loadSchedulersBackup() {
		return database.query(SCHEDULER_TABLE_NAME, null, null, null, null, null, null);
	}

	/**
	 * Load a scheduler from database.
	 * 
	 * @param id Id of the scheduler
	 * 
	 * @return Cursor with scheduler by id
	 * 
	 * @throws SQLException
	 */
	public Cursor getScheduler(long id) throws SQLException {
		Cursor mCursor = database.query(true, SCHEDULER_TABLE_NAME, new String[] {
				SCHEDULER_PRIMARY_KEY, SCHEDULER_COLUMN_NAME, SCHEDULER_COLUMN_IMAGE},
				SCHEDULER_PRIMARY_KEY + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	/**
	 * Load all tasks by idScheduler from database.
	 * 
	 * @param id Id of the scheduler
	 * 
	 * @return Cursor with task by idScheduler
	 * 
	 * @throws SQLException
	 */
	public Cursor getTaskByIdScheduler(long idScheduler) throws SQLException {
		Cursor mCursor = database.query(true, TASK_TABLE_NAME, new String[] {
				TASK_PRIMARY_KEY, TASK_COLUMN_FK_SCHEDULERS, TASK_COLUMN_CELL_NUMBER, TASK_COLUMN_ID_IMAGE},
				TASK_COLUMN_FK_SCHEDULERS + "=" + idScheduler, null, null, null, null, null);
		return mCursor;
	}
	
	/**
	 * Insert a task for into a scheduler.
	 * 
	 * @param id Id of the scheduler
	 * 
	 * @return Cursor with task by idScheduler
	 * 
	 * @throws SQLException
	 */
	public long insertTask(long idScheduler, long cellNumer, long idImage) throws SQLException {
		
		ContentValues initialValues = createTaskContentValues(idScheduler, cellNumer, idImage);
		return database.insert(TASK_TABLE_NAME, null, initialValues);
	}
	
	/**
	 * Delete all tasks by scheduler id.
	 * 
	 * @param id Identifier of scheduler
	 * 
	 * @return true if tasks have been deleted, else false
	 */
	public boolean deleteTasks(long id) {
		return database.delete(TASK_TABLE_NAME, TASK_COLUMN_FK_SCHEDULERS + "=" + id, null) > 0;
	}
	
	/**
	 * Delete all tasks by image id.
	 * 
	 * @param id Identifier of image
	 * 
	 * @return true if tasks have been deleted, else false
	 */
	public boolean deleteTasksByImageId(long id) {
		return database.delete(TASK_TABLE_NAME, TASK_COLUMN_ID_IMAGE + "=" + id, null) > 0;
	}
	
	/**
	 * Insert a new image for task.
	 * 
	 * @param imageBytes Image for task
	 * 
	 * @return id of new image
	 */
	public long insertImage(byte[] imageBytes) {
		ContentValues initialValues = createImageContentValues(imageBytes);
		return database.insert(IMAGES_TABLE_NAME, null, initialValues);
	}
	
	/**
	 * Delete all images. 
	 * 
	 * @return true if all schedulers have deleted, else false
	 */
	public boolean deleteAllImages() {
		return database.delete(IMAGES_TABLE_NAME, null , null) > 0;
	}

	/**
	 * Delete a image.
	 * 
	 * @param id Identifier of image
	 * 
	 * @return true if oimage has deleted, else false
	 */
	public boolean deleteImage(long id) {
		return database.delete(IMAGES_TABLE_NAME, IMAGES_PRIMARY_KEY + "=" + id, null) > 0;
	}

	/**
	 * Load all images from database.
	 * 
	 * @param sort Sorting field 
	 * 
	 * @return Cursor with all images of database
	 */
	public Cursor loadImages(String sort) {
		return database.query(IMAGES_TABLE_NAME, new String[] { IMAGES_PRIMARY_KEY, IMAGES_COLUMN_IMAGE },
				null, null, null, null, sort);
	}

	/**
	 * Load all images from database backup.
	 * 
	 * @param sort Sorting field 
	 * 
	 * @return Cursor with all images of database backup
	 */	
	public Cursor loadImagesBackup() {
		return database.query(IMAGES_PRIMARY_KEY, null, null, null, null, null, null);
	}

	/**
	 * Load a image from database.
	 * 
	 * @param id Id of the image
	 * 
	 * @return Cursor with image by id
	 * 
	 * @throws SQLException
	 */
	public Cursor getImage(long id) throws SQLException {
		Cursor mCursor = database.query(true, IMAGES_TABLE_NAME, new String[] {
				IMAGES_PRIMARY_KEY, IMAGES_COLUMN_IMAGE},
				IMAGES_PRIMARY_KEY + "=" + id, null, null, null, null, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	/**
	 * Creates a ContentValues object.
	 * 
	 * @param name Value for name fiels
	 * 
	 * @return ContentValues object
	 */
	private ContentValues createTaskContentValues(long idScheduler, long cellNumer, long idImage) {
		ContentValues values = new ContentValues();
		values.put(TASK_COLUMN_FK_SCHEDULERS, idScheduler);
		values.put(TASK_COLUMN_CELL_NUMBER, cellNumer);
		values.put(TASK_COLUMN_ID_IMAGE, idImage);
		return values;
	}
	
	/**
	 * Creates a ContentValues object.
	 * 
	 * @param name Value for name fiels
	 * @param imageBytes Bytes of an image
	 * 
	 * @return ContentValues object
	 */
	private ContentValues createSchedulerContentValues(String name, byte[] imageBytes) {
		ContentValues values = new ContentValues();
		values.put(SCHEDULER_COLUMN_NAME, name);
		if (imageBytes != null) {
			values.put(SCHEDULER_COLUMN_IMAGE, imageBytes);
		}
		return values;
	}
	
	/**
	 * Creates a ContentValues object.
	 * 
	 * @param name Value for name fiels
	 * 
	 * @return ContentValues object
	 */
	private ContentValues createSchedulerContentValues(String name) {
		return createSchedulerContentValues(name, null);
	}
	
	/**
	 * Creates a ContentValues object.
	 * 
	 * @param imageBytes Bytes of an image
	 * 
	 * @return ContentValues object
	 */
	private ContentValues createImageContentValues(byte[] imageBytes) {
		ContentValues values = new ContentValues();
		if (imageBytes != null) {
			values.put(IMAGES_COLUMN_IMAGE, imageBytes);
		}
		return values;
	}
	
	/**
	 * Clone method is overridden for Singleton pattern.
	 */
	public Object clone() throws CloneNotSupportedException {
	        throw new CloneNotSupportedException(); 
	}

	public SchedulerDBHelper getDBHelper() {
		return dbHelper;
	}
}
