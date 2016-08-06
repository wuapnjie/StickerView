package com.xiaopo.flying.stickerview.sticker;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by snowbean on 16-8-6.
 */
public class DrawableSticker extends Sticker {
    protected static final String TAG = "DrawableSticker";

    private Drawable mDrawable;

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        mDrawable.draw(canvas);
    }

    @Override
    public int getWidth() {
        return mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return mDrawable.getIntrinsicHeight();
    }

}
