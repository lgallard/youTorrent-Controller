package com.lgallardo.youtorrentcontroller;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Set;

class TorrentListAdapter extends ArrayAdapter<String> {

    private static HashMap<Integer, Boolean> mSelection = new HashMap<Integer, Boolean>();
    private String[] torrentsNames;
    private Torrent[] torrentsData;
    private Context context;

    public TorrentListAdapter(Context context, String[] torrentsNames, Torrent[] torrentsData) {
        super(context, R.layout.row, R.id.file, torrentsNames);

        this.context = context;
        this.torrentsNames = torrentsNames;
        this.torrentsData = torrentsData;

    }

    @Override
    public int getCount() {
        return (torrentsNames != null) ? torrentsNames.length : 1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


//        View row = super.getView(position, convertView, parent);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View row = inflater.inflate(R.layout.row, parent, false);

        if (torrentsData != null) {

            String file = torrentsData[position].getFile();

            String state = torrentsData[position].getState();

            int donwloadSpeedWeigth = torrentsData[position].getDownloadSpeedWeight();
            int uploadSpeedWeigth = torrentsData[position].getUploadSpeedWeight();

            TextView name = (TextView) row.findViewById(R.id.file);
            name.setText(file);

            TextView info = (TextView) row.findViewById(R.id.info);

            info.setText("" + torrentsData[position].getInfo());

            ImageView icon = (ImageView) row.findViewById(R.id.icon);

            if ("paused".equals(state)) {
                icon.setImageResource(R.drawable.paused);
            }


            if ("downloading".equals(state)) {
                icon.setImageResource(R.drawable.stalleddl);

                if (donwloadSpeedWeigth > 0) {
                    icon.setImageResource(R.drawable.downloading);
                }
            }

            if ("seeding".equals(state)) {

                icon.setImageResource(R.drawable.stalledup);

                if (uploadSpeedWeigth > 0) {
                    icon.setImageResource(R.drawable.uploading);
                }
            }

            if ("queued".equals(state)) {
                icon.setImageResource(R.drawable.queued);
            }

            if ("checking".equals(state)) {
                icon.setImageResource(R.drawable.ic_action_recheck);
            }

            if(MainActivity.packageName.equals("com.lgallardo.youtorrentcontrollerpro")) {
                // Set progress bar
                ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) row.findViewById(R.id.percentage);

                String percentage = torrentsData[position].getPercentage();

                Log.d("Debug", "TorrentListAdapter - percentage: " + percentage);

                progressBar.setProgress(Integer.parseInt(percentage));

                percentageTV.setText(percentage + "%");
            }

            row.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_light)); //default color

            if (mSelection.get(position) != null) {
                row.setBackgroundColor(getContext().getResources().getColor(android.R.color.holo_blue_light));// this is a selected position so make it blue
            }
        } else {

//            Log.d("Debug", "No results");

            TextView name = (TextView) row.findViewById(R.id.file);
            name.setText(context.getString(R.string.no_results));

            // Hide progress bar
            if(MainActivity.packageName.equals("com.lgallardo.youtorrentcontrollerpro")) {

                ProgressBar progressBar = (ProgressBar) row.findViewById(R.id.progressBar1);
                TextView percentageTV = (TextView) row.findViewById(R.id.percentage);

                progressBar.setVisibility(View.GONE);
                percentageTV.setVisibility(View.GONE);

            }


            ImageView icon = (ImageView) row.findViewById(R.id.icon);
            TextView info = (TextView) row.findViewById(R.id.info);

            icon.setVisibility(View.GONE);
            info.setVisibility(View.GONE);

            row.setBackgroundColor(getContext().getResources().getColor(android.R.color.background_light)); //default color

        }

        return (row);
    }


    public void setNewSelection(int position, boolean value) {

        Log.i("torrentListAdapter", "setNewSelection invoked " + position + " " + value);

        mSelection.put(position, value);
        notifyDataSetChanged();
    }

    public boolean isPositionChecked(int position) {
        Boolean result = mSelection.get(position);
        return result == null ? false : result;
    }

    public Set<Integer> getCurrentCheckedPosition() {
        return mSelection.keySet();
    }

    public void removeSelection(int position) {
        mSelection.remove(position);
        notifyDataSetChanged();
    }

    public void clearSelection() {
        mSelection = new HashMap<Integer, Boolean>();
        notifyDataSetChanged();
    }

    public void setNames(String[] names) {
        this.torrentsNames = null;
        this.torrentsNames = names;
    }

    public Torrent[] getData() {
        return this.torrentsData;
    }

    public void setData(Torrent[] data) {
        this.torrentsData = data;
    }

    @Override
    public boolean isEnabled(int position) {
        if (areAllItemsEnabled()) {
            return true;
        }else{
            return false;
        }
    }

    @Override
    public boolean areAllItemsEnabled() {
        if (torrentsData != null && !com.lgallardo.youtorrentcontroller.ItemstFragment.mSwipeRefreshLayout.isRefreshing()) {
            return true;
        } else {
            return false;
        }
    }
}