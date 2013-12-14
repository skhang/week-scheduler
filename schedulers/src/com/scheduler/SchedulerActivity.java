package com.scheduler;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.scheduler.db.SchedulerDBAdapter;

public class SchedulerActivity extends ListActivity {

	private SchedulerDBAdapter dbAdapter;
	private Cursor cursor;
	
	private Uri mImageCaptureUri;
	private ImageView mImageView;
	private AlertDialog cameraDialog;
	private Dialog schedulerDialog;
	
	private static final int PICK_FROM_CAMERA = 1;
	private static final int CROP_FROM_CAMERA = 2;
	private static final int PICK_FROM_FILE = 3;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dbAdapter = SchedulerDBAdapter.getInstace(this);
		dbAdapter.open();

		loadData();

		buildPictureDialog();
		
		ImageButton addScheduler = (ImageButton) findViewById(R.id.add_scheduler);
		addScheduler.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				schedulerDialog = new Dialog(arg0.getContext());
				schedulerDialog.setContentView(R.layout.edit_scheduler_dialog);
				schedulerDialog.setTitle(R.string.new_scheduler);
				schedulerDialog.show();

				mImageView = (ImageView)schedulerDialog.findViewById(R.id.imageView_picture);
				
				Button buttonOK = (Button) schedulerDialog.findViewById(R.id.button_ok);
				buttonOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						EditText editText = (EditText) schedulerDialog.findViewById(R.id.text_view_scheduler);
						String newName = editText.getText().toString();
						
						// Get imageview bytes
						BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();
						Bitmap bitmap = bitmapDrawable.getBitmap();
						byte[] imageBytes = getBitmapAsByteArray(bitmap);
						
						if (!"".equals(newName)) {
							dbAdapter.insertScheduler(editText.getText().toString(), imageBytes);
							cursor.requery();
							Toast.makeText(getApplicationContext(), R.string.scheduler_added, Toast.LENGTH_SHORT).show();
							schedulerDialog.dismiss();
						}
					}
				});

				Button buttonCancel = (Button) schedulerDialog.findViewById(R.id.button_cancel);
				buttonCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// Close dialog
						schedulerDialog.dismiss();
					}
				});
				
				Button buttonPicture = (Button) schedulerDialog.findViewById(R.id.button_picture);
				buttonPicture.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// Open camera/galley dialog
						cameraDialog.show();
					}
				});
			}
		});
	}

	public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
	    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	    bitmap.compress(CompressFormat.PNG, 0, outputStream);       
	    return outputStream.toByteArray();
	}
	
	private void buildPictureDialog() {

		final String[] items = new String[] { "Take from camera", "Select from gallery" };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		builder.setTitle("Select Image");
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

					intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

					try {
						intent.putExtra("return-data", true);

						startActivityForResult(intent, PICK_FROM_CAMERA);
					} catch (ActivityNotFoundException e) {
						e.printStackTrace();
					}
				} else { // pick from file
					Intent intent = new Intent();

					intent.setType("image/*");
					intent.setAction(Intent.ACTION_GET_CONTENT);

					startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
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
				mImageView.setImageBitmap(photo);
			}

			File f = new File(mImageCaptureUri.getPath());
			if (f.exists()) {
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
			Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

			return;
		} else {
			intent.setData(mImageCaptureUri);

			intent.putExtra("outputX", 60);
			intent.putExtra("outputY", 60);
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
				builder.setTitle("Choose Crop App");
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
	 
	private void loadData() {

		cursor = dbAdapter.loadSchedulers(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
		startManagingCursor(cursor);
		
		String[] from = new String[] { SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, SchedulerDBAdapter.SCHEDULER_COLUMN_NAME, SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY };
		int[] to = new int[] { R.id.img_scheduler, R.id.text_scheduler_name, R.id.img_edit_scheduler, R.id.img_delete_scheduler};
		SchedulerCursorAdapter notas = new SchedulerCursorAdapter(this, R.layout.row, cursor, from, to, dbAdapter);
		setListAdapter(notas);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy() {
		
		super.onDestroy();
		if (dbAdapter != null) {
			cursor.close();
			dbAdapter.close();
		}
	}
}
