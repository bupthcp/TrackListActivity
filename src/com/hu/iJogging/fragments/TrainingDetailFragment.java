package com.hu.iJogging.fragments;

import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.content.TrackDataHub.ListenerDataType;
import com.google.android.apps.mytracks.content.TrackDataListener;
import com.google.android.apps.mytracks.stats.TripStatistics;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.StringUtils;
import com.hu.iJogging.IJoggingActivity;
import com.hu.iJogging.IJoggingApplication;
import com.hu.iJogging.R;
import com.hu.iJogging.SelectSportsActivity;
import com.hu.iJogging.ViewHistoryActivity;
import com.hu.iJogging.common.MainZoneLayout;
import com.hu.iJogging.common.MotivationMainButton;
import com.hu.iJogging.common.SportMainButton;
import com.hu.iJogging.common.TextMeasuredView;
import com.hu.iJogging.content.Track;
import com.hu.iJogging.content.TracksColumns;
import com.hu.iJogging.content.Waypoint;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.EnumSet;
public class TrainingDetailFragment extends Fragment implements TrackDataListener {
  public static final String TRAINING_DETAIL_FTAGMENT_TAG = "TrainingDetailFragment";
  View mMeasureView = null;
  private MotivationMainButton mBtnMotivation;
  private View btnSport;
  private View buttonCountdownStop;
  private Button btnStart;
  ImageView btnMusic;
  ImageView imageGPS;
  ImageView ivSport;
  private Handler mSettingsChangeHandler;
  TextView tvGPS;
  TextMeasuredView tvSport;
  Activity mActivity;
  Boolean isViewHistory = false;
  private boolean metricUnits = true;

  MainZoneLayout mMainZone1;
  MainZoneLayout mMainZone2;
  MainZoneLayout mMainZone3;
  MainZoneLayout mMainZone4;
  
  private TrackDataHub trackDataHub;
  
  private UiUpdateThread uiUpdateThread = null;

  // The start time of the current track.
  private long startTime = -1L;

  private Location lastLocation = null;
  private TripStatistics lastTripStatistics = null;
  
  // A runnable to update the total time field.
  private final Runnable updateTotalTime = new Runnable() {
    public void run() {
      if (isRecording()) {
        setTotalTime(System.currentTimeMillis() - startTime);
      }
    }
  };

  
  private String TAG = TrainingDetailFragment.class.getSimpleName();
  
