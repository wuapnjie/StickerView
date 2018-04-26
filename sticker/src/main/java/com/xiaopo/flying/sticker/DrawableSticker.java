package com.xiaopo.flying.sticker;

/**
 * Mofified by M.Refaat on 3/25/2018.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import com.netaq.schoolvoice.R;
import com.xiaopo.flying.sticker.Sticker;

/**
 * @author wupanjie
 */
public class DrawableSticker extends Sticker {

    private Drawable drawable;
    public Rect realBounds;
    private Drawable allocationDrawable;
    private Rect allocationBounds;

    public DrawableSticker(Drawable drawable, Context context) {
        this.allocationDrawable = drawable;
        this.drawable = ContextCompat.getDrawable(context, R.drawable.empty_sticker); // an invisble/empty view to be considered as the mainn drawable
        realBounds = new Rect(300, 300, getAllocationWidth(), getAllocationHeight());
        int startX = (getAllocationWidth() / 2) - (getWidth() / 2);
        int startY = (getAllocationHeight() / 2) - (getHeight() / 2);
        allocationBounds = new Rect(startX - 300, startY - 300,
                startX + getWidth() + 300, startY + getHeight() + 300);
    }

    @NonNull
    @Override
    public Drawable getDrawable() {
        return drawable;
    }

    @Override
    public DrawableSticker setDrawable(@NonNull Drawable drawable) {
        this.drawable = drawable;
        return this;
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.save();
        canvas.concat(getMatrix());
        drawable.setBounds(allocationBounds);
        drawable.draw(canvas);
        canvas.restore();

        canvas.save();
        canvas.concat(getMatrix());
        allocationDrawable.setBounds(realBounds);
        allocationDrawable.draw(canvas);
        canvas.restore();
    }

    @NonNull
    @Override
    public DrawableSticker setAlpha(@IntRange(from = 0, to = 255) int alpha) {
        drawable.setAlpha(alpha);
        return this;
    }

    @Override
    public int getWidth() {
        return drawable.getIntrinsicWidth();
    }

    @Override
    public int getHeight() {
        return drawable.getIntrinsicHeight();
    }

    public int getAllocationWidth() {
        return allocationDrawable.getIntrinsicWidth();
    }

    public int getAllocationHeight() {
        return allocationDrawable.getIntrinsicHeight();
    }

    @Override
    public void release() {
        super.release();
        if (drawable != null) {
            drawable = null;
        }
    }
}
