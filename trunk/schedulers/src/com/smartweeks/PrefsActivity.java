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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.smartweeks.db.SchedulerDBAdapter;
import com.smartweeks.tasks.TaskItem;
import com.smartweeks.tasks.TaskItemAdapter;

public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		addAboutDialog();
		addTaskManagerDialog();
	}
	
	private void addAboutDialog() {
		
		Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("dialog_preference_about");
		dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		        public boolean onPreferenceClick(Preference preference) {
		        	
		        	// Create a Dialog component
					final Dialog dialog = new Dialog(preference.getContext());
					dialog.setContentView(R.layout.prefs_dialog);
					dialog.setTitle(getApplicationContext().getResources().getString(R.string.pref_dialog_title));
					Button dialogButton = (Button) dialog.findViewById(R.id.dialogButton);
					dialogButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					dialog.show();
		            return true;
		        }
		    }
		);
	}
	
	private void addTaskManagerDialog() {
		
		Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("dialog_preference_manage_tasks");
		dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		        public boolean onPreferenceClick(Preference preference) {
		        	
		        	final SchedulerDBAdapter dbAdapter = SchedulerDBAdapter.getInstace(preference.getContext());
		        	
		        	// Create a Dialog component
					final Dialog dialog = new Dialog(preference.getContext());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.images_dialog);

					// Prepare ListView in dialog
					ListView dialogListView = (ListView) dialog.findViewById(R.id.tasksList);
					
					final List<TaskItem> allImages = loadAllTasksFromDB();
					final TaskItemAdapter adapter = new TaskItemAdapter(preference.getContext(), R.layout.images_dialog_rom, allImages);
					dialogListView.setAdapter(adapter);

					ImageButton dialogNewTaskButton = (ImageButton) dialog.findViewById(R.id.imageButton_new_image);
					dialogNewTaskButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});

					ImageButton dialogDeleteTaskButton = (ImageButton) dialog.findViewById(R.id.imageButton_delete_image);
					dialogDeleteTaskButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Set<Integer> selectedItems = adapter.getSelectedItems();
							List<TaskItem> tasksToDelete = new ArrayList<TaskItem>();
							for (Integer currentId : selectedItems) {
								TaskItem item = allImages.get(currentId);
								dbAdapter.deleteImage(item.getId());
								tasksToDelete.add(item);
							}
							
							// Update adapter
							for (TaskItem itemToDelete: tasksToDelete) {
								adapter.remove(itemToDelete);
							}
							adapter.clearSelectedItems();
							adapter.notifyDataSetChanged();
						}
					});
					
					dialog.show();
		            return true;
		        }
		    }
		);
	}
	
	/**
	 * Load all tasks from data base.
	 * 
	 * @return List<TaskItem> allImages
	 */
	@SuppressLint("UseSparseArrays")
	private List<TaskItem> loadAllTasksFromDB() {
		
		List<TaskItem> allImages = new ArrayList<TaskItem>();
		
		SchedulerDBAdapter dbAdapter = SchedulerDBAdapter.getInstace(this);
		dbAdapter.open();
		Cursor cursor = dbAdapter.loadImages(SchedulerDBAdapter.IMAGES_PRIMARY_KEY);
		if (cursor != null && cursor.moveToFirst()) {
			do {
				int columnId = cursor.getColumnIndex(SchedulerDBAdapter.IMAGES_PRIMARY_KEY);
				int id = cursor.getInt(columnId);
				int columnImage = cursor.getColumnIndex(SchedulerDBAdapter.IMAGES_COLUMN_IMAGE);
				byte[] imageBytes = cursor.getBlob(columnImage);
				Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
				allImages.add(new TaskItem(id, bitmap));
		    } while (cursor.moveToNext());
		}
		cursor.close();
		
		return allImages;
	}
	
	public static void updateLanguage(Context context, String language) {
	    if (language != null && !"".equals(language)) {
	    	
	    	if (language.equalsIgnoreCase("default")) {
	    		Locale locale = Locale.getDefault();
		        Configuration config = new Configuration();
		        config.locale = locale;
		        context.getResources().updateConfiguration(config, null);
		        
	    	} else {
	    		Locale locale = new Locale(language);
		        //Locale.setDefault(locale);
		        Configuration config = new Configuration();
		        config.locale = locale;
		        context.getResources().updateConfiguration(config, null);
	    	}
	    }
	}
	
	@Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		
        if (key.equals("language")) {
        	SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
    		String language = sharedPrefs.getString("language", "en_EN");
        	updateLanguage(this, language);
        	restartPrefereces();
        }
    }
	
	@Override
	protected void onPause() {
	    super.onPause();
	    // Unregister the listener whenever a key changes
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
		
	private void restartPrefereces() {
		
		setPreferenceScreen(null);
		addPreferencesFromResource(R.xml.prefs);
		addAboutDialog();
		addTaskManagerDialog();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
}