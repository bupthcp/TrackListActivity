package com.hu.walkingnotes.baidumaps;

import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.android.apps.mytracks.Constants;
import com.google.android.apps.mytracks.util.LocationUtils;
import com.google.android.apps.mytracks.util.PreferencesUtils;
import com.google.android.apps.mytracks.util.UnitConversions;
import com.hu.iJogging.R;
import com.hu.iJogging.content.Waypoint;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class MapOverlay extends GraphicsOverlay{
  
  private final static String TAG = MapOverlay.class.getSimpleName();
  private ItemizedOverlay<OverlayItem> mMarkOverlay;
  
  public static final float WAYPOINT_X_ANCHOR = 13f / 48f;

  private static final float WAYPOINT_Y_ANCHOR = 43f / 48f;
  private static final float MARKER_X_ANCHOR = 50f / 96f;
  private static final float MARKER_Y_ANCHOR = 90f / 96f;
  private static final int INITIAL_LOCATIONS_SIZE = 1024;

  private final OnSharedPreferenceChangeListener
      sharedPreferenceChangeListener = new OnSharedPreferenceChangeListener() {
          @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
          if (key == null
              || key.equals(PreferencesUtils.getKey(context, R.string.track_color_mode_key))) {
            trackColorMode = PreferencesUtils.getString(
                context, R.string.track_color_mode_key, PreferencesUtils.TRACK_COLOR_MODE_DEFAULT);
            trackPath = TrackPathFactory.getTrackPath(context,"DYNAMIC");
//            trackPath = TrackPathFactory.getTrackPath(context, trackColorMode);
          }
        }
      };

  private final Context context;
  private final List<CachedLocation> locations;
  private final BlockingQueue<CachedLocation> pendingLocations;
  private final List<Waypoint> waypoints;

  private String trackColorMode = PreferencesUtils.TRACK_COLOR_MODE_DEFAULT;

  private boolean showEndMarker = true;
  private TrackPath trackPath;

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
  
  public MapOverlay(Activity activity, MapView mapView,ItemizedOverlay<OverlayItem> markOverlay) {
    super(mapView);
    
    this.context = activity;
    this.waypoints = new ArrayList<Waypoint>();
    this.locations = new ArrayList<CachedLocation>(INITIAL_LOCATIONS_SIZE);
    this.pendingLocations = new ArrayBlockingQueue<CachedLocation>(
        Constants.MAX_DISPLAYED_TRACK_POINTS, true);
    this.mMarkOverlay = markOverlay;

    context.getSharedPreferences(Constants.SETTINGS_NAME, Context.MODE_PRIVATE)
        .registerOnSharedPreferenceChangeListener(sharedPreferenceChangeListener);
    sharedPreferenceChangeListener.onSharedPreferenceChanged(null, null);
  }


  /**
   * Add a location.
   * 
   * @param location the location
   */
  public void addLocation(Location location) {
    // Queue up in the pendingLocations until it's merged with locations
    if (!pendingLocations.offer(new CachedLocation(location))) {
      Log.e(TAG, "Unable to add to pendingLocations.");
    }
  }

  /**
   * Adds a segment split.
   */
  public void addSegmentSplit() {
    // Queue up in the pendingLocations until it's merged with locations
    if (!pendingLocations.offer(new CachedLocation())) {
      Log.e(TAG, "Unable to add to pendingLocations.");
    }
  }

  /**
   * Clears the locations.
   */
  public void clearPoints() {
    synchronized (locations) {
      locations.clear();
      pendingLocations.clear();
    }
  }

  /**
   * Adds a waypoint.
   * 
   * @param waypoint the waypoint
   */
  public void addWaypoint(Waypoint waypoint) {
    synchronized (waypoints) {
      waypoints.add(waypoint);
    }
  }

  /**
   * Clears the waypoints.
   */
  public void clearWaypoints() {
    synchronized (waypoints) {
      waypoints.clear();
    }
  }

  /**
   * Sets whether to show the end marker.
   * 
   * @param show true to show the end marker
   */
  public void setShowEndMarker(boolean show) {
    showEndMarker = show;
  }

  /**
   * Updates the track, start and end markers, and waypoints.
   * 
   * @param googleMap the google map
   * @param paths the paths
   * @param reload true to reload all points
   */
  public void update( ArrayList<PolyLine> paths, boolean reload) {
    synchronized (locations) {
      // Merge pendingLocations with locations
      int newLocations = pendingLocations.drainTo(locations);
      // Call updateState first because we want to update its state each time
      // (for dynamic coloring)
      if (trackPath.updateState() || reload) {
//        googleMap.clear();
        this.removeAll();
        paths.clear();
        trackPath.updatePath(this, paths, 0, locations);
        updateStartAndEndMarkers();
//        updateWaypoints(googleMap);
      } else {
        if (newLocations != 0) {
          int numLocations = locations.size();
          Log.d(TAG, "newLocations size:"+newLocations);
          Log.d(TAG, "locations size:"+numLocations);
          trackPath.updatePath(this, paths, numLocations - newLocations, locations);
        }
      }
    }
  }

  /**
   * Updates the start and end markers.
   * 
   */
  private void updateStartAndEndMarkers() {
    mMarkOverlay.removeAll();
    // Add the end marker
    if (showEndMarker) {
      for (int i = locations.size() - 1; i >= 0; i--) {
        CachedLocation cachedLocation = locations.get(i);
        if (cachedLocation.valid) {
            OverlayItem endItem = new OverlayItem(cachedLocation.getGeoPoint(),null,null);
            endItem.setAnchor(MARKER_X_ANCHOR, MARKER_Y_ANCHOR);
            endItem.setMarker(context.getResources().getDrawable(R.drawable.red_dot));
            mMarkOverlay.addItem(endItem);
            break;
        }
      }
    }

    // Add the start marker
    for (int i = 0; i < locations.size(); i++) {
      CachedLocation cachedLocation = locations.get(i);
      if (cachedLocation.valid) {
          OverlayItem startItem = new OverlayItem(cachedLocation.getGeoPoint(),null,null);
          startItem.setAnchor(MARKER_X_ANCHOR, MARKER_Y_ANCHOR);
          startItem.setMarker(context.getResources().getDrawable(R.drawable.green_dot));
          mMarkOverlay.addItem(startItem);
          break;
      }
    }
  }
//
//  /**
//   * Updates the waypoints.
//   * 
//   * @param googleMap the google map.
//   */
//  private void updateWaypoints(GoogleMap googleMap) {
//    synchronized (waypoints) {
//      for (Waypoint waypoint : waypoints) {
//        Location location = waypoint.getLocation();
//        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
//        int drawableId = waypoint.getType() == WaypointType.STATISTICS ? R.drawable.yellow_pushpin
//            : R.drawable.blue_pushpin;
//        MarkerOptions markerOptions = new MarkerOptions().position(latLng)
//            .anchor(WAYPOINT_X_ANCHOR, WAYPOINT_Y_ANCHOR).draggable(false).visible(true)
//            .icon(BitmapDescriptorFactory.fromResource(drawableId))
//            .title(String.valueOf(waypoint.getId()));
//        googleMap.addMarker(markerOptions);
//      }
//    }
//  }
  
}
