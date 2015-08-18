/*******************************************************************************
 * Copyright (c) 2014 Luis M. Gallardo D..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Luis M. Gallardo D. - initial implementation
 ******************************************************************************/
package com.lgallardo.youtorrentcontroller;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerItemCustomAdapter extends ArrayAdapter<ObjectDrawerItem> {

	Context mContext;
	int layoutResourceId;

	public static ArrayList<ObjectDrawerItem> items;

	public DrawerItemCustomAdapter(Context mContext, int layoutResourceId,
								   ArrayList<ObjectDrawerItem> items) {

		super(mContext, R.layout.drawer_row, items);
		this.layoutResourceId = layoutResourceId;
		this.mContext = mContext;
		this.items= items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View listItem =  convertView;

		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		listItem = inflater.inflate(R.layout.drawer_row, parent, false);

		ImageView imageViewIcon = (ImageView) listItem.findViewById(R.id.imageViewIcon);
		TextView textViewName = (TextView) listItem.findViewById(R.id.textViewName);

		ObjectDrawerItem folder = items.get(position);
		
		imageViewIcon.setImageResource(folder.icon);
		textViewName.setText(folder.name);

		return listItem;
	}

}
