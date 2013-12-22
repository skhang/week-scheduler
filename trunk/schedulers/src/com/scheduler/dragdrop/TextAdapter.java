package com.scheduler.dragdrop;

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

import java.util.Calendar;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.scheduler.R;

public class TextAdapter extends BaseAdapter {

	private Context context;
	private static String[] DAYS_OF_WEEK;

	public TextAdapter(Context context) {
		this.context = context;
		DAYS_OF_WEEK = this.context.getResources().getStringArray(R.array.days_of_week);
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		TextView tv = null;

		if (convertView == null) {
			tv = new TextView(context);
			//tv.setLayoutParams(new GridView.LayoutParams(85, 25));
		} else {
			tv = (TextView) convertView;
		}
		tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, 20);
		tv.setTypeface(null, Typeface.BOLD);
		tv.setGravity(Gravity.CENTER);
		tv.setBackgroundResource(R.color.cell_filled_hover);
		tv.setText(DAYS_OF_WEEK[position]);
		tv.setTextColor(context.getResources().getColor(R.color.text_blue));
		
		if (position == getToday()) {
			tv.setBackgroundResource(R.color.today);
		}
		
		return tv;
	}

	private int getToday() {
		int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 2;
		if (today < 0) {
			today = 6;
		}
		return today;
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