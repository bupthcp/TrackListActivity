package com.hu.iJogging.fragments;

import com.google.android.apps.mytracks.content.TracksColumns;
import com.google.android.apps.mytracks.util.IntentUtils;
import com.google.android.apps.mytracks.util.ListItemUtils;
import com.google.android.apps.mytracks.util.StringUtils;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;
import com.hu.iJogging.ViewHistoryActivity;
import com.hu.iJogging.common.IconUtils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TrackListFragment extends Fragment{

  public static final String TRACK_LIST_FTAGMENT_TAG = "TrackListFragment";
  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_MARKER_ID = "marker_id";
  

  private boolean metricUnits = true;
  
  private ListView listView;
  private View mFragmentView;
  private ResourceCursorAdapter resourceCursorAdapter;
  private IJoggingActivity mActivity;

  // True to start a new recording.
  
  
  private static final String[] PROJECTION = new String[] {
    TracksColumns._ID,
    TracksColumns.NAME,
    TracksColumns.DESCRIPTION,
    TracksColumns.CATEGORY,
    TracksColumns.STARTTIME,
    TracksColumns.TOTALDISTANCE,
    TracksColumns.TOTALTIME,
    TracksColumns.ICON};
  

  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = (IJoggingActivity)activity;
    mActivity.findViewById(R.id.training_detail_container).setVisibility(View.GONE);
    mActivity.findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    activity.findViewById(R.id.fragment_container).setVisibility(View.VISIBLE);
    activity.findViewById(R.id.training_detail_container).setVisibility(View.GONE);
  }


  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
  }
  
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.track_list_fragment, container, false);
    listView = (ListView) mFragmentView.findViewById(R.id.track_list);
    listView.setEmptyView(mFragmentView.findViewById(R.id.track_list_empty));  
    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = IntentUtils.newIntent(mActivity, ViewHistoryActivity.class)
            .putExtra(EXTRA_TRACK_ID, id);
        startActivity(intent);
      }
    });
    listView.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener(){
      @Override
      public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        mActivity.getMenuInflater().inflate(R.menu.list_fragment_menu, menu);
      }
    });
//    registerForContextMenu(listView);
    resourceCursorAdapter = new ResourceCursorAdapter(getActivity(), R.layout.list_item, null, 0) {
        @Override
      public void bindView(View view, Context context, Cursor cursor) {
        int idIndex = cursor.getColumnIndex(TracksColumns._ID);
        int nameIndex = cursor.getColumnIndex(TracksColumns.NAME);
        int descriptionIndex = cursor.getColumnIndex(TracksColumns.DESCRIPTION);
        int categoryIndex = cursor.getColumnIndex(TracksColumns.CATEGORY);
        int startTimeIndex = cursor.getColumnIndexOrThrow(TracksColumns.STARTTIME);
        int totalDistanceIndex = cursor.getColumnIndexOrThrow(TracksColumns.TOTALDISTANCE);
        int totalTimeIndex = cursor.getColumnIndexOrThrow(TracksColumns.TOTALTIME);
        int iconIndex = cursor.getColumnIndex(TracksColumns.ICON);
        
        boolean isRecording = cursor.getLong(idIndex) == (((IJoggingActivity)getActivity()).recordingTrackId);
        String name = cursor.getString(nameIndex);
        int iconId = isRecording ? R.drawable.menu_record_track
            : IconUtils.getInstance(mContext).getIconDrawable(cursor.getString(iconIndex));
        String iconContentDescription = getString(isRecording ? R.string.icon_recording
            : R.string.icon_track);
        String category = cursor.getString(categoryIndex);
        String totalTime = isRecording 
            ? null : StringUtils.formatElapsedTime(cursor.getLong(totalTimeIndex));
        String totalDistance = isRecording ? null : StringUtils.formatDistance(
            getActivity(), cursor.getDouble(totalDistanceIndex), metricUnits);
        long startTime = cursor.getLong(startTimeIndex);
        String description = cursor.getString(descriptionIndex);
        ListItemUtils.setListItem(getActivity(),
            view,
            name,
            iconId,
            iconContentDescription,
            category,
            totalTime,
            totalDistance,
            startTime,
            description);
      }
    };
    listView.setAdapter(resourceCursorAdapter);
    getActivity().getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
      @Override
      public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
        return new CursorLoader(getActivity(),
            TracksColumns.CONTENT_URI,
            PROJECTION,
            null,
            null,
            TracksColumns._ID + " DESC");
      }

      @Override
      public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        resourceCursorAdapter.swapCursor(cursor);
      }

      @Override
      public void onLoaderReset(Loader<Cursor> loader) {
        resourceCursorAdapter.swapCursor(null);
      }
    });
    return mFragmentView;
  }

  
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    if (handleContextItem(item.getItemId(), ((AdapterContextMenuInfo) item.getMenuInfo()).id)) {
      return true;
    }
    return super.onContextItemSelected(item);
  }
  
  /**
   * Handles a context item selection.
   * 
   * @param itemId the menu item id
   * @param trackId the track id
   * @return true if handled.
   */
  private boolean handleContextItem(int itemId, long trackId) {
    switch (itemId) {
      case R.id.list_context_menu_delete:
        DeleteOneTrackDialogFragment.newInstance(trackId).show(
            getActivity().getSupportFragmentManager(), DeleteOneTrackDialogFragment.DELETE_ONE_TRACK_DIALOG_TAG);
        return true;
      default:
        return false;
    }
  }

  //在这里实现onDestroyView是为了保证在fragment切换的
  //时候，fragment的container是干净的，
  //如果不加上这个清理过程，有可能会出现两个fragment重叠
  //在一起显示的情况
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ViewGroup parentViewGroup = (ViewGroup) mFragmentView.getParent();
    if (parentViewGroup != null) {
      parentViewGroup.removeView(mFragmentView);
    }
  }
  

}
