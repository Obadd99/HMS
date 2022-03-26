package com.developers.healtywise.presentation.main.checkResult

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.developers.healtywise.R
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentCheckResultBinding
import com.developers.healtywise.databinding.FragmentSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import com.developers.healtywise.domin.models.account.User
import com.developers.healtywise.presentation.main.checkResult.adapter.ImageSliderAdapter
import javax.inject.Inject

@AndroidEntryPoint
class CheckResultFragment:Fragment() {
    private var _binding: FragmentCheckResultBinding? = null
    private val binding get() = _binding!!
    lateinit var userInfo:User
    @Inject lateinit var dataStoreManager: DataStoreManager

    @Inject
    lateinit var imageSliderAdapter : ImageSliderAdapter
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
                bindUserInfo()
            }
        }
    }

    private fun bindUserInfo() {

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