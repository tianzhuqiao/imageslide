# ImageSlide in Kotlin for Android

![img](docs/demo.gif)

# Usage
## 1. Add to project
Add **jcenter()** to repository in your project's build.gradle:
```gradle
allprojects {
    repositories {
        ...
        jcenter()
    }
}
```

Add **imageslide** to dependencies in your app's build.gradle: 
```gradle
dependencies {
    ...
    implementation 'com.feiyilin:imageslide:0.1.2'
}
```
## 2. Update layout xml
For the activity that you want to show the imagelide, add a **FrameLayout** to the end of the layout, e.g.,
```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.feiyilin.example.MainActivity">
    ...
    <FrameLayout
        android:id="@+id/imageslide_fragment"
        android:layout_width="0dp"
        android:layout_height="0dp"
        android:padding="0dp"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent">
    </FrameLayout>
</androidx.constraintlayout.widget.ConstraintLayout>
```

## 3. Update activity
Init the imageslide fragment
```kotlin
class MainActivity : ImageSlideActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        ...    
        // initialize the imageslide fragment
        initImageSlideFragment(R.id.imageslide_fragment)
    }
```

Show imageslide by calling **show**, e.g.,
```kotlin
val images = mutableListOf<ImageSlideItem>()
for (image in data) {
   val item = ImageSlideResItem().image(image)
   images.add(item)
}
imageSlideFragment.show(images, index)
```

Or check [ImageSlideActivity](./imageslide/src/main/java/com/feiyilin/imageslide/ImageSlideActivity.kt) if you want to use **ImageSlideFragment** directly in the activity.
# ImageSlideItem
* **ImageSlideResItem** is to load image from resource id
```kotlin
val item = ImageSlideResItem().image(imageResId)
```
* **ImageSlidePathItem** is to load image from path string
```kotlin
// add file resource
val item = ImageSlidePathItem().image("file:///android_asset/myimage.png")
// add url resource
val item2 = ImageSlidePathItem().image("http://i.imgur.com/DvpvklR.png")
```

To create a custom imageslide item
1. derive a custom item from **ImageSlideItem** 
```kotlin
open class ImageSlideCustomItem: ImageSlideItem() {
...
}
```
2. override **load** function to load the image into an **ImageView**
```kotlin
open class ImageSlideCustomItem: ImageSlideItem() {
    ...
    override fun load(view: ImageView?) {
        ...
    }
}
```

# Callbacks
* **onImageSlideHide**

    Called when imageslide fragment is shown/hidden.
    
* **onImageSlideLongClick**

    Called when long-click on an image.
    
* **onImageSlideSelected**

    Called when switch to a page.
