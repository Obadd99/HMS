package com.developers.healtywise.presentation.main.checkResult

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.utils.Constants
import com.developers.healtywise.common.helpers.utils.navigateSafely
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentCheckResultBinding
import dagger.hilt.android.AndroidEntryPoint
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.domin.models.main.CheckResult
import com.developers.healtywise.presentation.main.checkResult.adapter.ImageSliderAdapter
import kotlinx.coroutines.delay
import javax.inject.Inject

@AndroidEntryPoint
class CheckResultFragment:Fragment() {
    private var _binding: FragmentCheckResultBinding? = null
    private val binding get() = _binding!!
    private lateinit var userInfo:User
    private val navController by lazy { findNavController() }
    @Inject lateinit var dataStoreManager: DataStoreManager
    @Inject lateinit var glide:RequestManager
    @Inject lateinit var imageSliderAdapter : ImageSliderAdapter
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadingUserInfo()
        loadingImageSlider(
            listOf(getString(R.string.imageInfo6),
                getString(R.string.imageInfo1),
                getString(R.string.imageInfo2),
                getString(R.string.imageInfo3),
                getString(R.string.imageInfo4),
                getString(R.string.imageInfo5),
            )
        )

        setupFragmentActions()
    }

    private fun setupFragmentActions() {
        binding.floatingActionButtonCheckResult.setOnClickListener {
            val spo2=binding.etSpo2.text.toString()
            val temp=binding.etTemp.text.toString()
            val bpm=binding.etBpm.text.toString()
            if (validInputs(spo2,temp,bpm)){
                val userInputs=CheckResult(spo2.toFloat(),temp.toFloat(),bpm.toFloat())
                startCheckResult(userInputs)
            }
        }

        binding.backIcon.setOnClickListener { navController.popBackStack() }
    }

    private fun validInputs(spo2: String, temp: String, bpm: String): Boolean {

       return if (spo2.isEmpty()){
            snackbar("Spo2 must be not empty")
            binding.etSpo2.requestFocus()
           false
        }else  if (temp.isEmpty()){
            snackbar("Temperature must be not empty")
            binding.etTemp.requestFocus()
           false
        }else  if (bpm.isEmpty()){
            snackbar(" heart rate must be not empty")
            binding.etBpm.requestFocus()
           false
        }else{
            true
        }

    }

    private fun startCheckResult(userResult: CheckResult){
        lifecycleScope.launchWhenStarted {

            Log.i(Constants.TAG, "onViewCreated: spo2: ${userResult.calcDiagnosisForSpo2()}")
            Log.i(Constants.TAG, "onViewCreated: temp:  ${userResult.calcDiagnosisForTem()}")
            Log.i(Constants.TAG, "onViewCreated: bpm: ${userResult.calcDiagnosisForBPM()}")
            Log.i(Constants.TAG, "onViewCreated: covid: ${userResult.calcDiagnosisForCovid19()}")
            binding.checkResultSuccessLayout.isVisible = true
            binding.layoutContainerCheckResult.isVisible = false
            delay(3500)

            val data= bundleOf("userResult" to userResult)
            navController.navigateSafely(R.id.action_checkResultFragment_to_showUserResultFragment,data)
        }
    }

    private fun loadingImageSlider(pictures : List<String>) {
        imageSliderAdapter.images = pictures
        binding.sliderViewPager.offscreenPageLimit = 1
        binding.sliderViewPager.adapter = imageSliderAdapter
        binding. sliderViewPager.isVisible = true
        binding.viewFadingEdge.isVisible = true
        setUpSliderUpIndicators(pictures.size)
        binding.sliderViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position : Int) {
                super.onPageSelected(position)
                setCurrentSliderIndicators(position)
            }
        })
    }

    private fun setUpSliderUpIndicators(count : Int) {
        val indictors = arrayOfNulls<ImageView>(count)
        val layoutParms = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layoutParms.setMargins(8 , 0 , 8 , 0)
        for (i in indictors.indices) {
            indictors[i] = ImageView(requireContext())
            indictors[i]?.setImageDrawable(
                ContextCompat.getDrawable(
                    requireContext() , R.drawable.background_slider_indicator_inactive
                )
            )
            indictors[i]?.layoutParams = layoutParms
            binding.layoutSliderIndicators.addView(indictors[i])
        }
        binding.layoutSliderIndicators.isVisible = true
        setCurrentSliderIndicators(0)
    }

    private fun setCurrentSliderIndicators(position : Int) {
        val childCount =   binding.layoutSliderIndicators.childCount
        var i = 0;
        while (i < childCount) {
            val imageView =   binding.layoutSliderIndicators.getChildAt(i) as ImageView
            if (i == position) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext() , R.drawable.background_slider_indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireContext() , R.drawable.background_slider_indicator_inactive
                    )
                )
            }
            i ++
        }

    }

    private fun loadingUserInfo() {
        lifecycleScope.launchWhenStarted {
            dataStoreManager.getUserProfile().collect{
                userInfo=it
                    bindUserInfo(userInfo)

            }
        }
    }

    private fun bindUserInfo(userInfo: User) {

        binding.helloUserNameTv.text=getString(R.string.hello_user_name,"${userInfo.firstName} ${userInfo.lastName}")
        glide.load(userInfo.imageProfile).into(binding.icProfile)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentCheckResultBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}