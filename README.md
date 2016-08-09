# StickerView
A view which can add sticker and zoom,drag,delete it

###screenshots
![](https://github.com/wuapnjie/StickerView/blob/master/screenshots/stickerview.gif)

###usage
StickerView继承自ImageView
布局文件
```xml
<com.xiaopo.flying.sticker.StickerView
        android:id="@+id/sticker_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        android:src="@drawable/haizewang_215" />
```
添加贴纸，支持Bitmap和自定义的Drawable，若为Drawable则需知道确切的宽高
```java
stickerView.addSticker(bitmap)
stickerView.addSticker(drawable)
```


