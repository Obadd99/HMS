package com.developers.healtywise.presentation.main.home.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.healtywise.databinding.PostItemListBinding
import com.developers.healtywise.domin.models.main.Post
import javax.inject.Inject


class PostsAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,

) : RecyclerView.Adapter<PostsAdapter.PayViewHolder>() {


    var posts: List<Post>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }

        //
        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class PayViewHolder(val itemBinding: PostItemListBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: Post) {
            itemBinding.tvNoteMessage.text = item.text
            itemBinding.tvUserName.text = item.authorUsername
            itemBinding.tvDate.text = item.currentPostTime
            glide.load(item.authorProfilePictureUrl).into(itemBinding.imgUser)
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayViewHolder {
        val itemBinding =
            PostItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PayViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: PayViewHolder, position: Int) {

        val post = posts[position]


        holder.apply {
            bindData(post)
        }


    }


    override fun getItemCount(): Int = posts.size


}