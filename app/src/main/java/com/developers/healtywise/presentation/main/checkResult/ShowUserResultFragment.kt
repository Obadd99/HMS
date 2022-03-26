package com.developers.healtywise.presentation.main.checkResult

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.developers.healtywise.common.helpers.utils.Constants.TAG
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentCheckResultBinding
import com.developers.healtywise.databinding.FragmentShowUserResultBinding
import com.developers.healtywise.domin.models.account.User
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ShowUserResultFragment : Fragment() {
    private var _binding: FragmentShowUserResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var userInfo: User
    private val navController by lazy { findNavController() }
    private val args:ShowUserResultFragmentArgs by navArgs()
    @Inject
    lateinit var dataStoreManager: DataStoreManager
    @Inject
    lateinit var glide: RequestManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.i(TAG, "onViewCreated: spo2: ${args.userResult.calcDiagnosisForSpo2()}")
        Log.i(TAG, "onViewCreated: temp:  ${args.userResult.calcDiagnosisForTem()}")
        Log.i(TAG, "onViewCreated: bpm: ${args.userResult.calcDiagnosisForBPM()}")

        binding.textView.text="your spo2:${args.userResult.spo1} Diagnosis:${args.userResult.calcDiagnosisForSpo2()}\n" +
                "your Temp:${args.userResult.temp} Diagnosis:${args.userResult.calcDiagnosisForTem()}\n " +
                "your bmp:${args.userResult.bpm} Diagnosis:${args.userResult.calcDiagnosisForBPM()} "
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentShowUserResultBinding.inflate(inflater, container, false)
        return binding.root
    }

}