# StickerView
A view which can add sticker and zoom,drag,delete it

###screenshots
![](https://github.com/taoliuh/StickerView/blob/master/screenshots/Screenshot_2016-12-02-15-26-22.png)
![](https://github.com/taoliuh/StickerView/blob/master/screenshots/Screenshot_2016-12-02-15-26-58.png)

###usage
StickerView extends FrameLayout
in layout
```xml
<com.xiaopo.flying.sticker.StickerView
        android:id="@+id/sticker_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center" />
```
add sticker
if the sticker is drawable, it's intrinsic width and height can not be zero.
if the sticker is text, you can set text color, font and alignment and the region which holds the text.

```java
stickerView.addSticker(sticker)
stickerView.replace(sticker)
```

## update
* 2016/10/11 Add horizontal flip function
* 2016/10/12 Add Lock function to disable handle stickers
* 2016/11/30 Added text stickers which supports both text and image background.

## licence
MIT LICENCE
