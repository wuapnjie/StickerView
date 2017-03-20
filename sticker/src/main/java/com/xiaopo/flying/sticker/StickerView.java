package com.xiaopo.flying.sticker;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.FrameLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Sticker view
 * Created by snowbean on 16-8-2.
 */

public class StickerView extends FrameLayout {

    private final boolean showIcons;
    private final boolean showBorder;

    private enum ActionMode {
        NONE,   //nothing
        DRAG,   //drag the sticker with your finger
        ZOOM_WITH_TWO_FINGER,   //zoom in or zoom out the sticker and rotate the sticker with two finger
        ICON,    //touch in icon
        CLICK    //Click the Sticker
    }

    private static final String TAG = "StickerView";

    private static final int DEFAULT_MIN_CLICK_DELAY_TIME = 200;
    public static final int FLIP_HORIZONTALLY = 0;
    public static final int FLIP_VERTICALLY = 1;

    private Paint borderPaint;

    private RectF stickerRect;
    private Matrix sizeMatrix;
    private Matrix downMatrix;

    private Matrix moveMatrix;

    private BitmapStickerIcon currentIcon;

    private List<BitmapStickerIcon> icons = new ArrayList<>(4);
    //the first point down position
    private float downX;
    private float downY;

    private float oldDistance = 0f;
    private float oldRotation = 0f;

    private PointF midPoint;

    private ActionMode currentMode = ActionMode.NONE;
    private List<Sticker> stickers = new ArrayList<>();

    private Sticker handlingSticker;

    private boolean locked;
    private boolean constrained;

    private int touchSlop = 3;

    private OnStickerOperationListener onStickerOperationListener;

    private long lastClickTime = 0;
    private int minClickDelayTime = DEFAULT_MIN_CLICK_DELAY_TIME;

    public StickerView(Context context) {
        this(context, null);
    }

    public StickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StickerView);
        showIcons = a.getBoolean(R.styleable.StickerView_showIcons, false);
        showBorder = a.getBoolean(R.styleable.StickerView_showBorder, false);
