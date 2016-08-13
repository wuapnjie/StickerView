package com.xiaopo.flying.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;

/**
 * Created by snowbean on 16-8-6.
 */
public class DrawableSticker extends Sticker {
    protected static final String TAG = "DrawableSticker";

    private Drawable mDrawable;
    private Bitmap mBitmap;

    public DrawableSticker(Drawable drawable, Matrix matrix) {
        mDrawable = drawable;
        mMatrix = matrix;
    }

    public Drawable getDrawable() {
        return mDrawable;
    }

    public void setDrawable(Drawable drawable) {
        mDrawable = drawable;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }
        mBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(mBitmap);
        mDrawable.draw(c);
        canvas.drawBitmap(mBitmap, mMatrix, paint);
    }

    @Override
    public int getWidth() {
        return mDrawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return mDrawable.getIntrinsicHeight();
    }

    @Override
    public void release() {
        super.release();
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        if (mDrawable != null) {
            mDrawable = null;
        }
    }
}
