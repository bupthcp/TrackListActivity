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
package com.hu.walkingnotes.baidumaps;

import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.google.common.annotations.VisibleForTesting;
import com.hu.iJogging.R;
import com.hu.walkingnotes.baidumaps.MapOverlay.CachedLocation;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * A path painter that varies the path colors based on fixed speeds or average
 * speed margin depending of the TrackPathDescriptor passed to its constructor.
 * 
 * @author Vangelis S.
 */
public class MultiColorTrackPath implements TrackPath {
  private final TrackPathDescriptor trackPathDescriptor;
  private final int slowColor;
  private final int normalColor;
  private final int fastColor;
  
  public MultiColorTrackPath(Context context, TrackPathDescriptor trackPathDescriptor) {
    this.trackPathDescriptor = trackPathDescriptor;
    slowColor = context.getResources().getColor(R.color.slow_path);
    normalColor = context.getResources().getColor(R.color.normal_path);
    fastColor = context.getResources().getColor(R.color.fast_path);
  }

  @Override
  public boolean updateState() {
    return trackPathDescriptor.updateState();
  }

  @Override
  public void updatePath(MapOverlay mapOverlay, ArrayList<PolyLine> paths, int startIndex,
      List<CachedLocation> locations) {
    if (mapOverlay == null) {
      return;
    }
    if (startIndex >= locations.size()) {
      return;
    }
    
    boolean newSegment = startIndex == 0 || !locations.get(startIndex - 1).isValid();
    GeoPoint lastGeoPoint = startIndex != 0 ? locations.get(startIndex -1).getGeoPoint() : null;
    
    ArrayList<GeoPoint> lastSegmentPoints = new ArrayList<GeoPoint>();
    int lastSegmentColor = paths.size() != 0  ? paths.get(paths.size() - 1).getColor() : slowColor;
    boolean useLastPolyline = true;

    for (int i = startIndex; i < locations.size(); ++i) {
      CachedLocation cachedLocation = locations.get(i);

      // If not valid, start a new segment
      if (!cachedLocation.isValid()) {
        newSegment = true;
        lastGeoPoint = null;
        continue;
      }
      GeoPoint geoPoint = cachedLocation.getGeoPoint();
      int color = getColor(cachedLocation.getSpeed());
      
      // Either update point or draw a line from the last point
      if (newSegment) {
        TrackPathUtils.addPath(mapOverlay, paths, lastSegmentPoints, lastSegmentColor, useLastPolyline);
        useLastPolyline = false;
        lastSegmentColor = color;
        newSegment = false;
      }
      if (lastSegmentColor == color) {
        lastSegmentPoints.add(geoPoint);
      } else {
        TrackPathUtils.addPath(mapOverlay, paths, lastSegmentPoints, lastSegmentColor, useLastPolyline);
        useLastPolyline = false;
        if (lastGeoPoint != null) {
          lastSegmentPoints.add(lastGeoPoint);
        }
        lastSegmentPoints.add(geoPoint);
        lastSegmentColor = color;
      }
      lastGeoPoint = geoPoint;
    }
    TrackPathUtils.addPath(mapOverlay, paths, lastSegmentPoints, lastSegmentColor, useLastPolyline);
  }

  @VisibleForTesting
  protected int getColor(int speed) {
    if (speed <= trackPathDescriptor.getSlowSpeed()) {
      return slowColor;
    } else if (speed <= trackPathDescriptor.getNormalSpeed()) {
      return normalColor;
    } else {
      return fastColor;
    }
  }
  
  @VisibleForTesting
  protected int getSlowColor() {
    return slowColor;
  }

  @VisibleForTesting
  protected int getNormalColor() {
    return normalColor;
  }

  @VisibleForTesting
  protected int getFastColor() {
    return fastColor;
  }

}