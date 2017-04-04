package com.xiaopo.flying.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/**
 * @author wupanjie
 */
public class DrawableSticker extends Sticker {

  private Drawable drawable;
  private Rect realBounds;

  public DrawableSticker(Drawable drawable) {
    this.drawable = drawable;
    this.matrix = new Matrix();
    realBounds = new Rect(0, 0, getWidth(), getHeight());
  }

  @Override public Drawable getDrawable() {
    return drawable;
  }

  @Override public void setDrawable(Drawable drawable) {
    this.drawable = drawable;
  }

  @Override public void draw(Canvas canvas) {
    canvas.save();
    canvas.concat(matrix);
    drawable.setBounds(realBounds);
    drawable.draw(canvas);
    canvas.restore();
  }

  @Override
  public void setAlpha(int alpha) {
    drawable.setAlpha(alpha);
  }

  @Override public int getWidth() {
    return drawable.getIntrinsicWidth();
  }

  @Override public int getHeight() {
    return drawable.getIntrinsicHeight();
  }

  @Override public void release() {
    super.release();
    if (drawable != null) {
      drawable = null;
    }
  }
}
