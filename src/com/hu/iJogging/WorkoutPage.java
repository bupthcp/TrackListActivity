package com.hu.iJogging;

import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.view.View;
import android.widget.LinearLayout;
public class WorkoutPage {
  View mMeasureView = null;
  MainActivity mOwner = null;
  private static MotivationMainButton mBtnMotivation;
  private static View btnSport;
  
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
  }
}
