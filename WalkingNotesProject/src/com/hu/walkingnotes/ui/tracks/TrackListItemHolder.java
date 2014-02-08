package com.hu.walkingnotes.ui.tracks;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hu.iJogging.R;

import org.holoeverywhere.widget.CheckBox;

public class TrackListItemHolder {
    public ImageView iconImageView;
    public TextView nameTextView;
    public TextView timeDistanceTextView;
    public TextView dateTextView;
    public TextView timeTextView;
    public TextView recordingTextView;
    public TextView descriptionTextView;
    public CheckBox checkBox;
    
    public TrackListItemHolder(View view){
        iconImageView = (ImageView) view.findViewById(R.id.list_item_icon);
        nameTextView = (TextView) view.findViewById(R.id.list_item_name);
        timeDistanceTextView = (TextView) view.findViewById(R.id.list_item_time_distance);
        dateTextView = (TextView) view.findViewById(R.id.list_item_date);
        timeTextView = (TextView) view.findViewById(R.id.list_item_time);
        recordingTextView = (TextView) view.findViewById(R.id.list_item_recording);
        descriptionTextView = (TextView) view.findViewById(R.id.list_item_description);
        checkBox = (CheckBox) view.findViewById(R.id.check_box);
    }

}
