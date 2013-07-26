package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.baidu.mapapi.BMapManager;
import com.google.android.apps.mytracks.ImportActivity;
import com.google.android.apps.mytracks.MyTracksApplication;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.fragments.DeleteAllTrackDialogFragment;
import com.google.android.apps.mytracks.io.file.SaveActivity;
import com.google.android.apps.mytracks.io.file.TrackWriterFactory.TrackFileFormat;
import com.google.android.apps.mytracks.services.ITrackRecordingService;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.apps.mytracks.util.AnalyticsUtils;
import com.google.android.apps.mytracks.util.IntentUtils;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.TrackRecordingServiceConnectionUtils;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.Services.DownloadOfflineMapService.DownloadOfflineMapServiceBinder;
import com.hu.iJogging.Services.DownloadOfflineMapServiceConnection;
import com.hu.iJogging.fragments.DeleteOneTrackDialogFragment.DeleteOneTrackCaller;
import com.hu.iJogging.fragments.OfflineMapFragment;
import com.hu.iJogging.fragments.TrackListFragment;
import com.hu.iJogging.fragments.TrainingDetailContainerFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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

  public String currentSport = null;
  public long recordingTrackId = -1L;
  public static final String EXTRA_TRACK_ID = "track_id";
  
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private DownloadOfflineMapServiceConnection downloadOfflineMapServiceConnection;
  private boolean startNewRecording = false;
  private TrackDataHub trackDataHub;
  public static final String EXTRA_STR_CURRENT_SPORT = "currentSport";
  public static final int SELECT_SPORT_REQUEST_CODE = 0;
  public DownloadOfflineMapServiceBinder downloadOfflineMapServiceBinder;
  
  ViewPager mViewPager;
  ContainerPagerAdapter mContainerPagerAdapter;
  
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
  
  private final Runnable downloadOfflineMapBindChangedCallback = new Runnable() {
    @Override
    public void run() {
      downloadOfflineMapServiceBinder = downloadOfflineMapServiceConnection.getServiceIfBound();
      if (downloadOfflineMapServiceBinder == null) {
        Log.d(TAG, "downloadOfflineMapService service not available");
      }
      mContainerPagerAdapter = new ContainerPagerAdapter(IJoggingActivity.this, getSupportFragmentManager());
      mViewPager = (ViewPager)findViewById(R.id.training_detail_container);
      mViewPager.setVisibility(View.VISIBLE);
      findViewById(R.id.fragment_container).setVisibility(View.GONE);
      mViewPager.setAdapter(mContainerPagerAdapter);
    }
  };

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    //如果离线地图service没有启动，说明是第一次运行ijogging应用，需要启动
    //splash activity初始化离线地图service
    if(!DownloadOfflineMapServiceConnection.isOfflineServiceRunning(this)){
      Intent intent = new Intent(this,SplashActivity.class);
      startActivity(intent);
      finish();
    }
    recordingTrackId = PreferencesUtils.getLong(this, R.string.recording_track_id_key);
    IJoggingApplication app = (IJoggingApplication) this.getApplication();
    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, bindChangedCallback);
    downloadOfflineMapServiceConnection = new DownloadOfflineMapServiceConnection(this ,downloadOfflineMapBindChangedCallback);
    downloadOfflineMapServiceConnection.bindService();
    trackDataHub = app.getTrackDataHub();
    setupActionBar();
    if (null != mActionBar) {
      mActionBar.setSelectedNavigationItem(0);
    }
    
    this.setContentView(R.layout.i_jogging_main);
    FragmentManager.enableDebugLogging(false);
    
    mBMapMan=new BMapManager(getApplication());  
    mBMapMan.init(MyTracksApplication.mStrKey, null); 
  }
  
  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
    recordingTrackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L);
  }


  public void switchToTrackListFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction ft = fragmentManager.beginTransaction();
    TrackListFragment trackListFragment = new TrackListFragment();
    ft.replace(R.id.fragment_container, trackListFragment);
    ft.commit();
  }
  
  public void switchToOfflineMapFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction ft = fragmentManager.beginTransaction();
    OfflineMapFragment OfflineMapFragment = new OfflineMapFragment();
    ft.replace(R.id.fragment_container, OfflineMapFragment);
    ft.commit();
  }
  
  public void switchToTrainingDetailContainer() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    mViewPager = (ViewPager)findViewById(R.id.training_detail_container);
    mViewPager.setVisibility(View.VISIBLE);
    findViewById(R.id.fragment_container).setVisibility(View.GONE);
  }

  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = this.getSupportMenuInflater();
    inflater.inflate(R.menu.ijogging_activity_menu, menu);
    return super.onCreateOptionsMenu(menu);
  }
  
  

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    Intent intent;
    switch(item.getItemId()){
      case R.id.save_to_sd_action:
        startSaveActivity(TrackFileFormat.GPX);
        return true;
      case R.id.import_from_sd_action:
        AnalyticsUtils.sendPageViews(this, "/action/import");
        intent = IntentUtils.newIntent(this, ImportActivity.class)
            .putExtra(ImportActivity.EXTRA_IMPORT_ALL, true);
        startActivity(intent);
        return true;
      case R.id.delete_all_records:
        new DeleteAllTrackDialogFragment().show(
            getSupportFragmentManager(), DeleteAllTrackDialogFragment.DELETE_ALL_TRACK_DIALOG_TAG);
        return true;
      case R.id.exit_action:
        stopRecording();
        downloadOfflineMapServiceConnection.stop();
        downloadOfflineMapServiceConnection = null;
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }
  
  /**
   * Starts the {@link SaveActivity} to save all tracks.
   *
   * @param trackFileFormat the track file format
   */
  private void startSaveActivity(TrackFileFormat trackFileFormat) {
    AnalyticsUtils.sendPageViews(this, "/action/save_all");
    Intent intent = IntentUtils.newIntent(this, SaveActivity.class)
        .putExtra(SaveActivity.EXTRA_TRACK_FILE_FORMAT, (Parcelable) trackFileFormat);
    startActivity(intent);
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    if(mAdapter.getCurrentSpinner() == 1){
      menu.getItem(0).setVisible(false);
      menu.getItem(1).setVisible(false);
      menu.getItem(2).setVisible(false);
    }else if(mAdapter.getCurrentSpinner() == 3){
      menu.getItem(0).setVisible(true);
      menu.getItem(1).setVisible(true);
      menu.getItem(2).setVisible(true);
    }else{
      menu.getItem(0).setVisible(false);
      menu.getItem(1).setVisible(false);
      menu.getItem(2).setVisible(false);
    }
    return super.onPrepareOptionsMenu(menu);
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
  protected void onDestroy(){
    if(downloadOfflineMapServiceConnection != null){
      downloadOfflineMapServiceConnection.unbind();
    }
    super.onDestroy();
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
