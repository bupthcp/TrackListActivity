package com.hu.iJogging.common;

import com.google.android.maps.mytracks.R;

import android.content.Context;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

public class UIConfig {
  public static final int DEFAULT_VALUE = 0;

  public static void configListContainer(View paramView)
  {
    paramView.setBackgroundResource(ListConfig.colorListContainerBackground);
  }

  public static void configListNoSelection(Context paramContext, ListView paramListView)
  {
    paramListView.setDivider(paramContext.getResources().getDrawable(ListConfig.dividerId));
    paramListView.setDividerHeight(ListConfig.dividerHeightPixels);
    paramListView.setCacheColorHint(paramContext.getResources().getColor(ListConfig.colorListItem1));
  }

  public static void configListSelection(Context paramContext, ListView paramListView)
  {
    configListNoSelection(paramContext, paramListView);
    paramListView.setSelector(paramContext.getResources().getDrawable(ListConfig.selectorId));
    paramListView.setDrawSelectorOnTop(ListConfig.drawSelectorOnTop);
  }

  public static void configSeparator(Context paramContext, TextView paramTextView)
  {
    paramTextView.setBackgroundResource(ListConfig.separatorBackgroundId);
    paramTextView.setTextColor(paramContext.getResources().getColor(ListConfig.separatorTextColorId));
  }

  public static int[] getListColors()
  {
    int[] arrayOfInt = new int[2];
    arrayOfInt[0] = ListConfig.colorListItem1;
    arrayOfInt[1] = ListConfig.colorListItem2;
    return arrayOfInt;
  }

  public static class Account
  {
    public static int accountLogoId = R.drawable.endo_account;
    public static int iHaveAccountStringId = R.string.strHaveAccount;
  }

  public static class BusySpinners
  {
    public static int busySpinnerLargeId = R.drawable.busy_spinner;
    public static int busySpinnerSmallId = R.drawable.busy_spinner_small;
  }

  public static class ButtonBarConfig
  {
    public static int ButtonBarButtonDimTextColor = R.color.ButtonGrey;
    public static int ButtonBarButtonTextColor = R.color.white;
    public static int buttonBarOneButtonId;
    public static int buttonBarTwoButtonsId;
  }

  public static class ButtonConfig
  {
    public static int oneButtonId;
  }

  public static class DashboardConfig
  {
    public static boolean automaticallyShowAndHideGoalZone;
    public static int buttonCancelCountDownBackgroundId;
    public static int buttonCountDownBackgroundId;
    public static int buttonDimmedBackgroundId;
    public static int buttonPauseBackgroundId;
    public static int buttonResumeBackgroundId;
    public static int buttonStartBackgroundId;
    public static int buttonStopBackgroundId;
    public static int dbButtonAudioOffId;
    public static int dbButtonAudioOnId;
    public static int dbButtonMusicId;
    public static int dbTextTitleFontColorPressed;
    public static int dbTextTitleFontColorRest;
    public static int dbTextUnitFontColorPressed;
    public static int dbTextUnitFontColorRest;
    public static int dbTextValuesFontColorPressed;
    public static int dbTextValuesFontColorRest;
    public static boolean gpsStatusUpperCase;
    public static int motivationTextInfoFontColorPressed;
    public static int motivationTextInfoFontColorRest;
    public static int motivationTextTypeFontColorPressed;
    public static int motivationTextTypeFontColorRest;
    public static boolean showSpecialHrZone;
    public static boolean showZoneSelector;
    public static boolean showZoneTitle;
    public static int sportTextFontColorPressed;
    public static int sportTextFontColorRest;
    public static int trianglePressedId;
    public static int triangleRestId;
    public static int zoneSelectorId;
    public static boolean zoneUnitsInUpperCase;
  }

  public static class IconConfig
  {
    public static int headerButtonAddFriendsId;
    public static int headerButtonDetailsId;
    public static int headerButtonMapId;
  }

  public static class IconTextButtonConfig
  {
    public static int alpha;
    public static int background;
    public static int dimAlpha;
    public static int dimTextcolor = R.color.LightGrey;
    public static int textcolor = R.color.black;
    public static Typeface textface = Typeface.DEFAULT_BOLD;
    public static float textsize = 16.0F;
    public static float textsize_line2 = 12.0F;

    static
    {
      dimAlpha = 127;
      alpha = 255;
    }
  }

  public static class LVt
  {
    public static int iconExtension;
    public static int iconExtensionBw;
    public static int iconHostapp;
    public static int splashSrc = R.drawable.lvt_splash_free;

    static
    {
      iconHostapp = R.drawable.lvt_icon_108x108_free;
      iconExtension = R.drawable.lvt_icon_34x34_free;
      iconExtensionBw = R.drawable.lvt_icon_18x18_free_bw;
    }
  }

  public static class ListConfig
  {
    public static int colorListContainerBackground;
    public static int colorListItem1;
    public static int colorListItem2;
    public static int dividerHeightPixels;
    public static int dividerId;
    public static boolean drawSelectorOnTop = true;
    public static int selectorId;
    public static int separatorBackgroundId = R.color.list_separator_background;
    public static int separatorTextColorId = R.color.list_separator_text;
  }

  public static class LoginConfig
  {
    public static int cityScapeButtonId;
    public static int cityScapeCancelStringId;
    public static int cityScapeLayoutId;
    public static int loginTitleStringId;
  }

  public static class MenuKeyConfig
  {
    public static boolean newWorkoutInMenu;
    public static boolean settingsInMenu;
    public static boolean shareLatestOnFacebookInMenu;
  }

  public static class MotivationIconConfig
  {
    public static int beatYourselfIconId;
    public static int calorieGoalIconId;
    public static int intervalsIconId;
    public static int timeGoalIconId;
  }

  public static class NavigationConfig
  {
    public static boolean promptForAccountOnAppStart;
  }

  public static class NotificationConfig
  {
    public static int notificationIconId = R.drawable.header_icon;
    public static int notificationTitleStringId = R.string.strEndomondoSportTracker;
  }

  public static class SamsungConfigPro
  {
    public static boolean downloadTtsDataMarket;
    public static boolean jabraSupport;
  }

  public static class SettingsConfig
  {
    public static float defaultImageCornerRadius = 6.0F;
    public static boolean showProfileSettings;
    public static boolean showTitleInSettings;
  }

  public static class TitleConfig
  {
    public static int[] backgroundPaintColors;
    public static float[] backgroundPaintPositions;
    public static Shader.TileMode backgroundPaintTileMode;
    public static int titleAppNameId;
    public static int titleBannerImageId;
    public static int titleLogoDrawableId = 0;
    public static int titleOneLineId;
    public static int titleOneLineOneButtonId;
    public static int titleTwoLinesId;
    public static int titleTwoLinesOneButtonId;
    public static boolean useBackgroundPaint;
  }

  public static class ViewPagerConfig
  {
    public static int backgroundId = R.drawable.pager_indicator_header_bg;
    public static int bottomBorderColorId;
    public static boolean showIndicator;
    public static int textColorId;
    public static int textSizeInDp = 12;
    public static int textStyle = 1;

    static
    {
      textColorId = R.color.black;
      bottomBorderColorId = R.color.black;
      showIndicator = false;
    }
  }

  public static class WidgetConfig
  {
    public static int widgetItemLayoutId;
    public static int widgetLayoutId;
  }

  public static class WorkoutSummaryConfig
  {
    public static int backgroundImageColor = R.color.background_image;
    public static boolean showAllDetailsAtEndomondo = false;
  }
}
