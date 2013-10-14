package com.hu.iJogging.maps;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;

import android.graphics.Color;

import java.util.ArrayList;

public class PolyLine {
  private ArrayList<GeoPoint> points;
  private int color;
  private Geometry lineGeometry;
  private Symbol lineSymbol;
  private Symbol.Color lineColor;
  private Graphic lineGraphic;
  
  public PolyLine(){
    lineGeometry = new Geometry();
    lineSymbol = new Symbol();
    lineColor = lineSymbol.new Color();
    points = new ArrayList<GeoPoint>();
  }
  
  public void setPolyLineParam(int colorParam, int widthParam){
    lineColor.blue = Color.blue(colorParam);
    lineColor.red = Color.red(colorParam);
    lineColor.green = Color.green(colorParam);
    lineColor.alpha = Color.alpha(colorParam);
    lineSymbol.setLineSymbol(lineColor, widthParam);
    color = colorParam;
  }
  
  public void setPoints(ArrayList<GeoPoint> pointsParam){
    lineGeometry.setPolyLine(pointsParam.toArray(new GeoPoint[0]));
    points.clear();
    points.addAll(pointsParam);
    lineGraphic = new Graphic(lineGeometry, lineSymbol);
  }
  
  public ArrayList<GeoPoint> getPoints(){
    return points;
  }
  
  public int getColor(){
    return color;
  }
  
  public Graphic getLineGraphic(){
    return lineGraphic;
  }
}
