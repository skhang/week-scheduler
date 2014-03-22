package com.smartweeks.tasks;

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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.smartweeks.R;

/**
 * Adapter for task items.
 */
public class TaskItemAdapter extends ArrayAdapter<TaskItem> {

    private final Context context;
    private final List<TaskItem> items;
    private final int rowResourceId;
    
    private Set<Integer> selected;
    
    public TaskItemAdapter(Context context, int textViewResourceId, List<TaskItem> items) {

        super(context, textViewResourceId, items);

        this.context = context;
        this.items = items;
        this.rowResourceId = textViewResourceId;
        
        this.selected = new HashSet<Integer>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(rowResourceId, parent, false);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgTask);
        
        Bitmap bitmap =items.get(position).getBitmap();
        imageView.setImageBitmap(bitmap);
        
        final CheckBox check = (CheckBox) rowView.findViewById(R.id.checkBox_image);
        check.setId(position);
        
        check.setOnCheckedChangeListener(new OnCheckedChangeListener() { 
           
        	@Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {             

                if (isChecked) {  
                    selected.add(buttonView.getId());
                } else {
                	selected.remove(buttonView.getId());
                }
            }
        });
        
        return rowView;
    }
    
    public Set<Integer> getSelectedItems(){
    	return selected;
    }
    
    public void clearSelectedItems() {
    	 this.selected = new HashSet<Integer>();
    }
    
    @Override
    public void remove(TaskItem taskItem) {
    	items.remove(taskItem);
    }
}