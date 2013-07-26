package com.hu.iJogging.common;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.os.Bundle;
import android.util.Log;

public class CoordinateConvert {
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
  
  
  public static Bundle fromWgs84ToBaidu(GeoPoint paramGeoPoint)
  {
    return a(paramGeoPoint, 1);
  }

  public static Bundle fromGcjToBaidu(GeoPoint paramGeoPoint)
  {
    return a(paramGeoPoint, 2);
  }

  public static GeoPoint bundleDecode(Bundle paramBundle)
  {
    GeoPoint localGeoPoint = new GeoPoint(0, 0);
    String str1 = paramBundle.getString("x");
    String str2 = paramBundle.getString("y");
    char[] arrayOfChar1 = c.b(str1.toCharArray());
    str1 = String.valueOf(arrayOfChar1);
    char[] arrayOfChar2 = c.b(str2.toCharArray());
    str2 = String.valueOf(arrayOfChar2);
    int i = Integer.decode(str1).intValue();
    int j = Integer.decode(str2).intValue();
    localGeoPoint.setLongitudeE6(i);
    localGeoPoint.setLatitudeE6(j);
    return localGeoPoint;
  }

  static Bundle a(GeoPoint paramGeoPoint, int paramInt)
  {
    Bundle localBundle = new Bundle();
    localBundle.putInt("act", 15010250);
    localBundle.putInt("x", paramGeoPoint.getLongitudeE6());
    localBundle.putInt("y", paramGeoPoint.getLatitudeE6());
    localBundle.putInt("t", paramInt);
    sendBundle(localBundle);
    int i = localBundle.getInt("x");
    int j = localBundle.getInt("y");
    String str = String.valueOf(i);
    char[] arrayOfChar = c.a(str.toCharArray());
    str = String.valueOf(arrayOfChar);
    localBundle.remove("x");
    localBundle.putString("x", str);
    str = String.valueOf(j);
    arrayOfChar = c.a(str.toCharArray());
    str = String.valueOf(arrayOfChar);
    localBundle.remove("y");
    localBundle.putString("y", str);
    return localBundle;
  }
  
  
  public static native int sendBundle(Bundle paramBundle);
}
