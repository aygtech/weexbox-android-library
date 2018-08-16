package com.weexbox.core.adapter;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

public class BlurTransformation implements Transformation {

  private int mRadius;

  public BlurTransformation(int radius) {
    mRadius = radius;
  }

  @Override public Bitmap transform(Bitmap source) {
    if(mRadius <= 0) {
      return source;
    }
    Bitmap bitmap;
    try {
      bitmap = BlurTool.blur(source, mRadius);
    }catch (Exception e){
      bitmap = source;
    }
    if(bitmap != source) {
      source.recycle();
    }
    return bitmap;
  }

  @Override public String key() {
    return "BlurTransformation(radius=" + mRadius + ")";
  }
}
