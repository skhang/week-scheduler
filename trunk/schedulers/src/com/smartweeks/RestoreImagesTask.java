package com.smartweeks;

import java.io.ByteArrayOutputStream;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.Toast;

import com.smartweeks.db.SchedulerDBAdapter;

public class RestoreImagesTask extends AsyncTask<String, Void, Boolean> {

	private final ProgressDialog dialogo;
	private Context context;
	private SchedulerDBAdapter dbAdapter;

	public RestoreImagesTask(Context context, SchedulerDBAdapter dbAdapter) {
		dialogo = new ProgressDialog(context);
		this.context = context;
		this.dbAdapter = dbAdapter;
	}

	protected void onPreExecute() {
		this.dialogo.setMessage(context.getResources().getString(R.string.pref_task_restoring));
		this.dialogo.show();
	}

	/**
	 * Delete current tasks and restore tasks from resources.
	 */
	private void restoreTasks() {

		int[] iconIds = { R.drawable.blackboard_icon, R.drawable.homework_icon, R.drawable.games_icon, R.drawable.paint_icon, R.drawable.music_icon, R.drawable.guitar_icon, R.drawable.piano_icon, R.drawable.drums_icon, R.drawable.beach_icon,
				R.drawable.birthday_icon, R.drawable.museum_icon, R.drawable.laptop_icon, R.drawable.doctor_icon, R.drawable.bouncycastle_icon, R.drawable.swing_icon, R.drawable.cinema_icon, R.drawable.theatre_icon, R.drawable.grandparents_icon,
				R.drawable.english_icon, R.drawable.football_icon, R.drawable.ballet_icon, R.drawable.basketball_icon, R.drawable.swimming_icon, R.drawable.bike_icon, R.drawable.bowling_icon, R.drawable.karate_icon, R.drawable.rugby_icon,
				R.drawable.tennis_icon, R.drawable.present_icon, R.drawable.tree_icon };

		// Remove all images
		dbAdapter.deleteAllImages();

		// Remove all tasks
		dbAdapter.deleteAllTasks();

		byte[] img = null;
		Bitmap bitmap = null;
		ByteArrayOutputStream bos = null;
		for (int iconId : iconIds) {

			bitmap = BitmapFactory.decodeResource(context.getResources(), iconId);
			bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
			img = bos.toByteArray();
			dbAdapter.insertImage(img);
		}
	}

	protected Boolean doInBackground(final String... args) {

		restoreTasks();
		return true;
	}

	protected void onPostExecute(final Boolean success) {
		if (this.dialogo.isShowing()) {
			this.dialogo.dismiss();
		}
		
		Toast.makeText(context, context.getResources().getString(R.string.pref_task_restored), Toast.LENGTH_SHORT).show();
	}
}
