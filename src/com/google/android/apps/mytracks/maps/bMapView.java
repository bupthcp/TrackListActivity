/**
 * 
 */
package com.google.android.apps.mytracks.maps;

/**
 * @author huchenpeng
 *
 */
import com.amap.mapapi.map.MapView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;

public class bMapView extends MapView{

  private int test1;
  private int test2;
  private Context ctx;
  /**
   * @param arg0
   */
  public bMapView(Context arg0) {
    super(arg0);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   * @param arg1
   * @param arg2
   */
  public bMapView(Context arg0, AttributeSet arg1, int arg2) {
    super(arg0, arg1, arg2);
    // TODO Auto-generated constructor stub
  }

  /**
   * @param arg0
   * @param arg1
   */
  public bMapView(Context arg0, AttributeSet arg1) {
    super(arg0, arg1);
    // TODO Auto-generated constructor stub
  }
  
  public void init(){
//    if (this.b == null)
//      this.b = new com.baidu.mapapi.a(getContext(), this);
//    this.m = new MapController(this);
//    addView(this.b);
//    this.n.setOnZoomOutClickListener(new View.OnClickListener()
//    {
//      public void onClick(View paramView)
//      {
//        bMapView.this.g();
//      }
//    });
//    this.n.setOnZoomInClickListener(new View.OnClickListener()
//    {
//      public void onClick(View paramView)
//      {
//        bMapView.this.f();
//      }
//    });
//    this.n.setFocusable(true);
//    this.n.setVisibility(0);
//    this.n.measure(0, 0);
//    try
//    {
//      char[] arrayOfChar = { 'l', 'h' };
//      int i1;
//      if (Mj.i <= 180)
//        i1 = 0;
//      else
//        i1 = 1;
//      String str = "baidumap_logo_" + arrayOfChar[i1] + ".png";
//      AssetManager localAssetManager = this.ll.getAssets();
//      InputStream localInputStream = localAssetManager.open(str);
//      Bitmap localBitmap = BitmapFactory.decodeStream(localInputStream);
//      localInputStream.close();
//      if (localBitmap != null)
//      {
//        this.o.setImageBitmap(localBitmap);
//        this.o.setVisibility(0);
//        this.o.measure(0, 0);
//        this.o.setScaleType(ImageView.ScaleType.FIT_CENTER);
//        addView(this.o);
//      }
//    }
//    catch (Exception localException)
//    {
//      Log.d("MapView()", "initMapView() error!");
//      localException.printStackTrace();
//    }
//    this.b.setFocusable(true);
//    this.b.setFocusableInTouchMode(true);
//    this.d = d();
  }
  
  public ViewGroup.LayoutParams generateLayoutParams(AttributeSet paramAttributeSet)
  {
//    return new LayoutParams(this.ll, paramAttributeSet);
    return null;
  }

}
