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

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.Toast;

import com.smartweeks.R;

/**
 * A ViewGroup that supports dragging within it. Dragging starts in an object
 * that implements the DragSource interface and ends in an object that
 * implements the DropTarget interface.
 * 
 * <p>
 * This class used DragLayer in the Android Launcher activity as a model. It is
 * a bit different in several respects: (1) it supports dragging to a grid view
 * and trash area; (2) it dynamically adds drop targets when a drag-drop
 * sequence begins. The child views of the GridView are assumed to implement the
 * DropTarget interface.
 */
public class DragLayer extends FrameLayout implements DragController.DragListener {
	
	DragController mDragController;
	GridView mGridView;

	/**
	 * Used to create a new DragLayer from XML.
	 * 
	 * @param context The application's context.
	 * @param attrs The attribtues set containing the Workspace's customization values.
	 */
	public DragLayer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDragController(DragController controller) {
		mDragController = controller;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return mDragController.dispatchKeyEvent(event) || super.dispatchKeyEvent(event);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return mDragController.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return mDragController.onTouchEvent(ev);
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		return mDragController.dispatchUnhandledMove(focused, direction);
	}

	/**
	 * Get the value of the GridView property.
	 * 
	 * @return GridView
	 */
	public GridView getGridView() {
		return mGridView;
	} 

	/**
	 * Set the value of the GridView property.
	 * 
	 * @param newValue GridView
	 */
	public void setGridView(GridView newValue) {
		mGridView = newValue;
	}

	// DragListener Interface Methods

	/**
	 * A drag has begun.
	 * 
	 * @param source An object representing where the drag originated
	 * @param info The data associated with the object that is being dragged
	 * @param dragAction The drag action: either {@link DragController#DRAG_ACTION_MOVE} or {@link DragController#DRAG_ACTION_COPY}
	 */
	public void onDragStart(DragSource source, Object info, int dragAction) {
		// We are starting a drag.
		// Build up a list of DropTargets from the child views of the GridView. Tell the drag controller about them.
		if (mGridView != null) {
			int numVisibleChildren = mGridView.getChildCount();
			for (int i = 0; i < numVisibleChildren; i++) {
				DropTarget view = (DropTarget) mGridView.getChildAt(i);
				mDragController.addDropTarget(view);
			}
		}

		// Always add the delete_zone so there is a place to get rid of views.
		// Find the delete_zone and add it as a drop target.
		// That gives the user a place to drag views to get them off the screen.
		View v = findViewById(R.id.delete_zone_view);
		if (v != null) {
			DeleteZone dz = (DeleteZone) v;
			mDragController.addDropTarget(dz);
		}
	}

	/**
	 * A drag-drop operation has eneded.
	 */
	public void onDragEnd() {
		//toast("onDragEnd");
		mDragController.removeAllDropTargets();
	}

	/**
	 * Show a string on the screen via Toast.
	 * 
	 * @param msg  String
	 * @return void
	 */
	public void toast(String msg) {
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
	} 

} 