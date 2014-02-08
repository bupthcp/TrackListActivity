package com.hu.walkingnotes.ui.tracks;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hu.iJogging.R;

import org.holoeverywhere.widget.CheckBox;

import java.util.Calendar;

public class TrackListController {
    
    public static void configTracklistItem(Context context, View view, boolean isRecording, boolean isPaused,boolean isActionModePrepared,
            boolean isChecked,int iconId, int iconContentDescriptionId, String name, String category, String totalTime,
            String totalDistance, long startTime, String description, String sharedOwner){
        TrackListItemHolder holder = getHolder(view);
        if (isRecording) {
            iconId = isPaused ? R.drawable.track_paused : R.drawable.track_recording;
            iconContentDescriptionId = isPaused ? R.string.icon_pause_recording
                : R.string.icon_record_track;
          }

          holder.iconImageView.setImageResource(iconId);
          holder.iconImageView.setContentDescription(context.getString(iconContentDescriptionId));

          holder.nameTextView.setText(name);

          setTextView(holder.timeDistanceTextView,
              getTimeDistance(isRecording, sharedOwner, totalTime, totalDistance), 0);

          String[] startTimeDisplay = getStartTime(isRecording, context, startTime);
          setTextView(holder.dateTextView, startTimeDisplay[0], 0);

          setTextView(holder.timeTextView, startTimeDisplay[1], 0);

          String value = isRecording ? context.getString(
              isPaused ? R.string.generic_paused : R.string.generic_recording)
              : null;
          int color = isRecording ? context.getResources()
              .getColor(isPaused ? android.R.color.white : R.color.red)
              : 0;
          setTextView(holder.recordingTextView, value, color);

          setTextView(holder.descriptionTextView, getDescription(isRecording, category, description), 0);
          
          if(isActionModePrepared){
              holder.checkBox.setVisibility(View.VISIBLE);
              holder.checkBox.setChecked(isChecked);
          }else{
              holder.checkBox.setVisibility(View.GONE);
          }
    }

    private static TrackListItemHolder getHolder(View view){
        TrackListItemHolder holder = (TrackListItemHolder)view.getTag();
        if(holder == null){
            holder = new TrackListItemHolder(view);
            view.setTag(holder);
        }
        return holder;
    }
    
    /**
     * Gets the time/distance text.
     * 
     * @param isRecording true if recording
     * @param sharedOwner the shared owner
     * @param totalTime the total time
     * @param totalDistance the total distance
     */
    private static String getTimeDistance(
        boolean isRecording, String sharedOwner, String totalTime, String totalDistance) {
      if (isRecording) {
        return null;
      }
      StringBuffer buffer = new StringBuffer();
      if (sharedOwner != null && sharedOwner.length() != 0) {
        buffer.append(sharedOwner);      
      }
      if (totalTime != null && totalTime.length() != 0) {
        if (buffer.length() != 0) {
          buffer.append(" \u2027 ");
        }
        buffer.append(totalTime);
      }    
      if (totalDistance != null && totalDistance.length() != 0) {
        if (buffer.length() != 0) {
          buffer.append(" ");
        }
        buffer.append(" / " + totalDistance);
      }
      return buffer.toString();
    }

    /**
     * Gets the description text.
     * 
     * @param isRecording true if recording
     * @param category the category
     * @param description the description
     */
    private static String getDescription(boolean isRecording, String category, String description) {
      if (isRecording) {
        return null;
      }
      if (category == null || category.length() == 0) {
        return description;
      }

      StringBuffer buffer = new StringBuffer();

      buffer.append("[" + category + "]");
      if (description != null && description.length() != 0) {
        buffer.append(" " + description);
      }
      return buffer.toString();
    }

    /**
     * Gets the start time text.
     * 
     * @param isRecording true if recording
     * @param context the context
     * @param startTime the start time
     * @return array of two strings.
     */
    private static String[] getStartTime(boolean isRecording, Context context, long startTime) {
      if (isRecording || startTime == 0L) {
        return new String[] { null, null };
      }
      if (DateUtils.isToday(startTime)) {
        return new String[] { DateUtils.getRelativeTimeSpanString(
            startTime, System.currentTimeMillis(), DateUtils.MINUTE_IN_MILLIS,
            DateUtils.FORMAT_ABBREV_RELATIVE).toString(), null };
      }
      int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_ABBREV_ALL;
      if (!isThisYear(startTime)) {
        flags |= DateUtils.FORMAT_NUMERIC_DATE;
      }
      return new String[] { DateUtils.formatDateTime(context, startTime, flags),
          DateUtils.formatDateTime(
              context, startTime, DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_ABBREV_TIME) };
    }

    /**
     * True if the time is this year.
     * 
     * @param time the time
     */
    private static boolean isThisYear(long time) {
      Calendar now = Calendar.getInstance();
      Calendar calendar = Calendar.getInstance();
      now.setTimeInMillis(System.currentTimeMillis());
      calendar.setTimeInMillis(time);
      return now.get(Calendar.YEAR) == calendar.get(Calendar.YEAR);
    }

    /**
     * Sets a text view.
     * 
     * @param textView the text view
     * @param value the value for the text view
     */
    private static void setTextView(TextView textView, String value, int color) {
      if (value == null || value.length() == 0) {
        textView.setVisibility(View.GONE);
      } else {
        textView.setVisibility(View.VISIBLE);
        textView.setText(value);
        if (color != 0) {
          textView.setTextColor(color);
        }
      }
    }
}
