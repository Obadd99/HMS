package com.developers.healtywise.presentation.main.search.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.developers.healtywise.databinding.ItemUserSearchBinding
import com.developers.healtywise.databinding.PostItemListBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.Post
import javax.inject.Inject


class DoctorAdapter @Inject constructor(
    private val glide: RequestManager,
    private val context: Context,

) : RecyclerView.Adapter<DoctorAdapter.PayViewHolder>() {


    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)


    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.userId == newItem.userId
        }

        //
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }
    private val differ = AsyncListDiffer(this, diffCallback)

    inner class PayViewHolder(val itemBinding: ItemUserSearchBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bindData(item: User) {
            itemBinding.textEmail.text = item.email
            itemBinding.textUsername.text = "${item.firstName} ${item.lastName}"
            glide.load(item.imageProfile).into(itemBinding.userImage)
            setupActions(item)

        }

        private fun setupActions(item: User) {

            itemBinding.root.setOnClickListener {
                onItemClickListener?.let { action->
                    action(item)
                }
            }
        }


    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PayViewHolder {
        val itemBinding =
            ItemUserSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PayViewHolder(itemBinding)
    }

    @SuppressLint("Range")
    override fun onBindViewHolder(holder: PayViewHolder, position: Int) {

        val user = users[position]


        holder.apply {
            bindData(user)
        }


    }


    override fun getItemCount(): Int = users.size

    private var onItemClickListener: ((User) -> Unit)? = null

    fun setOnItemClickListener(listener: (User) -> Unit) {
        onItemClickListener = listener
    }
}