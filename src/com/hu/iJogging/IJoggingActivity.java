package com.hu.iJogging;

import com.baidu.mapapi.BMapManager;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.io.file.SaveActivity;
import com.google.android.apps.mytracks.io.file.TrackFileFormat;
import com.google.android.apps.mytracks.services.ITrackRecordingService;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.apps.mytracks.util.AnalyticsUtils;
import com.google.android.apps.mytracks.util.IntentUtils;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.TrackRecordingServiceConnectionUtils;
import com.hu.iJogging.fragments.DeleteAllTrackDialogFragment;
import com.hu.iJogging.fragments.DeleteOneTrackDialogFragment.DeleteOneTrackCaller;
import com.hu.iJogging.fragments.MapFragment;
import com.hu.iJogging.fragments.TrackListFragment;
import com.hu.iJogging.fragments.TrainingDetailContainerFragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

public class IJoggingActivity extends TrackActivity implements DeleteOneTrackCaller{
  private static final String TAG = IJoggingActivity.class.getSimpleName();

  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;
  
  private TrainingDetailContainerFragment trainingDetailContainerFragment;
  private MapFragment mapFragment;

  public String currentSport = null;
  public long recordingTrackId = -1L;
  public static final String EXTRA_TRACK_ID = "track_id";
  public static final String EXTRA_NEW_TRACK = "new_track";
  
  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private boolean startNewRecording = false;
  public static final String EXTRA_STR_CURRENT_SPORT = "currentSport";
  public static final int SELECT_SPORT_REQUEST_CODE = 0;
  
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
  }
  
  private final Runnable downloadOfflineMapBindChangedCallback = new Runnable() {
    @Override
    public void run() {

    }
  };

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    recordingTrackId = PreferencesUtils.getLong(this, R.string.recording_track_id_key);
    IJoggingApplication app = (IJoggingApplication) this.getApplication();
    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, bindChangedCallback);
    mContainerPagerAdapter = new ContainerPagerAdapter(IJoggingActivity.this, getSupportFragmentManager());
    
    trackDataHub = TrackDataHub.newInstance(this);
    setupActionBar();
    
    this.setContentView(R.layout.i_jogging_main);
    FragmentManager.enableDebugLogging(false);
    switchToTrainingDetailContainerFragment();
    mBMapMan=new BMapManager(getApplication());  
    mBMapMan.init(IJoggingApplication.mStrKey, null); 
  }
  
  @Override
  public void onNewIntent(Intent intent) {
    setIntent(intent);
    recordingTrackId = intent.getLongExtra(EXTRA_TRACK_ID, -1L);
  }
  


  public void switchToTrackListFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction ft = fragmentManager.beginTransaction();
    TrackListFragment trackListFragment = new TrackListFragment();
    ft.replace(R.id.fragment_container, trackListFragment);
    ft.commit();
  }
  
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = this.getMenuInflater();
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

  public void switchToTrainingDetailContainerFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
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

  @Override
  protected void onResume() {
    super.onResume();
    trackDataHub.start();
    if(recordingTrackId!=-1L){
      trackDataHub.loadTrack(recordingTrackId);
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
//    bindChangedCallback.run();
  }
  
  public Boolean isRecording(){
    return TrackRecordingServiceConnectionUtils.isRecording(this, trackRecordingServiceConnection);
  }
  
  public void stopRecording(){
    TrackRecordingServiceConnectionUtils.stopRecording(this, trackRecordingServiceConnection, false);
    recordingTrackId = -1L;
    PreferencesUtils.setLong(this, R.string.recording_track_id_key,recordingTrackId);
  }
  

  @Override
  public TrackRecordingServiceConnection getTrackRecordingServiceConnection() {
    return trackRecordingServiceConnection;
  }
}
