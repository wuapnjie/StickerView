package com.xiaopo.flying.stickerview.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;

/**
 * Created by snowbean on 16-8-6.
 */
public abstract class Sticker {
    protected static final String TAG = "Sticker";

    protected Matrix mMatrix;

    public Matrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(Matrix matrix) {
        mMatrix = matrix;
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public abstract int getWidth();

    public abstract int getHeight();

    public float[] getBoundPoints() {
        return new float[]{
                0f, 0f,
                getWidth(), 0f,
                0f, getHeight(),
                getWidth(), getHeight()
        };
    }

    public float[] getMappedBoundPoints() {
        float[] dst = new float[8];
        mMatrix.mapPoints(dst, getBoundPoints());
        return dst;
    }

    public float[] getMappedPoints(float[] src) {
        float[] dst = new float[src.length];
        mMatrix.mapPoints(dst, src);
        return dst;
    }


    public RectF getBound() {
        return new RectF(0, 0, getWidth(), getHeight());
    }

    public RectF getMappedBound() {
        RectF dst = new RectF();
        mMatrix.mapRect(dst, getBound());
        return dst;
    }

    public PointF getCenterPoint() {
        return new PointF(getWidth() / 2, getHeight() / 2);
    }

    public PointF getMappedCenterPoint() {
        PointF pointF = getCenterPoint();
        float[] dst = getMappedPoints(new float[]{
                pointF.x,
                pointF.y
        });
        return new PointF(dst[0], dst[1]);
    }

    public void release(){
        if (mMatrix!=null){
            mMatrix.reset();
            mMatrix = null;
        }
    }

}
