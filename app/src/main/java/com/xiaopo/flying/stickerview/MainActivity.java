package com.xiaopo.flying.stickerview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuItem;
import android.widget.Toast;

import com.xiaopo.flying.sticker.DrawableSticker;
import com.xiaopo.flying.sticker.Sticker;
import com.xiaopo.flying.sticker.StickerView;
import com.xiaopo.flying.sticker.TextSticker;
import com.xiaopo.flying.stickerview.util.FileUtil;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private StickerView mStickerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStickerView = (StickerView) findViewById(R.id.sticker_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final Bitmap bitmapReplace = BitmapFactory.decodeResource(getResources(), R.drawable.haizewang_2);
        mStickerView.setBackgroundColor(Color.WHITE);
        mStickerView.setLocked(false);

        Drawable bubble = ContextCompat.getDrawable(getApplicationContext(), R.drawable.bubble_sticker);
        final TextSticker textSticker = new TextSticker(getApplicationContext());
        textSticker.setDrawable(scaleImage(bubble, 0.5f), new Rect(100, 100, 400, 250));
        textSticker.setText(getString(R.string.text));
        textSticker.resizeText();
        mStickerView.addSticker(textSticker);

        mStickerView.setOnStickerClickListener(new StickerView.OnStickerClickListener() {
            @Override
            public void onStickerClick(Sticker sticker) {
                if (sticker == textSticker) {
                    textSticker.setDrawable(ContextCompat.getDrawable(getApplicationContext(),
                            R.drawable.transparent_background));
                    textSticker.setText("Hello, world!");
                    textSticker.setTextColor(Color.parseColor("#880000"));
                    textSticker.setTextAlign(Layout.Alignment.ALIGN_OPPOSITE);
                    textSticker.resizeText();
                    mStickerView.replace(textSticker);
                }
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
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_2);
            Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_23);
            Drawable drawable2 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_150);
            mStickerView.addSticker(new DrawableSticker(drawable));
            mStickerView.addSticker(new DrawableSticker(drawable1));
            mStickerView.addSticker(new DrawableSticker(drawable2));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 110
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_2);
            Drawable drawable1 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_23);
            Drawable drawable2 = ContextCompat.getDrawable(getApplicationContext(), R.drawable.haizewang_150);
            mStickerView.addSticker(new DrawableSticker(drawable));
            mStickerView.addSticker(new DrawableSticker(drawable1));
            mStickerView.addSticker(new DrawableSticker(drawable2));
        }
    }

    public Drawable scaleImage (Drawable image, float scaleFactor) {
        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);
        image = new BitmapDrawable(getResources(), bitmapResized);
        return image;

    }
}
