package com.developers.healtywise.presentation.main.search

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants.SEARCH_TIME_DELAY
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentSearchDoctorBinding
import com.developers.healtywise.presentation.main.search.adapter.DoctorAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SearchFragment:Fragment() {
    private var _binding: FragmentSearchDoctorBinding? = null
    private val binding get() = _binding!!
    private lateinit var  uiCommunicationListener: UICommunicationHelper

    private val searchViewModel:SearchViewModel by viewModels()
    private val navController by lazy { findNavController() }
    @Inject
    lateinit var doctorAdapter: DoctorAdapter
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private var job: Job? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        searchViewModel.getDoctors()
        setupFragmentActions()
        subscribeTOGetDoctors()
        setupRecyclerViewPosts()

        doctorAdapter.setOnItemClickListener {
            val action=SearchFragmentDirections.actionSearchFragmentToChatFragment(it)
            navController.navigate(action)
        }
    }

    private fun setupRecyclerViewPosts() =binding.notesRecyclerView.apply {
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
                    if (it.isNotEmpty()) {
                        searchViewModel.getDoctors(it.toString())
                    }else{
                        searchViewModel.getDoctors()
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