  private static final String[] PROJECTION = new String[] {
    TracksColumns._ID,
    TracksColumns.NAME,
    TracksColumns.DESCRIPTION,
    TracksColumns.CATEGORY,
    TracksColumns.STARTTIME,
    TracksColumns.TOTALDISTANCE,
    TracksColumns.TOTALTIME,
    TracksColumns.AVGSPEED,
    TracksColumns.MAXSPEED,
    TracksColumns.ICON};

  
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if(activity instanceof IJoggingActivity){
      isViewHistory = false;
    }else if(activity instanceof ViewHistoryActivity){
      isViewHistory = true;
    }
    mActivity = activity;
  }

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    trackDataHub = ((IJoggingApplication) getActivity().getApplication()).getTrackDataHub();
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    if (this.mMeasureView == null) {
      this.mMeasureView = getMeasureView(container);
    }
    setFocus();
    return mMeasureView;
  }
  
  @Override
  public void onResume(){
    super.onResume();
    //从SelectSportActivity跳转回IJoggingActivity之后，会先到onActivityResult
    //然后再到Fragment的onResume，所以，需要在这里再对SportMainButton进行一次设置
    if(!isViewHistory){
      ((SportMainButton)btnSport).setSport(((IJoggingActivity)mActivity).currentSport);
      if (uiUpdateThread != null) {
        uiUpdateThread.resume();
      }
//      if (uiUpdateThread == null ) {
//        uiUpdateThread = new UiUpdateThread();
//        uiUpdateThread.start();
//      } else if (uiUpdateThread != null ) {
//        uiUpdateThread.resume();
//      }
    }else{
      //将btnSport设置为从历史记录中读取出来的数据
      //并且不可点击切换
    }
    setStartandStopButtons();
    if(!isViewHistory){
      resumeTrackDataHub();
    }
  }
  
  @Override
  public void onPause() {
    super.onPause();
    if(!isViewHistory){
      pauseTrackDataHub();
      if (uiUpdateThread != null) {
        uiUpdateThread.interrupt();
        uiUpdateThread=null;
      }
    }
  }
  
  //在这里实现onDestroyView是为了保证在fragment切换的
  //时候，fragment的container是干净的，
  //如果不加上这个清理过程，有可能会出现两个fragment重叠
  //在一起显示的情况
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ViewGroup parentViewGroup = (ViewGroup) mMeasureView.getParent();
    if (parentViewGroup != null) {
      parentViewGroup.removeView(mMeasureView);
    }
  }

  private View getMeasureView(ViewGroup container) {
    //inflate的第三个参数设置为false，即不适用layout的root元素。因为laytout文件中会有root的laytout,这个时候就会与
    //container冲突了， 会有这个错误出现The specified child already has a parent. You must call removeView() on 
    //the child's parent first。
    //如果要使用inflater(int id,View container)这个方法，则container需要设置为null。这个方法和
    //inflater(int id,View container, boolean )的第三个参数为true时是等效的
    View localView = getActivity().getLayoutInflater().inflate(R.layout.workout_measure_view, container, false);
    mBtnMotivation = (MotivationMainButton) localView.findViewById(R.id.MotivationMainButton);
    mBtnMotivation.setVisibility(View.VISIBLE);
    btnSport = (LinearLayout) localView.findViewById(R.id.SportMainButton);
    btnSport.setVisibility(0);
    localView.findViewById(R.id.SportMainButtonSeperator).setVisibility(0);
    localView.findViewById(R.id.LLMainZone).setVisibility(0);
    return localView;
  }
  
  private void setStartandStopButtons(){
    if(!isViewHistory){
      if(((IJoggingActivity)mActivity).isRecording()){
        buttonCountdownStop.setClickable(true);
        buttonCountdownStop.setBackgroundResource(R.drawable.dashboard_button_stop);
        btnStart.setClickable(false);
        btnStart.setBackgroundResource(R.drawable.start_big_gray);
      }else{
        buttonCountdownStop.setClickable(false);
        buttonCountdownStop.setBackgroundResource(R.drawable.stop_gray);
        btnStart.setClickable(true);
        btnStart.setBackgroundResource(R.drawable.dashboard_button_start);
      }
    }
  }

  private void setFocus() {
    mBtnMotivation = (MotivationMainButton) mMeasureView.findViewById(R.id.MotivationMainButton);
    
    buttonCountdownStop = mMeasureView.findViewById(R.id.ButtonCountdownStop);
    if(isViewHistory){
      buttonCountdownStop.setVisibility(View.INVISIBLE);
    }else{
      buttonCountdownStop.setVisibility(View.VISIBLE);
      buttonCountdownStop.setBackgroundResource(R.drawable.stop_gray);
      buttonCountdownStop.setClickable(false);
      buttonCountdownStop.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ((IJoggingActivity)mActivity).stopRecording();
          pauseTrackDataHub();
          btnStart.setClickable(true);
          btnStart.setBackgroundResource(R.drawable.dashboard_button_start);
          buttonCountdownStop.setBackgroundResource(R.drawable.stop_gray);
          buttonCountdownStop.setClickable(false);
        }
      });
    }
    
    btnStart = (Button)mMeasureView.findViewById(R.id.ButtonStartPause);
    if(isViewHistory){
      //如果是查看界面，应该启动google地球进行播放
    }else{
      //如果是新训练界面，则启动记录
      btnStart.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          ((IJoggingActivity)mActivity).startRecording();
          buttonCountdownStop.setBackgroundResource(R.drawable.dashboard_button_stop);
          buttonCountdownStop.setClickable(true);
          btnStart.setClickable(false);
          btnStart.setBackgroundResource(R.drawable.start_big_gray);
          pauseTrackDataHub();
          resumeTrackDataHub();
        }
      });
    }
    
    
    btnSport = (LinearLayout) mMeasureView.findViewById(R.id.SportMainButton);
    this.ivSport = ((ImageView) this.mMeasureView.findViewById(R.id.ImageButtonSport));
    this.ivSport.setVisibility(0);
    int i = getActivity().getResources().getColor(R.color.EndoGreen);
    this.ivSport.setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    this.tvSport = ((TextMeasuredView) this.mMeasureView.findViewById(R.id.tvWoSport));
    if(!isViewHistory){
      btnSport.setOnClickListener(new View.OnClickListener() {
        public void onClick(View paramView) {
//          startSelectSportsFragment();
          startSelectSportsActivity();
        }
      });
      ((SportMainButton)btnSport).setSport(((IJoggingActivity)mActivity).currentSport);
    }else{
      //将btnSport设置为从历史记录中读取出来的数据
      //并且不可点击切换
    }

    this.imageGPS = ((ImageView) this.mMeasureView.findViewById(R.id.ImageViewGPS));
    this.tvGPS = ((TextView) this.mMeasureView.findViewById(R.id.TextViewGPS));
    Typeface localTypeface = Typeface.createFromAsset(this.getActivity().getAssets(),
        "fonts/Roboto-Regular.ttf");
    this.tvGPS.setTypeface(localTypeface);
    mBtnMotivation.setOnClickListener(new View.OnClickListener() {
      public void onClick(View paramView) {}
    });
    ((Button) this.mMeasureView.findViewById(R.id.ButtonMapCorner))
        .setOnTouchListener(new View.OnTouchListener() {
          public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
            if (paramMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
               startMapFragment();
            }
            return true;
          }
        });
    this.mMainZone1 = new MainZoneLayout(this.getActivity(), null, 1, MainZoneLayout.TYPE_DURATION, null);
    LinearLayout localLinearLayout2 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone1);
    localLinearLayout2.removeAllViews();
    localLinearLayout2.addView(this.mMainZone1);
    this.mMainZone2 = new MainZoneLayout(this.getActivity(), null, 2, MainZoneLayout.TYPE_DISTANCE, null);
    LinearLayout localLinearLayout3 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone2);
    localLinearLayout3.removeAllViews();
    localLinearLayout3.addView(this.mMainZone2);
    this.mMainZone3 = new MainZoneLayout(this.getActivity(), null, 3, MainZoneLayout.TYPE_AVERAGE_SPEED, null);
    LinearLayout localLinearLayout4 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone3);
    localLinearLayout4.removeAllViews();
    localLinearLayout4.addView(this.mMainZone3);
    this.mMainZone4 = new MainZoneLayout(this.getActivity(), null, 3, MainZoneLayout.TYPE_SPEED, null);
    LinearLayout localLinearLayout5 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone4);
    localLinearLayout5.removeAllViews();
    localLinearLayout5.addView(this.mMainZone4);
    
    if(isViewHistory){
      getActivity().getSupportLoaderManager().initLoader(0, null, new LoaderCallbacks<Cursor>() {
        @Override
        public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
          Log.d(TAG, "trackId"+((ViewHistoryActivity)mActivity).trackId);
          return new CursorLoader(getActivity(),
              TracksColumns.CONTENT_URI,
              PROJECTION,
              TracksColumns._ID + "="+((ViewHistoryActivity)mActivity).trackId,
              null,
              null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
          cursor.moveToFirst();
          int totalTimeIndex = cursor.getColumnIndexOrThrow(TracksColumns.TOTALTIME);
          int maxSpeedIndex = cursor.getColumnIndex(TracksColumns.MAXSPEED);
          int totalDistanceIndex = cursor.getColumnIndex(TracksColumns.TOTALDISTANCE);
          long totalTimeLong = cursor.getLong(totalTimeIndex);
          double totalDistanceDouble = cursor.getDouble(totalDistanceIndex);
          //平均速度与最高速度不同，在记录运动的同时并不是即时演算的，所以数据库中没有存储，
          //只在需要的时候才会使用总时间和总距离进行运算得出平均速度
          double avgSpeeddouble = totalDistanceDouble / ((double) totalTimeLong / 1000.0);
          Long maxSpeedLong = cursor.getLong(maxSpeedIndex);
          setTotalTime(totalTimeLong);
          setTotalDistance(totalDistanceDouble);
          setAvgSpeed(avgSpeeddouble);
          setSpeed(maxSpeedLong);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
      });    
    }
  }
  
  



  
  /**
   * Resumes the trackDataHub. Needs to be synchronized because trackDataHub can
   * be accessed by multiple threads.
   */
  private synchronized void resumeTrackDataHub() {
    trackDataHub.registerTrackDataListener(this, EnumSet.of(
        ListenerDataType.SELECTED_TRACK_CHANGED,
        ListenerDataType.TRACK_UPDATES,
        ListenerDataType.LOCATION_UPDATES,
        ListenerDataType.DISPLAY_PREFERENCES));
  }

  /**
   * Pauses the trackDataHub. Needs to be synchronized because trackDataHub can
   * be accessed by multiple threads.
   */
  private synchronized void pauseTrackDataHub() {
    trackDataHub.unregisterTrackDataListener(this);
  }
  
  /**
   * A thread that updates the total time field every second.
   */
  private class UiUpdateThread extends Thread {
    @Override
    public void run() {
      Log.d(TAG, "UI update thread started");
      while (PreferencesUtils.getLong(getActivity(), R.string.recording_track_id_key)
          != PreferencesUtils.RECORDING_TRACK_ID_DEFAULT) {
        getActivity().runOnUiThread(updateTotalTime);
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {
          Log.d(TAG, "UI update thread caught exception", e);
          break;
        }
      }
      Log.d(TAG, "UI update thread finished");
    }
  }

  /**
   * Returns true if recording. Needs to be synchronized because trackDataHub
   * can be accessed by multiple threads.
   */
  private synchronized boolean isRecording() {
    return trackDataHub != null && trackDataHub.isRecordingSelected();
  }
  
  private void setTotalTime(long totalTime){
    if(totalTime < 0){
      totalTime = 0;
    }
    String totalTimeStr = StringUtils.formatElapsedTime(totalTime);
    mMainZone1.setTitle(totalTimeStr,false);
  }
  
  private void setTotalDistance(double totalDistanceDouble){
    if(totalDistanceDouble < 0){
      totalDistanceDouble = 0;
    }
    String totalDistanceStr = StringUtils.formatDistanceWithoutUnit(
        getActivity(), totalDistanceDouble, metricUnits);
    if (totalDistanceDouble > 500.0) {
      mMainZone2.setTitle(totalDistanceStr,false);
    } else {
      mMainZone2.setTitle(totalDistanceStr,true);
    }

  }
  
  private void setAvgSpeed(double avgSpeedDouble){
    if(avgSpeedDouble < 0){
      avgSpeedDouble = 0;
    }
    String averageSpeedStr = StringUtils.formatSpeedWithoutUnit(getActivity(), avgSpeedDouble, metricUnits, true);
    mMainZone3.setTitle(averageSpeedStr,false);
  }
  
  private void setSpeed(double speedDouble){
    if(speedDouble < 0){
      speedDouble = 0;
    }
    String speedStr = StringUtils.formatSpeedWithoutUnit(getActivity(), speedDouble, metricUnits,true);
    mMainZone4.setTitle(speedStr,false);
  }
  
  private void updateUI(){
    setLocationValues(lastLocation);
    setTripStatisticsValues(lastTripStatistics);
  }
  
  private void setLocationValues(Location location){
    double speed = location == null ? 0 : location.getSpeed();
    setSpeed(speed);
  }
  
  private void setTripStatisticsValues(TripStatistics tripStatistics){
 // Set total distance
    double totalDistance = tripStatistics == null ? 0 : tripStatistics.getTotalDistance();
    boolean useTotalTime = PreferencesUtils.getBoolean(
        getActivity(), R.string.stats_use_total_time_key, PreferencesUtils.STATS_USE_TOTAL_TIME_DEFAULT);
    setTotalDistance(totalDistance);
    
    double averageSpeed;
    if (tripStatistics == null) {
      averageSpeed = 0;
    } else {
      averageSpeed = useTotalTime ? tripStatistics.getAverageSpeed()
          : tripStatistics.getAverageMovingSpeed();
    }
    setAvgSpeed(averageSpeed);
  }
  
  private void startMapFragment() {
    Fragment mapFragment = new MapFragment();
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.enter_workout_map, R.anim.exit_workout_map,
        R.anim.enter_workout_map, R.anim.exit_workout_map);
    ft.add(R.id.fragment_container, mapFragment, MapFragment.MAP_FRAGMENT_TAG);
    ft.commit();
  }
  
  private void startSelectSportsFragment(){
    SelectSportsFragment selectSportsFragment = new SelectSportsFragment();
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom,R.anim.enter_bottom, R.anim.exit_bottom);
    ft.replace(R.id.fragment_container, selectSportsFragment);
    ft.addToBackStack(null);
    ft.commit();
  }
  
  private void startSelectSportsActivity(){
    Intent startSelectiSportsIntent = new Intent(getActivity(),SelectSportsActivity.class);
    getActivity().startActivityForResult(startSelectiSportsIntent, IJoggingActivity.SELECT_SPORT_REQUEST_CODE);
  }

  @Override
  public void onProviderStateChange(ProviderState state) {
    if (isResumed() && (state == ProviderState.DISABLED || state == ProviderState.NO_FIX)) {
      getActivity().runOnUiThread(new Runnable() {
          @Override
        public void run() {
          lastLocation = null;
          setLocationValues(lastLocation);
        }
      });
    }
  }

  @Override
  public void onCurrentLocationChanged(final Location loc) {
    if (isResumed() && isRecording()) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          lastLocation = loc;
          setLocationValues(lastLocation);
        }
      });
    }
  }

  @Override
  public void onCurrentHeadingChanged(double heading) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onSelectedTrackChanged(Track track, boolean isRecording) {
    if (isResumed()) {
      if (uiUpdateThread == null && isRecording) {
        uiUpdateThread = new UiUpdateThread();
        uiUpdateThread.start();
      } else if (uiUpdateThread != null && !isRecording) {
        uiUpdateThread.interrupt();
        uiUpdateThread=null;
      }
    }
  }

  @Override
  public void onTrackUpdated(final Track track) {
    if (isResumed()) {
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (track == null || track.getTripStatistics() == null) {
            lastLocation = null;
            lastTripStatistics = null;
            updateUI();
            return;
          }
          lastTripStatistics = track.getTripStatistics();
          
          startTime = track.getTripStatistics().getStartTime();
          if (!isRecording()) {
            lastLocation = null;
          }
          updateUI();
        }
      });
    }
    
  }

  @Override
  public void clearTrackPoints() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onNewTrackPoint(Location loc) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onSampledOutTrackPoint(Location loc) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onSegmentSplit() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onNewTrackPointsDone() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void clearWaypoints() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onNewWaypoint(Waypoint wpt) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onNewWaypointsDone() {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean onUnitsChanged(boolean metric) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onReportSpeedChanged(boolean reportSpeed) {
    if (isResumed()) {
      getActivity().runOnUiThread(new Runnable() {
          @Override
        public void run() {
          updateUI();
        }
      });
    }
    return true;
  }
  
}
