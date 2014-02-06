package com.hu.iJogging.common;

public class DeviceConfig {
  public static final String APP_VARIANT_FREE = "Market";
  public static final String APP_VARIANT_HPB = "HPB";
  public static final String APP_VARIANT_PRO = "M-Pro";
  public static boolean BETA_TESTING_ENABLED = false;
  public static String FACEBOOK_KEY;
  public static final String[] FACEBOOK_PERMISSIONS;
  public static String GOOGLE_MAP_KEY = "0PJScFH8BSJ55OHT8Ii8z0ade3eQKdGcRiGz0rQ";
  public static boolean TTS_SUPPORT = false;
  public static boolean ZOOM_RECENTER_RECTANGLE_VISIBLE = false;
  public static String appVariant;
  public static String dbDir;
  public static final String dbDirFree = "/data/data/com.endomondo.android/databases";
  public static final String dbDirHpb = "/data/data/com.endomondo.android.hpb/databases";
  public static final String dbDirHtc = "/data/data/com.endomondo.htc/databases";
  public static final String dbDirPro = "/data/data/com.endomondo.android.pro/databases";
  public static final String dbFile = "EndomondoDatabase";
  public static String fileDir;
  public static final String fileDirFree = "/data/data/com.endomondo.android/files";
  public static final String fileDirHpb = "/data/data/com.endomondo.android.hpb/files";
  public static final String fileDirHtc = "/data/data/com.endomondo.htc/files";
  public static final String fileDirPro = "/data/data/com.endomondo.android.pro/files";
  public static final String fileLapspeak = "lapspeak.mp3";
  public static final String filePeptalk = "peptalk.mp3";
  public static String htmlDir;
  public static final String htmlDirFree = "data/data/com.endomondo.android/html";
  public static final String htmlDirHpb = "data/data/com.endomondo.android.hpb/html";
  public static final String htmlDirHtc = "data/data/com.endomondo.htc/html";
  public static final String htmlDirPro = "data/data/com.endomondo.android.pro/html";
  public static String imageDir;
  public static final String imageDirFree = "data/data/com.endomondo.android/images";
  public static final String imageDirHpb = "data/data/com.endomondo.android.hpb/images";
  public static final String imageDirHtc = "data/data/com.endomondo.htc/images";
  public static final String imageDirPro = "data/data/com.endomondo.android.pro/images";
  public static final String installationFile = "INSTALLATION";
  public static final String memCard = "/sdcard";
  public static String newsfeedDir;
  public static final String newsfeedDirFree = "data/data/com.endomondo.android/newsfeed";
  public static final String newsfeedDirHpb = "data/data/com.endomondo.android.hpb/newsfeed";
  public static final String newsfeedDirHtc = "data/data/com.endomondo.htc/newsfeed";
  public static final String newsfeedDirPro = "data/data/com.endomondo.android.pro/newsfeed";
  public static String workoutsDir;
  public static final String workoutsDirFree = "data/data/com.endomondo.android/workouts";
  public static final String workoutsDirHpb = "data/data/com.endomondo.android.hpb/workouts";
  public static final String workoutsDirHtc = "data/data/com.endomondo.htc/workouts";
  public static final String workoutsDirPro = "data/data/com.endomondo.android.pro/workouts";

  static
  {
    FACEBOOK_KEY = "202423869273";
    String[] arrayOfString = new String[2];
    arrayOfString[0] = "publish_actions";
    arrayOfString[1] = "email";
    FACEBOOK_PERMISSIONS = arrayOfString;
    BETA_TESTING_ENABLED = false;
    ZOOM_RECENTER_RECTANGLE_VISIBLE = false;
    TTS_SUPPORT = false;
  }

}
