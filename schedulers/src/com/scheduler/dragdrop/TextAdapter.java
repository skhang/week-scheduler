package com.scheduler.dragdrop;

import android.content.Context;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.scheduler.R;

public class TextAdapter extends BaseAdapter {

	private Context context;
	static final String[] DAYS_OF_WEEK = new String[] { "Mon.", "Tue.", "Wed.", "Thu.", "Fri.", "Sat.", "Sun." };

	public TextAdapter(Context context) {
		this.context = context;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		TextView tv = null;

		if (convertView == null) {
			tv = new TextView(context);
			//tv.setLayoutParams(new GridView.LayoutParams(85, 25));
		} else {
			tv = (TextView) convertView;
		}
		tv.setTextSize(10);
		tv.setTypeface(null, Typeface.BOLD);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(R.color.cell_filled_hover);
		tv.setText(DAYS_OF_WEEK[position]);
		
		return tv;
	}

	@Override
	public int getCount() {
		return DAYS_OF_WEEK.length;
	}



	@Override
	public Object getItem(int arg0) {
		return DAYS_OF_WEEK[arg0];
	}



	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
}