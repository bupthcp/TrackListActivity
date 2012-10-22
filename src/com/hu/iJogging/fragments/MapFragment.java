package com.hu.iJogging.fragments;

import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.Mj;
import com.baidu.mapapi.Overlay;
import com.google.android.apps.mytracks.MapOverlay;
import com.google.android.apps.mytracks.MyTracksApplication;
import com.google.android.apps.mytracks.content.MyTracksProviderUtils;
import com.google.android.apps.mytracks.content.MyTracksProviderUtils.Factory;
import com.google.android.apps.mytracks.content.Track;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.content.TrackDataHub.ListenerDataType;
import com.google.android.apps.mytracks.content.TrackDataListener;
import com.google.android.apps.mytracks.content.Waypoint;
import com.google.android.apps.mytracks.maps.bMapView;
import com.google.android.apps.mytracks.stats.TripStatistics;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.apps.mytracks.util.GeoRect;
import com.google.android.apps.mytracks.util.LocationUtils;
import com.google.android.maps.mytracks.R;
import com.hu.iJogging.IJoggingActivity;
import com.hu.iJogging.ViewHistoryActivity;
import com.hu.iJogging.common.LocationUtility;

import android.app.Activity;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.EnumSet;
import java.util.List;

public class MapFragment extends Fragment
implements View.OnTouchListener, View.OnClickListener, TrackDataListener{
  public static final String MAP_FRAGMENT_TAG = "mapFragment";
  Activity mActivity;
  Boolean isViewHistory = false;
  
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
  private bMapView mapView;
  private MapOverlay mapOverlay;
  private ImageButton myLocationImageButton;
  private TextView messageTextView;
  
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    if(activity instanceof IJoggingActivity){
      isViewHistory = false;
    }else if(activity instanceof ViewHistoryActivity){
      isViewHistory = true;
    }
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
    mapViewContainer = getActivity().getLayoutInflater().inflate(R.layout.map_fragment, container,false);
    mapView = (bMapView) mapViewContainer.findViewById(R.id.map_view);
    int i = 20;
    int j = 40;
    if (Mj.InitMapControlCC(i, j) == 1)
    {
      mapView.init();
      if (Mj.d != mapView)
      {
        Mj.d = mapView;
        if (mapView != null)
            mapView.b.a(mapView.getLeft(), mapView.getTop(), mapView.getRight(), mapView.getBottom());
      }
    }

    
    mapOverlay = new MapOverlay(getActivity());
    
    List<Overlay> overlays = mapView.getOverlays();
    overlays.clear();
    overlays.add(mapOverlay);
    
    mapView.requestFocus();
    mapView.setOnTouchListener(this);
    mapView.setBuiltInZoomControls(false);
    myLocationImageButton = (ImageButton) mapViewContainer.findViewById(R.id.map_my_location);
    myLocationImageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showMyLocation();       
      }
    });
    messageTextView = (TextView) mapViewContainer.findViewById(R.id.map_message);

    ApiAdapterFactory.getApiAdapter().invalidMenu(getActivity());
    
    ((Button) this.mapViewContainer.findViewById(R.id.ButtonDashboardCorner))
    .setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View paramView, MotionEvent paramMotionEvent) {
        if (paramMotionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//           backStack();
          ((IJoggingActivity)mActivity).switchToTrainingDetailFragment();
        }
        return true;
      }
    });
    
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
  }

  @Override
  public void onResume() {
    super.onResume();
    myLocationImageButton.setVisibility(View.VISIBLE);
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
  }

  //在这里实现onDestroyView是为了保证在fragment切换的
  //时候，fragment的container是干净的，
  //如果不加上这个清理过程，有可能会出现两个fragment重叠
  //在一起显示的情况
  @Override
  public void onDestroyView() {
    super.onDestroyView();
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

  @Override
  public void onProviderStateChange(ProviderState state) {
    final int messageId;
    final boolean isGpsDisabled;
    //如果是查看界面，则不需要显示gps的警告信息
    if(isViewHistory)
      return;
    switch (state) {
      case DISABLED:
        messageId = R.string.gps_need_to_enable;
        isGpsDisabled = true;
        break;
      case NO_FIX:
      case BAD_FIX:
        messageId = R.string.gps_wait_for_signal;
        isGpsDisabled = false;
        break;
      case GOOD_FIX:
        messageId = -1;
        isGpsDisabled = false;
        break;
      default:
        throw new IllegalArgumentException("Unexpected state: " + state);
    }

    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (messageId != -1) {
          messageTextView.setText(messageId);
          messageTextView.setVisibility(View.VISIBLE);
          myLocationImageButton.setVisibility(View.VISIBLE);

          if (isGpsDisabled) {
            Toast.makeText(getActivity(), R.string.gps_not_found, Toast.LENGTH_LONG).show();

            // Click to show the location source settings
            messageTextView.setOnClickListener(MapFragment.this);
          } else {
            messageTextView.setOnClickListener(null);
          }
        } else {
          messageTextView.setVisibility(View.GONE);
        }
      }
    });
  }

  @Override
  public void onCurrentLocationChanged(Location location) {
    currentLocation = location;
    updateCurrentLocation();
  }

  @Override
  public void onCurrentHeadingChanged(double heading) {
    if (mapOverlay.setHeading((float) heading)) {
      mapView.postInvalidate();
    }
  }

  @Override
  public void onSelectedTrackChanged(final Track track, final boolean isRecording) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        boolean hasTrack = track != null;
        mapOverlay.setTrackDrawingEnabled(hasTrack);
  
        if (hasTrack) { 
          synchronized (this) {
            /*
             * Synchronize to prevent race condition in changing markerTrackId
             * and markerId variables.
             */
            currentSelectedTrackId = track.getId();
            updateMap(track);
          }
          mapOverlay.setShowEndMarker(!isRecording);
        }
        mapView.invalidate();
      }
    });
  }

  @Override
  public void onTrackUpdated(Track track) {
    // We don't care.
  }

  @Override
  public void clearTrackPoints() {
    mapOverlay.clearPoints();
  }

  @Override
  public void onNewTrackPoint(Location location) {
    if (LocationUtils.isValidLocation(location)) {
      LocationUtils.setGeoInLocation(location);
      mapOverlay.addLocation(location);
    }
  }

  @Override
  public void onSampledOutTrackPoint(Location loc) {
    // We don't care.
  }

  @Override
  public void onSegmentSplit() {
    mapOverlay.addSegmentSplit();
  }

  @Override
  public void onNewTrackPointsDone() {
    mapView.postInvalidate();
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
    mapView.postInvalidate();
  }

  @Override
  public boolean onUnitsChanged(boolean metric) {
    // We don't care.
    return false;
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
    trackDataHub = ((MyTracksApplication) getActivity().getApplication()).getTrackDataHub();
    //如果是查看界面，不需要启动gps信息，所以就不用注册location相关的listener了，
    //也就不会触发gps
    if(isViewHistory){
      trackDataHub.registerTrackDataListener(this, EnumSet.of(
          ListenerDataType.SELECTED_TRACK_CHANGED,
          ListenerDataType.WAYPOINT_UPDATES,
          ListenerDataType.POINT_UPDATES,
          ListenerDataType.COMPASS_UPDATES));
    }else{
      trackDataHub.registerTrackDataListener(this, EnumSet.of(
          ListenerDataType.SELECTED_TRACK_CHANGED,
          ListenerDataType.WAYPOINT_UPDATES,
          ListenerDataType.POINT_UPDATES,
          ListenerDataType.LOCATION_UPDATES,
          ListenerDataType.COMPASS_UPDATES));
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

    final Location locationTmp = new Location(currentLocation);
    LocationUtils.setGeoInLocation(locationTmp);
    
    mapOverlay.setMyLocation(locationTmp);
    mapView.postInvalidate();

    if (locationTmp != null && keepMyLocationVisible && !isVisible(locationTmp)) {
      Runnable animateRunnable = new Runnable(){
        public void run(){
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
      };
      mapFragmentHandler.post(animateRunnable);
    }
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
      mapOverlay.setMyLocation(locationTmp);
    }
  }
  
  private void backStack(){
    getFragmentManager().popBackStack();
  }
}
