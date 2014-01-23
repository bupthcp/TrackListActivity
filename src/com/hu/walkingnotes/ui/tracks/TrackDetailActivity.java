/*
 * Copyright 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hu.walkingnotes.ui.tracks;

import com.baidu.mapapi.BMapManager;
import com.google.android.apps.mytracks.Constants;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.services.ITrackRecordingService;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.TrackRecordingServiceConnectionUtils;
import com.hu.iJogging.IJoggingApplication;
import com.hu.iJogging.R;
import com.hu.iJogging.content.MyTracksProviderUtils;
import com.hu.iJogging.content.Waypoint;
import com.hu.iJogging.content.WaypointCreationRequest;
import com.hu.walkingnotes.baidumaps.MapFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

/**
 * An activity to show the track detail.
 * 
 * @author Leif Hendrik Wilden
 * @author Rodrigo Damazio
 */
public class TrackDetailActivity extends ActionBarActivity {
  
  private String TAG = TrackDetailActivity.class.getSimpleName();

  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_MARKER_ID = "marker_id";
  
  public BMapManager mBMapMan = null;

  private static final String CURRENT_TAB_TAG_KEY = "current_tab_tag_key";

  // The following are set in onCreate
  private MyTracksProviderUtils myTracksProviderUtils;
  private SharedPreferences sharedPreferences;
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private TrackDataHub trackDataHub;
  private TabHost tabHost;
  private TrackController trackController;

  // From intent
  private long trackId;
  private long markerId;

  // Preferences
  private long recordingTrackId = PreferencesUtils.RECORDING_TRACK_ID_DEFAULT;
  private boolean recordingTrackPaused = PreferencesUtils.RECORDING_TRACK_PAUSED_DEFAULT;
  
  private boolean needLocationListener = false;
  
  private boolean startNewRecording = false; // true to start a new recording


  private final Runnable bindChangedCallback = new Runnable() {
      @Override
    public void run() {
      
      
      ITrackRecordingService service = trackRecordingServiceConnection.getServiceIfBound();
      if (service == null) {
        Log.d(TAG, "service not available to start gps or a new recording");
        return;
      }
      
      if (startNewRecording) {
        try {
          trackId = service.startNewTrack();
          trackDataHub.loadTrack(trackId);
          startNewRecording = false;
        } catch (RemoteException e) {
          Toast.makeText(
              TrackDetailActivity.this, R.string.track_list_record_error, Toast.LENGTH_LONG).show();
          Log.e(TAG, "Unable to start a new recording.", e);
        }
      }
      
      // After binding changes (is available), update the total time in
      // trackController.
      runOnUiThread(new Runnable() {
          @Override
        public void run() {
            trackController.update(checkIsRecording(), recordingTrackPaused);
        }
      });
    }
  };

