package com.developers.healtywise.presentation.main.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentHomeBinding
import com.developers.healtywise.databinding.FragmentLoginBinding
import com.developers.healtywise.databinding.FragmentSettingBinding
import com.developers.healtywise.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private val navController by lazy { findNavController() }

    @Inject
    lateinit var dataStoreManager: DataStoreManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupFragmentActions()
    }

    private fun setupFragmentActions() {
        binding.editProfile.setOnClickListener {
            val action = SettingFragmentDirections.actionSettingFragmentToEditProfileFragment()
            navController.navigate(action)
        }
        binding.icBackProfile.setOnClickListener {
            navController.popBackStack()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSettingBinding.inflate(inflater, container, false)
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