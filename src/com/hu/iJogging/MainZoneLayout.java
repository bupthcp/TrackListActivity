package com.hu.iJogging;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MainZoneLayout extends LinearLayout{

  private View mMapView;
  private Activity mOwner;
  private Typeface mRobotoLight;
  private Typeface mRobotoRegular;
  
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
    initializeView();
  }
  
  private void initializeView(){
    
  }
}
