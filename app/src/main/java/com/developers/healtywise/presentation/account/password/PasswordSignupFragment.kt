package com.developers.healtywise.presentation.account.password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.developers.healtywise.common.helpers.HealthyValidation
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentSignUpPasswordBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.presentation.account.register.RegisterViewModel
import com.developers.healtywise.presentation.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject


@AndroidEntryPoint
class PasswordSignupFragment : Fragment() {
    private var _binding: FragmentSignUpPasswordBinding? = null
    private val binding get() = _binding!!
    private lateinit var  uiCommunicationListener:UICommunicationHelper
    private val navController by lazy {
        findNavController()
    }
    private val registerViewModel: RegisterViewModel by viewModels()
    @Inject
    lateinit var dataStoreManager: DataStoreManager

    val args: PasswordSignupFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupActions()
        subscribeToRegisterFlow()

        checkPasswordValidation()
    }

    private fun checkPasswordValidation() {
        binding.etPassSignUp.doOnTextChanged { text, start, before, count ->
            binding.layoutPassSignUp.helperText=""
            binding.checkboxValid1.isChecked = HealthyValidation.optionOneForPassword(text.toString())
            binding.checkboxValid2.isChecked = HealthyValidation.optionTwoForPassword(text.toString())
            binding.checkboxValid3.isChecked =
                HealthyValidation.optionThreeForPassword(text.toString())
        }
        binding.etConfirmPassSignUp.doOnTextChanged { text, start, before, count ->

            if (text.toString()!=binding.etPassSignUp.text.toString())
                binding.layoutConfirmPassSignUp.helperText="confirm Password not match password"
            else  binding.layoutConfirmPassSignUp.helperText=""
        }
    }



    private fun setupActions() {
        binding.btnSignUp.setOnClickListener {
            val password=binding.etPassSignUp.text.toString()
            val confirmPassword=binding.etConfirmPassSignUp.text.toString()
            if (inputsIsValid(password,confirmPassword)) {
                registerViewModel.register(args.user,password)
            }
        }

        binding.icBackSignUp.setOnClickListener {
            // navigateToMainActivity()
            navController.popBackStack()
        }
    }
    private fun subscribeToRegisterFlow() {
        lifecycleScope.launchWhenStarted {
            registerViewModel.registerStateRegister.collect {
                it.data?.let {
                    snackbar("Register Successfully")
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

    private fun inputsIsValid(password: String, confirmPassword: String) =
        if (password.isEmpty()) {
            snackbar("Password  must be not empty")
            binding.layoutPassSignUp.helperText =
                "Password  must be not empty"
            false
        } else if (!binding.checkboxValid1.isChecked) {
            snackbar("Password must greater than 6 char and no white space ")
            binding.layoutPassSignUp.helperText =
                "Password must greater than 6 char and no white space"
            false

        } else if (!binding.checkboxValid2.isChecked) {
            snackbar("Password must  include 1 upperCase and lowerCase ")
            binding.layoutPassSignUp.helperText =
                "Password must  include 1 upperCase and lowerCase"
            false

        } else if (!binding.checkboxValid3.isChecked) {
            snackbar("Password must include 1 number")
            binding.layoutPassSignUp.helperText = "Password must include 1 number"
            false

        } else if (confirmPassword.isEmpty()) {
            snackbar("confirm password  is empty")
            binding.layoutConfirmPassSignUp.helperText="confirm Password not match password"
            false
        }
        else if(confirmPassword!=binding.etPassSignUp.text.toString()) {
            snackbar("confirm Password not match password")
            binding.layoutConfirmPassSignUp.helperText="confirm Password not match password"
            false
        }else {
            true
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
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpPasswordBinding.inflate(inflater, container, false)

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