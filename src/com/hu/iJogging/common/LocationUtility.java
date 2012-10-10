package com.hu.iJogging.common;

import static com.google.android.apps.mytracks.Constants.MAX_LOCATION_AGE_MS;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;


public class LocationUtility {
  private final LocationManager locationManager;
  private final Context mCtx;
  private static LocationUtility mInstance=null;
  
  public LocationUtility(Context context){
    locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    mCtx = context;
  }
  
  public static LocationUtility getInstance(Context context){
    if(null == mInstance){
      mInstance = new LocationUtility(context);
    }
    return mInstance;
  }
  
  public Location getLastKnownLocation() {
    // TODO: Let's look at more advanced algorithms to determine the best
    // current location.

    Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    final long now = System.currentTimeMillis();
    if (loc == null || loc.getTime() < now - MAX_LOCATION_AGE_MS) {
      // We don't have a recent GPS fix, just use cell towers if available
      loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
    }
    return loc;
  }
}
