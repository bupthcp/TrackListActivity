package com.hu.walkingnotes;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.android.apps.mytracks.content.Waypoint;
import com.google.android.maps.mytracks.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;

import java.util.ArrayList;

public class MapOverlay extends GraphicsOverlay{
  
  private final Drawable waypointMarker;
  private final Context context;
  private boolean trackDrawingEnabled;
  private boolean showEndMarker = true;
  private Location myLocation;
  private MKRoute route = new MKRoute();
  private ArrayList<GeoPoint> points = new ArrayList<GeoPoint>();
  private MapView mMapView;
  
  private final int markerWidth, markerHeight;

  public MapOverlay(Activity activity, MapView mapView) {
    super(mapView);
    
    this.context = activity;
    mMapView = mapView;
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
    points.add(point);
    if(points.size()>2){
    //构建线
      Geometry lineGeometry = new Geometry();
      //设定折线点坐标
      GeoPoint[] linePoints = new GeoPoint[2];
      linePoints[0] = points.get(points.size()-2);
      linePoints[1] = points.get(points.size()-1);
      lineGeometry.setPolyLine(linePoints);
      //设定样式
      Symbol lineSymbol = new Symbol();
      Symbol.Color lineColor = lineSymbol.new Color();
      lineColor.red = 255;
      lineColor.green = 0;
      lineColor.blue = 0;
      lineColor.alpha = 255;
      lineSymbol.setLineSymbol(lineColor, 4);
      //生成Graphic对象
      Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
      setData(lineGraphic);
      
     //构建点
      Geometry pointGeometry = new Geometry();
      //设置坐标
      pointGeometry.setPoint(point, 2);
      //设定样式
      Symbol pointSymbol = new Symbol();
      Symbol.Color pointColor = pointSymbol.new Color();
      pointColor.red = 255;
      pointColor.green = 0;
      pointColor.blue = 0;
      pointColor.alpha = 255;
      pointSymbol.setPointSymbol(pointColor);
      //生成Graphic对象
      Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
      setData(pointGraphic);
      
//      mMapView.refresh();
    }
    
  }
  
  public void addSegmentSplit() {
  }
  
  public void addWaypoint(Waypoint wpt) {
    GeoPoint point = new GeoPoint((int) (wpt.getLocation().getLatitude() * 1E6),
        (int) (wpt.getLocation().getLongitude() * 1E6));
    OverlayItem item = new OverlayItem(point, wpt.getName(), wpt.getDescription());
    item.setMarker(waypointMarker);
//    addItem(item);
  }
  
  public void clearPoints() {
    removeAll();
  }
  
  public void clearWaypoints() {
    //TO BE DONE
  }
  
  public void setTrackDrawingEnabled(boolean trackDrawingEnabled) {
    this.trackDrawingEnabled = trackDrawingEnabled;
  }

  public void setShowEndMarker(boolean showEndMarker) {
    this.showEndMarker = showEndMarker;
  }
  
  /**
   * Sets the pointer location (will be drawn on next invalidate).
   */
  public void setMyLocation(Location myLocation) {
    this.myLocation = myLocation;
  }

  /**
   * Sets the pointer heading in degrees (will be drawn on next invalidate).
   *
   * @return true if the visible heading changed (i.e. a redraw of pointer is
   *         potentially necessary)
   */
  public boolean setHeading(float heading) {
    return true;
  }


  
}
