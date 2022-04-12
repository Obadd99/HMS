package com.developers.healtywise.presentation.main.settings

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.dialog.CustomDialog
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.navigateSafely
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentSettingBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SettingFragment : Fragment() {
    private var _binding: FragmentSettingBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    @Inject
    lateinit var auth: FirebaseAuth
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
        binding.password.setOnClickListener {

            CustomDialog.showDialogForChangePassword(requireContext()) { current, new ->
                changeUserPassword(current,new){
                    uiCommunicationListener.isLoading(it)
                }

            }
        }
        binding.icBackProfile.setOnClickListener {
            navController.popBackStack()
        }
        binding.termsConditions.setOnClickListener {
            navController.navigateSafely(R.id.action_settingFragment_to_termsFragment)
        }
    }

    private fun changeUserPassword(current: String, new: String,showLoading:(Boolean)->Unit) {
        showLoading(true)
        val currentEmail: String = auth.currentUser?.email ?: ""
        val credential = EmailAuthProvider.getCredential(currentEmail, current)
        auth.currentUser?.reauthenticate(credential)?.addOnCompleteListener {
            if (it.isSuccessful) {
                auth.currentUser?.updatePassword(new)?.addOnCompleteListener {
                    if (it.isSuccessful) snackbar("Update password success")
                    else {
                        snackbar(it.exception?.localizedMessage ?: "")
                        Log.i(TAG, "setupFragmentActions: ${it.exception?.localizedMessage}")
                    }
                    showLoading(false)
                }
            } else {
                showLoading(false)
                snackbar("Current password is wrong!")
                return@addOnCompleteListener
            }
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