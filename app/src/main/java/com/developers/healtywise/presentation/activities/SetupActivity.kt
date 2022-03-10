package com.developers.healtywise.presentation.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants.ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT
import com.developers.healtywise.common.helpers.utils.statusBar
import com.developers.healtywise.databinding.ActivitySetupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
open class SetupActivity : AppCompatActivity(), UICommunicationHelper {
    private lateinit var binding: ActivitySetupBinding
    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        statusBar(R.color.colorPrimary)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_NOSENSOR

        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.navHostFragment.id) as NavHostFragment
        navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            hideMyProgress()
        }
        onNewIntent(intent)

    }


    open fun showMyProgress() {
        binding.layoutProgressView.isVisible = true
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)

    }

    open fun hideMyProgress() {
        binding.layoutProgressView.isVisible = false
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let {
            if (it.action== ACTION_LOGIN_FRAGMENT_AFTER_LOGOUT){
                navController.navigate(R.id.loginFragment)
            }
        }
    }
    override fun isLoading(loading: Boolean, mainActivity: Boolean) {
        if (!mainActivity) {
            if (loading) {
                showMyProgress()
            } else {
                hideMyProgress()
            }
        }
    }
}