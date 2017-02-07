package com.xiaopo.flying.stickerview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.xiaopo.flying.sticker.BitmapStickerIcon;
import com.xiaopo.flying.sticker.DeleteIconEvent;
import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.FlipHorizontallyEvent;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.sticker.ZoomIconEvent;
import com.xiaopo.flying.stickerview.util.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
  private static final String TAG = MainActivity.class.getSimpleName();
  private StickerView stickerView;
  private TextSticker sticker;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    stickerView = (StickerView) findViewById(R.id.sticker_view);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

    //currently you can config your own icons and icon event
    //the event you can custom
    BitmapStickerIcon deleteIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
        com.xiaopo.flying.sticker.R.drawable.sticker_ic_close_white_18dp),
        BitmapStickerIcon.LEFT_TOP);
    deleteIcon.setIconEvent(new DeleteIconEvent());

    BitmapStickerIcon zoomIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
        com.xiaopo.flying.sticker.R.drawable.sticker_ic_scale_white_18dp),
        BitmapStickerIcon.RIGHT_BOTOM);
    zoomIcon.setIconEvent(new ZoomIconEvent());

    BitmapStickerIcon flipIcon = new BitmapStickerIcon(ContextCompat.getDrawable(this,
        com.xiaopo.flying.sticker.R.drawable.sticker_ic_flip_white_18dp),
        BitmapStickerIcon.RIGHT_TOP);
    flipIcon.setIconEvent(new FlipHorizontallyEvent());

    BitmapStickerIcon heartIcon =
        new BitmapStickerIcon(ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp),
            BitmapStickerIcon.LEFT_BOTTOM);
    heartIcon.setIconEvent(new HelloIconEvent());

    stickerView.setIcons(Arrays.asList(deleteIcon, zoomIcon, flipIcon, heartIcon));

    //default icon layout
    //stickerView.configDefaultIcons();

    stickerView.setBackgroundColor(Color.WHITE);
    stickerView.setLocked(false);
    stickerView.setConstrained(true);

    sticker = new TextSticker(this);

    sticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
        R.drawable.sticker_transparent_background));
    sticker.setText("Hello, world!");
    sticker.setTextColor(Color.BLACK);
    sticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
    sticker.resizeText();

    stickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
      @Override public void onStickerClicked(Sticker sticker) {
        //stickerView.removeAllSticker();
        if (sticker instanceof TextSticker) {
          stickerView.replace(sticker);
          stickerView.invalidate();
        }
        Log.d(TAG, "onStickerClicked");
      }

      @Override public void onStickerDeleted(Sticker sticker) {
        Log.d(TAG, "onStickerDeleted");
      }

      @Override public void onStickerDragFinished(Sticker sticker) {
        Log.d(TAG, "onStickerDragFinished");
      }

      @Override public void onStickerZoomFinished(Sticker sticker) {
        Log.d(TAG, "onStickerZoomFinished");
      }

      @Override public void onStickerFlipped(Sticker sticker) {
        Log.d(TAG, "onStickerFlipped");
      }

      @Override public void onStickerDoubleTapped(Sticker sticker) {
        Log.d(TAG, "onDoubleTapped: double tap will be with two click");
      }
    });

    if (toolbar != null) {
      toolbar.setTitle(R.string.app_name);
      toolbar.inflateMenu(R.menu.menu_save);
      toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
        @Override public boolean onMenuItemClick(MenuItem item) {
          if (item.getItemId() == R.id.item_save) {
            File file = FileUtil.getNewFile(MainActivity.this, "Sticker");
            if (file != null) {
              stickerView.save(file);
              Toast.makeText(MainActivity.this, "saved in " + file.getAbsolutePath(),
                  Toast.LENGTH_SHORT).show();
            } else {
              Toast.makeText(MainActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
            }
          }
          //                    stickerView.replace(new DrawableSticker(
          //                            ContextCompat.getDrawable(MainActivity.this, R.drawable.haizewang_90)
          //                    ));
          return false;
        }
      });
    }

    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED
        || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        != PackageManager.PERMISSION_GRANTED) {
      ActivityCompat.requestPermissions(this,
          new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 110);
    } else {
      loadSticker();
    }
  }

  private void loadSticker() {
    Drawable drawable =
        ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_215);
    Drawable drawable1 =
        ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_23);
    stickerView.addSticker(new DrawableSticker(drawable));
    stickerView.addSticker(new DrawableSticker(drawable1));

    Drawable bubble = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bubble);
    final TextSticker textSticker = new TextSticker(getApplicationContext());
    textSticker.setDrawable(bubble);
    textSticker.setText("Sticker\n");
    textSticker.setMaxTextSize(14);
    textSticker.resizeText();
    stickerView.addSticker(textSticker);
  }

  @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == 110 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
      loadSticker();
    }
  }

  public void testReplace(View view) {
    if (stickerView.replace(sticker)) {
      Toast.makeText(MainActivity.this, "Replace Sticker successfully!", Toast.LENGTH_SHORT).show();
    } else {
      Toast.makeText(MainActivity.this, "Replace Sticker failed!", Toast.LENGTH_SHORT).show();
    }
  }

  public void testLock(View view) {
    stickerView.setLocked(!stickerView.isLocked());
  }

  public void testRemove(View view) {
    if (stickerView.removeCurrentSticker()) {
      Toast.makeText(MainActivity.this, "Remove current Sticker successfully!", Toast.LENGTH_SHORT)
          .show();
    } else {
      Toast.makeText(MainActivity.this, "Remove current Sticker failed!", Toast.LENGTH_SHORT)
          .show();
    }
  }

  public void testRemoveAll(View view) {
    stickerView.removeAllStickers();
  }

  public void reset(View view) {
    stickerView.removeAllStickers();
    loadSticker();
  }
}
