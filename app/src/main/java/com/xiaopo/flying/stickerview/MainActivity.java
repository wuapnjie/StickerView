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
import android.widget.Toast;

import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.stickerview.util.FileUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private StickerView mStickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStickerView = (StickerView) findViewById(R.id.sticker_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mStickerView.setBackgroundColor(Color.WHITE);
        mStickerView.setLocked(false);

        mStickerView.setOnStickerOperationListener(new StickerView.OnStickerOperationListener() {
            @Override
            public void onStickerClicked(Sticker sticker) {
                if (sticker instanceof TextSticker) {
                    TextSticker textSticker = (TextSticker) sticker;
                    textSticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.transparent_background));
                    textSticker.setText("Hello, world!");
                    textSticker.setTextColor(Color.BLACK);
                    textSticker.setTextAlign(Layout.Alignment.ALIGN_CENTER);
                    textSticker.resizeText();
                    mStickerView.replace(textSticker);
                }
                Log.d(TAG, "onStickerClicked");
            }

            @Override
            public void onStickerDeleted(Sticker sticker) {
                Log.d(TAG, "onStickerDeleted");
            }

            @Override
            public void onStickerDragFinished(Sticker sticker) {
                Log.d(TAG, "onStickerDragFinished");
            }

            @Override
            public void onStickerFlipped(Sticker sticker) {
                Log.d(TAG, "onStickerFlipped");
            }
        });

        if (toolbar != null) {
            toolbar.setTitle(R.string.app_name);
            toolbar.inflateMenu(R.menu.menu_save);
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.item_save) {
                        File file = FileUtil.getNewFile(MainActivity.this, "Sticker");
                        if (file != null) {
                            mStickerView.save(file);
                            Toast.makeText(MainActivity.this, "saved in " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "the file is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    return false;
                }
            });
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 110);
        } else {
            loadSticker();
        }
    }

    private void loadSticker() {
        Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_215);
        Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_23);
        mStickerView.addSticker(new DrawableSticker(drawable));
        mStickerView.addSticker(new DrawableSticker(drawable1));

        Drawable bubble = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bubble);
        final TextSticker textSticker = new TextSticker(getApplicationContext());
        textSticker.setDrawable(bubble);
        textSticker.setText("Sticker\n");
        textSticker.setMaxTextSize(14);
        textSticker.resizeText();
        mStickerView.addSticker(textSticker);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            loadSticker();
        }
    }

}
