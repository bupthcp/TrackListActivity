package com.hu.walkingnotes;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKRoute;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.android.apps.mytracks.Constants;
import com.google.android.apps.mytracks.util.LocationUtils;
import com.google.android.apps.mytracks.util.UnitConversions;
import com.hu.iJogging.R;
import com.hu.iJogging.content.Waypoint;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.util.Log;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MapOverlay extends GraphicsOverlay{
  
  private final static String TAG = MapOverlay.class.getSimpleName();
  
  public static final float WAYPOINT_X_ANCHOR = 13f / 48f;

  private static final float WAYPOINT_Y_ANCHOR = 43f / 48f;
  private static final float MARKER_X_ANCHOR = 50f / 96f;
  private static final float MARKER_Y_ANCHOR = 90f / 96f;
  
  private final Drawable waypointMarker;
  private final Context context;
  private boolean trackDrawingEnabled;
  private boolean showEndMarker = true;
  private Location myLocation;
  private MKRoute route = new MKRoute();
  private MapView mMapView;
  private GeoPoint lastPoint = null;
  
  private final int markerWidth, markerHeight;
  private final BlockingQueue<GeoPoint> pendingLocations;
  private GeoPoint[] locations = new GeoPoint[0];

  public MapOverlay(Activity activity, MapView mapView) {
    super(mapView);
    
    this.context = activity;
    mMapView = mapView;
    final Resources resources = context.getResources();
    waypointMarker = resources.getDrawable(R.drawable.blue_pushpin);
    markerWidth = waypointMarker.getIntrinsicWidth();
    markerHeight = waypointMarker.getIntrinsicHeight();
    waypointMarker.setBounds(0, 0, markerWidth, markerHeight);
    this.pendingLocations = new ArrayBlockingQueue<GeoPoint>(
        Constants.MAX_DISPLAYED_TRACK_POINTS, true);
  }
  
  /**
   * A pre-processed {@link Location} to speed up drawing.
   * 
   * @author Jimmy Shih
   */
  public static class CachedLocation {

    private final boolean valid;
    private final GeoPoint geoPoint;
    private final int speed;

    /**
     * Constructor for an invalid cached location.
     */
    public CachedLocation() {
      this.valid = false;
      this.geoPoint = null;
      this.speed = -1;
    }

    /**
     * Constructor for a potentially valid cached location.
     */
    public CachedLocation(Location location) {
      this.valid = LocationUtils.isValidLocation(location);
      this.geoPoint = valid ? new GeoPoint((int) (location.getLatitude() * 1E6),(int) (location.getLongitude() * 1E6)) : null;
      this.speed = (int) Math.floor(location.getSpeed() * UnitConversions.MS_TO_KMH);
    }

    /**
     * Returns true if the location is valid.
     */
    public boolean isValid() {
      return valid;
    }

    /**
     * Gets the speed in kilometers per hour.
     */
    public int getSpeed() {
      return speed;
    }

    /**
     * Gets the LatLng.
     */
    public GeoPoint getGeoPoint() {
      return geoPoint;
    }
  };
  
  /**
   * Add a location to the map overlay.
   *
   * NOTE: This method doesn't take ownership of the given location, so it is
   * safe to reuse the same location while calling this method.
   *
   * @param l the location to add.
   */
  public void addLocation(Location location) {
    // Queue up in the pendingLocations until it's merged with locations
    boolean valid = LocationUtils.isValidLocation(location);
    if(valid == false)
      return;
    GeoPoint point = new GeoPoint((int) (location.getLatitude() * 1E6),
      (int) (location.getLongitude() * 1E6));
    if (!pendingLocations.offer(point)) {
      Log.e(TAG, "Unable to add to pendingLocations.");
    }
//    GeoPoint point = new GeoPoint((int) (l.getLatitude() * 1E6),
//        (int) (l.getLongitude() * 1E6));
////    Log.d(TAG,"location is: " +l.toString());
//    if(lastPoint != null){
//    //构建线
//      Geometry lineGeometry = new Geometry();
//      //设定折线点坐标
//      GeoPoint[] linePoints = new GeoPoint[2];
//      linePoints[0] = lastPoint;
//      linePoints[1] = point;
//      lineGeometry.setPolyLine(linePoints);
//      //设定样式
//      Symbol lineSymbol = new Symbol();
//      Symbol.Color lineColor = lineSymbol.new Color();
//      lineColor.red = 255;
//      lineColor.green = 0;
//      lineColor.blue = 0;
//      lineColor.alpha = 255;
//      lineSymbol.setLineSymbol(lineColor, 4);
//      //生成Graphic对象
//      Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
////      Log.d(TAG,"lineGraphic is: " +lineGraphic.toString());
//      setData(lineGraphic);
//    }
//      
//    //构建点
//    Geometry pointGeometry = new Geometry();
//    //设置坐标
//    pointGeometry.setPoint(point, 2);
//    //设定样式
//    Symbol pointSymbol = new Symbol();
//    Symbol.Color pointColor = pointSymbol.new Color();
//    pointColor.red = 255;
//    pointColor.green = 0;
//    pointColor.blue = 0;
//    pointColor.alpha = 255;
//    pointSymbol.setPointSymbol(pointColor);
//    //生成Graphic对象
//    Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
////    Log.d(TAG,"pointGraphic is: " +pointGraphic.toString());
//    setData(pointGraphic);
//    lastPoint = point;
  }
  
  public void addSegmentSplit() {
  }
  
  public void update(){
    //解决了每次锁屏-解锁之后，会出现轨迹首尾相连，并且轨迹变粗的现象
    //(实际上就是每次解锁都会再画一条轨迹)
    //锁屏-解锁操作不会导致MapOverlay对象被销毁，所以pendingLocations
    //里面的内容还是存在的，解锁后,MapFragment会向MapOverlay中添加新一次
    //的查询结果，所以在地图上看起来就是首尾相连，轨迹变粗，实际上就是将
    //轨迹画了两遍乃至更多遍
    //解决的方法也很简单，就是在toArray之后，clear一下pendingLocations就可以
    //了。
    //原版的MyTracks方法用的是pendingLocations.drainTo，这个方法在导出队列
    //中的数据时，就会清空队列，所以原版没有问题。
      locations = pendingLocations.toArray(locations);
//      printLocation(locations);
      pendingLocations.clear();
      
      Geometry lineGeometry = new Geometry();
      lineGeometry.setPolyLine(locations);
      Symbol lineSymbol = new Symbol();
      Symbol.Color lineColor = lineSymbol.new Color();
      lineColor.red = 255;
      lineColor.green = 0;
      lineColor.blue = 0;
      lineColor.alpha = 255;
      lineSymbol.setLineSymbol(lineColor, 4);
      Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);
      setData(lineGraphic);
  }
  
  void printLocation(GeoPoint[] points){
    for(int i=0;i<points.length;i++){
      GeoPoint point = points[i];
      int lati = point.getLatitudeE6();
      int longti = point.getLongitudeE6();
      Log.d(TAG,"locations updated:"+Integer.toString(lati)+" "+Integer.toString(longti));
    }
  }
  
  public void addWaypoint(Waypoint wpt) {
    GeoPoint point = new GeoPoint((int) (wpt.getLocation().getLatitude() * 1E6),
        (int) (wpt.getLocation().getLongitude() * 1E6));
    OverlayItem item = new OverlayItem(point, wpt.getName(), wpt.getDescription());
    item.setMarker(waypointMarker);
//    addItem(item);
  }
  
  public void clearPoints() {
    Log.d(TAG,"clearPoints");
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
