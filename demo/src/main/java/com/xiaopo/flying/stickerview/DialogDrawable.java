package com.xiaopo.flying.stickerview;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

/**
 * Created by snowbean on 16-8-6.
 */
public class DialogDrawable extends Drawable {

    private Paint mBorderPaint;
    private Paint mTextPaint;
    private RectF mRectF;
    private int mRadius = 20;
    private int mOffsetY = 30;
    private int mOffsetX = 0;

    public DialogDrawable() {
        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.parseColor("#bb000000"));

        mTextPaint = new TextPaint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(40);

        mRectF = new RectF(0, 0, getIntrinsicWidth() - mOffsetX, getIntrinsicHeight() - mOffsetY);
    }

    @Override
    public int getIntrinsicWidth() {
        return 300;
    }

    @Override
    public int getIntrinsicHeight() {
        return 300;
    }

    public int getRadius() {
        return mRadius;
    }

    public void setRadius(int radius) {
        mRadius = radius;
    }

    public int getOffsetY() {
        return mOffsetY;
    }

    public void setOffsetY(int offsetY) {
        mOffsetY = offsetY;
    }

    public int getOffsetX() {
        return mOffsetX;
    }

    public void setOffsetX(int offsetX) {
        mOffsetX = offsetX;
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        if (mRectF == null) {
            mRectF = new RectF(left, top, right - mOffsetX, bottom - mOffsetY);
        } else {
            mRectF.left = left;
            mRectF.top = top;
            mRectF.right = right;
            mRectF.bottom = bottom;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mBorderPaint);

        Path path = new Path();
        path.moveTo(mRectF.right - 2 * mOffsetY - mOffsetX, mRectF.bottom);
        path.lineTo(mRectF.right - (2 * mOffsetY + mRadius) / 2 - mOffsetX, mRectF.bottom + mOffsetY);
        path.lineTo(mRectF.right - mRadius - mOffsetX, mRectF.bottom);

        float x = 0;
        float y = mRectF.centerY();

        String text = "One Piece";
        float[] characterWidths = new float[text.length()];
        int characterNum = mTextPaint.getTextWidths(text, characterWidths);

        float textWidth = 0f;
        for (int i = 0; i < characterNum; i++) {
            textWidth += characterWidths[i];
        }
        canvas.save();
        canvas.translate(mRectF.width() / 2 - textWidth / 2, 0);
        canvas.drawText("one piece", x, y, mTextPaint);

        canvas.restore();
        canvas.drawPath(path, mBorderPaint);
    }

    @Override
    public void setAlpha(int i) {
        mBorderPaint.setAlpha(i);
        mTextPaint.setAlpha(i);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return 0;
    }
}
