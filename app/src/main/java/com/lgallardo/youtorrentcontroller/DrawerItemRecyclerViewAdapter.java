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
import java.util.Iterator;

public class DrawerItemRecyclerViewAdapter extends RecyclerView.Adapter<DrawerItemRecyclerViewAdapter.ViewHolder> {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    private static final int TYPE_ITEM_ACTIVE = 2;
    private static final int TYPE_SERVER = 3;
    private static final int TYPE_SERVER_ACTIVE = 4;
    private static final int TYPE_CATEGORY = 5;


    // All items
    public static ArrayList<ObjectDrawerItem> items;

    // SUb items
    public static ArrayList<ObjectDrawerItem> serverItems;
    public static ArrayList<ObjectDrawerItem> actionItems;
    public static ArrayList<ObjectDrawerItem> settingsItems;
    public static ArrayList<ObjectDrawerItem> labelItems;


    public static int oldActionPosition = 1;
    public static int actionPosition = 0;

    private static MainActivity mainActivity;
    private static int drawerOffset = 1;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        int Holderid;
        int positionInItems = -1;

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

            Log.d("Debug", "DrawerItemRecyclerViewAdapter - >> ViewHolder");

            Holderid = 0;
            if (ViewType != TYPE_HEADER) {

                itemView.setClickable(true);
                itemView.setOnClickListener(this);

                Holderid = 1;
            }


//            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - ViewType: " + ViewType);
//            if (ViewType == TYPE_SERVER || ViewType == TYPE_SERVER_ACTIVE) {
//                drawerOffset = drawerOffset + 1;
//            }

            // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created
            imageViewIcon = (ImageView) itemView.findViewById(R.id.imageViewIcon);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);

        }


        // In order to track the item position in RecyclerView
        // Handle item click and set the selection
        @Override
        public void onClick(View view) {


            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick - serverItems size: " + DrawerItemRecyclerViewAdapter.serverItems.size());
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick - actionItems size: " + DrawerItemRecyclerViewAdapter.actionItems.size());
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick - settingsItems size: " + DrawerItemRecyclerViewAdapter.settingsItems.size());
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick - labelItems size: " + DrawerItemRecyclerViewAdapter.labelItems.size());
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick - items size: " + DrawerItemRecyclerViewAdapter.items.size());


            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - offSetPosition: " + (drawerOffset));


            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - getLayoutPosition: " + getLayoutPosition());

            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - positionInItems: " + positionInItems);


            ObjectDrawerItem drawerItem;


            // Check and toggle server category
            if (getLayoutPosition() == 1) {


                drawerItem = items.get(0);


                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - serverItems.size:  " + serverItems.size());

                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - items.size:  " + items.size());

                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - drawerItem.name:  " + drawerItem.name);

                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - drawerItem.getType():  " + drawerItem.getType());

                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - toggled Servers ");

                if (drawerItem.isActive()) {

                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Servers Category active");

                    // Remove all server items
                    Iterator iterator = items.iterator();

                    while (iterator.hasNext()) {

                        ObjectDrawerItem item = (ObjectDrawerItem) iterator.next();

                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Analysing: " + item.name);

                        if (item.getType() == TYPE_SERVER || item.getType() == TYPE_SERVER_ACTIVE) {

                            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Removing: " + item.name);
                            iterator.remove();
                        }
                    }

                    drawerItem.setActive(false);

                    drawerOffset = 1;
                } else {

                    Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Servers Category inactive");

                    // Insert all server items
                    for (int i = 0; i < serverItems.size(); i++) {

                        ObjectDrawerItem item = (ObjectDrawerItem) serverItems.get(i);

                        if (item.getType() == TYPE_SERVER || item.getType() == TYPE_SERVER_ACTIVE) {
                            items.add(i, serverItems.get(i));
                        }
                    }


                    drawerItem.setActive(true);

                    drawerOffset = serverItems.size() + 1;


                }


                items.set(0, drawerItem);

            }else {


                // Get action position
                actionPosition = getLayoutPosition() - drawerOffset;


                int layoutPosition = getLayoutPosition();

                drawerItem = items.get(getLayoutPosition() - 1);

                // Get Action
                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Action => " + drawerItem.getAction());


                // Disable all items
                for (int i = 0; i < items.size(); i++) {
                    ObjectDrawerItem item = items.get(i);

                if (item.getType() == TYPE_ITEM || item.getType() == TYPE_ITEM_ACTIVE) {
                    item.setActive(false);
                }

                    items.set(i, item);
                }

                // Mark new item as active
                drawerItem.setActive(true);
                items.set(layoutPosition - 1, drawerItem);


                // Perform Action

                // //    public static final String[] actionStates = new String[]{"all", "downloading", "completed", "pause", "active", "inactive"};
                // Refresh All
                if (drawerItem.getAction().equals("refreshAll")) {

                    drawerItem.setActive(true);
                    items.set(layoutPosition - 1, drawerItem);
                    mainActivity.refreshFromDrawerAction("all", drawerItem.name);

                    // Close drawer
                    mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);
                }

                // Refresh Downloading
                if (drawerItem.getAction().equals("refreshDownloading")) {

                    drawerItem.setActive(true);
                    items.set(layoutPosition - 1, drawerItem);
                    mainActivity.refreshFromDrawerAction("downloading", drawerItem.name);

                    // Close drawer
                    mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);
                }

                // Refresh Completed
                if (drawerItem.getAction().equals("refreshCompleted")) {

                    drawerItem.setActive(true);
                    items.set(layoutPosition - 1, drawerItem);
                    mainActivity.refreshFromDrawerAction("completed", drawerItem.name);

                    // Close drawer
                    mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);
                }

                // Refresh Pause
                if (drawerItem.getAction().equals("refreshPaused")) {

                    drawerItem.setActive(true);
                    items.set(layoutPosition - 1, drawerItem);
                    mainActivity.refreshFromDrawerAction("pause", drawerItem.name);

                    // Close drawer
                    mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);
                }


                // Refresh Active
                if (drawerItem.getAction().equals("refreshActive")) {

                    drawerItem.setActive(true);
                    items.set(layoutPosition - 1, drawerItem);
                    mainActivity.refreshFromDrawerAction("active", drawerItem.name);

                    // Close drawer
                    mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);
                }


                // Refresh Active
                if (drawerItem.getAction().equals("refreshInactive")) {

                    drawerItem.setActive(true);
                    items.set(layoutPosition - 1, drawerItem);
                    mainActivity.refreshFromDrawerAction("inactive", drawerItem.name);

                    // Close drawer
                    mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);
                }

                // Load banner
                mainActivity.loadBanner();


            }

            // Update drawer
            notifyDataSetChanged();




