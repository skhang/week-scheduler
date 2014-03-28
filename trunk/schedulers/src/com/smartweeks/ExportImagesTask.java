package com.smartweeks;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.widget.Toast;

import com.smartweeks.db.SchedulerDBHelper;

public class ExportImagesTask extends AsyncTask<String, Void, Boolean> {

	private static final File DATABASE_DIRECTORY = new File(Environment.getExternalStorageDirectory(), "smartweeks");
	private static final String PACKAGE_NAME = "com.smartweeks";
	private static final String DATABASE_NAME = SchedulerDBHelper.DB_NAME;
	private static final File DATA_DIRECTORY_DATABASE = new File(Environment.getDataDirectory() + "/data/" + PACKAGE_NAME + "/databases/" + DATABASE_NAME);
	private static final String DB_BAKCUP_NAME = "smart_weeks_backup.db";

	private ProgressDialog dialogo;
	private Context context;
	private String message;

	public ExportImagesTask(Context context) {
		this.dialogo = new ProgressDialog(context);
		this.context = context;
		this.message = context.getResources().getString(R.string.pref_task_exported);
	}

	protected void onPreExecute() {
		this.dialogo.setMessage(context.getResources().getString(R.string.pref_task_exporting));
		this.dialogo.show();
	}

	/**
	 * Export data base to external file.
	 * 
	 * @return true if images were exported, else false
	 */
	private boolean exportDataBase() {

		boolean result = false;
		
		if (!isPresentSD()) {
			message = context.getResources().getString(R.string.msg_no_sdcard_found);
			return false;
		}

		try {
			File dbFile = DATA_DIRECTORY_DATABASE;
			String filename = DB_BAKCUP_NAME;

			File exportDir = DATABASE_DIRECTORY;
			File file = new File(exportDir, filename);
			
			if (!exportDir.exists()) {
				exportDir.mkdirs();
			}
			
			file.createNewFile();
			copyFile(dbFile, file);
			result =  true;
			
		} catch (IOException e) {
			e.printStackTrace();
			message = context.getResources().getString(R.string.msg_error_exporting_database);
			result = false;
		}
		
		return result;
	}

	private void copyFile(File src, File dst) throws IOException {
		FileChannel inChannel = new FileInputStream(src).getChannel();
		FileChannel outChannel = new FileOutputStream(dst).getChannel();
		try {
			inChannel.transferTo(0, inChannel.size(), outChannel);
		} finally {
			if (inChannel != null)
				inChannel.close();
			if (outChannel != null)
				outChannel.close();
		}
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

		return exportDataBase();
	}

	protected void onPostExecute(final Boolean success) {
		if (this.dialogo.isShowing()) {
			this.dialogo.dismiss();
		}

		Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
	}
}
