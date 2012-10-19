package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.apps.mytracks.MyTracksApplication;
import com.google.android.apps.mytracks.TrackListActivity;
import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.content.Waypoint;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.apps.mytracks.util.TrackRecordingServiceConnectionUtils;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.fragments.TrainingDetailFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

public class ViewHistoryActivity extends SherlockFragmentActivity{
  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_MARKER_ID = "marker_id";

  private static final String TAG = ViewHistoryActivity.class.getSimpleName();
  private static final String CURRENT_TAG_KEY = "tab";
 
  private TrackDataHub trackDataHub;
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  public long trackId;
  public long markerId;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handleIntent(getIntent());
    ApiAdapterFactory.getApiAdapter().hideTitle(this);
    setContentView(R.layout.i_jogging_main);
    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, null);
    trackDataHub = ((MyTracksApplication) getApplication()).getTrackDataHub();
    trackDataHub.loadTrack(trackId);
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    TrainingDetailFragment trainingDetailFragment = new TrainingDetailFragment();
    ft.add(R.id.fragment_container, trainingDetailFragment);
    ft.commit();
    setupActionBar();
  }
  
  public void setupActionBar(){
    ActionBar actionBar = getSupportActionBar();
    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
    actionBar.setDisplayShowTitleEnabled(false);
    actionBar.setDisplayShowHomeEnabled(false);
    actionBar.setDisplayUseLogoEnabled(false);
    actionBar.setDisplayShowCustomEnabled(true);
    actionBar.setCustomView(R.layout.actionbar_cunstom_simple);
    View customView = actionBar.getCustomView();
    TextView tv = (TextView)customView.findViewById(R.id.simple_action_bar_title);
    tv.setText(R.string.strTrainingDetail);
    
    View iv = customView.findViewById(R.id.icon_back);
    iv.setOnClickListener(new View.OnClickListener(){
      @Override
      public void onClick(View v) {
        finish();
      }    
    });
  }
  
  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
    handleIntent(intent);
    trackDataHub.loadTrack(trackId);
  }
  

  @Override
  protected void onStart() {
    super.onStart();
    trackDataHub.start();
  }

  @Override
  protected void onResume() {
    super.onResume();
    TrackRecordingServiceConnectionUtils.resume(this, trackRecordingServiceConnection);
//    setTitle(trackId == PreferencesUtils.getLong(this, R.string.recording_track_id_key));
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
//    outState.putString(CURRENT_TAG_KEY, tabHost.getCurrentTabTag());
  }

  @Override
  protected void onStop() {
    super.onStop();
    trackDataHub.stop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    trackRecordingServiceConnection.unbind();
  }

  
  /**
   * Handles the data in the intent.
   */
  private void handleIntent(Intent intent) {
    trackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L);
    markerId = intent.getLongExtra(EXTRA_MARKER_ID, -1L);
    if (markerId != -1L) {
      Waypoint waypoint = MyTracksProviderUtils.Factory.get(this).getWaypoint(markerId);
      if (waypoint == null) {
        exit();
        return;
      }
      trackId = waypoint.getTrackId();
    }
    if (trackId == -1L) {
      exit();
      return;
    }
  }
  
  /**
   * Exists and returns to {@link TrackListActivity}.
   */
  private void exit() {
//    Intent newIntent = IntentUtils.newIntent(this, TrackListActivity.class);
//    startActivity(newIntent);
    finish();
  }
}