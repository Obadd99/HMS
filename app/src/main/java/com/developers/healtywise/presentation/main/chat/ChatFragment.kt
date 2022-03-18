package com.developers.healtywise.presentation.main.chat

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.decodeByte
import com.developers.healtywise.common.helpers.utils.encodeKey
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentChatBinding
import com.developers.healtywise.domin.models.main.ChatMessage
import com.developers.healtywise.presentation.main.chat.adapter.ChatAdapter
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.lang.reflect.Array
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList


@AndroidEntryPoint
class ChatFragment : Fragment() {
    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private val args: ChatFragmentArgs by navArgs()
    private val navController by lazy { findNavController() }

    private val sendMessageViewModel: SendMessageViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var chatAdapter: ChatAdapter
    private val chatsList: ArrayList<ChatMessage> by lazy {
        ArrayList<ChatMessage>()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
        lifecycleScope.launchWhenStarted {
            uiCommunicationListener.isLoading(true)
            dataStoreManager.getUserProfile().collect {
                chatAdapter.senderId = it.userId
                // sendMessageViewModel.getMessage(args.user.imageProfile,it.userId,args.user.userId)
                uiCommunicationListener.isLoading(false)
                addMessageHotSnap(it.userId, args.user.userId)
            }
        }

        setListenerActions()
        loadUserReceivedDetials()
        setupRecyclerViewMessages()
        subscribeToMessagesState()
        subscribeToSendMessagesState()
        binding.etMessage.doAfterTextChanged {
            if (chatsList.size > 0) {
                try {
                    binding.chatRecyclerView.scrollToPosition(  chatAdapter.itemCount - 1)
                }catch (e:Exception){
                    Log.i(TAG, "Exception:${e.localizedMessage} ")
                }
            }
        }
    }

    private fun addMessageHotSnap(sendId: String, receiverId: String) {
        val messages = FirebaseFirestore.getInstance().collection(Constants.MESSAGES)
        messages.whereEqualTo("sendId", sendId)
            .whereEqualTo("receiverId", receiverId)
            .addSnapshotListener(messageListener)
        messages.whereEqualTo("sendId", receiverId)
            .whereEqualTo("receiverId", sendId)
            .addSnapshotListener(messageListener)
    }

    private val messageListener: EventListener<QuerySnapshot> = EventListener { value, error ->
        value?.let {
            Log.i(TAG, "EventListener:${it.toString()} ")
            // val messages = it.toObjects(ChatMessage::class.java)
            it.documentChanges.forEach {
                if (it.type == DocumentChange.Type.ADDED) {
                    val message = it.document.toObject(ChatMessage::class.java)
                    message.also {
                        it.dateTimeMessage =
                            SimpleDateFormat("EEE, d MMM yyyy hh:mm aaa",
                                Locale.US).format(Date(it.date))
                        it.userReceiverData = args.user
                        it.message = decodeByte(it.message)
                    }
                    chatsList.add(message)
                }
            }
            chatsList.sortBy { it.date }
            chatAdapter.messages = chatsList
            if (chatsList.size > 0) {
                try {
                    binding.chatRecyclerView.scrollToPosition(  chatAdapter.itemCount - 1)
                }catch (e:Exception){
                    Log.i(TAG, "Exception:${e.localizedMessage} ")
                }
            }
        }
    }

    private fun subscribeToSendMessagesState() {
        lifecycleScope.launchWhenStarted {
            sendMessageViewModel.sendMessagedState.collect {
                Log.i(TAG, "subscribeToSendMessagesState: ${it.toString()}")
            }
        }
    }

    private fun subscribeToMessagesState() {

        lifecycleScope.launchWhenStarted {
            sendMessageViewModel.getMessageState.collect {
                Log.i(TAG, "subscribeToMessagesState: ${it}")
                it.error?.let {
                    snackbar(it)
                }
                it.data?.let {
                    chatAdapter.messages = it
                }
            }
        }
    }

    private fun loadUserReceivedDetials() {
        "${args.user.firstName} ${args.user.lastName}".also { binding.textName.text = it }
    }

    private fun setupRecyclerViewMessages() = binding.chatRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = chatAdapter
    }

    private fun setListenerActions() {
        binding.chatBackImaged.setOnClickListener {
            navController.popBackStack()
        }
        binding.sendLayout.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                val messageEncoded = encodeKey(message)
                sendMessage(messageEncoded, args.user.userId)
            }
        }
    }

    private fun sendMessage(messageEncoded: String, receiverId: String) {
        Log.i(TAG, "sendMessage: ${messageEncoded}")
        sendMessageViewModel.sendMessage(messageEncoded, receiverId)
        binding.etMessage.setText("")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentChatBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            uiCommunicationListener = context as UICommunicationHelper
        } catch (e: ClassCastException) {
            Log.e("AppDebug", "onAttach: $context must implement UICommunicationListener")
        }
    }
}