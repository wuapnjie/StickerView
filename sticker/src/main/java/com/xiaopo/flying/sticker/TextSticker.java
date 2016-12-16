package com.xiaopo.flying.sticker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;

/**
 * Customize your sticker with text and image background.
 * You can place some text into a given region, however,
 * you can also add a plain text sticker. To support text
 * auto resizing , I take most of the code from AutoResizeTextView.
 * See https://adilatwork.blogspot.com/2014/08/android-textview-which-resizes-its-text.html
 * Notice: It's not efficient to add long text due to too much of
 * StaticLayout object allocation.
 * <p>
 * Created by liutao on 30/11/2016.
 */

public class TextSticker extends Sticker {

  /**
   * Our ellipsis string.
   */
  private static final String mEllipsis = "\u2026";

  /**
   * Upper bounds for text size.
   * This acts as a starting point for resizing.
   */
  private float mMaxTextSizePixels;

  /**
   * Lower bounds for text size.
   */
  private float mMinTextSizePixels;

  /**
   * Line spacing multiplier.
   */
  private float mLineSpacingMultiplier = 1.0f;

  /**
   * Additional line spacing.
   */
  private float mLineSpacingExtra = 0.0f;

  private Context mContext;
  private Drawable mDrawable;
  private Rect mRealBounds;
  private Rect mTextRect;
  private StaticLayout mStaticLayout;
  private TextPaint mTextPaint;
  private Layout.Alignment mAlignment;
  private String mText;

  public TextSticker(Context context) {
    this(context, null);
  }

  public TextSticker(Context context, Drawable drawable) {
    mContext = context;
    mDrawable = drawable;
    if (drawable == null) {
      mDrawable = ContextCompat.getDrawable(context, R.drawable.transparent_background);
    }
    matrix = new Matrix();
    mTextPaint = new TextPaint(TextPaint.ANTI_ALIAS_FLAG);
    mRealBounds = new Rect(0, 0, getWidth(), getHeight());
    mTextRect = new Rect(0, 0, getWidth(), getHeight());
    mMinTextSizePixels = convertSpToPx(6);
    mMaxTextSizePixels = convertSpToPx(32);
    mAlignment = Layout.Alignment.ALIGN_CENTER;
    mTextPaint.setTextSize(mMaxTextSizePixels);
  }

  @Override public void draw(Canvas canvas) {
    canvas.save();
    canvas.concat(matrix);
    if (mDrawable != null) {
      mDrawable.setBounds(mRealBounds);
      mDrawable.draw(canvas);
    }
    canvas.restore();

    canvas.save();
    canvas.concat(matrix);
    if (mTextRect.width() == getWidth()) {
      int dy = getHeight() / 2 - mStaticLayout.getHeight() / 2;
      // center vertical
      canvas.translate(0, dy);
    } else {
      int dx = mTextRect.left;
      int dy = mTextRect.top + mTextRect.height() / 2 - mStaticLayout.getHeight() / 2;
      canvas.translate(dx, dy);
    }
    mStaticLayout.draw(canvas);
    canvas.restore();
  }

  @Override public int getWidth() {
    return mDrawable.getIntrinsicWidth();
  }

  @Override public int getHeight() {
    return mDrawable.getIntrinsicHeight();
  }

  @Override public void release() {
    super.release();
    if (mDrawable != null) {
      mDrawable = null;
    }
  }

  @Override public Drawable getDrawable() {
    return mDrawable;
  }

  @Override public void setDrawable(Drawable drawable) {
    mDrawable = drawable;
    mRealBounds.set(0, 0, getWidth(), getHeight());
    mTextRect.set(0, 0, getWidth(), getHeight());
  }

  public void setDrawable(Drawable drawable, Rect region) {
    mDrawable = drawable;
    mRealBounds.set(0, 0, getWidth(), getHeight());
    if (region == null) {
      mTextRect.set(0, 0, getWidth(), getHeight());
    } else {
      mTextRect.set(region.left, region.top, region.right, region.bottom);
    }
  }

  public void setTypeface(Typeface typeface) {
    mTextPaint.setTypeface(typeface);
  }

  public void setTextColor(int color) {
    mTextPaint.setColor(color);
  }

  public void setTextAlign(Layout.Alignment alignment) {
    mAlignment = alignment;
  }

  public void setMaxTextSize(float size) {
    setMaxTextSize(TypedValue.COMPLEX_UNIT_SP, size);
  }

  public void setMaxTextSize(int unit, float size) {
    mTextPaint.setTextSize(convertSpToPx(size));
    mMaxTextSizePixels = mTextPaint.getTextSize();
  }

