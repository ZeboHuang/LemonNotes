package com.vividbobo.lemonnotes.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.vividbobo.lemonnotes.databinding.ActivityVideoBinding

class VideoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVideoBinding
    private lateinit var player: SimpleExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uri = intent.getStringExtra("videoUri")
        player = SimpleExoPlayer.Builder(this).build()

        binding.palyerView.player = player
        val mediaItem = MediaItem.fromUri(Uri.parse(uri))
        player.addMediaItem(mediaItem)
        player.prepare()

        binding.palyerView.setOnClickListener {
            player.play()
        }
    }

    override fun onDestroy() {
        player.release()
        super.onDestroy()
    }

}