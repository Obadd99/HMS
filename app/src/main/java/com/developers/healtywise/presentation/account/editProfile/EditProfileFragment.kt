package com.developers.healtywise.presentation.account.editProfile

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.common.helpers.utils.PermissionsUtility
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentEditProfileBinding
import com.developers.healtywise.domin.models.account.User
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.enqueue
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment(), EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper
    private var imageProfileSelected: String? = null
    private val navController by lazy { findNavController() }
    private var userInfo: User? = null
    private val profileViewModel: ProfileViewModel by viewModels()
    private val client = ChatClient.instance()

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var glide: RequestManager


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFragmentActions()

        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                userInfo = it
                glide.load(it.imageProfile).into(binding.userImg)
                binding.etFirstName.setText(it.firstName)
                binding.etLastName.setText(it.lastName)
                binding.etEmail.setText(it.email)
                binding.etMobile.setText(it.mobile)
            }
        }
        subscribeToUpdateProfile()
    }

    private fun subscribeToUpdateProfile() {
        lifecycleScope.launchWhenStarted {
            profileViewModel.editProfileState.collect {
                it.error?.let { snackbar(it) }
                uiCommunicationListener.isLoading(it.isLoading)
                it.data?.let {
                    updateUserInChannel(it)
                    snackbar("Successful update your profile")
                    saveDataIntoLocal(it)
                }
            }
        }
    }

    private fun saveDataIntoLocal(it: User) {
        lifecycleScope.launchWhenStarted {
            dataStoreManager.saveUserProfile(it)
        }
    }

    private fun setupFragmentActions() {
        binding.backIcon.setOnClickListener {
            navController.popBackStack()
        }
        binding.updateProfileBtn.setOnClickListener {
            val firstName = binding.etFirstName.text.toString()
            val lastName = binding.etLastName.text.toString()
            val email = binding.etEmail.text.toString()
            val mobile = binding.etMobile.text.toString()
            if (checkInputs(firstName, lastName, email, mobile)) {
                userInfo?.let {
                    it.apply {
                        this.firstName = firstName
                        this.lastName = lastName
                        this.email = email
                        this.mobile = mobile
                        this.imageProfileUploaded = imageProfileSelected
                    }
                    updateProfile(it)
                }
            }


        }
        binding.openGallery.setOnClickListener {
            requestStoragePermissions()
        }

        binding.userImg.setOnClickListener {
            requestStoragePermissions()
        }
    }

    private fun checkInputs(
        firstName: String,
        lastName: String,
        email: String,
        mobile: String,
    ): Boolean {

        return if (firstName.isEmpty()) {
            snackbar("First name is require")
            binding.etFirstName.requestFocus()
            false
        } else if (lastName.isEmpty()) {
            snackbar("Last name is require")
            binding.etLastName.requestFocus()
            false
        } else if (email.isEmpty()) {
            snackbar("Email is require")
            binding.etEmail.requestFocus()
            false
        } else if (mobile.isEmpty()) {
            snackbar("Phone number is require")
            binding.etMobile.requestFocus()
            false
        } else {
            true
        }

    }

    private fun updateProfile(it: User) {
        profileViewModel.editProfile(it)

    }

    private fun updateUserInChannel(user: User){
        val user = io.getstream.chat.android.client.models.User(id = user.userId).apply {
            name = "${user.firstName} ${user.lastName}"
            image = user.imageProfile
            extraData = mutableMapOf(
                "doctor" to user.doctor,
                "image" to user.imageProfile,
                "name" to "${user.firstName} ${user.lastName}"
            )
        }
        client.updateUser(user).enqueue {
            Log.i(TAG, "updateUserInChannel: ${it.isSuccess}")
        }
    }
    private fun requestStoragePermissions() {

        when {
            PermissionsUtility.hasReadExternalStoragePermissions(requireContext()) -> {
                openGallery()
                return
            }
            else -> when {
                Build.VERSION.SDK_INT < Build.VERSION_CODES.Q -> {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.read_external_storage_message_permissions),
                        REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
                else -> {
                    EasyPermissions.requestPermissions(
                        this,
                        getString(R.string.read_external_storage_message_permissions),
                        REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS)
            EasyPermissions.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults,
                this
            )
    }


    private fun openGallery() {
        CropImage.startPickImageActivity(requireContext(), this)
    }

    private fun cropImage(uri: Uri) {
        CropImage.activity(uri).apply {
            setCropShape(CropImageView.CropShape.OVAL)
            setAspectRatio(1, 1)
            setMultiTouchEnabled(true)
            setGuidelines(CropImageView.Guidelines.ON)
        }.start(requireContext(), this)


    }

    @SuppressLint("CheckResult")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE -> {
                if (resultCode == Activity.RESULT_OK) {
                    val uri = CropImage.getPickImageResultUri(requireContext(), data)
                    cropImage(uri)
                }
            }
            CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE -> {
                val result = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    loadImage(result.uri)
                }
            }


        }

    }

    private fun loadImage(imageProfile: Uri) {
        imageProfile?.let { imageProfile ->
            imageProfileSelected = imageProfile.toString()
            glide.load(imageProfile).into(binding.userImg)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openGallery()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestStoragePermissions()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
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