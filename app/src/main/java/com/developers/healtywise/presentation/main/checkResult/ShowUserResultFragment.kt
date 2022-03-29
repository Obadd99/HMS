package com.developers.healtywise.presentation.main.checkResult

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.Constants
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentShowUserResultBinding
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.CheckResult
import com.developers.healtywise.domin.models.main.Result
import com.developers.healtywise.presentation.activities.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowUserResultFragment : Fragment() {
    private var _binding: FragmentShowUserResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var userInfo: User
    private val navController by lazy { findNavController() }
    private val args: ShowUserResultFragmentArgs by navArgs()
    private val saveResultViewModel: SaveResultViewModel by viewModels()
    private lateinit var uiCommunicationListener: UICommunicationHelper

    @Inject
    lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingUserInfo()
        bindData(args.userResult)
        fragmentActions(args.userResult)
        subscribeToSaveResult()
    }

    private fun subscribeToSaveResult() {
        lifecycleScope.launchWhenStarted {
            saveResultViewModel.saveResultState.collect {
                it.data?.let {
                    snackbar("Successfully")
                    navigateToMainActivity()
                }
                it.error?.let { snackbar(it) }

                uiCommunicationListener.isLoading(it.isLoading)
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(requireContext(), MainActivity::class.java).setAction(Constants.NAVIGATE_TO_WEB))
        requireActivity().finish()
    }

    private fun fragmentActions(userResult: CheckResult) {
        binding.backIcon.setOnClickListener { navController.popBackStack() }
        binding.sendBrowserBtn.setOnClickListener {

            val note = binding.tvNoteValue.text.toString()
            val result = Result(userInfo.userId,
                userResult.spo1,
                userResult.temp,
                userResult.bpm,
                userResult.calcDiagnosisForSpo2(),
                userResult.calcDiagnosisForTem(),
                userResult.calcDiagnosisForBPM(),
                userResult.calcDiagnosisForCovid19(),
                note, System.currentTimeMillis())
            saveResultViewModel.saveResult(userInfo.userId, result)
        }
    }

    private fun loadingUserInfo() {
        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect {
                userInfo = it
                bindUserInfo(userInfo)

            }
        }
    }

    private fun bindUserInfo(userInfo: User) {

        binding.helloUserNameTv.text =
            getString(R.string.hello_user_name, "${userInfo.firstName} ${userInfo.lastName}")
        glide.load(userInfo.imageProfile).into(binding.icProfile)
    }

    private fun bindData(userResult: CheckResult) {

        binding.tvValueSpo2.text = userResult.spo1.toString()
        binding.tvValueDiagonsisSpo2.text = userResult.calcDiagnosisForSpo2()

        binding.tvValueTemp.text = userResult.temp.toString()
        binding.tvValueDiagonsisTemp.text = userResult.calcDiagnosisForTem()

        binding.tvValueBpm.text = userResult.bpm.toString()
        binding.tvValueDiagonsisBpm.text = userResult.calcDiagnosisForBPM()

        binding.layoutCovid19.isVisible = userResult.calcDiagnosisForCovid19()

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentShowUserResultBinding.inflate(inflater, container, false)
        return binding.root
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