package com.scheduler;

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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.scheduler.db.SchedulerDBAdapter;
import com.scheduler.dragdrop.DragActivity;

/**
 * This class draws elements of the list
 */
public class SchedulerDataViewBinder implements SimpleCursorAdapter.ViewBinder {

	private Context context;
	private SchedulerDBAdapter dbAdapter;
	private Cursor mainCursor;
	private AlertDialog cameraDialog;
	private Dialog schedulerDialog;
	
	public SchedulerDataViewBinder(Context context, Cursor c, SchedulerDBAdapter dbAdapter, AlertDialog cameraDialog, Dialog schedulerDialog) {
		this.context = context;
		this.mainCursor = c;
		this.dbAdapter = dbAdapter;
		this.cameraDialog = cameraDialog;
		this.schedulerDialog = schedulerDialog;
	}

	@Override
	public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

		this.mainCursor = cursor;
		
		if (view.getId() == R.id.text_scheduler_name) {
			
			// Bind text scheduler name
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			int columnName = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
			String name = cursor.getString(columnName);
			
			TextView textSchedulerName = (TextView) view;
			textSchedulerName.setText(name);
			textSchedulerName.setTag(schedulerId + "#" + name );

			textSchedulerName.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(context, DragActivity.class);
					
					String args[] = arg0.getTag().toString().split("#");
					
					// Primary key and name of current scheduler as extra field
					Bundle extras = new Bundle();
					extras.putString(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, args[0]);
					extras.putString(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME, args[1]);
					i.putExtras(extras);
					
					context.startActivity(i);
				}
			});
			return true;
		}

		if (view.getId() == R.id.img_scheduler) {
			
			// Bind picture of scheduler
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			int columnName = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
			String name = cursor.getString(columnName);
			
			int columnImage = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_IMAGE);
			byte[] imageBytes = cursor.getBlob(columnImage);
			
			ImageView imageScheduler = (ImageView) view;
			imageScheduler.setTag(schedulerId + "#" + name);
			if (imageBytes != null && imageBytes.length > 0) {
				Bitmap b = BitmapFactory.decodeByteArray(imageBytes , 0, imageBytes.length);
				imageScheduler.setImageBitmap(roundCornerImage(b, 50));
			} else {
				imageScheduler.setImageBitmap(roundCornerImage(BitmapFactory.decodeResource(context.getResources(), R.drawable.king_icon), 50));
			}
			
			imageScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					Intent i = new Intent(context, DragActivity.class);
					
					String args[] = arg0.getTag().toString().split("#");
					
					// Primary key and name of current scheduler as extra field
					Bundle extras = new Bundle();
					extras.putString(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY, args[0]);
					extras.putString(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME, args[1]);
					i.putExtras(extras);
					
					context.startActivity(i);
				}
			});
			return true;
		}
		
		if (view.getId() == R.id.img_edit_scheduler) {
			// Bind edit Imagebutton
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			int columnName = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_NAME);
			String name = cursor.getString(columnName);
			
			int columnImage = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_COLUMN_IMAGE);
			final byte[] imageBytes  = cursor.getBlob(columnImage);
			
			ImageButton editScheduler = (ImageButton) view;
			editScheduler.setTag(schedulerId + "#" + name);
			
			editScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View arg0) {
					
					schedulerDialog.setTitle(R.string.edit_plan);
					schedulerDialog.show();

					ImageButton editScheduler = (ImageButton) arg0;
					
					String[] data = editScheduler.getTag().toString().split("#");
					final String schedulerId = data[0];
					final String schedulerName = data[1];
					EditText editText = (EditText) schedulerDialog.findViewById(R.id.text_view_scheduler);
					editText.setText(schedulerName);
					editText.setTag(schedulerId);
					
					// Get imageview bytes
					ImageView mImageView = (ImageView)schedulerDialog.findViewById(R.id.imageView_picture);
					Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes , 0, imageBytes .length);
					mImageView.setImageBitmap(bitmap);

					Button buttonOK = (Button) schedulerDialog.findViewById(R.id.button_ok);
					buttonOK.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							EditText editText = (EditText) schedulerDialog.findViewById(R.id.text_view_scheduler);
							String newName = editText.getText().toString();
							if (newName != null && !"".equals(newName.trim())) {
								
								// Get imageview bytes
								ImageView mImageView = (ImageView)schedulerDialog.findViewById(R.id.imageView_picture);
								BitmapDrawable bitmapDrawable = (BitmapDrawable) mImageView.getDrawable();
								Bitmap bitmap = bitmapDrawable.getBitmap();
								byte[] newImageBytes = SchedulerActivity.getBitmapAsByteArray(bitmap);
								
								dbAdapter.updateScheduler(Integer.valueOf(schedulerId), newName.trim(), newImageBytes);
								mainCursor.requery();
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
			return true;
		}
		
		if (view.getId() == R.id.img_delete_scheduler) {
			// Bind delete Imagebutton
			int columnId = cursor.getColumnIndex(SchedulerDBAdapter.SCHEDULER_PRIMARY_KEY);
			String schedulerId = cursor.getString(columnId);
			
			ImageButton deleteScheduler = (ImageButton) view;
			deleteScheduler.setTag(schedulerId);
			deleteScheduler.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(final View arg0) {
					AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);

					alertDialog.setTitle(context.getResources().getString(R.string.confirm_delete));
					alertDialog.setMessage(context.getResources().getString(R.string.confirm_delete_question));

					// Setting Icon to Dialog
					alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							// Remove current plan
							ImageButton deleteScheduler = (ImageButton) arg0;
							String schedulerId = deleteScheduler.getTag().toString();
							dbAdapter.deleteScheduler(Long.valueOf(schedulerId));
							mainCursor.requery();
							//Toast.makeText(context, R.string.scheduler_deleted, Toast.LENGTH_SHORT).show();
						}
					});
					alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					alertDialog.show();
				}
			});
			return true;
		}
		
		return false;
	}
	
	/**
	 * Add round corners to bitmap image.
	 * 
	 * @param src Source of image
	 * @param round Round pixels
	 * 
	 * @return Bitmap with round corners
	 */
	public Bitmap roundCornerImage(Bitmap src, float round) {
		
	     // Source image size
	     int width = src.getWidth();
	     int height = src.getHeight();
	     // create result bitmap output
	     Bitmap result = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	     // set canvas for painting
	     Canvas canvas = new Canvas(result);
	     canvas.drawARGB(0, 0, 0, 0);
	  
	     // configure paint
	     final Paint paint = new Paint();
	     paint.setAntiAlias(true);
	     paint.setColor(Color.BLACK);
	  
	     // configure rectangle for embedding
	     final Rect rect = new Rect(0, 0, width, height);
	     final RectF rectF = new RectF(rect);
	  
	     // draw Round rectangle to canvas
	     canvas.drawRoundRect(rectF, round, round, paint);
	  
	     // create Xfer mode
	     paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	     // draw source image to canvas
	     canvas.drawBitmap(src, rect, rect, paint);
	  
	     // return final image
	     return result;
	 }
}