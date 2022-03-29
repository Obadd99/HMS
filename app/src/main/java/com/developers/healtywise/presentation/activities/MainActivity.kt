package com.developers.healtywise.presentation.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.ColorStateList
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.core.widget.ImageViewCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.AddPostCommunicationHelper
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.dialog.CustomDialog
import com.developers.healtywise.common.helpers.utils.Constants.ACTION_NEW_MESSAGE_SENT
import com.developers.healtywise.common.helpers.utils.Constants.NAVIGATE_TO_WEB
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.navigateSafely
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.common.helpers.utils.statusBar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.ActivityMainBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.presentation.main.home.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.models.Device
import io.getstream.chat.android.client.models.PushProvider
import io.getstream.chat.android.core.internal.InternalStreamChatApi
import kotlinx.coroutines.flow.collectLatest
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), UICommunicationHelper, AddPostCommunicationHelper {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val client = ChatClient.instance()

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    private val homeViewModel: HomeViewModel by viewModels()
    private var userinfo: User? = null

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

        subscriptToCreatePostState()

        onNewIntent(intent)

    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        intent?.let{
            val channelId=it.getStringExtra("channelId")
            val channelType=it.getStringExtra("channelType")

            if(it.action==ACTION_NEW_MESSAGE_SENT){
                channelId?.let {id->
                    val bundle = bundleOf("channelId" to "$channelType:$channelId")
                    navController.navigate(R.id.chatFragment, bundle)
                }
            }else if(it.action==NAVIGATE_TO_WEB){

                val  browserIntent =  Intent(Intent.ACTION_VIEW, Uri.parse("https://health-wise.netlify.app"));
                startActivity(browserIntent);
            }else{

            }
        }
    }


    private fun setupVisibilityOfBottomNavigation() {
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
          //  hideMyProgress()
            setupBottomNavClicked(icHome = destination.id == R.id.homeFragment,
                icMessage = destination.id == R.id.messageFragment,
                icSetting = destination.id == R.id.settingFragment,
                icNotification = destination.id == R.id.searchFragment, outMainActivity = true)
            when (destination.id) {
                R.id.editProfileFragment,
                R.id.profileFragment,
                R.id.chatFragment,
                R.id.messageFragment,
                R.id.checkResultFragment,
                R.id.showUserResultFragment,
                -> {
                    hideBottomNavigation()
                }
                else -> {
                    showBottomNavigation()
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                userinfo = it
                setupChatClient(it)
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
            if (!binding.icMessageView.isVisible && client.getCurrentUser() != null) {
                setupBottomNavClicked(icMessage = true)
            }
        }

        binding.icFloatingSend.setOnClickListener {
            uploadPost()
        }

        binding.icSearch.setOnClickListener {
            if (!binding.icNotificationView.isVisible && client.getCurrentUser() != null) {
                setupBottomNavClicked(icNotification = true)
            }
        }

        binding.icSetting.setOnClickListener {
            if (!binding.icSettingsView.isVisible) {
                setupBottomNavClicked(icSetting = true)
            }
        }

    }

    private fun setupChatClient(it: User) {
        if (client.getCurrentUser() == null) {
            Log.i(TAG, "setupChatClient: ")
            val user = io.getstream.chat.android.client.models.User(id = it.userId).apply {
                name = "${it.firstName} ${it.lastName}"
                image = it.imageProfile
                extraData = mutableMapOf(
                    "doctor" to it.doctor,
                    "image" to it.imageProfile,
                    "name" to "${it.firstName} ${it.lastName}"
                )
            }
            val token = client.devToken(it.userId)
            client.connectUser(
                user = user,
                token = token
            ).enqueue { result ->
                if (result.isSuccess) {
                    Log.i(TAG, "setupChatClient: setup channels")
                } else {
                    binding.root snackbar (result.error().message.toString())
                }
            }
        }

        Log.i(TAG, "setupChatClient: ${client.getCurrentUser()}")

    }

    open fun setupBottomNavClicked(
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
        Log.i(TAG, "showMyProgress: ")
    }

    private fun hideMyProgress() {
        binding.layoutProgressView.isVisible = false
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
        Log.i(TAG, "hideMyProgress: ")

    }

    fun myProgressState(): Boolean = binding.layoutProgressView.isVisible


    override fun isLoading(loading: Boolean, mainActivity: Boolean) {
        if (mainActivity) {
            if (loading) showMyProgress() else hideMyProgress()
        }
    }

    override fun uploadPost() {
        Log.i(TAG, "uploadPost: ")
        userinfo?.let {
            if (it.doctor) {
                CustomDialog.showDialogForAddPost(this@MainActivity) {
                    homeViewModel.createPost(it)
                }
            } else {
                Log.i(TAG, "uploadPost: navigate")
                navController.navigate(R.id.checkResultFragment)
            }
        }
    }

    private fun subscriptToCreatePostState() {
        lifecycleScope.launchWhenStarted {
            homeViewModel.createPostStateCreatePost.collect {
                it.data?.let {
                    binding.root snackbar ("Success Uploaded")
                    homeViewModel.getPosts()
                }
                if (it.isLoading) showMyProgress() else hideMyProgress()
                it.error?.let {
                    binding.root snackbar (it)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
//            client.disconnectSocket()
        }catch (e:Exception){}
    }
}