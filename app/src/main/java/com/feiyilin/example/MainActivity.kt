package com.feiyilin.example

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.feiyilin.imageslide.ImageSlideActivity
import com.feiyilin.imageslide.ImageSlideItem
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : ImageSlideActivity() {

    private var data = arrayListOf<Int>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        initImageSlideFragment(R.id.main_container)
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
            showImageSlide(images, index)
        }
    }

    override fun onImageSlideLongClick(image: ImageSlideItem, index: Int) {

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

        Picasso.get().load(image.resId).into(target)
    }

    override fun onImageSliceSelected(index: Int) {
    }

    private val target = object : com.squareup.picasso.Target {
        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: Drawable?) {
        }

        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
        }

        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
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