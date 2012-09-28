package com.hu.iJogging;

import com.google.android.maps.mytracks.R;
import com.hu.iJogging.common.UIConfig;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainZoneLayout extends LinearLayout{

  private View mMapView;
  private Activity mOwner;
  private Typeface mRobotoLight;
  private Typeface mRobotoRegular;
  
  private TextMeasuredView mTitle;
  private ImageView mTitleIcon;
  private ImageView mTriangle;
  private TextMeasuredView mUnit;
  private TextMeasuredView mValue;
  private static FormatterUnits mFormatterUnits;
  
  public int mType;
  private int mZone;
  
  public MainZoneLayout(Activity paramActivity, AttributeSet paramAttributeSet, int paramInt1, int paramInt2, View paramView)
  {
    super(paramActivity, paramAttributeSet);
    this.mOwner = paramActivity;
    this.mType = paramInt2;
    this.mZone = paramInt1;
    this.mMapView = paramView;
    //mFormatterUnits = FormatterUnits.getFormatter();
    this.mRobotoLight = Typeface.createFromAsset(this.mOwner.getAssets(), "fonts/Roboto-Light.ttf");
    this.mRobotoRegular = Typeface.createFromAsset(this.mOwner.getAssets(), "fonts/Roboto-Regular.ttf");
    mFormatterUnits = FormatterUnits.getFormatter();
    initializeView();
  }
  
  private void initializeView(){
    setupDefaultView();
  }
  
  private void setupDefaultView(){
    LinearLayout localLinearLayout = (LinearLayout)((LayoutInflater)this.mOwner.getSystemService("layout_inflater")).inflate(R.layout.default_workout_zone34_item_view, this);
    this.mTitleIcon = ((ImageView)localLinearLayout.findViewById(R.id.TVmainZoneIcon));
    this.mTitle = ((TextMeasuredView)localLinearLayout.findViewById(R.id.TVmainZoneTitle));
    this.mTitle.setTypeface(this.mRobotoLight);
    this.mValue = ((TextMeasuredView)localLinearLayout.findViewById(R.id.TVmainZoneValue));
    this.mValue.setTypeface(this.mRobotoRegular);
    this.mUnit = ((TextMeasuredView)localLinearLayout.findViewById(R.id.TVmainZoneUnit));
    this.mUnit.setTypeface(this.mRobotoLight);
    this.mTriangle = ((ImageView)localLinearLayout.findViewById(R.id.ImageDbMoreTriangle));
    this.mTriangle.setImageResource(UIConfig.DashboardConfig.triangleRestId);
    setTitle();
  }
  
  private void setTitle(){
    switch(mType){
      case 1:
        setTwoLinesText(R.drawable.dashboard_distance_icon, this.mOwner.getString(R.string.strDistance), mFormatterUnits.getDistanceText(this.mOwner));
        break;
      case 2:
        break;
      default:
        break;
    }
  }
  
  //paramInt:icon
  //paramString1:
  //paramString2:
  private void setTwoLinesText(int paramInt, String paramString1, String paramString2){
    if (paramInt >= 0){
      this.mTitleIcon.setImageResource(paramInt);
      this.mTitleIcon.setVisibility(View.VISIBLE);
      this.mTitle.setText(paramString1.toUpperCase());
      this.mUnit.setText(paramString2);
      this.mUnit.setVisibility(View.VISIBLE);
    }
  }
}
