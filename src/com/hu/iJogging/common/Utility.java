package com.hu.iJogging.common;

import android.content.Context;
import android.util.TypedValue;

public class Utility {
  private static float dip;
  
  static
  {
    dip = -1.0F;
  }
  
  public static float getDip(Context paramContext)
  {
    if (dip == -1.0F)
      dip = TypedValue.applyDimension(1, 1.0F, paramContext.getResources().getDisplayMetrics());
    return dip;
  }
}
