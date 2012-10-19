package com.hu.iJogging;

import com.google.android.maps.mytracks.R;

import android.content.Context;

public class FormatterMetric extends FormatterUnits{
  private static final int descStringId = 0;
  private static final float metersPerKM = 1000.0F;
  
  public String getDistanceTextShort(Context paramContext)
  {
    return paramContext.getResources().getString(R.string.strKilometerShortUnit);
  }
  
  public String getDistanceText(Context paramContext)
  {
    return paramContext.getApplicationContext().getString(R.string.strDistanceFormat);
  }
  
  public String getDistanceMeterText(Context paramContext)
  {
    return paramContext.getApplicationContext().getString(R.string.strDistanceMeterFormat);
  }
  
  public String getSpeedText(Context paramContext)
  {
    return paramContext.getApplicationContext().getString(R.string.strKmH);
  }
}
