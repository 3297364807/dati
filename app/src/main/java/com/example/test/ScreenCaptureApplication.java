package com.example.test;

import android.app.Application;
import android.graphics.Bitmap;
public class ScreenCaptureApplication extends Application {


  private Bitmap mScreenCaptureBitmap;

  @Override
  public void onCreate() {
    super.onCreate();
  }


  public Bitmap getmScreenCaptureBitmap() {
    return mScreenCaptureBitmap;
  }

  public void setmScreenCaptureBitmap(Bitmap mScreenCaptureBitmap) {
    this.mScreenCaptureBitmap = mScreenCaptureBitmap;
  }
}
