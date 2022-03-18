package com.developers.healtywise.presentation.main.message

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentMessageBinding
import com.developers.healtywise.presentation.main.message.adapter.RecentMessageAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private val navController by lazy { findNavController() }

    private val messageViewModel: MessageViewModel by viewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var recentMessageAdapter: RecentMessageAdapter


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                messageViewModel.getRecentMessage(it.userId)
            }

        }

        subscribeToGetRecentMessage()
        setupRecyclerViewRecentMessages()

        setupFragmentActions()
        setupAdapterActions()
    }

    private fun setupAdapterActions() {
        recentMessageAdapter.setOnItemClickListener {
            it.userReceiverData?.let {
                val action = MessageFragmentDirections.actionMessageFragmentToChatFragment(it)
                navController.navigate(action)

            }
            }
        }

        private fun setupFragmentActions() {
            binding.chatBackImaged.setOnClickListener {
                navController.popBackStack()
            }
        }

        private fun setupRecyclerViewRecentMessages() = binding.chatRecyclerView.apply {
            itemAnimator = null
            isNestedScrollingEnabled = true
            layoutManager = LinearLayoutManager(requireContext())
            adapter = recentMessageAdapter
        }

        private fun subscribeToGetRecentMessage() {
            lifecycleScope.launchWhenStarted {
                messageViewModel.getRecentMessageUiState.collect {
                    it.error?.let {
                        snackbar(it)
                    }
                    uiCommunicationListener.isLoading(it.isLoading)
                    it.data?.let {
                        recentMessageAdapter.recents = it
                    }
                }
            }
        }

        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?,
        ): View? {
            _binding = FragmentMessageBinding.inflate(inflater, container, false)
            return binding.root
        }


        override fun onDestroyView() {
            super.onDestroyView()
            _binding = null
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