package com.xiaopo.flying.stickerview.sticker;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by snowbean on 16-8-2.
 */

public class BitmapSticker extends Sticker {
    private Bitmap mBitmap;

    public BitmapSticker(Bitmap bitmap, Matrix matrix) {
        mBitmap = bitmap;
        mMatrix = matrix;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(mBitmap, mMatrix, paint);
    }

    @Override
    public int getWidth() {
        return mBitmap.getWidth();
    }

    @Override
    public int getHeight() {
        return mBitmap.getHeight();
    }


    public void release() {
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
        }

        if (mMatrix != null) {
            mMatrix.reset();
            mMatrix = null;
        }

    }

}
