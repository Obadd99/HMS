package com.developers.healtywise.presentation.main.home

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.developers.healtywise.common.helpers.AddPostCommunicationHelper
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentHomeBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.presentation.activities.MainActivity
import com.developers.healtywise.presentation.main.home.adapter.PostsAdapter
import com.developers.healtywise.presentation.main.search.adapter.DoctorAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper
    private lateinit var addPostCommunicationHelper: AddPostCommunicationHelper

    private val navController by lazy { findNavController() }

    private val homeViewModel: HomeViewModel by activityViewModels()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var postAdapter: PostsAdapter

    private var userInfo: User?=null
    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setWelcomeMessageForUser()
        loadUserInfo()
        setupFragmentActions()
        subscribeTOGetPostsDoctor()
        setupRecyclerViewPosts()


    }

    private fun subscribeTOGetPostsDoctor() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.getPostState.collect {
                Log.i("GAMALRAGAB", "subscribeTOGetPostsDoctor: ${it.toString()}")
                uiCommunicationListener.isLoading(loading = it.isLoading)

                it.data?.let {
                    postAdapter.posts = it
                    binding.layoutEmptyView.emptyView.isVisible = it.isEmpty()
                    if (it.isNotEmpty())
                        binding.notesRecyclerView.scrollToPosition(0)
                }
                it.error?.let {
                    binding.layoutEmptyView.emptyView.isVisible = true
                    binding.layoutEmptyView.textEmptyErr.text = it
                    snackbar(it)
                }
            }
        }
    }

    private fun loadUserInfo() {
        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                glide.load(it.imageProfile).into(binding.icProfile)
                binding.icAddImg.isVisible = it.doctor
                userInfo=it
            }
        }
    }

    private fun setupFragmentActions() {
        binding.icProfile.setOnClickListener {
            val action = HomeFragmentDirections.actionHomeFragmentToProfileFragment()
            navController.navigate(action)
        }

        binding.icAddImg.setOnClickListener {
            addPostCommunicationHelper.uploadPost()
        }
        binding.etSearch.setOnClickListener {
            (requireActivity() as MainActivity).setupBottomNavClicked(icNotification = true)
        }


    }

    private fun setWelcomeMessageForUser() {
        val c: Calendar = Calendar.getInstance()
        val timeOfDay: Int = c.get(Calendar.HOUR_OF_DAY)

        when (timeOfDay) {
            in 0..11 -> {
                binding.welcomeMessageTv.text = "Good Morning"
            }
            in 12..15 -> {
                binding.welcomeMessageTv.text = "Good Afternoon"
            }
            in 16..20 -> {
                binding.welcomeMessageTv.text = "Good Evening"
            }
            in 21..23 -> {
                binding.welcomeMessageTv.text = "Good Night"
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
            addPostCommunicationHelper = context as AddPostCommunicationHelper
        } catch (e: ClassCastException) {
            Log.e("AppDebug", "onAttach: $context must implement UICommunicationListener")
        }
    }

    private fun setupRecyclerViewPosts() = binding.notesRecyclerView.apply {
        itemAnimator = null
        isNestedScrollingEnabled = true
        layoutManager = LinearLayoutManager(requireContext())
        adapter = postAdapter
    }


    override fun onStart() {
        super.onStart()
        homeViewModel.getPosts()

    }



}