/*
 * Copyright 2008 Google Inc.
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
package com.google.android.apps.mytracks;

import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.services.RemoveTempFilesService;
import com.google.android.apps.mytracks.util.AnalyticsUtils;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.maps.mytracks.BuildConfig;
import com.hu.iJogging.common.ConfigFree;

import android.app.Application;
import android.content.Intent;

/**
 * MyTracksApplication for keeping global state.
 *
 * @author Jimmy Shih
 */
public class MyTracksApplication extends Application {

  private TrackDataHub trackDataHub;
  
  static MyTracksApplication mDemoApp;
  

  

  
  @Override
  //��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
  public void onTerminate() {
      // TODO Auto-generated method stub

      super.onTerminate();
  }
  @Override
  public void onCreate() {
    super.onCreate();
    mDemoApp = this;

    if (BuildConfig.DEBUG) {
      ApiAdapterFactory.getApiAdapter().enableStrictMode();
    }
    AnalyticsUtils.sendPageViews(getApplicationContext(), "/appstart");
    Intent intent = new Intent(this, RemoveTempFilesService.class);
    startService(intent);
    ConfigFree.configure();
  }

  /**
   * Gets the application's TrackDataHub.
   * 
   * Note: use synchronized to make sure only one instance is created per application.
   */
  public synchronized TrackDataHub getTrackDataHub() {
    if (trackDataHub == null) {
      trackDataHub = TrackDataHub.newInstance(getApplicationContext());
    }
    return trackDataHub;
  }
}