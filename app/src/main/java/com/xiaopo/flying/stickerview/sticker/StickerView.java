package com.xiaopo.flying.stickerview.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.xiaopo.flying.stickerview.R;
import com.xiaopo.flying.stickerview.util.BitmapUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * sticker view
 * Created by snowbean on 16-8-2.
 */

public class StickerView extends ImageView {
    private enum ActionMode {
        NONE,   //nothing
        DRAG,   //drag the sticker with your finger
        ZOOM_WITH_TWO_FINGER,   //zoom in or zoom out the sticker and rotate the sticker with two finger
        ZOOM_WITH_ICON,    //zoom in or zoom out the sticker and rotate the sticker with icon
        DELETE  //delete the handling sticker
    }

    private static final String TAG = "StickerView";
    public static final float DEFAULT_ICON_RADIUS = 30f;
    public static final float DEFAULT_ICON_EXTRA_RADIUS = 10f;

    private Paint mBitmapPaint;
    private Paint mBorderPaint;

    private RectF mStickerRect;

    private Matrix mSizeMatrix;
    private Matrix mDownMatrix;
    private Matrix mMoveMatrix;

    private BitmapStickerIcon mDeleteIcon;
    private BitmapStickerIcon mZoomIcon;

    private float mIconRadius = DEFAULT_ICON_RADIUS;
    private float mIconExtraRadius = DEFAULT_ICON_EXTRA_RADIUS;

    //the first point down position
    private float mDownX;
    private float mDownY;

    private float mOldDistance = 1f;
    private float mOldRotation = 0;

    private PointF mMidPoint;

    private ActionMode mCurrentMode = ActionMode.NONE;

    private List<Sticker> mStickers = new ArrayList<>();
    private Sticker mHandlingSticker;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setFilterBitmap(true);

        mBorderPaint = new Paint();
        mBorderPaint.setAntiAlias(true);
        mBorderPaint.setColor(Color.BLACK);
        mBorderPaint.setAlpha(160);

        mSizeMatrix = new Matrix();
        mDownMatrix = new Matrix();
        mMoveMatrix = new Matrix();

        mStickerRect = new RectF();

