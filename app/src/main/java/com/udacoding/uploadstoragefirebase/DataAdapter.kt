package com.udacoding.uploadstoragefirebase

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.item_image.view.*

class DataAdapter(private val data: List<StorageReference>?) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data?.get(position)
        holder.bind(item)
    }

    class ViewHolder(val view: View): RecyclerView.ViewHolder(view){

        fun bind(item: StorageReference?){

            item?.downloadUrl?.continueWith {
                Glide.with(view.context).load(it.result).into(view.imageView)
            }

            view.textView.text = item?.name

        }

    }
}