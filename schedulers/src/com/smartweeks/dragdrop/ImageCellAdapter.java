package com.smartweeks.dragdrop;

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

import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.smartweeks.R;

/**
 * This class is used with a GridView object. 
 * It provides a set of ImageCell objects that support dragging and dropping.
 */
public class ImageCellAdapter extends BaseAdapter {

	public ViewGroup mParentView = null;
	private Context mContext;
	Map<Integer,Integer> taskMap;
	Map<Integer,Bitmap> allImagesMap;
	
	public ImageCellAdapter(Context c, Map<Integer,Integer> taskMap, Map<Integer,Bitmap> allImagesMap) {
		mContext = c;
		this.taskMap = taskMap;
		this.allImagesMap = allImagesMap;
	}

	public int getCount() {
		Resources res = mContext.getResources();
		int numImages = res.getInteger(R.integer.num_images);
		return numImages;
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return position;
	}
	
	/**
	 * Return a view object for the grid.
	 * 
	 * @return ImageCell view object for the grid
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		mParentView = parent;

		ImageCell v = null;
		Integer imageId = taskMap.get(position);
		if (convertView == null) {
			if (imageId != null) {
				v = new ImageCell(mContext);
				v.setImageBitmap(allImagesMap.get(imageId));
				v.setTag(imageId);
				v.mEmpty = false;
				v.setBackgroundResource(R.color.cell_filled);
			} else {
				// If it's not recycled, create a new ImageCell.
				v = new ImageCell(mContext);
				v.mEmpty = true;
				v.setBackgroundResource(R.color.cell_empty);
			}
			
			//v.setLayoutParams(new GridView.LayoutParams(85, 85));
			v.setScaleType(ImageView.ScaleType.CENTER_CROP);
			//v.setPadding(8, 8, 8, 8);
			
		} else {
			v = (ImageCell) convertView;
			if (imageId != null) {
				v.mEmpty = false;
				v.setBackgroundResource(R.color.cell_filled);
			} else {
				v.mEmpty = true;
				v.setBackgroundResource(R.color.cell_empty);
			}
		}

		v.mCellNumber = position;
		v.mGrid = (GridView) mParentView;
		
		v.setOnTouchListener((View.OnTouchListener) mContext);
		v.setOnClickListener((View.OnClickListener) mContext);
		v.setOnLongClickListener((View.OnLongClickListener) mContext);

		return v;
	}
}