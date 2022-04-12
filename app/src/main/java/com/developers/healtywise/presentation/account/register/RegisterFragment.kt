package com.developers.healtywise.presentation.account.register

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.HealthyValidation
import com.developers.healtywise.common.helpers.utils.Constants.REQUEST_CODE_READ_EXTERNAL_STORAGE_PERMISSIONS
import com.developers.healtywise.common.helpers.utils.PermissionsUtility
import com.developers.healtywise.common.helpers.utils.navigateSafely
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.databinding.FragmentSignUpCreationDataBinding
import com.developers.healtywise.domin.models.account.User
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class RegisterFragment : Fragment(), AdapterView.OnItemSelectedListener,
    EasyPermissions.PermissionCallbacks {
    private var _binding: FragmentSignUpCreationDataBinding? = null
    private val binding get() = _binding!!

    private val navController by lazy { findNavController() }
    private var startDate: Date? = null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var cal = Calendar.getInstance()
    private var doctor: Boolean = true
    private var imageUserProfile: String? = null
    @Inject lateinit var glide:RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageUserProfile?.let {
            loadImage(it.toUri())
        }

        setupFragmentActions()

        initStatusSpinner()

    }

    private fun initStatusSpinner(
        statusResponse: Array<String> = resources.getStringArray(R.array.itemsJob),
    ) {

        val citiesAdapter = ArrayAdapter(
            requireContext(),
            R.layout.layout_spinner_categories,
            statusResponse
        )

        citiesAdapter.setDropDownViewResource(R.layout.my_drop_down_item)
        binding.layoutJobSpinner.adapter = citiesAdapter
        binding.layoutJobSpinner.setSelection(0)
        binding.layoutJobSpinner.onItemSelectedListener = this

    }


    private fun setupFragmentActions() {
        binding.icBackSignUp.setOnClickListener {
            navController.popBackStack()
        }
        binding.nextBtnSignUp.setOnClickListener {
            val firstName = binding.etFirstName.text.toString().trim()
            val lastName = binding.etLastName.text.toString().trim()
            val email = binding.etEmailSignUp.text.toString().trim()
            val mobileNumber = binding.etPhoneSignUp.text.toString().trim()
            val birthdate = binding.etBirthDate.text.toString().trim()
            val male = binding.maleRdaio.isChecked
            if (inputsIsVaild(firstName, lastName, email, mobileNumber, birthdate)) {
                navigateToPasswordScreen(User(firstName = firstName,
                    lastName = lastName,
                    email = email,
                    mobile = mobileNumber,
                    doctor = doctor,
                    male = male,
                    imageProfileUploaded=imageUserProfile,
                    birthDate = birthdate))
            }

        }
        binding.etBirthDate.setOnClickListener {
            calenderStart()
        }
        binding.imgProfile.setOnClickListener {
            requestPermissions()
        }
        binding.openGallery.setOnClickListener {
            requestPermissions()
        }
        binding.tvPolicy.setOnClickListener {
            navController.navigateSafely(R.id.action_registerFragment_to_termsFragment2)
        }
    }

    private fun navigateToPasswordScreen(user: User) {
        val action = RegisterFragmentDirections.actionRegisterFragmentToPasswordSignupFragment(user)
        navController.navigate(action)
    }

    private fun inputsIsVaild(
        firstName: String,
        lastName: String,
        email: String,
        mobile: String,
        birthdate: String,
    ): Boolean {
        return if (firstName.isEmpty()) {
            snackbar("First name is require*")
            binding.etFirstName.requestFocus()
            false
        } else if (lastName.isEmpty()) {
            snackbar("Last name is require*")
            binding.etLastName.requestFocus()
            false
        } else if (email.isEmpty()) {
            snackbar("Email is require*")
            binding.etEmailSignUp.requestFocus()
            false
        } else if (!HealthyValidation.isValidEmail(email)) {
            snackbar("Email is not valid")
            binding.etPhoneSignUp.requestFocus()
            false
        } else if (mobile.isEmpty()) {
            snackbar("Mobile number is require*")
            binding.etPhoneSignUp.requestFocus()
            false
        } else if (!HealthyValidation.validateMobile(mobile)) {
            snackbar("Mobile number is not valid")
            binding.etPhoneSignUp.requestFocus()
            false
        } else if (birthdate.isEmpty()) {
            snackbar("Birth Date is require*")
            false
        } else {
            true
        }
    }


    private fun calenderStart() {
        dateSetListener =
            DatePickerDialog.OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, monthOfYear)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                updateStartDateInView()
            }
        val dialog: DatePickerDialog = DatePickerDialog(
            requireContext(),
            dateSetListener,
            // set DatePickerDialog to point to today's date when it loads up
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        )
        dialog.datePicker.maxDate = System.currentTimeMillis() - 1000
        dialog.show()
    }

    @SuppressLint("NewApi")
    private fun updateStartDateInView() {
        val myFormat = "yyyy/MM/dd" // mention the format you need
        val sdf = SimpleDateFormat(myFormat, Locale.US)
        binding.etBirthDate.setText(sdf.format(cal.time))
        startDate = cal.time
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentSignUpCreationDataBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        when (p2) {
            0 -> {
                doctor = true
            }

            else -> {
                doctor = false
            }
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {

    }

    //
    private fun requestPermissions() {

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

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        } else {
            requestPermissions()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        openGallery()
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

    private fun loadImage(uri: Uri?) {
        uri?.let { myUri ->
            binding.openGallery.visibility = View.GONE
            imageUserProfile = myUri.toString()
            glide.load(myUri).into( binding.imgProfile)

        }
    }

}