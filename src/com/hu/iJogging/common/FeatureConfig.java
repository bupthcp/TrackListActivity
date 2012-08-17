package com.hu.iJogging.common;

public class FeatureConfig {
  public static boolean allowLoginOverrideForAccount;
  public static boolean androidAccountIntegration;
  public static FeatureState beatAFriend;
  public static FeatureState beatYourself;
  public static FeatureState calorieGoal;
  public static FeatureState competeOnRoute;
  public static String defaultCountDown;
  public static String defaultIntervalsAudioCoach;
  public static boolean enableStepCounter;
  public static FeatureState graphs;
  public static boolean groupGoalMotivations;
  public static boolean headsetControls;
  public static FeatureState intervals;
  public static boolean loginEndsWithProfileEntry;
  public static boolean logout;
  public static FeatureState lowPowerMode;
  public static int mainUIZone1;
  public static int mainUIZone2;
  public static int mainUIZone3;
  public static int mainUIZone4;
  public static int mapUIZone1;
  public static int mapUIZone2;
  public static boolean newsFeedWidget;
  public static boolean playLatestPeptalk;
  public static int profilePicMaxSize;
  public static boolean promptForSignupOnWorkoutStop;
  public static boolean searchDataFromFree;
  public static boolean searchDataFromHtc;
  public static boolean searchFriends;
  public static boolean showBmiInfo;
  public static boolean showCityScapeOnFacebookLogin;
  public static boolean showJobType;
  public static boolean showNewMsgIndicator;
  public static boolean showSettingsAbout;
  public static boolean showShopLink;
  public static boolean showStepCounterSensitivitySetting;
  public static boolean stepCounterOnByDefault;
  public static int termsTextDescriptionStringId;
  public static int termsTextRawId;
  public static FeatureState timeGoal;
  public static boolean upgradeToPro;
  public static boolean upgradeToProMap = true;
  public static boolean uploadJobType;
  public static boolean useNewCalorieCalculations;
  public static long waitNoteLagTime;
  public static long waitNoteMaxTime;

  static
  {
    defaultCountDown = "0";
    defaultIntervalsAudioCoach = "3";
    allowLoginOverrideForAccount = false;
    useNewCalorieCalculations = true;
    showCityScapeOnFacebookLogin = true;
    waitNoteMaxTime = 60000L;
    waitNoteLagTime = 500L;
    uploadJobType = false;
    showJobType = false;
    showBmiInfo = false;
    showSettingsAbout = true;
    profilePicMaxSize = 1024;
    enableStepCounter = false;
    stepCounterOnByDefault = false;
    showStepCounterSensitivitySetting = false;
  }

  public static enum FeatureState {
    AVAILABLE, UPGRADE_AVAILABLE, IN_PRO_ONLY, HIDDEN
  }

  public static class settingsDefaultValues
  {
    public static float height;
    public static float weight = 75.0F;

    static
    {
      height = 175.0F;
    }
  }
}
