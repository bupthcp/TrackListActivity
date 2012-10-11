package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.baidu.mapapi.MKOfflineMapListener;
import com.google.android.apps.mytracks.MyTracksApplication;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.fragments.TrackListFragment;
import com.hu.iJogging.fragments.TrainingDetailFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

public class IJoggingActivity extends SherlockFragmentActivity{
  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;
  private WorkoutPage mWorkoutPage = null;
  
  private MKOfflineMap mOffline = null;

  
  private void setupActionBar(){
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayShowHomeEnabled(false);
    mAdapter = new ActionBarAdapter(this);
    mActionBar.setListNavigationCallbacks(mAdapter,mAdapter.new OnNaviListener());
    mActionBar.setSelectedNavigationItem(1);
  }


  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    if(null != mActionBar){
      mActionBar.setSelectedNavigationItem(0);
    }
    setupActionBar();
    this.setContentView(R.layout.i_jogging_main);
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    TrainingDetailFragment trainingDetailFragment = new TrainingDetailFragment();
    ft.add(R.id.fragment_container, trainingDetailFragment);
    ft.commit();

    //≥ı ºªØbaiduµÿÕº
    MyTracksApplication app = (MyTracksApplication)this.getApplication();
    mOffline = new MKOfflineMap();
    mOffline.init(app.mBMapMan, new MKOfflineMapListener() {
      @Override
      public void onGetOfflineMapState(int type, int state) {
        switch (type) {
          case MKOfflineMap.TYPE_DOWNLOAD_UPDATE: {
            MKOLUpdateElement update = mOffline.getUpdateInfo(state);
            // mText.setText(String.format("%s : %d%%", update.cityName,
            // update.ratio));
          }
            break;
          case MKOfflineMap.TYPE_NEW_OFFLINE:
            Log.d("OfflineDemo", String.format("add offlinemap num:%d", state));
            break;
          case MKOfflineMap.TYPE_VER_UPDATE:
            Log.d("OfflineDemo", String.format("new offlinemap ver"));
            break;
        }
      }
    });
  }
  
  public void switchToTrackListFragment(){
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    TrackListFragment trackListFragment = new TrackListFragment();
    ft.replace(R.id.fragment_container, trackListFragment);
    ft.commit();
  }
  
  public void switchToTrainingDetailFragment(){
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    TrainingDetailFragment trainingDetailFragment = new TrainingDetailFragment();
    ft.replace(R.id.fragment_container, trainingDetailFragment);
    ft.commit();
  }

  
  @Override
  protected void onResume() {
    super.onResume();
  }
}
