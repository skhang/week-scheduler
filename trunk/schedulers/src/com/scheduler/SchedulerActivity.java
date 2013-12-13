package com.scheduler;

import android.app.Dialog;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.scheduler.db.SchedulerDBAdapter;

public class SchedulerActivity extends ListActivity {

	private SchedulerDBAdapter dbAdapter;
	private Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		dbAdapter = SchedulerDBAdapter.getInstace(this);
		dbAdapter.open();

		loadData();

		ImageButton addScheduler = (ImageButton) findViewById(R.id.add_scheduler);
		addScheduler.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final Dialog dialog = new Dialog(arg0.getContext());
				dialog.setContentView(R.layout.edit_scheduler_dialog);
				dialog.setTitle(R.string.new_scheduler);
				dialog.show();

				Button buttonOK = (Button) dialog.findViewById(R.id.button_ok);
				buttonOK.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						EditText editText = (EditText) dialog.findViewById(R.id.text_view_scheduler);
						String newName = editText.getText().toString();
						if (!"".equals(newName)) {
							dbAdapter.insertScheduler(editText.getText().toString());
							cursor.requery();
							Toast.makeText(getApplicationContext(), R.string.scheduler_added, Toast.LENGTH_SHORT).show();
							dialog.dismiss();
						}
					}
				});

				Button buttonCancel = (Button) dialog.findViewById(R.id.button_cancel);
				buttonCancel.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// Close dialog
						dialog.dismiss();
					}
				});
			}
		});
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
