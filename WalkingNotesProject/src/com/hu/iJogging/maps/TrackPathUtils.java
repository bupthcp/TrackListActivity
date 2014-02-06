/*
 * Copyright 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.hu.iJogging.maps;

import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.util.Log;

import java.util.ArrayList;

/**
 * Various utility functions for track path painting.
 * 
 * @author Vangelis S.
 */
public class TrackPathUtils {
  
  private static final String TAG = TrackPathUtils.class.getSimpleName();
  private static final int TRACK_WIDTH = 4;

  private TrackPathUtils() {}

  /**
   * Add a path.
   * 
   * @param googleMap the google map
   * @param paths the existing paths
   * @param points the path points
   * @param color the path color
   * @param append true to append to the last path
   */
  public static void addPath(MapOverlay mapOverlay, ArrayList<PolyLine> paths,
      ArrayList<GeoPoint> points, int color, boolean append) {
    if (points.size() == 0) {
      return;
    }
    if (append && paths.size() != 0) {
      PolyLine lastPolyline = paths.get(paths.size() - 1);
      ArrayList<GeoPoint> pathPoints = new ArrayList<GeoPoint>();
      pathPoints.addAll(lastPolyline.getPoints());
      pathPoints.addAll(points);
      Log.d(TAG, "polyline pathlastPolylinePoints:"+lastPolyline.getPoints().size());
      
      mapOverlay.removeGraphic(lastPolyline.getLineGraphic().getID());
      lastPolyline.setPoints(pathPoints);
      mapOverlay.setData(lastPolyline.getLineGraphic());
      
      Log.d(TAG, "polyline pathPoints:"+pathPoints.size());
      Log.d(TAG, String.format("polyline pathcolor:%x", lastPolyline.getColor()));
    } else {
      PolyLine polyLine = new PolyLine();
      polyLine.setPolyLineParam(color, TRACK_WIDTH);
      polyLine.setPoints(points);
      paths.add(polyLine);
      Log.d(TAG, "polyline points:"+points.size());
      Log.d(TAG, String.format("polyline color:%x", color));
      mapOverlay.setData(polyLine.getLineGraphic());
    }
    points.clear();

  }
}