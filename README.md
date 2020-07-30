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
    implementation 'com.feiyilin:imageslide:0.1.1'
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
Show imageslide by calling **showImageSlide**, e.g.,
```kotlin
class MainActivity : ImageSlideActivity() {
    private var onImageItemClickListener = object :
        OnImageItemClickListener {
        override fun onItemImageClick(index: Int) {
            val images: ArrayList<ImageSlideItem> = arrayListOf()
            for (image in data) {
                val item = ImageSlideItem()
                item.resId = image
                images.add(item)
            }
            // show imageslide
            showImageSlide(images, index)
        }
    }
}
```
**imageslide** uses [Picasso](https://square.github.io/picasso/) to load the image. So you can also load a image from a file or url.
```kotlin
    // add drawable resource
    val itemDrawable = ImageSlideItem()
    itemDrawable.resId = image
    // add file resource
    val itemFile = ImageSlideItem()
    itemFile.path = "file:///android_asset/myimage.png"
    // add url resource
    val itemUrl = ImageSlideItem()
    itemUrl.path = url
```

Event callback
```kotlin
class MainActivity : ImageSlideActivity() {
    ...
    override fun onImageSlideLongClick(image: ImageSlideItem, index: Int) {
        // long click on index-th page
    }
    override fun onImageSlideSelected(image: ImageSlideItem, index: Int) {
        // switch to index-th page
    }
}
```
