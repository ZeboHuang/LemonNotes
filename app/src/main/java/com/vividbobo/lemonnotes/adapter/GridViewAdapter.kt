package com.vividbobo.lemonnotes.adapter

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.vividbobo.lemonnotes.R
import com.vividbobo.lemonnotes.activity.PhotoActivity
import com.vividbobo.lemonnotes.activity.VideoActivity
import com.vividbobo.lemonnotes.entity.NoteMedia


/**
 * @author: vibrantBobo
 * @date: 2021/9/20
 * @description:
 */

class GridViewAdapter(val context: Context, val mData: ArrayList<String>) : BaseAdapter() {
    var isShowDelete = false

    fun setIsShowDelete(isShowDelete: Boolean) {
        this.isShowDelete = isShowDelete
        notifyDataSetChanged()
    }


    override fun getCount(): Int {
        return mData.size
    }

    override fun getItem(p0: Int): Any {
        return mData[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    @SuppressLint("ResourceType")
    override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
        val view = if (p1 == null) {
            LayoutInflater.from(context).inflate(R.layout.image_item, p2, false)
        } else p1

        val dm = DisplayMetrics()
        (context as Activity).windowManager.defaultDisplay.getMetrics(dm)
        val height = dm.heightPixels
        view.layoutParams.height = height / 6


        val imageView: ImageView = view.findViewById(R.id.image_item)
        val imageDelete: ImageView = view.findViewById(R.id.image_delete)

        val uri = mData[p0]

//        imageView.setOnClickListener {
//            if (uri.toString().contains("image")) {
//                val intent = Intent(context, PhotoActivity::class.java)
//                intent.putExtra("photoUri", uri)
//                context.startActivity(intent)
//            } else {
//                //video
//                val intent = Intent(context, VideoActivity::class.java)
//                intent.putExtra("videoUri", uri)
//                context.startActivity(intent)
//            }
//        }

        Glide.with(context).load(Uri.parse(uri))
            .centerCrop()
            .into(imageView)
        if (!isShowDelete) {
            imageDelete.visibility = View.GONE
        } else {
            imageDelete.visibility = View.VISIBLE
        }
        return view
    }

}