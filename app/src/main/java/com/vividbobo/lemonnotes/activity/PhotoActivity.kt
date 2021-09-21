package com.vividbobo.lemonnotes.activity

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.PhotoView
import com.vividbobo.lemonnotes.R
import com.vividbobo.lemonnotes.databinding.ActivityPhotoBinding

class PhotoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPhotoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhotoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val uri = intent.getStringExtra("photoUri")
        Glide.with(this).load(Uri.parse(uri)).into(binding.photoView)
    }
}