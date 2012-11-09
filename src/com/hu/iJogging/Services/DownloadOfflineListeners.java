package com.hu.iJogging.Services;



import java.util.HashSet;
import java.util.Set;

public class DownloadOfflineListeners {
  private final Set<DownloadOfflineListener> registeredListeners = new HashSet<DownloadOfflineListener>();
  
  public boolean registerDownloadOfflineListener(DownloadOfflineListener listener){
    return registeredListeners.add(listener);
  }
  
  public boolean unRegisterDownloadOfflineListener(DownloadOfflineListener listener){
    return registeredListeners.remove(listener);
  }
  
  public Set<DownloadOfflineListener> getRegisteredListeners(){
    return registeredListeners;
  }
}
