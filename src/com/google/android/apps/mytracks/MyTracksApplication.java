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

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.google.android.apps.mytracks.content.TrackDataHub;
import com.google.android.apps.mytracks.services.RemoveTempFilesService;
import com.google.android.apps.mytracks.util.AnalyticsUtils;
import com.google.android.apps.mytracks.util.ApiAdapterFactory;
import com.google.android.maps.mytracks.BuildConfig;

import android.app.Application;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * MyTracksApplication for keeping global state.
 *
 * @author Jimmy Shih
 */
public class MyTracksApplication extends Application {

  private TrackDataHub trackDataHub;
  
  static MyTracksApplication mDemoApp;
  
  //�ٶ�MapAPI�Ĺ�����
  public BMapManager mBMapMan = null;
  
  // ��ȨKey
  // TODO: ����������Key,
  // �����ַ��http://dev.baidu.com/wiki/static/imap/key/
  public String mStrKey = "9D523C2DF19F58B614526AD0B1270698A9B8234C";
  public boolean m_bKeyRight = true; // ��ȨKey��ȷ����֤ͨ��
  
  // �����¼���������������ͨ�������������Ȩ��֤�����
  public static class MyGeneralListener implements MKGeneralListener {
      @Override
      public void onGetNetworkState(int iError) {
          Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
          Toast.makeText(MyTracksApplication.mDemoApp.getApplicationContext(), "���������������",
                  Toast.LENGTH_LONG).show();
      }

      @Override
      public void onGetPermissionState(int iError) {
          Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
          if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
              // ��ȨKey����
              Toast.makeText(MyTracksApplication.mDemoApp.getApplicationContext(), 
                      "����BMapApiDemoApp.java�ļ�������ȷ����ȨKey��",
                      Toast.LENGTH_LONG).show();
              MyTracksApplication.mDemoApp.m_bKeyRight = false;
          }
      }
  }

  
  @Override
  //��������app���˳�֮ǰ����mapadpi��destroy()�����������ظ���ʼ��������ʱ������
  public void onTerminate() {
      // TODO Auto-generated method stub
      if (mBMapMan != null) {
          mBMapMan.destroy();
          mBMapMan = null;
      }
      super.onTerminate();
  }
  @Override
  public void onCreate() {
    super.onCreate();
    mDemoApp = this;
    mBMapMan = new BMapManager(this);
    mBMapMan.init(this.mStrKey, new MyGeneralListener());
    mBMapMan.getLocationManager().setNotifyInternal(10, 5);
    if (BuildConfig.DEBUG) {
      ApiAdapterFactory.getApiAdapter().enableStrictMode();
    }
    AnalyticsUtils.sendPageViews(getApplicationContext(), "/appstart");
    Intent intent = new Intent(this, RemoveTempFilesService.class);
    startService(intent);
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
