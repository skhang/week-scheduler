package com.scheduler.db;

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
	
	public static final String TASK_TABLE_NAME = "tasks";
	public static final String TASK_PRIMARY_KEY = "_id";
	public static final String TASK_COLUMN_FK_SCHEDULERS = "id_scheduler";
	public static final String TASK_COLUMN_CELL_NUMBER = "cell_number";
	public static final String TASK_COLUMN_ID_IMAGE = "id_image";
	
	private Context context;
	private SQLiteDatabase database;
	private SchedulerDBHelper bdHelper;

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
		
		if (bdHelper == null) {
			bdHelper = new SchedulerDBHelper(context);
		}
		
		if (database == null) {
			database = bdHelper.getWritableDatabase();
		}
		
		return this;
	}

	/**
	 * Close database.
	 */
	public void close() {
		if (bdHelper != null) {
			bdHelper.close();
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
	 * Update a new scheduler.
	 * 
	 * @param name Name of scheduer
	 * 
	 * @return id of new scheduler
	 */
	public boolean updateScheduler(long id, String name) {
		ContentValues updateValues = createSchedulerContentValues(name);
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
	 * Delete a scheduler.
	 * 
	 * @param id Identifier of scheduler
	 * 
	 * @return true if scheduler has deleted, else false
	 */
	public boolean deleteScheduler(long id) {
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
		return database.query(SCHEDULER_TABLE_NAME, new String[] { SCHEDULER_PRIMARY_KEY, SCHEDULER_COLUMN_NAME },
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
				SCHEDULER_PRIMARY_KEY, SCHEDULER_COLUMN_NAME},
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
	 * 
	 * @return ContentValues object
	 */
	private ContentValues createSchedulerContentValues(String name) {
		ContentValues values = new ContentValues();
		values.put(SCHEDULER_COLUMN_NAME, name);
		return values;
	}
	
	/**
	 * Clone method is overridden for Singleton pattern.
	 */
	public Object clone() throws CloneNotSupportedException {
	        throw new CloneNotSupportedException(); 
	}
}
