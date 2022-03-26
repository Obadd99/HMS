package com.developers.healtywise.presentation.main.checkResult.adapter

import android.annotation.SuppressLint
import android.content.Context

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.healtywise.databinding.ItemContainerSliderImageBinding
import javax.inject.Inject


class ImageSliderAdapter @Inject constructor(
    private val glide : RequestManager,
    private val context : Context,
) : RecyclerView.Adapter<ImageSliderAdapter.ImageSliderViewHolder>() {


    var images : List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private val diffCallback = object : DiffUtil.ItemCallback<String>() {
        override fun areContentsTheSame(oldItem : String , newItem : String) : Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem : String , newItem : String) : Boolean {
            return oldItem== newItem
        }
    }
    private val differ = AsyncListDiffer(this , diffCallback)

   inner class ImageSliderViewHolder(val myitemView:  ItemContainerSliderImageBinding) : RecyclerView.ViewHolder(myitemView.root) {
        fun bind(image: String) {
            glide.load(image).into(myitemView.imageSliderContent)
        }
    }

    override fun onCreateViewHolder(parent : ViewGroup , viewType : Int) : ImageSliderViewHolder {
        val itemBinding =
            ItemContainerSliderImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ImageSliderViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder : ImageSliderViewHolder , position : Int) {
        val image = images[position]
        holder.apply {

            holder.bind(image)


            itemView.setOnClickListener {
                   onItemClickListener?.let { click->
                       click(image)
                   }
            }

        }
    }

    override fun getItemCount() : Int = images.size

    private var onItemClickListener : ((String) -> Unit)? = null

    fun setOnItemClickListener(listener : (String) -> Unit) {
        onItemClickListener = listener
    }


}