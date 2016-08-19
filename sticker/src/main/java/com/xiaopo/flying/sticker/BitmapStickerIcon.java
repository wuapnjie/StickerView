package com.xiaopo.flying.sticker;

import android.graphics.drawable.Drawable;

/**
 * Created by snowbean on 16-8-5.
 */
public class BitmapStickerIcon extends DrawableSticker {
    private float x;
    private float y;

    public BitmapStickerIcon(Drawable drawable) {
        super(drawable);
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }
}
