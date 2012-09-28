package com.hu.iJogging;

import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
public class WorkoutPage {
  View mMeasureView = null;
  MainActivity mOwner = null;
  private static MotivationMainButton mBtnMotivation;
  private static View btnSport;
  ImageView btnMusic;
  ImageView imageGPS;
  ImageView ivSport;
  private Handler mSettingsChangeHandler;
  private DashBoardStretchSpace mSpace1;
  TextView tvGPS;
  TextMeasuredView tvSport;
  
  MainZoneLayout mMainZone1;
  MainZoneLayout mMainZone2;
  MainZoneLayout mMainZone3;
  MainZoneLayout mMainZone4;
  
  
  public WorkoutPage(MainActivity paramActivity)
  {
    this.mOwner = paramActivity;
  }
  
  public void setView()
  {
    if (this.mMeasureView == null)
      this.mMeasureView = getMeasureView(this.mOwner);
    this.mOwner.setContentView(this.mMeasureView);
  }
  
  public static void setFakeView(Activity paramActivity)
  {
    View localView = paramActivity.getLayoutInflater().inflate(R.layout.workout_splash, null);
    mBtnMotivation = (MotivationMainButton)localView.findViewById(R.id.MotivationMainButton);
    btnSport = (LinearLayout)localView.findViewById(R.id.SportMainButton);
    paramActivity.setContentView(localView);
  }
  
  private static View getMeasureView(Activity paramActivity)
  {
    View localView = paramActivity.getLayoutInflater().inflate(R.layout.workout_measure_view, null);
    mBtnMotivation = (MotivationMainButton)localView.findViewById(R.id.MotivationMainButton);
    mBtnMotivation.setVisibility(View.VISIBLE);
    btnSport = (LinearLayout)localView.findViewById(R.id.SportMainButton);
    btnSport.setVisibility(0);
    localView.findViewById(R.id.SportMainButtonSeperator).setVisibility(0);
    localView.findViewById(R.id.LLMainZone).setVisibility(0);
    return localView;
  }
  
  public void setFocus(){
    //TODO
    //add mMainZone1 and different zone here
    this.ivSport = ((ImageView)this.mMeasureView.findViewById(R.id.ImageButtonSport));
    this.ivSport.setVisibility(0);
    int i = this.mOwner.getResources().getColor(R.color.EndoGreen);
    this.ivSport.setColorFilter(i, PorterDuff.Mode.SRC_ATOP);
    this.tvSport = ((TextMeasuredView)this.mMeasureView.findViewById(R.id.tvWoSport));
    btnSport.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        WorkoutPage.this.startSportDialog();
      }
    });
    this.imageGPS = ((ImageView)this.mMeasureView.findViewById(R.id.ImageViewGPS));
    this.tvGPS = ((TextView)this.mMeasureView.findViewById(R.id.TextViewGPS));
    Typeface localTypeface = Typeface.createFromAsset(this.mOwner.getAssets(), "fonts/Roboto-Regular.ttf");
    this.tvGPS.setTypeface(localTypeface);
    mBtnMotivation.setOnClickListener(new View.OnClickListener()
    {
      public void onClick(View paramView)
      {
        WorkoutPage.this.startMotivationDialog();
      }
    });
    ((Button)this.mMeasureView.findViewById(R.id.ButtonMapCorner)).setOnTouchListener(new View.OnTouchListener()
    {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent)
      {
        if (paramMotionEvent.getAction() == 0)
          WorkoutPage.this.startMapsActivity();
        return true;
      }
    });
    this.mSpace1 = ((DashBoardStretchSpace)this.mMeasureView.findViewById(R.id.space1));
    LinearLayout localLinearLayout1 = (LinearLayout)this.mMeasureView.findViewById(R.id.LLIntervalsZone);
    localLinearLayout1.removeAllViews();
//    this.mIntervalZone = new IntervalZone(this.mOwner, null);
//    this.mIntervalZone.setSpaceLL(this.mSpace1);
//    localLinearLayout1.addView(this.mIntervalZone);
    this.mMainZone1 = new MainZoneLayout(this.mOwner, null, 1, 1, null);
    LinearLayout localLinearLayout2 = (LinearLayout)this.mMeasureView.findViewById(R.id.LLMainZone1);
    localLinearLayout2.removeAllViews();
    localLinearLayout2.addView(this.mMainZone1);
    this.mMainZone2 = new MainZoneLayout(this.mOwner, null, 1,1, null);
    LinearLayout localLinearLayout3 = (LinearLayout)this.mMeasureView.findViewById(R.id.LLMainZone2);
    localLinearLayout3.removeAllViews();
    localLinearLayout3.addView(this.mMainZone2);
  }
  
  private void startMotivationDialog()
  {
  }

  private void startSportDialog()
  {
  }
  
  private void startMapsActivity()
  {

  }
}
