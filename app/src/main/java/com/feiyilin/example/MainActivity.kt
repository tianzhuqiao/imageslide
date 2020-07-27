package com.feiyilin.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feiyilin.imageslide.ImageSlideFragment
import com.feiyilin.imageslide.ImageSlideItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), ImageSlideFragment.ImageSlideCallBack {

    private val imageSlideFragment: ImageSlideFragment = ImageSlideFragment()

    private var data = arrayListOf<Int>()
    private var systemUiVisibility: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        systemUiVisibility = this.window.decorView.systemUiVisibility
        image_recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)

            adapter =
                ImageListAdapter(
                    data,
                    onImageItemClickListener
                ).apply {
                }
        }
        data.add(R.drawable.image1)
        data.add(R.drawable.image2)
        data.add(R.drawable.image3)
        data.add(R.drawable.image4)
        data.add(R.drawable.image5)

        if (supportFragmentManager.findFragmentByTag("image_slide") == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.main_container, imageSlideFragment, "image_slide")
                .hide(imageSlideFragment)
                .commit()
            imageSlideFragment.setCallBack(this)
        }
        supportFragmentManager.executePendingTransactions()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1
            )

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return
        }
    }


    fun setFullscreen(fullscreen: Boolean) {
        if (Build.VERSION.SDK_INT > 10) {
            var flags = systemUiVisibility
            if (fullscreen) {
                flags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            }
            this.window.decorView.systemUiVisibility = flags
        } else {
            this.window
                .setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN
                )
        }
    }

    private var onImageItemClickListener = object :
        OnImageItemClickListener {
        override fun onItemImageClick(index: Int) {
            val images: ArrayList<ImageSlideItem> = arrayListOf()
            for (image in data) {
                val item = ImageSlideItem()
                item.resId = image
                images.add(item)
            }
            imageSlideFragment.setImages(images)
            imageSlideFragment.setSelected(index)
            this@MainActivity.supportFragmentManager.beginTransaction().show(imageSlideFragment)
                .commit()
        }
    }

    override fun onImageSlideHide(hide: Boolean) {
        if (hide) {
            this@MainActivity.setFullscreen(false)
            this@MainActivity.window
                .clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        } else {
            this@MainActivity.setFullscreen(true)
            this@MainActivity.window
                .addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }
    }

    override fun onImageSlideLongClick(image: ImageSlideItem, index: Int) {
        Picasso.get().load(image.resId).into(target)
    }

    private val target = object : com.squareup.picasso.Target {
        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
            // can not reuse marker since it may be reclustered
            // https://stackoverflow.com/questions/41902478/illegalargumentexception-unmanaged-descriptor-using-gms-maps-model-marker-setic
            bitmap?.let {
                val stream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, stream)

                val fileName: String = SimpleDateFormat("yyyyMMddHHmm'.jpg'").format(Date())
                val file = File(cacheDir, fileName)
                val fOut = FileOutputStream(file)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut)
                fOut.flush()
                fOut.close()
                file.setReadable(true, false)

                val imageUri: Uri = FileProvider.getUriForFile(
                    this@MainActivity,
                    "com.feiyilin.example.provider",  //(use your app signature + ".provider" )
                    file
                )
                val shareIntent = Intent()
                shareIntent.action = Intent.ACTION_SEND
                shareIntent.type = "image/jepg"
                shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri)
                this@MainActivity.startActivity(Intent.createChooser(shareIntent, ""))
            }
        }
    }
}