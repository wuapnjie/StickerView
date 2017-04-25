package com.xiaopo.flying.sticker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.Math.round;

/**
 * @author wupanjie
 */
class StickerUtils {
  private static final String TAG = "StickerView";

  public static File saveImageToGallery(@NonNull File file, @NonNull Bitmap bmp) {
    if (bmp == null) {
      throw new IllegalArgumentException("bmp should not be null");
    }
    try {
      FileOutputStream fos = new FileOutputStream(file);
      bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
      fos.flush();
      fos.close();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Log.e(TAG, "saveImageToGallery: the path of bmp is " + file.getAbsolutePath());
    return file;
  }

  public static void notifySystemGallery(@NonNull Context context, @NonNull File file) {
    if (file == null || !file.exists()) {
      throw new IllegalArgumentException("bmp should not be null");
    }

    try {
      MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
          file.getName(), null);
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("File couldn't be found");
    }
    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
  }

  @NonNull public static RectF trapToRect(@NonNull float[] array) {
    RectF r = new RectF();
    trapToRect(r, array);
    return r;
  }

  public static void trapToRect(@NonNull RectF r, @NonNull float[] array) {
    r.set(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY,
        Float.NEGATIVE_INFINITY);
    for (int i = 1; i < array.length; i += 2) {
      float x = round(array[i - 1] * 10) / 10.f;
      float y = round(array[i] * 10) / 10.f;
      r.left = (x < r.left) ? x : r.left;
      r.top = (y < r.top) ? y : r.top;
      r.right = (x > r.right) ? x : r.right;
      r.bottom = (y > r.bottom) ? y : r.bottom;
    }
    r.sort();
  }
}
