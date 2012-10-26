package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.baidu.mapapi.MKOfflineMapListener;
import com.google.android.apps.mytracks.MyTracksApplication;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.services.ITrackRecordingService;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.apps.mytracks.util.TrackRecordingServiceConnectionUtils;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.fragments.DeleteOneTrackDialogFragment.DeleteOneTrackCaller;
import com.hu.iJogging.fragments.TrackListFragment;
import com.hu.iJogging.fragments.TrainingDetailContainerFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class IJoggingActivity extends SherlockFragmentActivity implements DeleteOneTrackCaller{
  private static final String TAG = IJoggingActivity.class.getSimpleName();

  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;

  private MKOfflineMap mOffline = null;


  public String currentSport = null;
  public long recordingTrackId = -1L;
  
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private boolean startNewRecording = false;
  private TrackDataHub trackDataHub;
  public static final String EXTRA_STR_CURRENT_SPORT = "currentSport";
  public static final int SELECT_SPORT_REQUEST_CODE = 0;
  
  ViewPager mViewPager;
  ContainerPagerAdapter mContainerPagerAdapter;

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
    MyTracksApplication app = (MyTracksApplication) this.getApplication();
    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, bindChangedCallback);
    trackDataHub = app.getTrackDataHub();
    setupActionBar();
    if (null != mActionBar) {
      mActionBar.setSelectedNavigationItem(0);
    }
    
    this.setContentView(R.layout.i_jogging_main);
    FragmentManager.enableDebugLogging(true);
//    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
//    TrainingDetailContainerFragment trainingDetailContainerFragment = new TrainingDetailContainerFragment();
//    ft.add(R.id.fragment_container, trainingDetailContainerFragment);
//    ft.commit();
    
    mContainerPagerAdapter = new ContainerPagerAdapter(this, getSupportFragmentManager());
    mViewPager = (ViewPager)findViewById(R.id.training_detail_container);
    mViewPager.setVisibility(View.VISIBLE);
    findViewById(R.id.fragment_container).setVisibility(View.GONE);
    mViewPager.setAdapter(mContainerPagerAdapter);

    // 初始化baidu地图
    
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


  public void switchToTrackListFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction ft = fragmentManager.beginTransaction();
    TrackListFragment trackListFragment = new TrackListFragment();
    ft.replace(R.id.fragment_container, trackListFragment);
    ft.commit();
  }
  
  public void switchToTrainingDetailContainer() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    mViewPager = (ViewPager)findViewById(R.id.training_detail_container);
    mViewPager.setVisibility(View.VISIBLE);
    findViewById(R.id.fragment_container).setVisibility(View.GONE);
  }

  
  // switch系列的方法用于在全局切换fragment，所以在切换前必须调用popBackStack
  // 将backstack中的所有fragment清理掉，否则，在切换过程中会出现重叠显示的问题
  // 这个方法也必须在commit之前调用，如果在commit之后调用，backstack会发生变化
  // 就没有办法清理干净了，会出现重叠显示的效果。
  public void switchToTrainingDetailContainerFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    // 这个语句的效果是将backstack内的所有tag不是null的entry全部清理，其实就是
    // 将所有的entry全部清理出去
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction ft = fragmentManager.beginTransaction();
    TrainingDetailContainerFragment trainingDetailContainerFragment = new TrainingDetailContainerFragment();
    ft.replace(R.id.fragment_container, trainingDetailContainerFragment);
    ft.commit();
  }

  @Override
  protected void onResume() {
    super.onResume();
    if(recordingTrackId!=-1L){
      trackDataHub.loadTrack(recordingTrackId);
      trackDataHub.start();
    }
  }
  
  @Override
  protected void onPause() {
    super.onPause();
    trackDataHub.stop();
  }
  
  @Override
  protected void onStart() {
    super.onStart();
  }
  
  @Override
  protected void onStop() {
    super.onStop();
  }
  
  @Override
  public void onBackPressed(){
    super.onBackPressed();
    
  }
  
  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if((requestCode == SELECT_SPORT_REQUEST_CODE)&&(resultCode == Activity.RESULT_OK)){
      currentSport = data.getStringExtra("currentSport");
    }
  }
  
  // Callback when the trackRecordingServiceConnection binding changes.
  private final Runnable bindChangedCallback = new Runnable() {
    @Override
    public void run() {
      if (!startNewRecording) { return; }

      ITrackRecordingService service = trackRecordingServiceConnection.getServiceIfBound();
      if (service == null) {
        Log.d(TAG, "service not available to start a new recording");
        return;
      }
      try {
        recordingTrackId = service.startNewTrack();
        trackDataHub.loadTrack(recordingTrackId);
        trackDataHub.start();
        startNewRecording = false;
        Toast.makeText(IJoggingActivity.this, R.string.track_list_record_success,
            Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        Toast.makeText(IJoggingActivity.this, R.string.track_list_record_error, Toast.LENGTH_LONG)
            .show();
        Log.e(TAG, "Unable to start a new recording.", e);
      }
    }
  };
  
  /**
   * Starts a new recording.
   */
  public void startRecording() {
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
  
  public Boolean isRecording(){
    return TrackRecordingServiceConnectionUtils.isRecording(this, trackRecordingServiceConnection);
  }
  
  public void stopRecording(){
    TrackRecordingServiceConnectionUtils.stop(this, trackRecordingServiceConnection, false);
    recordingTrackId = -1L;
  }

  @Override
  public TrackRecordingServiceConnection getTrackRecordingServiceConnection() {
    return trackRecordingServiceConnection;
  }
}
