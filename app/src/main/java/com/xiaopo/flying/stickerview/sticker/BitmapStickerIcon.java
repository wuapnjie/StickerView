package com.xiaopo.flying.stickerview.sticker;

import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * Created by snowbean on 16-8-5.
 */
public class BitmapStickerIcon extends BitmapSticker {
    private float x;
    private float y;

    public BitmapStickerIcon(Bitmap bitmap, Matrix matrix) {
        super(bitmap, matrix);
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
