package com.vividbobo.lemonnotes.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Spannable
import android.util.Log
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.vividbobo.lemonnotes.R
import com.vividbobo.lemonnotes.adapter.GridViewAdapter
import com.vividbobo.lemonnotes.databinding.ActivityNoteBinding
import com.vividbobo.lemonnotes.entity.Span
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class NoteActivity : AppCompatActivity() {

    val PHOTO_SELECT = 1
    val VIDEO_SELECT = 2
    val IMAGE_CAPTURE = 3
    val VIDEO_CPATURE = 4

    val READ_STORAGE_PHOTO = 100
    val READ_STORAGE_VIDEO = 101
    val CAMERA_CAPTURE = 102


    private val mediaSource = ArrayList<String>()
    private lateinit var gridViewAdapter: GridViewAdapter

    private var imageUri: Uri? = null
    private var videoUri: Uri? = null
    private val authorities = "com.vividbobo.lemonnotes.fileProvider"


    private val TAG = "NoteActivity"
    private lateinit var binding: ActivityNoteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        gridViewAdapter = GridViewAdapter(this, mediaSource)
        binding.gridView.adapter = gridViewAdapter

        val url =
            "https://tse1-mm.cn.bing.net/th/id/OIP-C.KFnkmx1HPSLs6FaWU4s0nwHaEo?w=292&h=182&c=7&r=0&o=5&dpr=1.25&pid=1.7"

        binding.capture.setOnClickListener {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    CAMERA_CAPTURE
                )
            } else {
                showBottomSheetDialog()
            }
        }


        binding.photoAlbum.setOnClickListener {
            //权限申请
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PHOTO
                )
            } else {
                photoSelect()
            }
        }
        binding.videoAlbum.setOnClickListener {
            //权限申请
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    READ_STORAGE_PHOTO
                )
            } else {
                videoSelect()
            }
        }
        binding.setBold.setOnClickListener {
            if (binding.richText.isBold()) {
                binding.richText.setBold(false)
            } else {
                binding.richText.setBold(true)
            }
        }
        binding.setItalic.setOnClickListener {
            if (binding.richText.isItalic()) {
                binding.richText.setItalic(false)
            } else {
                binding.richText.setItalic(true)
            }
        }
        binding.setUnderline.setOnClickListener {
            if (binding.richText.isUnderline()) {
                binding.richText.setUnderline(false)
            } else {
                binding.richText.setUnderline(true)
            }
        }
        binding.setStrikethrough.setOnClickListener {
            if (binding.richText.isStrikethroughLine()) {
                binding.richText.setStrikethroughLine(false)
            } else {
                binding.richText.setStrikethroughLine(true)
            }
        }

        binding.setFontSize.setOnClickListener {


        }


        val spans: ArrayList<Span> = arrayListOf(
            Span(Span.BOLD, 3, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE),
//            Span(Span.ITALIC, 3, 9),
//            Span(Span.STRIKETHROUGHLINE, 0, 10)
        )

