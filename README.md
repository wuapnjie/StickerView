# StickerView
A view which can add sticker and zoom,drag,flip,delete it

## Screenshots
![](https://github.com/wuapnjie/StickerView/blob/master/screenshots/screenshot1.png)
![](https://github.com/wuapnjie/StickerView/blob/master/screenshots/screenshot2.png)

## Usage

In your **build.gradle**

```gradle
compile 'com.flying.xiaopo:sticker:1.2.4'
```

**Tips**:StickerView extends FrameLayout
#### In layout
```xml
<com.xiaopo.flying.sticker.StickerView
        android:id="@+id/sticker_view"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_gravity="center">
    <!-- custom, maybe you will like to put an ImageView--> 
    <ImageView
        android:src="@drawable/haizewang_2"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />
</com.xiaopo.flying.sticker.StickerView>
```
#### Add sticker
If the sticker is drawable, it's intrinsic width and height can not be zero.
If the sticker is text, you can set text color, font and alignment and the region which holds the text.

```java
stickerView.addSticker(sticker)
stickerView.replace(sticker)
```

Also you can custom the icon

```java
public void setDeleteIcon(deleteIcon)
public void setZoomIcon(zoomIcon)
public void setFlipIcon(flipIcon)
```

## Update

* **2016/10/11** Add horizontal flip function
* **2016/10/12** Add Lock function to disable handle stickers
* **2016/11/30** Added text stickers which supports both text and image background. Thanks to [taoliuh](https://github.com/taoliuh)
* **2016/12/02** Fixed the region of sticker bigger bug,and add more custom configure.
* **2016/12/03** Add more callback
* **2016/12/14** Add [PhotoView](https://github.com/chrisbanes/PhotoView) support

## Todo
[] Constrain the sticker's moving area

## Licence

```
Copyright 2016 wuapnjie

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