  /**
   * Sets the lower text size limit
   *
   * @param minTextSizeScaledPixels the minimum size to use for text in this view,
   * in scaled pixels.
   */
  public void setMinTextSize(float minTextSizeScaledPixels) {
    mMinTextSizePixels = convertSpToPx(minTextSizeScaledPixels);
  }

  public void setLineSpacing(float add, float multiplier) {
    mLineSpacingMultiplier = multiplier;
    mLineSpacingExtra = add;
  }

  public void setText(String text) {
    mText = text;
  }

  public String getText() {
    return mText;
  }

  /**
   * Resize this view's text size with respect to its width and height
   * (minus padding). You should always call this method after the initialization.
   */
  public void resizeText() {
    final int availableHeightPixels = mTextRect.height();

    final int availableWidthPixels = mTextRect.width();

    final CharSequence text = getText();

    // Safety check
    // (Do not resize if the view does not have dimensions or if there is no text)
    if (text == null
        || text.length() <= 0
        || availableHeightPixels <= 0
        || availableWidthPixels <= 0
        || mMaxTextSizePixels <= 0) {
      return;
    }

    float targetTextSizePixels = mMaxTextSizePixels;
    int targetTextHeightPixels =
        getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);

    // Until we either fit within our TextView
    // or we have reached our minimum text size,
    // incrementally try smaller sizes
    while (targetTextHeightPixels > availableHeightPixels
        && targetTextSizePixels > mMinTextSizePixels) {
      targetTextSizePixels = Math.max(targetTextSizePixels - 2, mMinTextSizePixels);

      targetTextHeightPixels =
          getTextHeightPixels(text, availableWidthPixels, targetTextSizePixels);
    }

    // If we have reached our minimum text size and the text still doesn't fit,
    // append an ellipsis
    // (NOTE: Auto-ellipsize doesn't work hence why we have to do it here)
    if (targetTextSizePixels == mMinTextSizePixels
        && targetTextHeightPixels > availableHeightPixels) {
      // Make a copy of the original TextPaint object for measuring
      TextPaint textPaintCopy = new TextPaint(mTextPaint);
      textPaintCopy.setTextSize(targetTextSizePixels);

      // Measure using a StaticLayout instance
      StaticLayout staticLayout =
          new StaticLayout(text, textPaintCopy, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
              mLineSpacingMultiplier, mLineSpacingExtra, false);

      // Check that we have a least one line of rendered text
      if (staticLayout.getLineCount() > 0) {
        // Since the line at the specific vertical position would be cut off,
        // we must trim up to the previous line and add an ellipsis
        int lastLine = staticLayout.getLineForVertical(availableHeightPixels) - 1;

        if (lastLine >= 0) {
          int startOffset = staticLayout.getLineStart(lastLine);
          int endOffset = staticLayout.getLineEnd(lastLine);
          float lineWidthPixels = staticLayout.getLineWidth(lastLine);
          float ellipseWidth = textPaintCopy.measureText(mEllipsis);

          // Trim characters off until we have enough room to draw the ellipsis
          while (availableWidthPixels < lineWidthPixels + ellipseWidth) {
            endOffset--;
            lineWidthPixels =
                textPaintCopy.measureText(text.subSequence(startOffset, endOffset + 1).toString());
          }

          setText(text.subSequence(0, endOffset) + mEllipsis);
        }
      }
    }
    mTextPaint.setTextSize(targetTextSizePixels);
    mStaticLayout =
        new StaticLayout(mText, mTextPaint, mTextRect.width(), mAlignment, mLineSpacingMultiplier,
            mLineSpacingExtra, true);
  }

  /**
   * @return lower text size limit, in pixels.
   */
  public float getMinTextSizePixels() {
    return mMinTextSizePixels;
  }

  /**
   * Sets the text size of a clone of the view's {@link TextPaint} object
   * and uses a {@link StaticLayout} instance to measure the height of the text.
   *
   * @return the height of the text when placed in a view
   * with the specified width
   * and when the text has the specified size.
   */
  private int getTextHeightPixels(CharSequence source, int availableWidthPixels,
      float textSizePixels) {
    mTextPaint.setTextSize(textSizePixels);
    // It's not efficient to create a StaticLayout instance
    // every time when measuring, we can use StaticLayout.Builder
    // since api 23.
    StaticLayout staticLayout =
        new StaticLayout(source, mTextPaint, availableWidthPixels, Layout.Alignment.ALIGN_NORMAL,
            mLineSpacingMultiplier, mLineSpacingExtra, true);
    return staticLayout.getHeight();
  }

  /**
   * @return the number of pixels which scaledPixels corresponds to on the device.
   */
  private float convertSpToPx(float scaledPixels) {
    return scaledPixels * mContext.getResources().getDisplayMetrics().scaledDensity;
  }
}
