package com.hu.iJogging;

import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.hu.iJogging.content.MyTracksProviderUtils;
import com.hu.iJogging.content.Waypoint;
import com.hu.iJogging.fragments.MapFragment;
import com.hu.iJogging.fragments.TrainingDetailContainerFragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

public class ViewHistoryActivity extends TrackActivity{
  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_MARKER_ID = "marker_id";

  private static final String TAG = ViewHistoryActivity.class.getSimpleName();
  private static final String CURRENT_TAG_KEY = "tab";

//  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private TrainingDetailContainerFragment trainingDetailContainerFragment;
  private MapFragment mapFragment;
  
  public long trackId;
  public long markerId;
  
  ViewPager mViewPager;
  ContainerPagerAdapter mContainerPagerAdapter;
  
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    handleIntent(getIntent());
    ApiAdapterFactory.getApiAdapter().hideTitle(this);
    setContentView(R.layout.i_jogging_main);
//    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, null);
    trackDataHub = TrackDataHub.newInstance(this);

    setupActionBar();
    switchToTrainingDetailContainerFragment();
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
    
  }

  @Override
  protected void onResume() {
    super.onResume();
    
    trackDataHub.start();
    trackDataHub.loadTrack(trackId);
//    TrackRecordingServiceConnectionUtils.resume(this, trackRecordingServiceConnection);
//    setTitle(trackId == PreferencesUtils.getLong(this, R.string.recording_track_id_key));
  }
  
  protected void onPause(){
    super.onPause();
    trackDataHub.stop();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
//    outState.putString(CURRENT_TAG_KEY, tabHost.getCurrentTabTag());
  }

  @Override
  protected void onStop() {
    super.onStop();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
//    trackRecordingServiceConnection.unbind();
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
  
  
  public void switchToTrainingDetailContainerFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction ft = fragmentManager.beginTransaction();
    trainingDetailContainerFragment = new TrainingDetailContainerFragment();
    ft.replace(R.id.fragment_container, trainingDetailContainerFragment);
    ft.commit();
  }
  
  public void switchToMapFragment(){
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction ft = fragmentManager.beginTransaction();
    mapFragment = new MapFragment();
    ft.replace(R.id.fragment_container, mapFragment);
    ft.commit();
  }
  
  /**
   * Exists and returns to {@link TrackListActivity}.
   */
  private void exit() {
    finish();
  }
}