//            // If the header is not selected and an action is selected
//            if (actionPosition > 0 && actionPosition < 9) {
//
////                // Mark old item as inactive
////                drawerItem = DrawerItemRecyclerViewAdapter.items.get(oldActionPosition);
////                drawerItem.setActive(false);
////                DrawerItemRecyclerViewAdapter.items.set(oldActionPosition, drawerItem);
//
////                oldActionPosition = actionPosition;
//
//                // Disable all items
//                for (int i = 0; i < items.size(); i++) {
//                    drawerItem = items.get(i);
//
//                    if (drawerItem.getType() == TYPE_ITEM || drawerItem.getType() == TYPE_ITEM_ACTIVE) {
//                        drawerItem.setActive(false);
//                    }
//
//                    items.set(i, drawerItem);
//                }
//
//                // Mark new item as active
//                drawerItem = items.get(actionPosition + drawerOffset - 1);
//                drawerItem.setActive(true);
//                items.set(actionPosition + drawerOffset - 1, drawerItem);
//
////                notifyItemChanged(actionPosition + 1);
//
//                notifyDataSetChanged();
//
//
//                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - getLayoutPosition: " + getLayoutPosition());
//
//                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - actionPosition: " + (actionPosition));
//
//
//                switch (actionPosition) {
//                    case 1:
//                        mainActivity.refreshFromDrawerAction("all", actionPosition);
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: All");
//                        break;
//                    case 2:
//                        mainActivity.refreshFromDrawerAction("downloading", actionPosition);
//                        mainActivity.saveLastState("downloading");
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Downloading");
//                        break;
//                    case 3:
//                        mainActivity.refreshFromDrawerAction("completed", actionPosition);
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Completed");
//                        break;
//                    case 4:
//                        mainActivity.refreshFromDrawerAction("pause", actionPosition);
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Pause");
//                        break;
//                    case 5:
//                        mainActivity.refreshFromDrawerAction("active", actionPosition);
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Active");
//                        break;
//                    case 6:
//                        mainActivity.refreshFromDrawerAction("inactive", actionPosition);
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Inactive");
//                        break;
//                    case 7:
//                        mainActivity.openSettings();
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Settings");
//                        break;
//                    case 8:
//                        if (MainActivity.packageName.equals("com.lgallardo.youtorrentcontroller")) {
//                            // Get Pro version
//                            mainActivity.getPRO();
//                        } else {
//                            mainActivity.openHelp();
//                        }
//                        break;
//                    case 9:
//                        mainActivity.openHelp();
//                        break;
//                    default:
//                        mainActivity.saveLastState(MainActivity.currentState);
//                        Log.d("Debug", "DrawerItemRecyclerViewAdapter - action: Default");
//                        break;
//                }
//                // Close drawer
//                mainActivity.drawerLayout.closeDrawer(mainActivity.mRecyclerView);
//            }
//
//            // Load banner
//            mainActivity.loadBanner();


        }

    }


    DrawerItemRecyclerViewAdapter(MainActivity mainActivity, ArrayList<ObjectDrawerItem> serverItems, ArrayList<ObjectDrawerItem> actionItems, ArrayList<ObjectDrawerItem> settingsItems, ArrayList<ObjectDrawerItem> labelItems) {

        this.mainActivity = mainActivity;


        // All items

        DrawerItemRecyclerViewAdapter.items = items;


        DrawerItemRecyclerViewAdapter.serverItems = serverItems;
        DrawerItemRecyclerViewAdapter.actionItems = actionItems;
        DrawerItemRecyclerViewAdapter.settingsItems = settingsItems;
        DrawerItemRecyclerViewAdapter.labelItems = labelItems;

        DrawerItemRecyclerViewAdapter.items = new ArrayList<ObjectDrawerItem>();

        // Add items
        DrawerItemRecyclerViewAdapter.items.addAll(serverItems);
        DrawerItemRecyclerViewAdapter.items.addAll(actionItems);
        DrawerItemRecyclerViewAdapter.items.addAll(settingsItems);

        if (labelItems != null) {
            DrawerItemRecyclerViewAdapter.items.addAll(labelItems);
        } else {
            DrawerItemRecyclerViewAdapter.labelItems = new ArrayList<ObjectDrawerItem>();
        }

        Log.d("Debug", "DrawerItemRecyclerViewAdapter - Constructor - serverItems size: " + DrawerItemRecyclerViewAdapter.serverItems.size());
        Log.d("Debug", "DrawerItemRecyclerViewAdapter - Constructor - actionItems size: " + DrawerItemRecyclerViewAdapter.actionItems.size());
        Log.d("Debug", "DrawerItemRecyclerViewAdapter - Constructor - settingsItems size: " + DrawerItemRecyclerViewAdapter.settingsItems.size());
        Log.d("Debug", "DrawerItemRecyclerViewAdapter - Constructor - labelItems size: " + DrawerItemRecyclerViewAdapter.labelItems.size());
        Log.d("Debug", "DrawerItemRecyclerViewAdapter - Constructor - items size: " + DrawerItemRecyclerViewAdapter.items.size());

        drawerOffset = 1;

        ObjectDrawerItem drawerItem;

//        // Add server items to array
//        for (int i = 0; i < items.size(); i++) {
//            ObjectDrawerItem item = items.get(i);
//
//            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Adding to items: " + items.get(i).name);
//
//            if (item.getType() == TYPE_SERVER || item.getType() == TYPE_SERVER_ACTIVE) {
//                serverItems.add(item);
//            }
//        }


        // Remove all server items
        Iterator iterator = items.iterator();

        while (iterator.hasNext()) {

            ObjectDrawerItem item = (ObjectDrawerItem) iterator.next();

            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Analysing: " + item.name);
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Action is: " + item.getAction());


            if (item.getType() == TYPE_SERVER || item.getType() == TYPE_SERVER_ACTIVE) {

                Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - Removing: " + item.name);
                iterator.remove();
            }


        }


    }

    public void refreshDrawer(ArrayList<ObjectDrawerItem> serverItems, ArrayList<ObjectDrawerItem> actionItems, ArrayList<ObjectDrawerItem> settingsItems, ArrayList<ObjectDrawerItem> labelItems) {

        DrawerItemRecyclerViewAdapter.serverItems = serverItems;
        DrawerItemRecyclerViewAdapter.actionItems = actionItems;
        DrawerItemRecyclerViewAdapter.settingsItems = settingsItems;


        DrawerItemRecyclerViewAdapter.items = new ArrayList<ObjectDrawerItem>();

        // Add items
        DrawerItemRecyclerViewAdapter.items.addAll(serverItems);
        DrawerItemRecyclerViewAdapter.items.addAll(actionItems);
        DrawerItemRecyclerViewAdapter.items.addAll(settingsItems);

        if (labelItems != null) {
            DrawerItemRecyclerViewAdapter.items.addAll(labelItems);
        }


        // Refresh
        notifyDataSetChanged();

    }


    //Below first we override the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public DrawerItemRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

