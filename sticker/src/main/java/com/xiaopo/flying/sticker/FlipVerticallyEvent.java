package com.xiaopo.flying.sticker;

import android.view.MotionEvent;

/**
 * @author wupanjie
 */

public class FlipVerticallyEvent extends AbstractFlipEvent {

  @Override
  @StickerView.Flip
  protected int getFlipDirection() {
    return StickerView.FLIP_VERTICALLY;
  }
}
