package com.example.chatapp.message

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.R
import kotlinx.android.synthetic.main.item_media_view.view.*

class MediaAdapter(private val context: Context, private val mediaUriList: ArrayList<String>)
    : RecyclerView.Adapter<MediaAdapter.MediaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder {
       val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media_view, null, false)
        return MediaViewHolder(view)
    }

    override fun getItemCount(): Int = mediaUriList.size

    override fun onBindViewHolder(holder: MediaViewHolder, position: Int) {
        Glide.with(context).load(Uri.parse(mediaUriList[position])).into(holder.myImage)
    }


    inner class MediaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val myImage: ImageView = itemView.image_view_id


    }

}