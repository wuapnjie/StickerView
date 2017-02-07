package com.xiaopo.flying.sticker;

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public class FlipVerticallyEvent implements StickerIconEvent {
  @Override public void onActionDown(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionMove(StickerView stickerView, MotionEvent event) {

  }

  @Override public void onActionUp(StickerView stickerView, MotionEvent event) {
    stickerView.flipCurrentSticker(StickerView.FLIP_VERTICALLY);
  }
}