//        Log.d("Debug", "DrawerItemRecyclerViewAdapter - OnClick() - ViewType: " + viewType);
//        if (viewType == TYPE_SERVER || viewType == TYPE_SERVER_ACTIVE) {
//            drawerOffset = drawerOffset + 1;
//        }

        // Here w

        if (viewType == TYPE_CATEGORY) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_ITEM) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_ITEM_ACTIVE) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row_active, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_SERVER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

//            drawerOffset = drawerOffset + 1;

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_SERVER_ACTIVE) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_row_active, parent, false); //Inflating the layout

            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

//            drawerOffset = drawerOffset + 1;

            return vhItem; // Returning the created object

            //inflate your layout and pass it to view holder

        } else if (viewType == TYPE_HEADER) {


            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header, parent, false); //Inflating the layout

            ViewHolder vhHeader = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

//            drawerOffset = drawerOffset + 1;

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


            ObjectDrawerItem item = items.get(position - 1);


//            if (item.getType() != TYPE_CATEGORY) {
                holder.imageViewIcon.setImageResource(item.icon);
//            }


            holder.textViewName.setText(item.name);

            holder.positionInItems = (position - 1);

//            oldActionPosition = actionPosition;
//            actionPosition = position;

//            Log.d("Debug", "DrawerItemRecyclerViewAdapter - position: " + position);


        } else {

            // header

            return;
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

//        Log.d("Debug", "DrawerItemRecyclerViewAdapter - items.size(): " + items.size());
//        Log.d("Debug", "DrawerItemRecyclerViewAdapter - position: " + position);

        if (isPositionHeader(position)) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_HEADER");
            return TYPE_HEADER;
        }

        if (items.get(position - 1).getType() == TYPE_ITEM && items.get(position - 1).isActive()) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_ITEM_ACTIVE");
            return TYPE_ITEM_ACTIVE;
        }

        if (items.get(position - 1).getType() == TYPE_CATEGORY) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_CATEGORY");
            return TYPE_CATEGORY;
        }

        if (items.get(position - 1).getType() == TYPE_SERVER && !(items.get(position - 1).isActive())) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_SERVER");
            return TYPE_ITEM;
        }

        if (items.get(position - 1).getType() == TYPE_SERVER && items.get(position - 1).isActive()) {
            Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_SERVER_ACTIVE");
            return TYPE_SERVER_ACTIVE;
        }

        // Default
        Log.d("Debug", "DrawerItemRecyclerViewAdapter - TYPE_ITEM");
        return TYPE_ITEM;

    }

    private boolean isPositionHeader(int position) {
        return position == 0;
    }

}
