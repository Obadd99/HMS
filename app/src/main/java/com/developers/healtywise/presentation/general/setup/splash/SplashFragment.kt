package com.developers.healtywise.presentation.general.setup.splash

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.utils.deleteBackStakeAfterNavigate
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentSplashBinding
import com.developers.healtywise.presentation.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment: Fragment() {
    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    private val splashViewModel: SplashViewModel by viewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val anim = AnimationUtils.loadAnimation(context, R.anim.logo_anim)
        binding.splashLogo.startAnimation(anim)
        splashViewModel.checkUserState()

        lifecycleScope.launchWhenStarted {

            splashViewModel.userState.collect {

                if (it){
                    navigateToMainActivity()
                }else{
                    navigateToLoginFragment()
                }
            }
        }
    }

    private fun navigateToLoginFragment() {
        val options = deleteBackStakeAfterNavigate(R.id.splashFragment)
        val action = SplashFragmentDirections.actionSplashFragmentToLoginFragment()
        navController.navigate(action, options)
    }

    private fun navigateToMainActivity() {
        val intent = Intent(requireContext(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}