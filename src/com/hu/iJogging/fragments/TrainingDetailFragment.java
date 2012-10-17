package com.hu.iJogging.fragments;

import com.google.android.apps.mytracks.content.TracksColumns;
import com.google.android.apps.mytracks.util.StringUtils;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;
import com.hu.iJogging.MainZoneLayout;
import com.hu.iJogging.MotivationMainButton;
import com.hu.iJogging.SportMainButton;
import com.hu.iJogging.TextMeasuredView;
import com.hu.iJogging.ViewHistoryActivity;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class TrainingDetailFragment extends Fragment {
  View mMeasureView = null;
  private static MotivationMainButton mBtnMotivation;
  private static View btnSport;
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
  
  String totalTime;
  String totalDistance;
  String averageSpeed;
  String maxSpeed;
  
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

  }
  
  //������ʵ��onDestroyView��Ϊ�˱�֤��fragment�л���
  //ʱ��fragment��container�Ǹɾ��ģ�
  //������������������̣��п��ܻ��������fragment�ص�
  //��һ����ʾ�����
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    ViewGroup parentViewGroup = (ViewGroup) mMeasureView.getParent();
    if (parentViewGroup != null) {
      parentViewGroup.removeView(mMeasureView);
    }
  }

  private View getMeasureView(ViewGroup container) {
    //inflate�ĵ�������������Ϊfalse����������layout��rootԪ�ء���Ϊlaytout�ļ��л���root��laytout,���ʱ��ͻ���
    //container��ͻ�ˣ� ��������������The specified child already has a parent. You must call removeView() on 
    //the child's parent first��
    //���Ҫʹ��inflater(int id,View container)�����������container��Ҫ����Ϊnull�����������
    //inflater(int id,View container, boolean )�ĵ���������Ϊtrueʱ�ǵ�Ч��
    View localView = getActivity().getLayoutInflater().inflate(R.layout.workout_measure_view, container, false);
    mBtnMotivation = (MotivationMainButton) localView.findViewById(R.id.MotivationMainButton);
    mBtnMotivation.setVisibility(View.VISIBLE);
    btnSport = (LinearLayout) localView.findViewById(R.id.SportMainButton);
    btnSport.setVisibility(0);
    localView.findViewById(R.id.SportMainButtonSeperator).setVisibility(0);
    localView.findViewById(R.id.LLMainZone).setVisibility(0);
    return localView;
  }

  private void setFocus() {
    mBtnMotivation = (MotivationMainButton) mMeasureView.findViewById(R.id.MotivationMainButton);
    btnSport = (LinearLayout) mMeasureView.findViewById(R.id.SportMainButton);
    this.ivSport = ((ImageView) this.mMeasureView.findViewById(R.id.ImageButtonSport));
    this.ivSport.setVisibility(0);
    int i = getActivity().getResources().getColor(R.color.EndoGreen);
    this.ivSport.setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    this.tvSport = ((TextMeasuredView) this.mMeasureView.findViewById(R.id.tvWoSport));
    if(!isViewHistory){
      btnSport.setOnClickListener(new View.OnClickListener() {
        public void onClick(View paramView) {
          startSelectSportsFragment();
        }
      });
      ((SportMainButton)btnSport).setSport(((IJoggingActivity)mActivity).currentSport);
    }else{
      //��btnSport����Ϊ����ʷ��¼�ж�ȡ����������
      //���Ҳ��ɵ���л�
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
          return new CursorLoader(getActivity(),
              TracksColumns.CONTENT_URI,
              PROJECTION,
              null,
              null,
              TracksColumns._ID + "="+((ViewHistoryActivity)mActivity).trackId);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
          cursor.moveToFirst();
          int totalTimeIndex = cursor.getColumnIndexOrThrow(TracksColumns.TOTALTIME);
          int averageSpeedIndex = cursor.getColumnIndex(TracksColumns.AVGSPEED);
          int maxSpeedIndex = cursor.getColumnIndex(TracksColumns.MAXSPEED);
          int totalDistanceIndex = cursor.getColumnIndex(TracksColumns.TOTALDISTANCE);
          totalTime = StringUtils.formatElapsedTime(cursor.getLong(totalTimeIndex));
          totalDistance = StringUtils.formatDistance(
              getActivity(), cursor.getDouble(totalDistanceIndex), metricUnits);
          averageSpeed = StringUtils.formatSpeed(getActivity(), averageSpeedIndex, metricUnits, true);
          maxSpeed = StringUtils.formatSpeed(getActivity(), maxSpeedIndex, metricUnits, true);
          mMainZone1.setTitle(totalTime);
          mMainZone2.setTitle(totalDistance);
          mMainZone3.setTitle(averageSpeed);
          mMainZone4.setTitle(maxSpeed);
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
      });    
    }
  }
  
  
  
  private void startMapFragment(){
    MapFragment mapFragment = new MapFragment();
    FragmentTransaction ft = getFragmentManager().beginTransaction();
    ft.setCustomAnimations(R.anim.enter_workout_map, R.anim.exit_workout_map,R.anim.enter_workout_map, R.anim.exit_workout_map);
    ft.replace(R.id.fragment_container, mapFragment);
    ft.addToBackStack(null);
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
  
}
