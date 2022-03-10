package com.developers.healtywise.presentation.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants.ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT
import com.developers.healtywise.common.helpers.utils.statusBar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UICommunicationHelper {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var authInstance: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBar(R.color.colorPrimary)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR


        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController
        setupVisibilityOfBottomNavigation()

        itemBottomNavigationClicked(navController)
//
//        binding.logoutTv.setOnClickListener {
//            CustomDialog.showDialogForLogout(
//                this@MainActivity
//            ) {
//                logout()
//            }
//        }

    }

    private fun setupVisibilityOfBottomNavigation() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            hideMyProgress()
            when (destination.id) {
                R.id.editProfileFragment,
                R.id.profileFragment,-> {
                    hideBottomNavigation()
                    hideMyProgress()
                }else -> {
                    showBottomNavigation()
                    hideMyProgress()
                }
            }
        }

    }


    private fun itemBottomNavigationClicked(navController: NavController) {


        binding.icHome.setOnClickListener {
            if (!binding.icHomeView.isVisible) {
                setupBottomNavClicked(icHome = true)
            }
        }
        binding.icMessage.setOnClickListener {
            if (!binding.icMessageView.isVisible) {
                setupBottomNavClicked(icMessage = true)
            }

        }

        binding.icFloatingSend.setOnClickListener {

        }

        binding.icSearch.setOnClickListener {
            if (!binding.icNotificationView.isVisible) {
                setupBottomNavClicked(icNotification = true)
            }
        }

        binding.icSetting.setOnClickListener {
            if (!binding.icSettingsView.isVisible) {
                setupBottomNavClicked(icSetting = true)
            }
        }

    }


    private fun setupBottomNavClicked(
        icHome: Boolean = false,
        icMessage: Boolean = false,
        icNotification: Boolean = false,
        icSetting: Boolean = false,
        outMainActivity: Boolean = false,
    ) {

        binding.icHomeView.isVisible = icHome
        binding.icMessageView.isVisible = icMessage
        binding.icNotificationView.isVisible = icNotification
        binding.icSettingsView.isVisible = icSetting

        val blue = ContextCompat.getColor(this, R.color.colorPrimary)
        when {
            icHome -> {
                ImageViewCompat.setImageTintList(binding.icHome, ColorStateList.valueOf(blue))
                ImageViewCompat.setImageTintList(
                    binding.icMessage,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(
                    binding.icSearch,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(
                    binding.icSetting,
                    ColorStateList.valueOf(Color.GRAY)
                )

                if (!outMainActivity)
                    navController.navigate(R.id.homeFragment)
            }

            icMessage -> {
                ImageViewCompat.setImageTintList(
                    binding.icHome,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(binding.icMessage, ColorStateList.valueOf(blue))
                ImageViewCompat.setImageTintList(
                    binding.icSearch,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(
                    binding.icSetting,
                    ColorStateList.valueOf(Color.GRAY)
                )
                if (!outMainActivity)
                    navController.navigate(R.id.messageFragment)
            }

            icNotification -> {
                ImageViewCompat.setImageTintList(
                    binding.icHome,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(
                    binding.icMessage,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(
                    binding.icSearch,
                    ColorStateList.valueOf(blue)
                )
                ImageViewCompat.setImageTintList(
                    binding.icSetting,
                    ColorStateList.valueOf(Color.GRAY)
                )
                if (!outMainActivity)
                    navController.navigate(R.id.searchFragment)

            }

            icSetting -> {
                ImageViewCompat.setImageTintList(
                    binding.icHome,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(
                    binding.icMessage,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(
                    binding.icSearch,
                    ColorStateList.valueOf(Color.GRAY)
                )
                ImageViewCompat.setImageTintList(binding.icSetting, ColorStateList.valueOf(blue))

                if (!outMainActivity)
                    navController.navigate(R.id.settingFragment)


            }

        }


    }


    //(activity as MainActivity?)!!.hide()
    private fun showBottomNavigation() {
        binding.lnBottomSheet.visibility = View.VISIBLE
    }

    private fun hideBottomNavigation() {
        binding.lnBottomSheet.visibility = View.GONE
    }

    private fun showMyProgress() {

        binding.layoutProgressView.isVisible = true
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

    private fun hideMyProgress() {
        binding.layoutProgressView.isVisible = false
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    fun myProgressState(): Boolean = binding.layoutProgressView.isVisible

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
            Intent(this, SetupActivity::class.java)
                .setAction(ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT)
        )
        finish()
    }

    override fun isLoading(loading: Boolean, mainActivity: Boolean) {
        if (mainActivity) {
            if (loading) showMyProgress() else hideMyProgress()
        }
    }
}