package com.hu.walkingnotes;

import com.baidu.mapapi.BMapManager;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.ActionBarAdapter;
import com.hu.iJogging.IJoggingApplication;
import com.hu.iJogging.fragments.MapFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class NotesActivity extends ActionBarActivity{
  
  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;

  public String currentSport = null;
  public long recordingTrackId = -1L;
  public static final String EXTRA_TRACK_ID = "track_id";
  
  private boolean startNewRecording = false;
  private TrackDataHub trackDataHub;
  
  public BMapManager mBMapMan = null;
  
  public void setupActionBar() {
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayShowHomeEnabled(false);
    this.mActionBar.setDisplayUseLogoEnabled(false);
    this.mActionBar.setDisplayShowCustomEnabled(false);
    mAdapter = new ActionBarAdapter(this);
    mActionBar.setListNavigationCallbacks(mAdapter, mAdapter.new OnNaviListener());
//    mActionBar.setSelectedNavigationItem(1);
  }
  
  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    recordingTrackId = PreferencesUtils.getLong(this, R.string.recording_track_id_key);
    IJoggingApplication app = (IJoggingApplication) this.getApplication();
    trackDataHub = app.getTrackDataHub();
    setupActionBar();
    if (null != mActionBar) {
      mActionBar.setSelectedNavigationItem(0);
    }
    this.setContentView(R.layout.notes_activity);
    FragmentManager.enableDebugLogging(false);
    
    mBMapMan=new BMapManager(getApplication());  
    mBMapMan.init(IJoggingApplication.mStrKey, null); 
  }
  
  private void startMapFragment() {
    Fragment mapFragment = new MapFragment();
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    ft.add(R.id.fragment_container, mapFragment, MapFragment.MAP_FRAGMENT_TAG);
    ft.commit();
  }

}
