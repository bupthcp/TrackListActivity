package com.hu.iJogging.fragments;

import com.google.android.maps.mytracks.R;
import com.hu.iJogging.MainZoneLayout;
import com.hu.iJogging.MotivationMainButton;
import com.hu.iJogging.TextMeasuredView;

import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

  MainZoneLayout mMainZone1;
  MainZoneLayout mMainZone2;
  MainZoneLayout mMainZone3;
  MainZoneLayout mMainZone4;

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

  private void setFocus() {
    mBtnMotivation = (MotivationMainButton) mMeasureView.findViewById(R.id.MotivationMainButton);
    btnSport = (LinearLayout) mMeasureView.findViewById(R.id.SportMainButton);
    this.ivSport = ((ImageView) this.mMeasureView.findViewById(R.id.ImageButtonSport));
    this.ivSport.setVisibility(0);
    int i = getActivity().getResources().getColor(R.color.EndoGreen);
    this.ivSport.setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    this.tvSport = ((TextMeasuredView) this.mMeasureView.findViewById(R.id.tvWoSport));
    btnSport.setOnClickListener(new View.OnClickListener() {
      public void onClick(View paramView) {
        startSelectSportsFragment();
      }
    });
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
    this.mMainZone1 = new MainZoneLayout(this.getActivity(), null, 1, 1, null);
    LinearLayout localLinearLayout2 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone1);
    localLinearLayout2.removeAllViews();
    localLinearLayout2.addView(this.mMainZone1);
    this.mMainZone2 = new MainZoneLayout(this.getActivity(), null, 2, 2, null);
    LinearLayout localLinearLayout3 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone2);
    localLinearLayout3.removeAllViews();
    localLinearLayout3.addView(this.mMainZone2);
    this.mMainZone3 = new MainZoneLayout(this.getActivity(), null, 3, 3, null);
    LinearLayout localLinearLayout4 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone3);
    localLinearLayout4.removeAllViews();
    localLinearLayout4.addView(this.mMainZone3);
    this.mMainZone4 = new MainZoneLayout(this.getActivity(), null, 3, 4, null);
    LinearLayout localLinearLayout5 = (LinearLayout) this.mMeasureView
        .findViewById(R.id.LLMainZone4);
    localLinearLayout5.removeAllViews();
    localLinearLayout5.addView(this.mMainZone4);
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
    ft.setCustomAnimations(R.anim.enter_bottom, R.anim.exit_bottom,R.anim.enter_bottom, R.anim.enter_bottom);
    ft.replace(R.id.fragment_container, selectSportsFragment);
    ft.addToBackStack(null);
    ft.commit();
  }
  
}
