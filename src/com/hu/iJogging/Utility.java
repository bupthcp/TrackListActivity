package com.hu.iJogging;

import android.content.Context;
import android.util.TypedValue;

public class Utility {
  private static float dip;
  
  public static float getDip(Context paramContext)
  {
    if (dip == -1.0F)
      dip = TypedValue.applyDimension(1, 1.0F, paramContext.getResources().getDisplayMetrics());
    return dip;
  }
}
