package com.hu.walkingnotes.baidumaps;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKOfflineMap;
import com.baidu.mapapi.map.MKOfflineMapListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Overlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.content.TrackDataListener;
import com.google.android.apps.mytracks.content.TrackDataType;
import com.google.android.apps.mytracks.stats.TripStatistics;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.apps.mytracks.util.GeoRect;
import com.google.android.apps.mytracks.util.LocationUtils;
import com.hu.iJogging.R;
import com.hu.iJogging.common.LocationUtility;
import com.hu.iJogging.content.MyTracksProviderUtils;
import com.hu.iJogging.content.MyTracksProviderUtils.Factory;
import com.hu.iJogging.content.Track;
import com.hu.iJogging.content.Waypoint;
import com.hu.walkingnotes.ui.tracks.TrackDetailActivity;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class MapFragment extends Fragment
implements View.OnTouchListener, View.OnClickListener, TrackDataListener{
  public static final String MAP_FRAGMENT_TAG = "MapFragment";
  Activity mActivity;
  Boolean needLocationListener = false;
  
  private Handler mapFragmentHandler= new Handler();
  
  private static final String KEY_CURRENT_LOCATION = "currentLocation";
  private static final String KEY_KEEP_MY_LOCATION_VISIBLE = "keepMyLocationVisible";

  private TrackDataHub trackDataHub;

  // True to keep my location visible.
  private boolean keepMyLocationVisible = true;

  // True to zoom to my location. Only apply when keepMyLocationVisible is true.
  private boolean zoomToMyLocation;

  // The track id of the marker to show.
  private long markerTrackId;

  // The marker id to show
  private long markerId;

  // The current selected track id. Set in onSelectedTrackChanged.
  private long currentSelectedTrackId;

  // The current location. Set in onCurrentLocationChanged.
  private Location currentLocation;

  // UI elements
  private View mapViewContainer;
  private MapView mapView;
  private MapOverlay mapOverlay;
  private MyLocationMapOverlay myLocationOverlay;
  private ImageButton myLocationImageButton;
  private TextView messageTextView;
  private MKOfflineMap mOffline = null;
  
  //Current paths
   private ArrayList<PolyLine> paths = new ArrayList<PolyLine>();
  private boolean reloadPaths = true;
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    mActivity = activity;
  }
  

  @Override
  public void onCreate(Bundle bundle) {
    super.onCreate(bundle);
    setHasOptionsMenu(true);
  }

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    mapViewContainer = getActivity().getLayoutInflater().inflate(R.layout.map_fragment_baidu, container,false);
    mapView = (MapView) mapViewContainer.findViewById(R.id.map_view);

    
    mapOverlay = new MapOverlay(getActivity(),mapView);
    myLocationOverlay = new MyLocationMapOverlay(getActivity(),mapView);
    
    List<Overlay> overlays = mapView.getOverlays();
    overlays.clear();
    overlays.add(mapOverlay);
    overlays.add(myLocationOverlay);
    myLocationOverlay.enableCompass();
    myLocationOverlay.setMarker(null);
    
    mapView.requestFocus();
    mapView.setOnTouchListener(this);
    mapView.setBuiltInZoomControls(false);
    
    mOffline = new MKOfflineMap();    
    mOffline.init(mapView.getController(), new MKOfflineMapListener(){
      @Override
      public void onGetOfflineMapState(int arg0, int arg1) {
        // TODO Auto-generated method stub
        
      }});
    mOffline.scan();
    
    myLocationImageButton = (ImageButton) mapViewContainer.findViewById(R.id.map_my_location);
    myLocationImageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showMyLocation();       
      }
    });
    messageTextView = (TextView) mapViewContainer.findViewById(R.id.map_message);

    ApiAdapterFactory.getApiAdapter().invalidMenu(getActivity());
    
    mapView.refresh();
    return mapViewContainer;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (savedInstanceState != null) {
      keepMyLocationVisible = savedInstanceState.getBoolean(KEY_KEEP_MY_LOCATION_VISIBLE, false);
      currentLocation = (Location) savedInstanceState.getParcelable(KEY_CURRENT_LOCATION);
      if (currentLocation != null) {
        updateCurrentLocation();
      }
    }
    needLocationListener = ((TrackDetailActivity)mActivity).getNeedLocationListener();
  }

  @Override
  public void onResume() {
    super.onResume();
    myLocationImageButton.setVisibility(View.VISIBLE);
    mapView.onResume();
    resumeTrackDataHub();
    initMapCenter();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putBoolean(KEY_KEEP_MY_LOCATION_VISIBLE, keepMyLocationVisible);
    if (currentLocation != null) {
      outState.putParcelable(KEY_CURRENT_LOCATION, currentLocation);
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    pauseTrackDataHub();
    mapView.onPause();
  }

  //在这里实现onDestroyView是为了保证在fragment切换的
  //时候，fragment的container是干净的，
  //如果不加上这个清理过程，有可能会出现两个fragment重叠
  //在一起显示的情况
  @Override
  public void onDestroyView() {
    super.onDestroyView();
    mOffline.destroy();
    mapView.destroy();
    ViewGroup parentViewGroup = (ViewGroup) mapViewContainer.getParent();
    if (parentViewGroup != null) {
      parentViewGroup.removeView(mapViewContainer);
    }
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflator) {
    menuInflator.inflate(R.menu.map, menu);
  }

  @Override
  public void onPrepareOptionsMenu(Menu menu) {
    int titleId = R.string.menu_satellite_mode;
    if (mapView != null) {
      titleId = mapView.isSatellite() ? R.string.menu_map_mode : R.string.menu_satellite_mode;
    }
    menu.findItem(R.id.map_satellite_mode).setTitle(titleId);
    super.onPrepareOptionsMenu(menu);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem menuItem) {
    if (mapView != null && menuItem.getItemId() == R.id.map_satellite_mode) {
      mapView.setSatellite(!mapView.isSatellite());
      return true;
    }
    return super.onOptionsItemSelected(menuItem);
  }

  /**
   * Shows my location.
   */
  private void showMyLocation() {
    updateTrackDataHub();
    keepMyLocationVisible = true;
    zoomToMyLocation = true;
    if (currentLocation != null) {
      updateCurrentLocation();
    }
  }

  /**
   * Shows the marker.
   * 
   * @param id the marker id
   */
  private void showMarker(long id) {
    MyTracksProviderUtils MyTracksProviderUtils = Factory.get(getActivity());
    Waypoint waypoint = MyTracksProviderUtils.getWaypoint(id);
    if (waypoint != null && waypoint.getLocation() != null) {
      keepMyLocationVisible = false;
      GeoPoint center = new GeoPoint((int) (waypoint.getLocation().getLatitude() * 1E6),
          (int) (waypoint.getLocation().getLongitude() * 1E6));
      mapView.getController().setCenter(center);
      mapView.getController().setZoom(mapView.getMaxZoomLevel());
      mapView.invalidate();
    }
  }

  /**
   * Shows the marker.
   *
   * @param trackId the track id
   * @param id the marker id
   */
  public void showMarker(long trackId, long id) {
    /*
     * Synchronize to prevent race condition in changing markerTrackId and
     * markerId variables.
     */
    synchronized (this) {
      if (trackId == currentSelectedTrackId) {
        showMarker(id);
        markerTrackId = -1L;
        markerId = -1L;
        return;
      }
      markerTrackId = trackId;
      markerId = id;
    }
  }

  @Override
  public boolean onTouch(View view, MotionEvent event) {
    if (keepMyLocationVisible && event.getAction() == MotionEvent.ACTION_MOVE) {
      if (!isVisible(currentLocation)) {
        /*
         * Only set to false when no longer visible. Thus can keep showing the
         * current location with the next location update.
         */
        keepMyLocationVisible = false;
      }
    }
    return false;
  }

  @Override
  public void onClick(View v) {
    if (v == messageTextView) {
      Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
      startActivity(intent);
    }
  }

//  @Override
//  public void onProviderStateChange(ProviderState state) {
//    final int messageId;
//    final boolean isGpsDisabled;
//    //如果是查看界面，则不需要显示gps的警告信息
//    if(isViewHistory)
//      return;
//    switch (state) {
//      case DISABLED:
//        messageId = R.string.gps_need_to_enable;
//        isGpsDisabled = true;
//        break;
//      case NO_FIX:
//      case BAD_FIX:
//        messageId = R.string.gps_wait_for_signal;
//        isGpsDisabled = false;
//        break;
//      case GOOD_FIX:
//        messageId = -1;
//        isGpsDisabled = false;
//        break;
//      default:
//        throw new IllegalArgumentException("Unexpected state: " + state);
//    }

//    getActivity().runOnUiThread(new Runnable() {
//      @Override
//      public void run() {
//        if (messageId != -1) {
//          messageTextView.setText(messageId);
//          messageTextView.setVisibility(View.VISIBLE);
//          myLocationImageButton.setVisibility(View.VISIBLE);
//
//          if (isGpsDisabled) {
//            Toast.makeText(getActivity(), R.string.gps_not_found, Toast.LENGTH_LONG).show();
//
//            // Click to show the location source settings
//            messageTextView.setOnClickListener(MapFragment.this);
//          } else {
//            messageTextView.setOnClickListener(null);
//          }
//        } else {
//          messageTextView.setVisibility(View.GONE);
//        }
//      }
//    });
//  }

  @Override
  public void onLocationChanged(Location location) {
    if(location != null){
      currentLocation = location;
      updateCurrentLocation();
    }
  }

  @Override
  public void onHeadingChanged(final double heading) {
    if(isResumed()){
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          if (myLocationOverlay.setHeading((float) heading)) {
            myLocationOverlay.setMarker(null);
            mapView.refresh();
          }
        }

      });
    }
  }

  @Override
  public void onSelectedTrackChanged(final Track track) {
    if(isResumed()){
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          boolean hasTrack = track != null;
//          mapOverlay.setTrackDrawingEnabled(hasTrack);

          if (hasTrack) {
            synchronized (this) {
              /*
               * Synchronize to prevent race condition in changing markerTrackId
               * and markerId variables.
               */
              currentSelectedTrackId = track.getId();
              updateMap(track);
            }
            // mapOverlay.setShowEndMarker(!isRecording);
          }
          mapView.refresh();
        }
      });
    }
  }

  @Override
  public void onTrackUpdated(Track track) {
    // We don't care.
  }

  @Override
  public void clearTrackPoints() {
    //clear points操作的发起都是在ListenerThread上进行的
    //我们可以查看loadNewDataForListener的操作顺序，如果是
    //reloadAll的话，会先调用clearTrackPoints，然后再进行
    //数据的加载，这是一个串行的过程。我之前犯的错误就是将
    //clearTrackPoints的动作又转交给了UiThread去进行，所以
    //造成了clearTrackPoints不是在数据加载之前执行完毕，而是
    //在数据加载的过程中执行，这样的话，就会造成一部分路径不
    //不会显示出来
//    getActivity().runOnUiThread(new Runnable(){
//      @Override
//      public void run() {
//        mapOverlay.clearPoints();
//      }
//    });
    mapOverlay.clearPoints();
    reloadPaths = true;
  }

  @Override
  public void onSampledInTrackPoint(final Location location) {
    if(isResumed()){
      if (LocationUtils.isValidLocation(location)) {
        LocationUtils.setGeoInLocation(location);
        mapOverlay.addLocation(location);
        Log.d(MAP_FRAGMENT_TAG, "onSampledInTrackPoint");
      }
    }
  }

  @Override
  public void onSampledOutTrackPoint(Location loc) {
    // We don't care.
  }


  @Override
  public void onNewTrackPointsDone() {
    if(isResumed()){
      getActivity().runOnUiThread(new Runnable() {
        @Override
        public void run() {
          mapOverlay.update(paths,reloadPaths);
          Log.d(MAP_FRAGMENT_TAG, "onNewTrackPointsDone");
          mapView.refresh();
          reloadPaths = false;
        }
      });
    }
  }
  
  @Override
  public void onLocationStateChanged(LocationState locationState) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public void onSegmentSplit(Location location) {
    // TODO Auto-generated method stub
    
  }

  @Override
  public boolean onMetricUnitsChanged(boolean metricUnits) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public boolean onMinRecordingDistanceChanged(int minRecordingDistance) {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public void clearWaypoints() {
    mapOverlay.clearWaypoints();
  }

  @Override
  public void onNewWaypoint(Waypoint waypoint) {
    if (waypoint != null && LocationUtils.isValidLocation(waypoint.getLocation())) {
      // TODO: Optimize locking inside addWaypoint
      Location locationTmp = waypoint.getLocation();
      LocationUtils.setGeoInLocation(locationTmp);
      waypoint.setLocation(locationTmp);
      mapOverlay.addWaypoint(waypoint);
    }
  }

  @Override
  public void onNewWaypointsDone() {
    getActivity().runOnUiThread(new Runnable(){
      @Override
      public void run() {
        mapView.refresh();
      }
    });
  }


  @Override
  public boolean onReportSpeedChanged(boolean reportSpeed) {
    // We don't care.
    return false;
  }
  
  /**
   * Resumes the trackDataHub. Needs to be synchronized because trackDataHub can be
   * accessed by multiple threads.
   */
  private synchronized void resumeTrackDataHub() {
    trackDataHub = ((TrackDetailActivity)getActivity()).getTrackDataHub();
    //如果是查看界面，不需要启动gps信息，所以就不用注册location相关的listener了，
    //也就不会触发gps
    if(needLocationListener){
      trackDataHub.registerTrackDataListener(this, EnumSet.of(
        TrackDataType.SELECTED_TRACK,
        TrackDataType.WAYPOINTS_TABLE,
        TrackDataType.SAMPLED_IN_TRACK_POINTS_TABLE,
        TrackDataType.LOCATION,
        TrackDataType.HEADING));
    }else{
      trackDataHub.registerTrackDataListener(this, EnumSet.of(
          TrackDataType.SELECTED_TRACK,
          TrackDataType.WAYPOINTS_TABLE,
          TrackDataType.SAMPLED_IN_TRACK_POINTS_TABLE,
          TrackDataType.HEADING));
    }
  }
  
  /**
   * Pauses the trackDataHub. Needs to be synchronized because trackDataHub can be
   * accessed by multiple threads. 
   */
  private synchronized void pauseTrackDataHub() {
    trackDataHub.unregisterTrackDataListener(this);
    trackDataHub = null;
  }

  /**
   * Updates the trackDataHub. Needs to be synchronized because trackDataHub can be
   * accessed by multiple threads. 
   */
  private synchronized void updateTrackDataHub() {
    if (trackDataHub != null) {
      trackDataHub.forceUpdateLocation();
    }
  }
  
  /**
   * Updates the map by either zooming to the requested marker or showing the track.
   *
   * @param track the track
   */
  private void updateMap(Track track) {
    if (track.getId() == markerTrackId) {
      // Show the marker
      showMarker(markerId);

      markerTrackId = -1L;
      markerId = -1L;
    } else {
      // Show the track
      showTrack(track);
    }
  }

  /**
   * Returns true if the location is visible.
   *
   * @param location the location
   */
  private boolean isVisible(Location location) {
    if (location == null || mapView == null) {
      return false;
    }
    GeoPoint mapCenter = mapView.getMapCenter();
    int latitudeSpan = mapView.getLatitudeSpan();
    int longitudeSpan = mapView.getLongitudeSpan();
  
    /*
     * The bottom of the mapView is obscured by the zoom controls, subtract its
     * height from the visible area.
     */
    GeoPoint zoomControlBottom = mapView.getProjection().fromPixels(0, mapView.getHeight());
    GeoPoint zoomControlTop = mapView.getProjection().fromPixels(
        0, mapView.getHeight() );
    int zoomControlMargin = Math.abs(zoomControlTop.getLatitudeE6()
        - zoomControlBottom.getLatitudeE6());
    GeoRect geoRect = new GeoRect(mapCenter, latitudeSpan, longitudeSpan);
    geoRect.top += zoomControlMargin;
  
    GeoPoint geoPoint = LocationUtils.getGeoPoint(location);
    return geoRect.contains(geoPoint);
  }

  /**
   * Updates the current location and centers it if necessary.
   */
  private void updateCurrentLocation() {
    if (mapOverlay == null || mapView == null) {
      return;
    }

    Runnable updateRunnable = new Runnable() {
      public void run() {
        final Location locationTmp = new Location(currentLocation);
        LocationUtils.setGeoInLocation(locationTmp);

        LocationData locData = new LocationData();
        locData.latitude = locationTmp.getLatitude();
        locData.longitude = locationTmp.getLongitude();
        myLocationOverlay.setData(locData);
        mapView.refresh();

        if (locationTmp != null && keepMyLocationVisible && !isVisible(locationTmp)) {
          GeoPoint geoPoint = LocationUtils.getGeoPoint(locationTmp);
          MapController mapController = mapView.getController();
          mapController.animateTo(geoPoint);
          if (zoomToMyLocation) {
            // Only zoom in the first time we show the location.
            zoomToMyLocation = false;
            if (mapView.getZoomLevel() < mapView.getMaxZoomLevel()) {
              mapController.setZoom(mapView.getMaxZoomLevel());
            }
          }
        }
      }
    };
    mapFragmentHandler.post(updateRunnable);
  }

  /**
   * Shows the track.
   * 将一条路线的跨越的经纬度范围框出来，然后将整个路线呈现在这个框里
   * @param track the track
   */
  private void showTrack(Track track) {
    if (mapView == null || track == null || track.getNumberOfPoints() < 2) {
      return;
    }

    TripStatistics tripStatistics = track.getTripStatistics();
    int bottom = tripStatistics.getBottom();
    int left = tripStatistics.getLeft();
    int latitudeSpanE6 = tripStatistics.getTop() - bottom;
    int longitudeSpanE6 = tripStatistics.getRight() - left;
    if (latitudeSpanE6 > 0 && latitudeSpanE6 < 180E6 && longitudeSpanE6 > 0
        && longitudeSpanE6 < 360E6) {
      keepMyLocationVisible = false;
      GeoPoint center = new GeoPoint(bottom + latitudeSpanE6 / 2, left + longitudeSpanE6 / 2);
      if (LocationUtils.isValidGeoPoint(center)) {
        mapView.getController().setCenter(center);
        mapView.getController().setCenter(LocationUtils.convertToBaiduGeopoint(center));
        mapView.getController().zoomToSpan(latitudeSpanE6, longitudeSpanE6);
      }
    }
  }
  
  /*
   * 从系统取得上次记录的最后位置，初始化当前的地图中心，否则每次baidu都会
   * 将地图中信设置为北京
   */
  private void initMapCenter(){
    if(currentLocation == null){
      Location locationTmp=LocationUtility.getInstance(getActivity()).getLastKnownLocation();
      if(locationTmp == null)
        return;
      currentLocation = locationTmp;
      LocationUtils.setGeoInLocation(locationTmp);
      MapController mMapController = mapView.getController();  // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
      GeoPoint geoPoint = LocationUtils.getGeoPoint(locationTmp);
      mMapController.setCenter(geoPoint);  //设置地图中心点
      mMapController.setZoom(12);    //设置地图zoom级别
      LocationData locData = new LocationData();
      locData.latitude = locationTmp.getLatitude();
      locData.longitude = locationTmp.getLongitude();
      myLocationOverlay.setData(locData);
      mapView.refresh();
    }
  }
  
  private void backStack(){
    getFragmentManager().popBackStack();
  }

}