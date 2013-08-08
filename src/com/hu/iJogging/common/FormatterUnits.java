package com.hu.iJogging.common;

import android.content.Context;

public abstract class FormatterUnits {
  protected static float inchesInCm;
  protected static String stringLiter;
  protected static String stringMeter;
  protected static String stringMeters;
  protected static String stringOunce;
  protected static String stringOunces;
  protected static String stringYard;
  protected static String stringYards;
  protected static float yardInMeters = 0.9144F;
  
  static
  {
    inchesInCm = 2.54F;
    stringYard = "yard";
    stringYards = "yards";
    stringMeter = "meter";
    stringMeters = "meters";
    stringLiter = "liter";
    stringOunce = "ounce";
    stringOunces = "ounces";
  }
  
  public static FormatterUnits getFormatter()
  {
      return new FormatterMetric();
  }
  
  public String getDistanceText(Context paramContext)
  {
    return null;
  }
  
  public String getSpeedText(Context paramContext)
  {
    return null;
  }
  
  public String getDistanceMeterText(Context paramContext){
    return null;
  }
}
