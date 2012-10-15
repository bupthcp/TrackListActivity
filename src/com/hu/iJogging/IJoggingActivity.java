package com.hu.iJogging;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.baidu.mapapi.MKOLUpdateElement;
import com.baidu.mapapi.MKOfflineMap;
import com.baidu.mapapi.MKOfflineMapListener;
import com.google.android.apps.mytracks.MyTracksApplication;
import com.google.android.apps.mytracks.services.ITrackRecordingService;
import com.google.android.apps.mytracks.services.TrackRecordingServiceConnection;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.fragments.TrackListFragment;
import com.hu.iJogging.fragments.TrainingDetailFragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.widget.Toast;

public class IJoggingActivity extends SherlockFragmentActivity {
  private static final String TAG = IJoggingActivity.class.getSimpleName();

  private ActionBar mActionBar;
  private ActionBarAdapter mAdapter = null;
  private WorkoutPage mWorkoutPage = null;

  private MKOfflineMap mOffline = null;

  private TrackRecordingServiceConnection trackRecordingServiceConnection;
  private boolean startNewRecording = false;
  public long recordingTrackId;

  private void setupActionBar() {
    this.mActionBar = getSupportActionBar();
    this.mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
    this.mActionBar.setDisplayShowTitleEnabled(false);
    this.mActionBar.setDisplayShowHomeEnabled(false);
    this.mActionBar.setDisplayUseLogoEnabled(false);
    mAdapter = new ActionBarAdapter(this);
    mActionBar.setListNavigationCallbacks(mAdapter, mAdapter.new OnNaviListener());
    mActionBar.setSelectedNavigationItem(1);
  }

  @Override
  protected void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    if (null != mActionBar) {
      mActionBar.setSelectedNavigationItem(0);
    }
    
    this.setContentView(R.layout.i_jogging_main);
    FragmentManager.enableDebugLogging(true);
    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
    TrainingDetailFragment trainingDetailFragment = new TrainingDetailFragment();
    ft.add(R.id.fragment_container, trainingDetailFragment);
    ft.commit();

    // 初始化baidu地图
    MyTracksApplication app = (MyTracksApplication) this.getApplication();
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

    trackRecordingServiceConnection = new TrackRecordingServiceConnection(this, bindChangedCallback);
  }

  // switch系列的方法用于在全局切换fragment，所以在切换前必须调用popBackStack
  // 将backstack中的所有fragment清理掉，否则，在切换过程中会出现重叠显示的问题
  // 这个方法也必须在commit之前调用，如果在commit之后调用，backstack会发生变化
  // 就没有办法清理干净了，会出现重叠显示的效果。
  public void switchToTrackListFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    // 这个语句的效果是将backstack内的所有tag不是null的entry全部清理，其实就是
    // 将所有的entry全部清理出去
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction ft = fragmentManager.beginTransaction();
    TrackListFragment trackListFragment = new TrackListFragment();
    ft.replace(R.id.fragment_container, trackListFragment);
    ft.commit();

  }

  public void switchToTrainingDetailFragment() {
    FragmentManager fragmentManager = getSupportFragmentManager();
    fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    FragmentTransaction ft = fragmentManager.beginTransaction();
    TrainingDetailFragment trainingDetailFragment = new TrainingDetailFragment();
    ft.replace(R.id.fragment_container, trainingDetailFragment);
    ft.commit();
  }

  @Override
  protected void onResume() {
    super.onResume();
    setupActionBar();
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
        startNewRecording = false;
        // Intent intent = IntentUtils.newIntent(TrackListActivity.this,
        // TrackDetailActivity.class)
        // .putExtra(TrackDetailActivity.EXTRA_TRACK_ID, recordingTrackId);
        // startActivity(intent);
        Toast.makeText(IJoggingActivity.this, R.string.track_list_record_success,
            Toast.LENGTH_SHORT).show();
      } catch (Exception e) {
        Toast.makeText(IJoggingActivity.this, R.string.track_list_record_error, Toast.LENGTH_LONG)
            .show();
        Log.e(TAG, "Unable to start a new recording.", e);
      }
    }
  };
}
