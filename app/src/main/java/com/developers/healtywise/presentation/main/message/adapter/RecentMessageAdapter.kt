package com.developers.healtywise.presentation.main.message.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.healtywise.databinding.ItemUserRecentMessageBinding
import com.developers.healtywise.databinding.PostItemListBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.domin.models.main.Post
import javax.inject.Inject


class RecentMessageAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,

) : RecyclerView.Adapter<RecentMessageAdapter.PayViewHolder>() {


    var recents: List<ChatMessage>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<ChatMessage>() {
        override fun areContentsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.id == newItem.id
        }

        //
        override fun areItemsTheSame(oldItem: ChatMessage, newItem: ChatMessage): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class PayViewHolder(val itemBinding: ItemUserRecentMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: ChatMessage) {
            itemBinding.textRecentMessage.text = item.message
            item.userReceiverData?.let {
                itemBinding.textUsername.text = "${it.firstName} ${it.lastName}"
                glide.load(it.imageProfile).into(itemBinding.userImage)
            }

            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { action->
                    action(item)
                }
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayViewHolder {
        val itemBinding =
            ItemUserRecentMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PayViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: PayViewHolder, position: Int) {

        val recent = recents[position]


        holder.apply {
            bindData(recent)
        }


    }


    override fun getItemCount(): Int = recents.size

    private var onItemClickListener: ((ChatMessage) -> Unit)? = null

    fun setOnItemClickListener(listener: (ChatMessage) -> Unit) {
        onItemClickListener = listener
    }

}