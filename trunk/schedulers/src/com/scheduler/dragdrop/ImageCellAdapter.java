package com.scheduler.dragdrop;

import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.scheduler.R;

/**
 * This class is used with a GridView object. 
 * It provides a set of ImageCell objects that support dragging and dropping.
 */
public class ImageCellAdapter extends BaseAdapter {

	public static final int DEFAULT_NUM_IMAGES = 8;

	public ViewGroup mParentView = null;
	private Context mContext;
	Map<Integer,Integer> taskMap;
	
	public ImageCellAdapter(Context c, Map<Integer,Integer> taskMap) {
		mContext = c;
		this.taskMap = taskMap;
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
	 * getView Return a view object for the grid.
	 * 
	 * @return ImageCell
	 */
	public View getView(int position, View convertView, ViewGroup parent) {
		mParentView = parent;

		ImageCell v = null;
		Integer imageId = taskMap.get(position);
		if (convertView == null) {
			if (imageId != null) {
				v = new ImageCell(mContext);
				v.setImageResource(imageId);
				v.setTag(imageId);
				v.mEmpty = false;
				v.setBackgroundResource(R.color.cell_filled);
			} else {
				// If it's not recycled, create a new ImageCell.
				v = new ImageCell(mContext);
				v.mEmpty = true;
				v.setBackgroundResource(R.color.cell_empty);
			}
			
			v.setLayoutParams(new GridView.LayoutParams(85, 85));
			v.setScaleType(ImageView.ScaleType.CENTER_CROP);
			v.setPadding(8, 8, 8, 8);
			
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
		
		// v.setBackgroundResource (R.color.drop_target_enabled);
		// v.mGrid.requestDisallowInterceptTouchEvent (true);
		// v.setImageResource (R.drawable.hello);

		// Set up to relay events to the activity.
		// The activity decides which events trigger drag operations.
		// Activities like the Android Launcher require a long click to get a
		// drag operation started.
		v.setOnTouchListener((View.OnTouchListener) mContext);
		v.setOnClickListener((View.OnClickListener) mContext);
		v.setOnLongClickListener((View.OnLongClickListener) mContext);

		return v;
	}
}