package com.hu.iJogging.common;

import android.net.Uri;

import java.lang.reflect.Field;

public class DownloadsReflected {
  public static final String TAG = DownloadsReflected.class.getSimpleName();
  
  public static Uri getDownloadsContentURI(){
    Uri uri = null;
    //这种依靠反射的方法并不是很可取。在2.3版本的时候，downloads下面是直接可以找到CONTENT_URI
    //但是在4.0之后，downloads发生了很大的改变。CONTENT_URI移动到了downloads下面的impl中，
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
