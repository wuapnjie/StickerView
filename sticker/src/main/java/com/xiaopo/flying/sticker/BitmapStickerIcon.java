package com.xiaopo.flying.sticker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by snowbean on 16-8-5.
 */
public class BitmapStickerIcon extends DrawableSticker {
  public static final float DEFAULT_ICON_RADIUS = 30f;
  public static final float DEFAULT_ICON_EXTRA_RADIUS = 10f;

  private float mIconRadius = DEFAULT_ICON_RADIUS;
  private float mIconExtraRadius = DEFAULT_ICON_EXTRA_RADIUS;
  private float x;
  private float y;

  public BitmapStickerIcon(Drawable drawable) {
    super(drawable);
  }

  public void draw(Canvas canvas, Paint paint) {
    canvas.drawCircle(x, y, mIconRadius, paint);
    super.draw(canvas);
  }

  public float getX() {
    return x;
  }

  public void setX(float x) {
    this.x = x;
  }

  public float getY() {
    return y;
  }

  public void setY(float y) {
    this.y = y;
  }

  public float getIconRadius() {
    return mIconRadius;
  }

  public void setIconRadius(float iconRadius) {
    mIconRadius = iconRadius;
  }

  public float getIconExtraRadius() {
    return mIconExtraRadius;
  }

  public void setIconExtraRadius(float iconExtraRadius) {
    mIconExtraRadius = iconExtraRadius;
  }
}
