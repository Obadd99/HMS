package com.developers.healtywise.presentation.account.userProfile

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.dialog.CustomDialog
import com.developers.healtywise.common.helpers.utils.Constants
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentEditProfileBinding
import com.developers.healtywise.databinding.FragmentHomeBinding
import com.developers.healtywise.databinding.FragmentProfileBinding
import com.developers.healtywise.presentation.activities.SetupActivity
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    private val navController by lazy { findNavController() }

    @Inject
    lateinit var authInstance: FirebaseAuth

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var glide: RequestManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFragmentActions()

        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                glide.load(it.imageProfile).into(binding.imgUser)
                binding.userName.text = "${it.firstName} ${it.lastName}"
                binding.mailOfUser.text = it.email
            }
        }
    }

    private fun setupFragmentActions() {
        binding.icBackProfile.setOnClickListener {
            navController.popBackStack()
        }
        binding.logout.setOnClickListener {
            CustomDialog.showDialogForLogout(
                requireContext()
            ) {
                logout()
            }
        }
    }

    private fun logout() {
        lifecycleScope.launchWhenStarted {
            async {
                authInstance.signOut()
                dataStoreManager.logOut()
            }.await()
            navigateToSetupActivity()

        }
    }

    private fun navigateToSetupActivity() {
        startActivity(
            Intent(requireContext(), SetupActivity::class.java)
                .setAction(Constants.ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT)
        )
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        uiCommunicationListener.isLoading(loading = false, mainActivity = true)
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