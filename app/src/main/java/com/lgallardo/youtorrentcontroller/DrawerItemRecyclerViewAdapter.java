package com.lgallardo.youtorrentcontroller;

/**
 * Created by lgallard on 28/08/15.
 */

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class DrawerItemRecyclerViewAdapter extends RecyclerView.Adapter<DrawerItemRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    public static final int TYPE_ITEM_ACTIVE = 2;


    public static ArrayList<ObjectDrawerItem> items;
    public static int oldActionPosition = 1;
    public static int actionPosition = 0;

    private static MainActivity mainActivity;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int Holderid;

//        TextView textView;
//        ImageView imageView;
//
//        ImageView profile;
//        TextView Name;
//        TextView email;


        // New
        ImageView imageViewIcon;
        TextView textViewName;


        public ViewHolder(final View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            itemView.setClickable(true);
            itemView.setOnClickListener(this);

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

            if (ViewType == TYPE_ITEM || ViewType == TYPE_ITEM_ACTIVE ) {

                imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
                textViewName = (TextView) itemView.findViewById(R.id.textViewName);

                Holderid = 1;                                               // setting holder id as 1 as the object being populated are of type item row
            } else {


//                Name = (TextView) itemView.findViewById(R.id.name);         // Creating Text View object from header.xml for name
//                email = (TextView) itemView.findViewById(R.id.email);       // Creating Text View object from header.xml for email
//                profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                Holderid = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
            }


        }


        // In order to track the item position in RecyclerView
        // Handle item click and set the selection          
        @Override
        public void onClick(View view) {

            // Redraw the old selection and the new

            // Mark old item as inactive
            ObjectDrawerItem drawerItem = DrawerItemRecyclerViewAdapter.items.get(oldActionPosition-1);
            drawerItem.setActive(false);
            DrawerItemRecyclerViewAdapter.items.set(oldActionPosition-1,drawerItem);
//            notifyItemChanged(oldActionPosition);
            notifyDataSetChanged();

            // Mark old item as in active
            actionPosition = getLayoutPosition();
            oldActionPosition = actionPosition;
            drawerItem = DrawerItemRecyclerViewAdapter.items.get(actionPosition-1);
            drawerItem.setActive(true);
            DrawerItemRecyclerViewAdapter.items.set(actionPosition-1,drawerItem);

            notifyItemChanged(actionPosition);


            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - id: " + getLayoutPosition());

            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - actionPosition: " + actionPosition);
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - oldActionPosition: " + oldActionPosition);

            switch (actionPosition) {
                case 1:
                    mainActivity.refreshFromDrawerAction("all", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: All");
                    break;
                case 2:
                    mainActivity.refreshFromDrawerAction("downloading", actionPosition);
                    mainActivity.saveLastState("downloading");
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Downloading");
                    break;
                case 3:
                    mainActivity.refreshFromDrawerAction("completed", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Completed");
                    break;
                case 4:
                    mainActivity.refreshFromDrawerAction("pause", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Pause");
                    break;
                case 5:
                    mainActivity.refreshFromDrawerAction("active", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Active");
                    break;
                case 6:
                    mainActivity.refreshFromDrawerAction("inactive", actionPosition);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Inactive");
                    break;
                case 7:

                    mainActivity.openSettings();
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Settings");
                    break;

                default:
                    mainActivity.saveLastState(MainActivity.currentState);
                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Default");
                    break;
            }

            // Close drawer
            mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);


        }
    }


    DrawerItemRecyclerViewAdapter(MainActivity mainActivity, ArrayList<ObjectDrawerItem> items) {

        this.mainActivity = mainActivity;
        DrawerItemRecyclerViewAdapter.items = items;

    }


    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public DrawerItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_ITEM_ACTIVE) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row_active, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhHeader; //returning the object created


        }
        return null;

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(DrawerItemRecyclerViewAdapter.ViewHolder holder, int position) {
        if (holder.Holderid == 1) {                              // as the list view is going to be called after the header view so we decrement the
            // position by 1 and pass it to the holder while setting the text and image


            if (items == null) {
                Log.d("Debug", "DrawerItemRecyclerViewAdapter - items is null");
            }


            ObjectDrawerItem folder = items.get(position - 1);

            if (holder == null) {
                Log.d("Debug", "DrawerItemRecyclerViewAdapter - holder is null");
            }

            if (holder.imageViewIcon == null) {
                Log.d("Debug", "DrawerItemRecyclerViewAdapter - imageViewIcon is null");
            }

            if (folder == null) {
                Log.d("Debug", "DrawerItemRecyclerViewAdapter - folder is null");
            }

            holder.imageViewIcon.setImageResource(folder.icon);
            holder.textViewName.setText(folder.name);

//            oldActionPosition = actionPosition;
//            actionPosition = position;

            Log.d("Debug", "DrawerItemRecyclerViewAdapter - position: " + position);


        } else {

//            holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
//            holder.Name.setText(name);
//            holder.email.setText(email);
        }
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        // Return the number of items in the list (header + item actions)
        return items.size() + 1;
    }


    // Witht the following method we check what type of view is being passed
    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position)) {
            Log.d("Debug","DrawerItemRecyclerViewAdapter - TYPE_HEADER");
            return TYPE_HEADER;
        }


        if (items.get(position-1).isActive()) {
            Log.d("Debug","DrawerItemRecyclerViewAdapter - TYPE_ITEM_ACTIVE");
            return TYPE_ITEM_ACTIVE;
        }

        Log.d("Debug","DrawerItemRecyclerViewAdapter - TYPE_ITEM");
        return TYPE_ITEM;

    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
