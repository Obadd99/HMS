package com.developers.healtywise.presentation.main.chat.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.healtywise.databinding.ItemContentRecivedMessageBinding
import com.developers.healtywise.databinding.ItemContentSendMessageBinding
import com.developers.healtywise.databinding.ItemUserSearchBinding
import com.developers.healtywise.databinding.PostItemListBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.domin.models.main.Post
import javax.inject.Inject


class ChatAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECIVE = 2

    var messages: List<ChatMessage>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    lateinit var senderId: String

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

    inner class ChatSendViewHolder(val itemBinding: ItemContentSendMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: ChatMessage) {
            itemBinding.textMessage.text = item.message
            itemBinding.textDateTime.text = item.dateTimeMessage
        }

    }

    inner class ChatReceiverViewHolder(val itemBinding: ItemContentRecivedMessageBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {
        fun bindData(item: ChatMessage) {
            itemBinding.textMessage.text = item.message
            itemBinding.textDateTime.text = item.dateTimeMessage
            item.userReceiverData?.let {
                glide.load(it.imageProfile).into(itemBinding.userImage)
            }
        }

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val itemBinding =
                ItemContentSendMessageBinding.inflate(LayoutInflater.from(parent.context),
                    parent,
                    false)
            ChatSendViewHolder(itemBinding)
        } else {
            val itemBinding =
                ItemContentRecivedMessageBinding.inflate(LayoutInflater.from(parent.context),
                    parent,
                    false)
            ChatReceiverViewHolder(itemBinding)
        }

    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val message = messages[position]


        holder.apply {
            if (getItemViewType(position)==VIEW_TYPE_SENT){
                (this as ChatSendViewHolder ).bindData(message)
            }else{
                (this as ChatReceiverViewHolder).bindData(message)
            }
        }


    }


    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sendId == senderId) {
            VIEW_TYPE_SENT
        } else VIEW_TYPE_RECIVE

    }


    override fun getItemCount(): Int = messages.size


}