package com.hu.iJogging.Services;

import com.baidu.mapapi.MKOLUpdateElement;

public interface DownloadOfflineListener {
  
  void notifyOfflineMapStateUpdate(MKOLUpdateElement update);
}