  /*
   * Note that sharedPreferenceChangeListener cannot be an anonymous inner
   * class. Anonymous inner class will get garbage collected.
   */
  private final OnSharedPreferenceChangeListener
      sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
          @Override
        public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {
          // Note that key can be null
          if (key == null || key.equals(
              PreferencesUtils.getKey(TrackDetailActivity.this, R.string.recording_track_id_key))) {
            recordingTrackId = PreferencesUtils.getLong(
                TrackDetailActivity.this, R.string.recording_track_id_key);
          }
          if (key == null || key.equals(PreferencesUtils.getKey(
              TrackDetailActivity.this, R.string.recording_track_paused_key))) {
            recordingTrackPaused = PreferencesUtils.getBoolean(TrackDetailActivity.this,
                R.string.recording_track_paused_key,
                PreferencesUtils.RECORDING_TRACK_PAUSED_DEFAULT);
          }
          if (key == null || key.equals(
              PreferencesUtils.getKey(TrackDetailActivity.this, R.string.sensor_type_key))) {
          }
          if (key != null) {
            runOnUiThread(new Runnable() {
                @Override
              public void run() {
                ApiAdapterFactory.getApiAdapter().invalidMenu(TrackDetailActivity.this);
                boolean isRecording = checkIsRecording();
                trackController.update(isRecording, recordingTrackPaused);
              }
            });
          }
        }
      };

  private final OnClickListener recordListener = new OnClickListener() {
      @Override
    public void onClick(View v) {
      if (recordingTrackId == PreferencesUtils.RECORDING_TRACK_ID_DEFAULT) {
        startRecording();
      } else {
        if (recordingTrackPaused) {
          // Paused -> Resume
          TrackRecordingServiceConnectionUtils.resumeTrack(trackRecordingServiceConnection);
          trackController.update(true, false);
        } else {
          // Recording -> Paused
          TrackRecordingServiceConnectionUtils.pauseTrack(trackRecordingServiceConnection);
          trackController.update(true, true);
        }
      }
    }
  };

  private final OnClickListener stopListener = new OnClickListener() {
      @Override
    public void onClick(View v) {
      TrackRecordingServiceConnectionUtils.stopRecording(
          TrackDetailActivity.this, trackRecordingServiceConnection, true);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    mBMapMan=new BMapManager(getApplication());  
    mBMapMan.init(IJoggingApplication.mStrKey, null); 
    setContentView(getLayoutResId());
    myTracksProviderUtils = MyTracksProviderUtils.Factory.get(this);
    handleIntent(getIntent());

    sharedPreferences = getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE);

    trackRecordingServiceConnection = new TrackRecordingServiceConnection(
        this, bindChangedCallback);
    trackDataHub = TrackDataHub.newInstance(this);

    tabHost = (TabHost) findViewById(android.R.id.tabhost);
    tabHost.setup();

    TabSpec mapTabSpec = tabHost.newTabSpec(MapFragment.MAP_FRAGMENT_TAG).setIndicator(
        getString(R.string.track_detail_map_tab),
        getResources().getDrawable(R.drawable.ic_tab_map))
        .setContent(R.id.map_fragment);
    tabHost.addTab(mapTabSpec);

    TabSpec chartTabSpec = tabHost.newTabSpec(ChartFragment.CHART_FRAGMENT_TAG).setIndicator(
        getString(R.string.track_detail_chart_tab),
        getResources().getDrawable(R.drawable.ic_tab_chart))
        .setContent(R.id.chart_fragment);
    tabHost.addTab(chartTabSpec);

    TabSpec statsTabSpec = tabHost.newTabSpec(StatsFragment.STATS_FRAGMENT_TAG).setIndicator(
        getString(R.string.track_detail_stats_tab),
        getResources().getDrawable(R.drawable.ic_tab_stats))
        .setContent(R.id.stats_fragment);
    tabHost.addTab(statsTabSpec);
    
    tabHost.setCurrentTab(0);

    if (savedInstanceState != null) {
      tabHost.setCurrentTabByTag(savedInstanceState.getString(CURRENT_TAB_TAG_KEY));
    }
    
    
    trackController = new TrackController(
        this, trackRecordingServiceConnection, needLocationListener, recordListener, stopListener);
    
    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    getSupportActionBar().setIcon(R.color.transparent);
    getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
  }

  @Override
  protected void onStart() {
    super.onStart();

    sharedPreferences.registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    sharedPreferenceChangeListener.onSharedPreferenceChanged(null, null);

    TrackRecordingServiceConnectionUtils.startConnection(this, trackRecordingServiceConnection);
    trackDataHub.start();
  }

  @Override
  protected void onResume() {
    super.onResume();
    trackDataHub.loadTrack(trackId);

    // Update UI
    ApiAdapterFactory.getApiAdapter().invalidMenu(this);
    boolean isRecording = checkIsRecording();
    trackController.onResume(isRecording, recordingTrackPaused);
  }

  @Override
  protected void onPause() {
    super.onPause();
    trackController.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    sharedPreferences.unregisterOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    trackRecordingServiceConnection.unbind();
    trackDataHub.stop();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(CURRENT_TAB_TAG_KEY, tabHost.getCurrentTabTag());
  }

  protected int getLayoutResId() {
    return R.layout.track_detail;
  }

  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
    handleIntent(intent);
  }


  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    return super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if(item.getItemId() == android.R.id.home){
      this.finish();
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onTrackballEvent(MotionEvent event) {
    if (event.getAction() == MotionEvent.ACTION_DOWN) {
      if (checkIsRecording() && !recordingTrackPaused) {
        TrackRecordingServiceConnectionUtils.addMarker(
            this, trackRecordingServiceConnection, WaypointCreationRequest.DEFAULT_WAYPOINT);
        return true;
      }
    }
    return super.onTrackballEvent(event);
  }


  protected TrackRecordingServiceConnection getTrackRecordingServiceConnection() {
    return trackRecordingServiceConnection;
  }

  /**
   * Gets the {@link TrackDataHub}.
   */
  public TrackDataHub getTrackDataHub() {
    return trackDataHub;
  }

  /**
   * Gets the track id.
   */
  public long getTrackId() {
    return trackId;
  }
  
  /**
   * Gets the marker id.
   */
  public long getMarkerId() {
    return markerId;
  }
  
  public boolean getNeedLocationListener(){
    return needLocationListener;
  }
  
  /**
   * Handles the data in the intent.
   */
  private void handleIntent(Intent intent) {
    trackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L);
    markerId = intent.getLongExtra(EXTRA_MARKER_ID, -1L);
    if (markerId != -1L) {
      // Use the trackId from the marker
      Waypoint waypoint = myTracksProviderUtils.getWaypoint(markerId);
      if (waypoint == null) {
        finish();
        return;
      }
      trackId = waypoint.getTrackId();
    }
    if (trackId == -1L) {
      needLocationListener = true;
      trackId = PreferencesUtils.getLong(
          TrackDetailActivity.this, R.string.recording_track_id_key);
      return;
    }
//    Track track = myTracksProviderUtils.getTrack(trackId);
//    if (track == null) {
//      // Use the last track if markerId is not set
//      if (markerId == -1L) {
//        track = myTracksProviderUtils.getLastTrack();
//        if (track != null) {
//          trackId = track.getId();
//          return;
//        }
//      }
//      finish();
//      return;
//    }
  }
  
  /**
   * Starts a new recording.
   */
  private void startRecording() {
    startNewRecording = true;
    trackRecordingServiceConnection.startAndBind();

    /*
     * If the binding has happened, then invoke the callback to start a new
     * recording. If the binding hasn't happened, then invoking the callback
     * will have no effect. But when the binding occurs, the callback will get
     * invoked.
     */
    bindChangedCallback.run();
  }
  
  private boolean checkIsRecording(){
    if(trackId == recordingTrackId){
      if(recordingTrackId == PreferencesUtils.RECORDING_TRACK_ID_DEFAULT){
        return false;
      }else{
        return true;
      }
    }else{
      return false;
    }
  }

}