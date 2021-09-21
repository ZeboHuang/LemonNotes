package com.vividbobo.lemonnotes.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.vividbobo.lemonnotes.database.DatabaseHelper
import com.vividbobo.lemonnotes.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolBar)
    }
}