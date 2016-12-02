package com.xiaopo.flying.sticker;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

/**
 * Created by snowbean on 16-8-6.
 */
public abstract class Sticker {
    protected static final String TAG = "Sticker";

    protected Matrix mMatrix;
    protected boolean mIsFlipped;
    private float[] mMatrixValues = new float[9];

    public boolean isFlipped() {
        return mIsFlipped;
    }

    public void setFlipped(boolean flipped) {
        mIsFlipped = flipped;
    }

    public Matrix getMatrix() {
        return mMatrix;
    }

    public void setMatrix(Matrix matrix) {
        mMatrix.set(matrix);
    }

    public abstract void draw(Canvas canvas);

    public abstract int getWidth();

    public abstract int getHeight();

    public abstract void setDrawable(Drawable drawable);

    public abstract Drawable getDrawable();

    public float[] getBoundPoints() {
        if (!mIsFlipped) {
            return new float[]{
                    0f, 0f,
                    getWidth(), 0f,
                    0f, getHeight(),
                    getWidth(), getHeight()
            };
        } else {
            return new float[]{
                    getWidth(), 0f,
                    0f, 0f,
                    getWidth(), getHeight(),
                    0f, getHeight()
            };
        }
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

    public float getCurrentScale() {
        return getMatrixScale(mMatrix);
    }

    public float getCurrentHeight() {
        return getMatrixScale(mMatrix) * getHeight();
    }

    public float getCurrentWidth() {
        return getMatrixScale(mMatrix) * getWidth();
    }

    /**
     * This method calculates scale value for given Matrix object.
     */
    private float getMatrixScale(@NonNull Matrix matrix) {
        return (float) Math.sqrt(Math.pow(getMatrixValue(matrix, Matrix.MSCALE_X), 2)
                + Math.pow(getMatrixValue(matrix, Matrix.MSKEW_Y), 2));
    }

    /**
     * @return - current image rotation angle.
     */
    public float getCurrentAngle() {
        return getMatrixAngle(mMatrix);
    }

    /**
     * This method calculates rotation angle for given Matrix object.
     */
    private float getMatrixAngle(@NonNull Matrix matrix) {
        return (float) -(Math.atan2(getMatrixValue(matrix, Matrix.MSKEW_X),
                getMatrixValue(matrix, Matrix.MSCALE_X)) * (180 / Math.PI));
    }

    private float getMatrixValue(@NonNull Matrix matrix, @IntRange(from = 0, to = 9) int valueIndex) {
        matrix.getValues(mMatrixValues);
        return mMatrixValues[valueIndex];
    }

    public void release() {
    }

}
