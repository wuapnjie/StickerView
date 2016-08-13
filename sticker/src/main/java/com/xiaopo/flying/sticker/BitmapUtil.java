package com.xiaopo.flying.sticker;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by snowbean on 16-8-5.
 */
class BitmapUtil {
    private static final String TAG = "BitmapUtil";

    public static String saveImageToGallery(Bitmap bmp) {
        if (bmp == null) {
            Log.e(TAG, "saveImageToGallery: the bitmap is null");
            return "";
        }
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "Playalot");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.e(TAG, "saveImageToGallery: the path of bmp is " + file.getAbsolutePath());
        return file.getAbsolutePath();

    }

    // 把文件插入到系统图库
    public static void notifySystemGallery(Context context, File file) {
        if (file == null || !file.exists()) {
            Log.e(TAG, "notifySystemGallery: the file do not exist.");
            return;
        }

        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), file.getName(), null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }
}
