package com.hu.iJogging.common;

import android.net.Uri;

import java.lang.reflect.Field;

public class DownloadsReflected {
  public static final String TAG = DownloadsReflected.class.getSimpleName();
  
  public static Uri getDownloadsContentURI(){
    Uri uri = null;
    //������������ķ��������Ǻܿ�ȡ����2.3�汾��ʱ��downloads������ֱ�ӿ����ҵ�CONTENT_URI
    //������4.0֮��downloads�����˺ܴ�ĸı䡣CONTENT_URI�ƶ�����downloads�����impl�У�
    try{
      Class<?> classtype = Class.forName("android.provider.Downloads");
      Field temp = classtype.getDeclaredField("CONTENT_URI");
      uri = (Uri)temp.get(null);
    }catch(Exception e){
      e.printStackTrace();
    }finally{
      return uri;
    }
  }
}
