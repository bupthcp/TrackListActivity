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
  
  //百度MapAPI的管理类
  public BMapManager mBMapMan = null;
  
  // 授权Key
  // TODO: 请输入您的Key,
  // 申请地址：http://dev.baidu.com/wiki/static/imap/key/
  public String mStrKey = "9D523C2DF19F58B614526AD0B1270698A9B8234C";
  public boolean m_bKeyRight = true; // 授权Key正确，验证通过
  
  // 常用事件监听，用来处理通常的网络错误，授权验证错误等
  public static class MyGeneralListener implements MKGeneralListener {
      @Override
      public void onGetNetworkState(int iError) {
          Log.d("MyGeneralListener", "onGetNetworkState error is "+ iError);
          Toast.makeText(MyTracksApplication.mDemoApp.getApplicationContext(), "您的网络出错啦！",
                  Toast.LENGTH_LONG).show();
      }

      @Override
      public void onGetPermissionState(int iError) {
          Log.d("MyGeneralListener", "onGetPermissionState error is "+ iError);
          if (iError ==  MKEvent.ERROR_PERMISSION_DENIED) {
              // 授权Key错误：
              Toast.makeText(MyTracksApplication.mDemoApp.getApplicationContext(), 
                      "请在BMapApiDemoApp.java文件输入正确的授权Key！",
                      Toast.LENGTH_LONG).show();
              MyTracksApplication.mDemoApp.m_bKeyRight = false;
          }
      }
  }

  
  @Override
  //建议在您app的退出之前调用mapadpi的destroy()函数，避免重复初始化带来的时间消耗
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