        Bitmap deleteIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_close_white_18dp);
        mDeleteIcon = new BitmapStickerIcon(deleteIcon, new Matrix());
        Bitmap zoomIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_scale_white_18dp);
        mZoomIcon = new BitmapStickerIcon(zoomIcon, new Matrix());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            mStickerRect.left = left;
            mStickerRect.top = top;
            mStickerRect.right = right;
            mStickerRect.bottom = bottom;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (int i = 0; i < mStickers.size(); i++) {
            Sticker sticker = mStickers.get(i);
            if (sticker != null) {
                sticker.draw(canvas, mBitmapPaint);
            }
        }

        if (mHandlingSticker != null) {

            float[] bitmapPoints = getStickerPoints(mHandlingSticker);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            canvas.drawLine(x1, y1, x2, y2, mBorderPaint);
            canvas.drawLine(x1, y1, x3, y3, mBorderPaint);
            canvas.drawLine(x2, y2, x4, y4, mBorderPaint);
            canvas.drawLine(x4, y4, x3, y3, mBorderPaint);

            float rotation = calculateRotation(x3, y3, x4, y4);
            //draw delete icon
            canvas.drawCircle(x1, y1, mIconRadius, mBorderPaint);
            mDeleteIcon.setX(x1);
            mDeleteIcon.setY(y1);
            mDeleteIcon.getMatrix().reset();

            mDeleteIcon.getMatrix().postRotate(
                    rotation, mDeleteIcon.getBitmap().getWidth() / 2, mDeleteIcon.getBitmap().getHeight() / 2);
            mDeleteIcon.getMatrix().postTranslate(
                    x1 - mDeleteIcon.getBitmap().getWidth() / 2, y1 - mDeleteIcon.getBitmap().getHeight() / 2);

            canvas.drawBitmap(mDeleteIcon.getBitmap(), mDeleteIcon.getMatrix(), mBitmapPaint);

            //draw zoom icon
            canvas.drawCircle(x4, y4, mIconRadius, mBorderPaint);
            mZoomIcon.setX(x4);
            mZoomIcon.setY(y4);

            mZoomIcon.getMatrix().reset();
            mZoomIcon.getMatrix().postRotate(
                    45f + rotation, mZoomIcon.getBitmap().getWidth() / 2, mZoomIcon.getBitmap().getHeight() / 2);

            mZoomIcon.getMatrix().postTranslate(
                    x4 - mZoomIcon.getBitmap().getWidth() / 2, y4 - mZoomIcon.getBitmap().getHeight() / 2);

            canvas.drawBitmap(mZoomIcon.getBitmap(), mZoomIcon.getMatrix(), mBitmapPaint);
        }

    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mCurrentMode = ActionMode.DRAG;

                mDownX = event.getX();
                mDownY = event.getY();

                if (checkDeleteIconTouched(mIconExtraRadius)) {
                    mCurrentMode = ActionMode.DELETE;
                } else if (checkZoomIconTouched(mIconExtraRadius)) {
                    mCurrentMode = ActionMode.ZOOM_WITH_ICON;
                    mMidPoint = calculateMidPoint();
                    mOldDistance = calculateDistance(mMidPoint.x, mMidPoint.y, mDownX, mDownY);
                    mOldRotation = calculateRotation(mMidPoint.x, mMidPoint.y, mDownX, mDownY);
                } else {
                    mHandlingSticker = findHandlingSticker();
                }

                if (mHandlingSticker != null) {
                    mDownMatrix.set(mHandlingSticker.getMatrix());
                }

                invalidate();
                break;


            case MotionEvent.ACTION_POINTER_DOWN:
                Log.d(TAG, "onTouchEvent: point down ->" + event.getPointerCount());

                mOldDistance = calculateDistance(event);
                mOldRotation = calculateRotation(event);

                mMidPoint = calculateMidPoint(event);

                if (mHandlingSticker != null &&
                        isInStickerArea(mHandlingSticker, event.getX(1), event.getY(1)) &&
                        !checkDeleteIconTouched(mIconExtraRadius))

                    mCurrentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                break;

            case MotionEvent.ACTION_MOVE:
                switch (mCurrentMode) {
                    case NONE:
                        break;
                    case DRAG:

                        if (mHandlingSticker != null) {
                            mMoveMatrix.set(mDownMatrix);
                            mMoveMatrix.postTranslate(event.getX() - mDownX, event.getY() - mDownY);
//                            mHandlingSticker.getMatrix().reset();
                            mHandlingSticker.getMatrix().set(mMoveMatrix);
                        }
                        break;
                    case ZOOM_WITH_TWO_FINGER:
                        if (mHandlingSticker != null) {
                            float newDistance = calculateDistance(event);
                            float newRotation = calculateRotation(event);

                            mMoveMatrix.set(mDownMatrix);
                            mMoveMatrix.postScale(
                                    newDistance / mOldDistance, newDistance / mOldDistance, mMidPoint.x, mMidPoint.y);
                            mMoveMatrix.postRotate(newRotation - mOldRotation, mMidPoint.x, mMidPoint.y);
//                            mHandlingSticker.getMatrix().reset();
                            mHandlingSticker.getMatrix().set(mMoveMatrix);
                        }

                        break;

                    case ZOOM_WITH_ICON:
                        if (mHandlingSticker != null) {
                            float newDistance = calculateDistance(mMidPoint.x, mMidPoint.y, event.getX(), event.getY());
                            float newRotation = calculateRotation(mMidPoint.x, mMidPoint.y, event.getX(), event.getY());

                            mMoveMatrix.set(mDownMatrix);
                            mMoveMatrix.postScale(
                                    newDistance / mOldDistance, newDistance / mOldDistance, mMidPoint.x, mMidPoint.y);
                            mMoveMatrix.postRotate(newRotation - mOldRotation, mMidPoint.x, mMidPoint.y);
//                            mHandlingSticker.getMatrix().reset();
                            mHandlingSticker.getMatrix().set(mMoveMatrix);
                        }

                        break;
                }
                invalidate();

                break;

            case MotionEvent.ACTION_UP:
                if (mCurrentMode == ActionMode.DELETE) {
                    mStickers.remove(mHandlingSticker);
                    mHandlingSticker.release();
                    mHandlingSticker = null;
                    invalidate();
                }
                mCurrentMode = ActionMode.NONE;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                mCurrentMode = ActionMode.NONE;
                break;

        }

        return true;
    }

    //判断是否按在缩放按钮区域
    private boolean checkZoomIconTouched(float extraRadius) {
        float x = mZoomIcon.getX() - mDownX;
        float y = mZoomIcon.getY() - mDownY;
        float distance_pow_2 = x * x + y * y;
        return distance_pow_2 <= (mIconRadius + extraRadius) * (mIconRadius + extraRadius);
    }

    //判断是否按在删除按钮区域
    private boolean checkDeleteIconTouched(float extraRadius) {
        float x = mDeleteIcon.getX() - mDownX;
        float y = mDeleteIcon.getY() - mDownY;
        float distance_pow_2 = x * x + y * y;
        return distance_pow_2 <= (mIconRadius + extraRadius) * (mIconRadius + extraRadius);
    }

    //找到点击的区域属于哪个贴纸
    private Sticker findHandlingSticker() {
        for (int i = mStickers.size() - 1; i >= 0; i--) {
            if (isInStickerArea(mStickers.get(i), mDownX, mDownY)) {
                Log.d(TAG, "findHandlingSticker: " + i);
                return mStickers.get(i);
            }
        }
        return null;
    }

    private boolean isInStickerArea(Sticker sticker, float downX, float downY) {
        RectF dst = sticker.getMappedBound();
        return dst.contains(downX, downY);
    }

    private PointF calculateMidPoint(MotionEvent event) {
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    private PointF calculateMidPoint() {
        return mHandlingSticker.getMappedCenterPoint();
    }

    //计算两点形成的直线与x轴的旋转角度
    private float calculateRotation(MotionEvent event) {
        double x = event.getX(0) - event.getX(1);
        double y = event.getY(0) - event.getY(1);
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    private float calculateRotation(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;
        double radians = Math.atan2(y, x);
        return (float) Math.toDegrees(radians);
    }

    //计算两点间的距离
    private float calculateDistance(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);

        return (float) Math.sqrt(x * x + y * y);
    }

    private float calculateDistance(float x1, float y1, float x2, float y2) {
        double x = x1 - x2;
        double y = y1 - y2;

        return (float) Math.sqrt(x * x + y * y);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.e(TAG, "onSizeChanged: ");
        for (int i = 0; i < mStickers.size(); i++) {
            Sticker sticker = mStickers.get(i);
            if (sticker != null) {
                transformSticker(sticker);
            }
        }

    }

    //sticker的图片会过大或过小，需要转化
    //step 1：使sticker图片的中心与View的中心重合
    //step 2：计算缩放值，进行缩放
    private void transformSticker(Sticker sticker) {
        if (sticker == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }


        if (mSizeMatrix != null) {
            mSizeMatrix.reset();
        }

        //step 1
        float offsetX = (getWidth() - sticker.getWidth()) / 2;
        float offsetY = (getHeight() - sticker.getHeight()) / 2;

        mSizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / sticker.getWidth();
        } else {
            scaleFactor = (float) getHeight() / sticker.getHeight();
        }

        mSizeMatrix.postScale(scaleFactor / 2, scaleFactor / 2,
                getWidth() / 2, getHeight() / 2);

        sticker.getMatrix().reset();
        sticker.getMatrix().set(mSizeMatrix);

        invalidate();
    }

    public float[] getStickerPoints(Sticker sticker) {
        if (sticker == null) return new float[8];

        return sticker.getMappedBoundPoints();
    }

    public void setSticker(BitmapSticker bitmapSticker) {
        mStickers.clear();
        mStickers.add(bitmapSticker);
        mHandlingSticker = bitmapSticker;
        invalidate();
    }

    public void addSticker(BitmapSticker bitmapSticker) {
        mStickers.add(bitmapSticker);
        mHandlingSticker = bitmapSticker;
        invalidate();
    }

    public void addSticker(Bitmap stickerBitmap) {
        Matrix matrix = new Matrix();

        Sticker bitmapSticker = new BitmapSticker(stickerBitmap, matrix);

        float offsetX = (getWidth() - bitmapSticker.getWidth()) / 2;
        float offsetY = (getHeight() - bitmapSticker.getHeight()) / 2;
        bitmapSticker.getMatrix().postTranslate(offsetX, offsetY);

        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / bitmapSticker.getWidth();
        } else {
            scaleFactor = (float) getHeight() / bitmapSticker.getHeight();
        }
        bitmapSticker.getMatrix().postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        mHandlingSticker = bitmapSticker;
        mStickers.add(bitmapSticker);

        invalidate();
    }

    public void addSticker(Drawable stickerDrawable) {
        Matrix matrix = new Matrix();

        Sticker drawableSticker = new DrawableSticker(stickerDrawable, matrix);

        float offsetX = (getWidth() - drawableSticker.getWidth()) / 2;
        float offsetY = (getHeight() - drawableSticker.getHeight()) / 2;
        drawableSticker.getMatrix().postTranslate(offsetX, offsetY);

        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / stickerDrawable.getIntrinsicWidth();
        } else {
            scaleFactor = (float) getHeight() / stickerDrawable.getIntrinsicWidth();
        }
        drawableSticker.getMatrix().postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        mHandlingSticker = drawableSticker;
        mStickers.add(drawableSticker);

        invalidate();
    }

    public void save(File file) {
        Bitmap bitmap = null;
        FileOutputStream outputStream = null;

        try {
            bitmap = createBitmap();
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

            BitmapUtil.notifySystemGallery(getContext(), file);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (bitmap != null) {
                bitmap.recycle();
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public Bitmap createBitmap() {
        mHandlingSticker = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        return bitmap;
    }

    public float getIconRadius() {
        return mIconRadius;
    }

    public void setIconRadius(float iconRadius) {
        mIconRadius = iconRadius;
        invalidate();
    }

    public float getIconExtraRadius() {
        return mIconExtraRadius;
    }

    public void setIconExtraRadius(float iconExtraRadius) {
        mIconExtraRadius = iconExtraRadius;
    }
}
