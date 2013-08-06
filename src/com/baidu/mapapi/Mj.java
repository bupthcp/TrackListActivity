package com.baidu.mapapi;

import android.os.Bundle;
import android.util.Log;

public class Mj {
  static
  {
    try
    {
      System.loadLibrary("BMapApiEngine_v1_3_5");
    }
    catch (UnsatisfiedLinkError localUnsatisfiedLinkError)
    {
      Log.d("BMapApiEngine_v1_3_5", "BMapApiEngine_v1_3_5 library not found!");
      Log.d("BMapApiEngine_v1_3_5", localUnsatisfiedLinkError.getLocalizedMessage());
    }
  }
  
  public static native int sendBundle(Bundle paramBundle);
}
