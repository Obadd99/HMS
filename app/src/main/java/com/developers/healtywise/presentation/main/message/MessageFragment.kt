package com.developers.healtywise.presentation.main.message

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.ChannelListLoadingViewBinding
import com.developers.healtywise.databinding.FragmentMessageBinding
import com.developers.healtywise.databinding.LogoutDialogLayoutBinding
import com.developers.healtywise.domin.models.account.User
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Channel
import io.getstream.chat.android.client.models.Filters
import io.getstream.chat.android.ui.channel.list.header.viewmodel.ChannelListHeaderViewModel
import io.getstream.chat.android.ui.channel.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.ChannelListViewModel
import io.getstream.chat.android.ui.channel.list.viewmodel.bindView
import io.getstream.chat.android.ui.channel.list.viewmodel.factory.ChannelListViewModelFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MessageFragment : Fragment() {
    private var _binding: FragmentMessageBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper
    private var userInfo: User? = null
    private val client = ChatClient.instance()

    private val navController by lazy { findNavController() }

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
// Inflate loading view
        val loadingView: ChannelListLoadingViewBinding = ChannelListLoadingViewBinding.inflate(LayoutInflater.from(context))
// Set loading view
        binding.channelListView.setLoadingView(loadingView.root, FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT))

        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                userInfo = it
                withContext(Dispatchers.Main) {
                    userInfo?.let {
                        setupChannels(it)
                        binding.tvTitle.text = if (!it.doctor) "Your Doctor's" else "Your patient's"
                    }
                }
            }

        }
        setupFragmentActions()


    }

    private fun setupFragmentActions() {
        binding.channelListView.setChannelItemClickListener { channel ->
            val action =
                MessageFragmentDirections.actionMessageFragmentToChatFragment(channelId = channel.cid)
            navController.navigate(action)
        }
        binding.icAddImg.setOnClickListener {
            navController.navigate(R.id.searchFragment)
        }
        binding.chatBackImaged.setOnClickListener {
            navController.popBackStack()
        }

        binding.channelListView.setChannelInfoClickListener { channel ->
            // Handle channel info click
        }
        binding.channelListView.setUserClickListener { user ->
            // Handle member click
        }

    }


    private fun setupChannels(user: User) {
        client.getCurrentUser()?.let {
            val filter = Filters.and(
                Filters.eq("type", "messaging"),
                Filters.`in`("members", listOf(it.id))
            )
            val viewModelFactory =
                ChannelListViewModelFactory(filter, ChannelListViewModel.DEFAULT_SORT)
            val channelsViewModel: ChannelListViewModel by viewModels { viewModelFactory }
            val listHeaderViewModel: ChannelListHeaderViewModel by viewModels()
            channelsViewModel.bindView(binding.channelListView, viewLifecycleOwner)
            //  listHeaderViewModel.bindView(binding.userHeaderLayout, viewLifecycleOwner)
            Log.i(TAG, "setupChannels: ${listHeaderViewModel.currentUser.value.toString()} ")
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