package com.developers.healtywise.presentation.main.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants.SEARCH_TIME_DELAY
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.navigateSafely
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentSearchDoctorBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.presentation.main.search.adapter.DoctorAdapter
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment : Fragment() {
    private var _binding: FragmentSearchDoctorBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private val searchViewModel: SearchViewModel by viewModels()
    private val navController by lazy { findNavController() }

    @Inject
    lateinit var doctorAdapter: DoctorAdapter

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private var job: Job? = null
    private var userInfo: User? = null
    private val client = ChatClient.instance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                userInfo = it
                userInfo?.let {
                    searchViewModel.getDoctorsOrNormalUsers(currentUserId = it.userId,
                        userDoctor = it.doctor)
                    binding.tvTitle.text =
                        if (!it.doctor) getString(R.string.search_to_find_your_doctor) else getString(
                            R.string.search_to_find_your_patient)
                }
            }
        }

        setupFragmentActions()
        subscribeTOGetDoctors()
        setupRecyclerViewPosts()

        doctorAdapter.setOnItemClickListener {
            uiCommunicationListener.isLoading(true)
            createChannel(it)
        }
    }

    private fun createChannel(userSelected: User) {
        client.createChannel(
            channelType = "messaging",
            members = listOf(client.getCurrentUser()!!.id, userSelected.userId)
        ).enqueue {
            if (it.isSuccess) {
                val data= bundleOf("channelId" to it.data().cid)

                navController.navigateSafely(R.id.action_searchFragment_to_chatFragment,data)
            } else {
                snackbar("this user not have channel")
            }
        }
        uiCommunicationListener.isLoading(false)
    }

    private fun setupRecyclerViewPosts() = binding.notesRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = doctorAdapter
    }

    private fun subscribeTOGetDoctors() {
        lifecycleScope.launchWhenStarted {
            searchViewModel.getDoctorState.collect {
                uiCommunicationListener.isLoading(loading = it.isLoading)

                it.data?.let {
                    doctorAdapter.users = it
                    binding.layoutEmptyView.emptyView.isVisible = it.isEmpty()
                }
                it.error?.let {
                    Log.i(TAG, "subscribeTOGetDoctors: ${it}")
                    binding.layoutEmptyView.emptyView.isVisible = true
                    binding.layoutEmptyView.textEmptyErr.text = it
                    snackbar(it)
                }
            }
        }

    }

    private fun setupFragmentActions() {
        binding.backIcon.setOnClickListener {
            navController.popBackStack()
        }

        binding.etSearch.doAfterTextChanged { text ->
            job?.cancel()
            job = lifecycleScope.launch {
                delay(SEARCH_TIME_DELAY)
                text?.let {
                    userInfo?.let { currentUser ->
                        if (it.isNotEmpty()) {
                            searchViewModel.getDoctorsOrNormalUsers(it.toString(),
                                currentUser.userId,
                                userDoctor = currentUser.doctor)
                        } else {
                            searchViewModel.getDoctorsOrNormalUsers(currentUserId = currentUser.userId,
                                userDoctor = currentUser.doctor)
                        }
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSearchDoctorBinding.inflate(inflater, container, false)
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