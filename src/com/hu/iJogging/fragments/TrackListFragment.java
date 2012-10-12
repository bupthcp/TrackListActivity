package com.hu.iJogging.fragments;

import com.google.android.apps.mytracks.content.TracksColumns;
import com.google.android.apps.mytracks.fragments.DeleteOneTrackDialogFragment.DeleteOneTrackCaller;
import com.google.android.apps.mytracks.services.ITrackRecordingService;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.apps.mytracks.util.ListItemUtils;
import com.google.android.apps.mytracks.util.StringUtils;
import com.google.android.apps.mytracks.util.TrackIconUtils;
import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.ResourceCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class TrackListFragment extends Fragment implements DeleteOneTrackCaller{

  private static final String TAG = TrackListFragment.class.getSimpleName();
  
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private boolean metricUnits;
  private long recordingTrackId;
  private ListView listView;
  private View mFragmentView;
  private ResourceCursorAdapter resourceCursorAdapter;

  // True to start a new recording.
  private boolean startNewRecording = false;

  private MenuItem recordTrackMenuItem;
  private MenuItem stopRecordingMenuItem;
  private MenuItem searchMenuItem;
  private MenuItem importMenuItem;
  private MenuItem saveAllMenuItem;
  private MenuItem deleteAllMenuItem;
  
  private static final String[] PROJECTION = new String[] {
    TracksColumns._ID,
    TracksColumns.NAME,
    TracksColumns.DESCRIPTION,
    TracksColumns.CATEGORY,
    TracksColumns.STARTTIME,
    TracksColumns.TOTALDISTANCE,
    TracksColumns.TOTALTIME,
    TracksColumns.ICON};
  
//Callback when the trackRecordingServiceConnection binding changes.
 private final Runnable bindChangedCallback = new Runnable() {
   @Override
   public void run() {
     if (!startNewRecording) {
       return;
     }

     ITrackRecordingService service = trackRecordingServiceConnection.getServiceIfBound();
     if (service == null) {
       Log.d(TAG, "service not available to start a new recording");
       return;
     }
     try {
       recordingTrackId = service.startNewTrack();
       startNewRecording = false;
//       Intent intent = IntentUtils.newIntent(TrackListActivity.this, TrackDetailActivity.class)
//           .putExtra(TrackDetailActivity.EXTRA_TRACK_ID, recordingTrackId);
//       startActivity(intent);
       Toast.makeText(
           getActivity(), R.string.track_list_record_success, Toast.LENGTH_SHORT).show();
     } catch (Exception e) {
       Toast.makeText(getActivity(), R.string.track_list_record_error, Toast.LENGTH_LONG)
           .show();
       Log.e(TAG, "Unable to start a new recording.", e);
     }
   }
 };
  
  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    trackRecordingServiceConnection = new TrackRecordingServiceConnection(
        getActivity(), bindChangedCallback);
  }
  
  
  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mFragmentView = getActivity().getLayoutInflater().inflate(R.layout.track_list_fragment, container, false);
    listView = (ListView) mFragmentView.findViewById(R.id.track_list);
    listView.setEmptyView(mFragmentView.findViewById(R.id.track_list_empty));  
    listView.setOnItemClickListener(new OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

      }
    });
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
        
        boolean isRecording = cursor.getLong(idIndex) == recordingTrackId;
        String name = cursor.getString(nameIndex);
        int iconId = isRecording ? R.drawable.menu_record_track
            : TrackIconUtils.getIconDrawable(cursor.getString(iconIndex));
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
  
  @Override
  public TrackRecordingServiceConnection getTrackRecordingServiceConnection() {
    // TODO Auto-generated method stub
    return null;
  }

}