//        binding.logBtn.setOnClickListener {
//
//        }
//
//        binding.testBtn.setOnClickListener {
//            binding.richText.setText("abc")
//            binding.richText.apply {
//                setText(setSpans2(SpannableString(text), spans))
//            }
//        }

        binding.gridView.setOnItemClickListener { adapterView, view, i, l ->

            if (gridViewAdapter.isShowDelete) {
                removeMedia(i)
                Snackbar.make(view, "deleted", Snackbar.LENGTH_SHORT).show()
            } else {
                val uri = mediaSource[i]
                if (uri.toString().contains("image") || uri.toString().contains("jpg")) {
                    val intent = Intent(this, PhotoActivity::class.java)
                    intent.putExtra("photoUri", uri)
                    this.startActivity(intent)
                } else {
                    //video
                    val intent = Intent(this, VideoActivity::class.java)
                    intent.putExtra("videoUri", uri)
                    this.startActivity(intent)
                }
            }
        }

        binding.gridView.setOnItemLongClickListener { adapterView, view, i, l ->
            if (gridViewAdapter.isShowDelete) {
                gridViewAdapter.setIsShowDelete(false)
            } else {
                gridViewAdapter.setIsShowDelete(true)
            }
            true
        }

    }

    fun removeMedia(position: Int) {
        mediaSource.removeAt(position)
        gridViewAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        //写入数据库

    }


    fun photoSelect() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        startActivityForResult(intent, PHOTO_SELECT)
    }

    fun videoSelect() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "video/*"
        startActivityForResult(intent, VIDEO_SELECT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            PHOTO_SELECT -> {
                if (resultCode == RESULT_OK) {
                    data?.let {
                        if (data.data != null) {
                            mediaSource.add(data.data.toString())
                            Log.d(TAG, "onActivityResult: 图片单选")
                        } else {
                            Log.d(TAG, "onActivityResult: 图片多选")

                            data.clipData?.let {
                                for (i in 0 until it.itemCount) {
                                    val item = it.getItemAt(i)
                                    mediaSource.add(item.uri.toString())
                                }
                            }
                        }
                        gridViewAdapter.notifyDataSetChanged()
                    }
                }
            }
            VIDEO_SELECT -> {
                if (resultCode == RESULT_OK) {
                    data?.let {
                        if (data.data != null) {
                            mediaSource.add(data.data.toString())

                            Log.d(TAG, "onActivityResult: 视频单选")
                        } else {
                            Log.d(TAG, "onActivityResult: 视频多选")

                            data.clipData?.let {
                                for (i in 0 until it.itemCount) {
                                    val item = it.getItemAt(i)
                                    mediaSource.add(item.uri.toString())
                                }
                            }
                        }
                        gridViewAdapter.notifyDataSetChanged()
                    }
                }
            }
            IMAGE_CAPTURE -> {
                if (resultCode == RESULT_OK) {
                    //设置图片。。。
                    imageUri?.let {
                        Log.d(TAG, "onActivityResult: imageUri: ${imageUri.toString()}")
                        mediaSource.add(imageUri.toString())
                        gridViewAdapter.notifyDataSetChanged()
                    }
                }
            }
            VIDEO_CPATURE -> {
                if (resultCode == RESULT_OK) {
                    videoUri?.let {
                        mediaSource.add(videoUri.toString())
                        gridViewAdapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            READ_STORAGE_PHOTO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    photoSelect()
                }
            }
            READ_STORAGE_VIDEO -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    videoSelect()
                }
            }
            CAMERA_CAPTURE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showBottomSheetDialog()
                }
            }
        }
    }

    fun photoCapture() {
        //拍照功能
        val date = Date()
        val imagePath = "photo/" + SimpleDateFormat("yyyyMMdd").format(date).toString()
        val imageName = "IMG_" + SimpleDateFormat("yyyyMMdd_hhmmss").format(date) + ".jpg"
        val imageFile = File(this.getExternalFilesDir(imagePath), imageName)
        Log.d("photoCapture", "photoCapture: " + imageFile.absolutePath)
        imageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //使用fileProvider,注意manifest xml 两个都要配置一下
            FileProvider.getUriForFile(this, authorities, imageFile)
        } else {
            Uri.fromFile(imageFile)
        }

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, IMAGE_CAPTURE)
    }

    fun videoCapture() {
        val date = Date()
        val videoPath = "video/" + SimpleDateFormat("yyyyMMdd").format(date).toString()
        val videoName = "VIDEO_" + SimpleDateFormat("yyyyMMdd_hhmmss").format(date) + ".mp4"
        val videoFile = File(this.getExternalFilesDir(videoPath), videoName)

        videoUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //使用fileProvider,注意manifest xml 两个都要配置一下
            FileProvider.getUriForFile(this, authorities, videoFile)
        } else {
            Uri.fromFile(videoFile)
        }

        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, videoUri)
        startActivityForResult(intent, VIDEO_CPATURE)
    }

    fun showBottomSheetDialog() {
        val bottomSheet = BottomSheetDialog(this)
        bottomSheet.setContentView(R.layout.modal_bottom_sheet_content)

        val capture_photo = bottomSheet.findViewById<LinearLayout>(R.id.capture_photo)
        val capture_video = bottomSheet.findViewById<LinearLayout>(R.id.capture_video)

        capture_photo?.setOnClickListener {
            photoCapture()
            bottomSheet.dismiss()
        }
        capture_video?.setOnClickListener {
            videoCapture()
            bottomSheet.dismiss()
        }

        bottomSheet.show()
    }
}