//    if (s != null) {
//      this.setAlternativeKeyLabel(s.toString());
//    }
        a.recycle();

        borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setColor(Color.BLACK);
        borderPaint.setAlpha(128);

        sizeMatrix = new Matrix();
        downMatrix = new Matrix();
        moveMatrix = new Matrix();

        stickerRect = new RectF();

        configDefaultIcons();
    }

    public void configDefaultIcons() {
        BitmapStickerIcon deleteIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_close_white_18dp),
                BitmapStickerIcon.LEFT_TOP);
        deleteIcon.setIconEvent(new DeleteIconEvent());
        BitmapStickerIcon zoomIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_scale_white_18dp),
                BitmapStickerIcon.RIGHT_BOTOM);
        zoomIcon.setIconEvent(new ZoomIconEvent());
        BitmapStickerIcon flipIcon = new BitmapStickerIcon(
                ContextCompat.getDrawable(getContext(), R.drawable.sticker_ic_flip_white_18dp),
                BitmapStickerIcon.RIGHT_TOP);
        flipIcon.setIconEvent(new FlipHorizontallyEvent());

        icons.clear();
        icons.add(deleteIcon);
        icons.add(zoomIcon);
        icons.add(flipIcon);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (changed) {
            stickerRect.left = left;
            stickerRect.top = top;
            stickerRect.right = right;
            stickerRect.bottom = bottom;
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        drawStickers(canvas);
    }

    private void drawStickers(Canvas canvas) {
        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            if (sticker != null) {
                sticker.draw(canvas);
            }
        }

        if (handlingSticker != null && !locked) {


            float[] bitmapPoints = getStickerPoints(handlingSticker);

            float x1 = bitmapPoints[0];
            float y1 = bitmapPoints[1];
            float x2 = bitmapPoints[2];
            float y2 = bitmapPoints[3];
            float x3 = bitmapPoints[4];
            float y3 = bitmapPoints[5];
            float x4 = bitmapPoints[6];
            float y4 = bitmapPoints[7];

            if (showBorder) {
                canvas.drawLine(x1, y1, x2, y2, borderPaint);
                canvas.drawLine(x1, y1, x3, y3, borderPaint);
                canvas.drawLine(x2, y2, x4, y4, borderPaint);
                canvas.drawLine(x4, y4, x3, y3, borderPaint);
            }

            //draw icons
            if (showIcons) {
                float rotation = calculateRotation(x4, y4, x3, y3);
                for (BitmapStickerIcon icon : icons) {
                    switch (icon.getPosition()) {
                        case BitmapStickerIcon.LEFT_TOP:

                            configIconMatrix(icon, x1, y1, rotation);
                            break;

                        case BitmapStickerIcon.RIGHT_TOP:
                            configIconMatrix(icon, x2, y2, rotation);
                            break;

                        case BitmapStickerIcon.LEFT_BOTTOM:
                            configIconMatrix(icon, x3, y3, rotation);
                            break;

                        case BitmapStickerIcon.RIGHT_BOTOM:
                            configIconMatrix(icon, x4, y4, rotation);
                            break;
                    }
                    icon.draw(canvas, borderPaint);
                }
            }
        }
    }

    private void configIconMatrix(BitmapStickerIcon icon, float x, float y, float rotation) {
        icon.setX(x);
        icon.setY(y);
        icon.getMatrix().reset();

        icon.getMatrix().postRotate(rotation, icon.getWidth() / 2, icon.getHeight() / 2);
        icon.getMatrix().postTranslate(x - icon.getWidth() / 2, y - icon.getHeight() / 2);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (locked) return super.onInterceptTouchEvent(ev);

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();

                return findCurrentIconTouched() != null || findHandlingSticker() != null;
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (locked) return super.onTouchEvent(event);

        int action = MotionEventCompat.getActionMasked(event);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                currentMode = ActionMode.DRAG;

                downX = event.getX();
                downY = event.getY();

                midPoint = calculateMidPoint();
                oldDistance = calculateDistance(midPoint.x, midPoint.y, downX, downY);
                oldRotation = calculateRotation(midPoint.x, midPoint.y, downX, downY);

                currentIcon = findCurrentIconTouched();
                if (currentIcon != null) {
                    currentMode = ActionMode.ICON;
                    currentIcon.onActionDown(this, event);
                } else {
                    handlingSticker = findHandlingSticker();
                }

                if (handlingSticker != null) {
                    downMatrix.set(handlingSticker.getMatrix());
                }

                invalidate();
                break;

            case MotionEvent.ACTION_POINTER_DOWN:

                oldDistance = calculateDistance(event);
                oldRotation = calculateRotation(event);

                midPoint = calculateMidPoint(event);

                if (handlingSticker != null && isInStickerArea(handlingSticker, event.getX(1),
                        event.getY(1)) && findCurrentIconTouched() == null) {
                    currentMode = ActionMode.ZOOM_WITH_TWO_FINGER;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                handleCurrentMode(event);
                invalidate();
                break;

            case MotionEvent.ACTION_UP:
                long currentTime = SystemClock.uptimeMillis();

                if (currentMode == ActionMode.ICON && currentIcon != null && handlingSticker != null) {
                    currentIcon.onActionUp(this, event);
                }

                if (currentMode == ActionMode.DRAG
                        && Math.abs(event.getX() - downX) < touchSlop
                        && Math.abs(event.getY() - downY) < touchSlop
                        && handlingSticker != null) {
                    currentMode = ActionMode.CLICK;
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerClicked(handlingSticker);
                    }
                    if (currentTime - lastClickTime < minClickDelayTime) {
                        if (onStickerOperationListener != null) {
                            onStickerOperationListener.onStickerDoubleTapped(handlingSticker);
                        }
                    }
                }

                if (currentMode == ActionMode.DRAG && handlingSticker != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerDragFinished(handlingSticker);
                    }
                }

                currentMode = ActionMode.NONE;
                lastClickTime = currentTime;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                if (currentMode == ActionMode.ZOOM_WITH_TWO_FINGER && handlingSticker != null) {
                    if (onStickerOperationListener != null) {
                        onStickerOperationListener.onStickerZoomFinished(handlingSticker);
                    }
                }
                currentMode = ActionMode.NONE;
                break;
        }//end of switch(action)

        return true;
    }

    private void handleCurrentMode(MotionEvent event) {
        switch (currentMode) {
            case NONE:
                break;
            case DRAG:
                if (handlingSticker != null) {
                    moveMatrix.set(downMatrix);
                    moveMatrix.postTranslate(event.getX() - downX, event.getY() - downY);
                    handlingSticker.getMatrix().set(moveMatrix);
                    //constrain sticker
                    if (constrained) constrainSticker();
                }
                break;
            case ZOOM_WITH_TWO_FINGER:
                if (handlingSticker != null) {
                    float newDistance = calculateDistance(event);
                    float newRotation = calculateRotation(event);

                    moveMatrix.set(downMatrix);
                    moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                            midPoint.y);
                    moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
                    handlingSticker.getMatrix().set(moveMatrix);
                }

                break;

            case ICON:
                if (handlingSticker != null && currentIcon != null) {
                    currentIcon.onActionMove(this, event);
                }

                break;
        }// end of switch(currentMode)
    }

    public void zoomAndRotateCurrentSticker(MotionEvent event) {
        zoomAndRotateSticker(handlingSticker, event);
    }

    public void zoomAndRotateSticker(Sticker sticker, MotionEvent event) {
        if (sticker != null) {
            float newDistance = calculateDistance(midPoint.x, midPoint.y, event.getX(), event.getY());
            float newRotation = calculateRotation(midPoint.x, midPoint.y, event.getX(), event.getY());

            moveMatrix.set(downMatrix);
            moveMatrix.postScale(newDistance / oldDistance, newDistance / oldDistance, midPoint.x,
                    midPoint.y);
            moveMatrix.postRotate(newRotation - oldRotation, midPoint.x, midPoint.y);
            handlingSticker.getMatrix().set(moveMatrix);
        }
    }

    private void constrainSticker() {
        float moveX = 0;
        float moveY = 0;
        PointF currentCenterPoint = handlingSticker.getMappedCenterPoint();
        if (currentCenterPoint.x < 0) {
            moveX = -currentCenterPoint.x;
        }

        if (currentCenterPoint.x > getWidth()) {
            moveX = getWidth() - currentCenterPoint.x;
        }

        if (currentCenterPoint.y < 0) {
            moveY = -currentCenterPoint.y;
        }

        if (currentCenterPoint.y > getHeight()) {
            moveY = getHeight() - currentCenterPoint.y;
        }

        handlingSticker.getMatrix().postTranslate(moveX, moveY);
    }

    private BitmapStickerIcon findCurrentIconTouched() {
        for (BitmapStickerIcon icon : icons) {
            float x = icon.getX() - downX;
            float y = icon.getY() - downY;
            float distance_pow_2 = x * x + y * y;
            if (distance_pow_2 <= Math.pow(icon.getIconRadius() + icon.getIconRadius(), 2)) {
                return icon;
            }
        }

        return null;
    }

    /**
     * find the touched Sticker
     **/
    private Sticker findHandlingSticker() {
        for (int i = stickers.size() - 1; i >= 0; i--) {
            if (isInStickerArea(stickers.get(i), downX, downY)) {
                return stickers.get(i);
            }
        }
        return null;
    }

    private boolean isInStickerArea(Sticker sticker, float downX, float downY) {
        return sticker.contains(downX, downY);
    }

    private PointF calculateMidPoint(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return new PointF();
        float x = (event.getX(0) + event.getX(1)) / 2;
        float y = (event.getY(0) + event.getY(1)) / 2;
        return new PointF(x, y);
    }

    private PointF calculateMidPoint() {
        if (handlingSticker == null) return new PointF();
        return handlingSticker.getMappedCenterPoint();
    }

    /**
     * calculate rotation in line with two fingers and x-axis
     **/
    private float calculateRotation(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
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

    /**
     * calculate Distance in two fingers
     **/
    private float calculateDistance(MotionEvent event) {
        if (event == null || event.getPointerCount() < 2) return 0f;
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
        for (int i = 0; i < stickers.size(); i++) {
            Sticker sticker = stickers.get(i);
            if (sticker != null) {
                transformSticker(sticker);
            }
        }
    }

    /**
     * Sticker's drawable will be too bigger or smaller
     * This method is to transform it to fit
     * step 1：let the center of the sticker image is coincident with the center of the View.
     * step 2：Calculate the zoom and zoom
     **/
    private void transformSticker(Sticker sticker) {
        if (sticker == null) {
            Log.e(TAG, "transformSticker: the bitmapSticker is null or the bitmapSticker bitmap is null");
            return;
        }

        if (sizeMatrix != null) {
            sizeMatrix.reset();
        }

        //step 1
        float offsetX = (getWidth() - sticker.getWidth()) / 2;
        float offsetY = (getHeight() - sticker.getHeight()) / 2;

        sizeMatrix.postTranslate(offsetX, offsetY);

        //step 2
        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / sticker.getWidth();
        } else {
            scaleFactor = (float) getHeight() / sticker.getHeight();
        }

        sizeMatrix.postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        sticker.getMatrix().reset();
        sticker.getMatrix().set(sizeMatrix);

        invalidate();
    }

    public void flipCurrentSticker(int direction) {
        flip(handlingSticker, direction);
    }

    public void flip(Sticker sticker, int direction) {
        if (sticker != null) {
            if (direction == FLIP_HORIZONTALLY) {
                sticker.getMatrix().preScale(-1, 1, sticker.getCenterPoint().x, sticker.getCenterPoint().y);
                sticker.setFlippedHorizontally(!sticker.isFlippedHorizontally);
            } else if (direction == FLIP_VERTICALLY) {
                sticker.getMatrix().preScale(1, -1, sticker.getCenterPoint().x, sticker.getCenterPoint().y);
                sticker.setFlippedVertically(!sticker.isFlippedVertically);
            }

            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerFlipped(sticker);
            }

            invalidate();
        }
    }

    public boolean replace(Sticker sticker) {
        return replace(sticker, true);
    }

    public boolean replace(Sticker sticker, boolean needStayState) {
        if (handlingSticker != null && sticker != null) {
            if (needStayState) {
                sticker.getMatrix().set(handlingSticker.getMatrix());
                sticker.setFlippedVertically(handlingSticker.isFlippedVertically());
                sticker.setFlippedHorizontally(handlingSticker.isFlippedHorizontally());
            } else {
                handlingSticker.getMatrix().reset();
                // reset scale, angle, and put it in center
                float offsetX = (getWidth() - handlingSticker.getWidth()) / 2;
                float offsetY = (getHeight() - handlingSticker.getHeight()) / 2;
                sticker.getMatrix().postTranslate(offsetX, offsetY);

                float scaleFactor;
                if (getWidth() < getHeight()) {
                    scaleFactor = (float) getWidth() / handlingSticker.getDrawable().getIntrinsicWidth();
                } else {
                    scaleFactor = (float) getHeight() / handlingSticker.getDrawable().getIntrinsicHeight();
                }
                sticker.getMatrix()
                        .postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);
            }
            int index = stickers.indexOf(handlingSticker);
            stickers.set(index, sticker);
            handlingSticker = sticker;

            invalidate();
            return true;
        } else {
            return false;
        }
    }

    public boolean remove(Sticker sticker) {
        if (stickers.contains(sticker)) {
            stickers.remove(sticker);
            if (onStickerOperationListener != null) {
                onStickerOperationListener.onStickerDeleted(sticker);
            }
            if (handlingSticker == sticker) {
                handlingSticker = null;
            }
            invalidate();

            return true;
        } else {
            Log.d(TAG, "remove: the sticker is not in this StickerView");

            return false;
        }
    }

    public boolean removeCurrentSticker() {
        return remove(handlingSticker);
    }

    public void removeAllStickers() {
        stickers.clear();
        if (handlingSticker != null) {
            handlingSticker.release();
            handlingSticker = null;
        }
        invalidate();
    }

    public void addSticker(Sticker sticker) {
        if (sticker == null) {
            Log.e(TAG, "Sticker to be added is null!");
            return;
        }
        float offsetX = (getWidth() - sticker.getWidth()) / 2;
        float offsetY = (getHeight() - sticker.getHeight()) / 2;
        sticker.getMatrix().postTranslate(offsetX, offsetY);

        float scaleFactor;
        if (getWidth() < getHeight()) {
            scaleFactor = (float) getWidth() / sticker.getDrawable().getIntrinsicWidth();
        } else {
            scaleFactor = (float) getHeight() / sticker.getDrawable().getIntrinsicHeight();
        }
        sticker.getMatrix()
                .postScale(scaleFactor / 2, scaleFactor / 2, getWidth() / 2, getHeight() / 2);

        handlingSticker = sticker;
        stickers.add(sticker);

        invalidate();
    }

    public float[] getStickerPoints(Sticker sticker) {
        if (sticker == null) return new float[8];
        return sticker.getMappedBoundPoints();
    }

    public void save(File file) {
        StickerUtils.saveImageToGallery(file, createBitmap());
        StickerUtils.notifySystemGallery(getContext(), file);
    }

    public Bitmap createBitmap() {
        handlingSticker = null;
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);
        return bitmap;
    }

    public int getStickerCount() {
        return stickers.size();
    }

    public boolean isNoneSticker() {
        return getStickerCount() == 0;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
        invalidate();
    }

    public void setMinClickDelayTime(int minClickDelayTime) {
        this.minClickDelayTime = minClickDelayTime;
    }

    public int getMinClickDelayTime() {
        return minClickDelayTime;
    }

    public boolean isConstrained() {
        return constrained;
    }

    public void setConstrained(boolean constrained) {
        this.constrained = constrained;
        postInvalidate();
    }

    public void setOnStickerOperationListener(OnStickerOperationListener onStickerOperationListener) {
        this.onStickerOperationListener = onStickerOperationListener;
    }

    public OnStickerOperationListener getOnStickerOperationListener() {
        return onStickerOperationListener;
    }

    public Sticker getCurrentSticker() {
        return handlingSticker;
    }

    public List<BitmapStickerIcon> getIcons() {
        return icons;
    }

    public void setIcons(List<BitmapStickerIcon> icons) {
        this.icons = icons;
        invalidate();
    }

    public interface OnStickerOperationListener {
        void onStickerClicked(Sticker sticker);

        void onStickerDeleted(Sticker sticker);

        void onStickerDragFinished(Sticker sticker);

        void onStickerZoomFinished(Sticker sticker);

        void onStickerFlipped(Sticker sticker);

        void onStickerDoubleTapped(Sticker sticker);
    }
}
