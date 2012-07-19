/**
 * 
 */
package com.google.android.apps.mytracks.fragments;

/**
 * @author huchenpeng
 *
 */
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
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
import com.google.android.apps.mytracks.stats.TripStatistics;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.apps.mytracks.util.GeoRect;
import com.google.android.apps.mytracks.util.LocationUtils;
import com.google.android.maps.mytracks.R;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.EnumSet;
import java.util.List;
public class BMapActivity extends MapActivity implements View.OnTouchListener, View.OnClickListener, TrackDataListener{
  
  public static final String MAP_FRAGMENT_TAG = "mapFragment";
  private static final String KEY_CURRENT_LOCATION = "currentLocation";
  private static final String KEY_KEEP_MY_LOCATION_VISIBLE = "keepMyLocationVisible";

  private TrackDataHub trackDataHub;

  // True to keep my location visible.
  private boolean keepMyLocationVisible;

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
  private ImageButton myLocationImageButton;
  private TextView messageTextView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    MyTracksApplication app = (MyTracksApplication)this.getApplication();
    if (app.mBMapMan == null) {
        app.mBMapMan = new BMapManager(getApplication());
        app.mBMapMan.init(app.mStrKey, new MyTracksApplication.MyGeneralListener());
    }
    app.mBMapMan.start();
    // 如果使用地图SDK，请初始化地图Activity
    long iTime = System.nanoTime();
    super.initMapActivity(app.mBMapMan);
    iTime = System.nanoTime() - iTime;
    Log.d("MapViewDemo", "the init time is  " + iTime);
    if (savedInstanceState != null) {
      keepMyLocationVisible = savedInstanceState.getBoolean(KEY_KEEP_MY_LOCATION_VISIBLE, false);
      currentLocation = (Location) savedInstanceState.getParcelable(KEY_CURRENT_LOCATION);
      if (currentLocation != null) {
        updateCurrentLocation();
      }
    }
  }

  /* (non-Javadoc)
   * @see com.baidu.mapapi.MapActivity#isRouteDisplayed()
   */
  @Override
  protected boolean isRouteDisplayed() {
    // TODO Auto-generated method stub
    return false;
  }
  @Override
  public void onStart() {
    setContentView(R.layout.map);
    mapView = (MapView) mapViewContainer.findViewById(R.id.map_view);
    mapOverlay = new MapOverlay(this);
    
    List<Overlay> overlays = mapView.getOverlays();
    overlays.clear();
    overlays.add(mapOverlay);
    
    mapView.requestFocus();
    mapView.setOnTouchListener(this);
    mapView.setBuiltInZoomControls(true);
    myLocationImageButton = (ImageButton) mapViewContainer.findViewById(R.id.map_my_location);
    myLocationImageButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        showMyLocation();       
      }
    });
    messageTextView = (TextView) mapViewContainer.findViewById(R.id.map_message);

    ApiAdapterFactory.getApiAdapter().invalidMenu(this);
  }


  @Override
  public void onResume() {
    super.onResume();
    resumeTrackDataHub();
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

  @Override
  public void onDestroy() {
    super.onDestroy();
    ViewGroup parentViewGroup = (ViewGroup) mapViewContainer.getParent();
    if (parentViewGroup != null) {
      parentViewGroup.removeView(mapViewContainer);
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater menuInflator = getMenuInflater();
    menuInflator.inflate(R.menu.map, menu);
    return true;
  }

  @Override
  public boolean onPrepareOptionsMenu(Menu menu) {
    int titleId = R.string.menu_satellite_mode;
    if (mapView != null) {
      titleId = mapView.isSatellite() ? R.string.menu_map_mode : R.string.menu_satellite_mode;
    }
    menu.findItem(R.id.map_satellite_mode).setTitle(titleId);
    super.onPrepareOptionsMenu(menu);
    return true;
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
    MyTracksProviderUtils MyTracksProviderUtils = Factory.get(this);
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

    this.runOnUiThread(new Runnable() {
      @Override
      public void run() {
        if (messageId != -1) {
          messageTextView.setText(messageId);
          messageTextView.setVisibility(View.VISIBLE);

          if (isGpsDisabled) {
            Toast.makeText(BMapActivity.this, R.string.gps_not_found, Toast.LENGTH_LONG).show();

            // Click to show the location source settings
            messageTextView.setOnClickListener(BMapActivity.this);
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
    this.runOnUiThread(new Runnable() {
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
    trackDataHub = ((MyTracksApplication) this.getApplication()).getTrackDataHub();
    trackDataHub.registerTrackDataListener(this, EnumSet.of(
        ListenerDataType.SELECTED_TRACK_CHANGED,
        ListenerDataType.WAYPOINT_UPDATES,
        ListenerDataType.POINT_UPDATES,
        ListenerDataType.LOCATION_UPDATES,
        ListenerDataType.COMPASS_UPDATES));
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

    mapOverlay.setMyLocation(currentLocation);
    mapView.postInvalidate();

    if (currentLocation != null && keepMyLocationVisible && !isVisible(currentLocation)) {
      GeoPoint geoPoint = LocationUtils.getGeoPoint(currentLocation);
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

  /**
   * Shows the track.
   *
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
        mapView.getController().zoomToSpan(latitudeSpanE6, longitudeSpanE6);
      }
    }
  }

}
