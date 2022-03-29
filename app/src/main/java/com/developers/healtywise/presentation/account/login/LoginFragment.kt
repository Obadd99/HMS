package com.developers.healtywise.presentation.account.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.HealthyValidation
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.dialog.CustomDialog
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentLoginBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.presentation.activities.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject


@AndroidEntryPoint
class LoginFragment:Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var  uiCommunicationListener: UICommunicationHelper

    private val navController by lazy { findNavController() }
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    @Inject lateinit var auth: FirebaseAuth
    private val loginViewModel:LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActions()
        subscribeToLoginFlow()
    }

    private fun setupActions() {
        binding.loginBtn.setOnClickListener {
            val email=binding.etEmailLogin.text.toString().trim()
            val password=binding.etPassLogin.text.toString().trim()
            if (inputsValid(email,password)){
                loginViewModel.login(email,password)
            }
        }
        binding.tvSignUp.setOnClickListener {
            navController.navigate(R.id.registerFragment)
        }
        binding.tvForgetPass.setOnClickListener {
            CustomDialog.showForgetPasswordDialogue(requireContext()){email->
               auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                          snackbar("Successfully sent email verification, please check it  ")
                        }else{
                            snackbar(task.exception?.localizedMessage?:"")
                        }
                    }
            }
        }
    }


    private fun inputsValid(email: String, password: String): Boolean {
        return if (email.isEmpty()) {
            binding.etEmailLogin.requestFocus()
            binding.etEmailLogin.error = "Email is require"
            snackbar("Email is require")
            false
        } else if (password.isEmpty()) {


            binding.etPassLogin.requestFocus()
            binding.etPassLogin.error = "Password is require"
            snackbar("Password is require")
            false
        } else if (!HealthyValidation.isValidEmail(email)) {
            binding.etEmailLogin.requestFocus()
            binding.etEmailLogin.error = "Email is not valid"
            snackbar("Email is valid")
            false
        } else {
            true
        }

    }

    private fun subscribeToLoginFlow() {
        lifecycleScope.launchWhenStarted {
            loginViewModel.loginState.collect {
                it.data?.let {
                    snackbar("Login Successfully")
                    saveDataInLocal(it)
                }
                uiCommunicationListener.isLoading(it.isLoading,false)
                it.error?.let {
                    snackbar(it)
                }
            }
        }
    }

    private fun saveDataInLocal(user: User) {
        lifecycleScope.launchWhenCreated {
            async {
                dataStoreManager.saveUserProfile(user)
            }.await()
            navigateToMainActivity()
        }
    }

    private fun navigateToMainActivity() {
        startActivity(
            Intent(requireContext(), MainActivity::class.java)
                .setFlags((Intent.FLAG_ACTIVITY_CLEAR_TASK) and (Intent.FLAG_ACTIVITY_CLEAR_TOP))
        )
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        uiCommunicationListener.isLoading(loading = false, mainActivity = false)
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