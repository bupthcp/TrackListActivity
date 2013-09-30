package com.hu.iJogging;

import com.google.android.apps.mytracks.content.TrackDataHub;

import android.support.v7.app.ActionBarActivity;

public class TrackActivity extends ActionBarActivity{
  protected TrackDataHub trackDataHub;
  
  public TrackDataHub getTrackDataHub(){
    return trackDataHub;
  }
}
