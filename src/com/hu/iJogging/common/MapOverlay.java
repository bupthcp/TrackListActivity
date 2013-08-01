package com.hu.iJogging.common;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.RouteOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.android.apps.mytracks.content.Waypoint;
import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;

public class MapOverlay extends RouteOverlay{
  
  private final Drawable waypointMarker;
  private final Context context;
  
  private final int markerWidth, markerHeight;

  public MapOverlay(Activity activity, MapView mapView) {
    super(activity, mapView);
    
    this.context = activity;
    final Resources resources = context.getResources();
    waypointMarker = resources.getDrawable(R.drawable.blue_pushpin);
    markerWidth = waypointMarker.getIntrinsicWidth();
    markerHeight = waypointMarker.getIntrinsicHeight();
    waypointMarker.setBounds(0, 0, markerWidth, markerHeight);
  }
  
  /**
   * Add a location to the map overlay.
   *
   * NOTE: This method doesn't take ownership of the given location, so it is
   * safe to reuse the same location while calling this method.
   *
   * @param l the location to add.
   */
  public void addLocation(Location l) {
    GeoPoint point = new GeoPoint((int) (l.getLatitude() * 1E6),
        (int) (l.getLongitude() * 1E6));
    OverlayItem item = new OverlayItem(point, null, null);
    addItem(item);
  }
  
  public void addSegmentSplit() {
  }
  
  public void addWaypoint(Waypoint wpt) {
    GeoPoint point = new GeoPoint((int) (wpt.getLocation().getLatitude() * 1E6),
        (int) (wpt.getLocation().getLongitude() * 1E6));
    OverlayItem item = new OverlayItem(point, wpt.getName(), wpt.getDescription());
    item.setMarker(waypointMarker);
    addItem(item);
  }
  
  public void clearPoints() {
    removeAll();
  }
  
  public void clearWaypoints() {
    //TO BE DONE
  }

}
