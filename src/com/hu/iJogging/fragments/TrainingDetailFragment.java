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
      this.mMeasureView = getMeasureView();
    }
    setFocus();
    return mMeasureView;
  }

  private View getMeasureView() {
    View localView = getActivity().getLayoutInflater().inflate(R.layout.workout_measure_view, null);
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
      public void onClick(View paramView) {}
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
    ft.add(R.id.fragment_container, mapFragment);
    ft.addToBackStack(null);
    ft.commit();
  }
  
}
