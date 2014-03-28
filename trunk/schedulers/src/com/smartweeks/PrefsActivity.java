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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.smartweeks.db.SchedulerDBAdapter;
import com.smartweeks.tasks.TaskItem;
import com.smartweeks.tasks.TaskItemAdapter;

public class PrefsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

	private SchedulerDBAdapter dbAdapter;
	private TaskItemAdapter adapter;
			
	private Uri mImageCaptureUri;
	private AlertDialog cameraDialog;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.prefs);
		
		dbAdapter = SchedulerDBAdapter.getInstace(this);
		dbAdapter.open();
		
		getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		
		addAboutDialog();
		addRestoreTasks();
		addExportTasksToDB();
		addImportTasksToDB();
		addTaskManagerDialog();
		buildPictureDialog();
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
	
	private void addRestoreTasks() {
		
		Preference dialogRestoreTasks = (Preference) getPreferenceScreen().findPreference("preference_restore_tasks");
		dialogRestoreTasks.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {

				final Preference dialogPreference = preference;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(preference.getContext());
				alertDialogBuilder.setTitle(dialogPreference.getContext().getResources().getString(R.string.pref_task_restore_title));
				// set dialog message
				alertDialogBuilder.setMessage(dialogPreference.getContext().getResources().getString(R.string.pref_task_restore_confirm)).setCancelable(false)
				.setPositiveButton(dialogPreference.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new RestoreImagesTask(dialogPreference.getContext(), dbAdapter).execute();
						dialog.dismiss();
					}
				}).setNegativeButton(dialogPreference.getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return true;
			}

		});
	}

	private void addExportTasksToDB() {
		
		Preference dialogRestoreTasks = (Preference) getPreferenceScreen().findPreference("preference_export_tasks");
		dialogRestoreTasks.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {

				final Preference dialogPreference = preference;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(preference.getContext());
				alertDialogBuilder.setTitle(dialogPreference.getContext().getResources().getString(R.string.pref_task_export_title));
				// set dialog message
				alertDialogBuilder.setMessage(dialogPreference.getContext().getResources().getString(R.string.pref_task_export_confirm)).setCancelable(false)
				.setPositiveButton(dialogPreference.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new ExportImagesTask(dialogPreference.getContext()).execute();
						dialog.dismiss();
					}
				}).setNegativeButton(dialogPreference.getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return true;
			}

		});
	}

	private void addImportTasksToDB() {
		
		Preference dialogRestoreTasks = (Preference) getPreferenceScreen().findPreference("preference_import_tasks");
		dialogRestoreTasks.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			public boolean onPreferenceClick(Preference preference) {

				final Preference dialogPreference = preference;
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(preference.getContext());
				alertDialogBuilder.setTitle(dialogPreference.getContext().getResources().getString(R.string.pref_task_import_title));
				// set dialog message
				alertDialogBuilder.setMessage(dialogPreference.getContext().getResources().getString(R.string.pref_task_import_confirm)).setCancelable(false)
				.setPositiveButton(dialogPreference.getContext().getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						new ImportImagesTask(dialogPreference.getContext(), dbAdapter).execute();
						dialog.dismiss();
					}
				}).setNegativeButton(dialogPreference.getContext().getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				AlertDialog alertDialog = alertDialogBuilder.create();
				alertDialog.show();
				return true;
			}

		});
	}

	private void addTaskManagerDialog() {
		
		Preference dialogPreference = (Preference) getPreferenceScreen().findPreference("dialog_preference_manage_tasks");
		dialogPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
		        public boolean onPreferenceClick(Preference preference) {
		        	
		        	dbAdapter = SchedulerDBAdapter.getInstace(preference.getContext());
		        	
		        	// Create a Dialog component
					final Dialog dialog = new Dialog(preference.getContext());
					dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
					dialog.setContentView(R.layout.images_dialog);

					// Prepare ListView in dialog
					ListView dialogListView = (ListView) dialog.findViewById(R.id.tasksList);
					dialogListView.setEmptyView(dialog.findViewById(R.id.empty));
					
					final List<TaskItem> allImages = loadAllTasksFromDB();
					adapter = new TaskItemAdapter(preference.getContext(), R.layout.images_dialog_rom, allImages);
					dialogListView.setAdapter(adapter);

					ImageButton dialogCloseButton = (ImageButton) dialog.findViewById(R.id.imageButton_back);
					dialogCloseButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							dialog.dismiss();
						}
					});
					
					ImageButton dialogNewTaskButton = (ImageButton) dialog.findViewById(R.id.imageButton_new_image);
					dialogNewTaskButton.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// Open camera/galley dialog
							cameraDialog.show();
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
								// Remove image
								dbAdapter.deleteImage(item.getId());
								// Remove tasks associated
								dbAdapter.deleteTasksByImageId(item.getId());
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
	
	private void buildPictureDialog() {

		final String[] items = new String[] { getResources().getString(R.string.from_camera), getResources().getString(R.string.from_gallery), getResources().getString(R.string.cancel) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle(getResources().getString(R.string.select_image));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { 
				if (item == 0) {
					// Pick from camera
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));
					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
					intent.putExtra("return-data", true);
					startActivityForResult(intent, PICK_FROM_CAMERA);
					
				} else if (item == 1) { 
					// Pick from file
					Intent intent = new Intent();
					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);
					startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.complete_action)), PICK_FROM_FILE);
					
				} else {
					// Cancel - close current dialog
					cameraDialog.dismiss();
				}
			}
		});

		cameraDialog = builder.create();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;

		switch (requestCode) {
		case PICK_FROM_CAMERA:
			doCrop();

			break;

		case PICK_FROM_FILE:
			mImageCaptureUri = data.getData();
			doCrop();
			break;

		case CROP_FROM_CAMERA:
			Bundle extras = data.getExtras();
			if (extras != null) {
				Bitmap photo = extras.getParcelable("data");
				if (photo != null) {
					
					// Insert image into database
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
					byte[] byteArray = stream.toByteArray();
					long id = dbAdapter.insertImage(byteArray);
					
					// Add image to adapter
					adapter.add(new TaskItem((int)id, photo));
					adapter.notifyDataSetChanged();
				}
			}

			File f = new File(mImageCaptureUri.getPath());
			if (f != null && f.exists()) {
				f.delete();
			}

			break;
		}
	}
	
	 private void doCrop() {
		final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setType("image/*");

		List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

		int size = list.size();

		if (size == 0) {
			
			Toast.makeText(this, getResources().getString(R.string.not_image_crop_app), Toast.LENGTH_SHORT).show();
			return;
			
		} else {
			
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 256);
			intent.putExtra("outputY", 256);
			intent.putExtra("aspectX", 1);
			intent.putExtra("aspectY", 1);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", true);

			if (size == 1) {
				Intent i = new Intent(intent);
				ResolveInfo res = list.get(0);

				i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

				startActivityForResult(i, CROP_FROM_CAMERA);
			} else {
				for (ResolveInfo res : list) {
					final CropOption co = new CropOption();

					co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
					co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
					co.appIntent = new Intent(intent);

					co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

					cropOptions.add(co);
				}

				CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				builder.setTitle(getResources().getString(R.string.choose_crop_app));
				builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int item) {
						startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
					}
				});

				builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {

						if (mImageCaptureUri != null) {
							getContentResolver().delete(mImageCaptureUri, null, null);
							mImageCaptureUri = null;
						}
					}
				});

				AlertDialog alert = builder.create();

				alert.show();
			}
		}
	}
	
	/**
	 * Load all tasks from data base.
	 * 
	 * @return List<TaskItem> allImages
	 */
	@SuppressLint("UseSparseArrays")
	private List<TaskItem> loadAllTasksFromDB() {
		
		List<TaskItem> allImages = new ArrayList<TaskItem>();
		
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