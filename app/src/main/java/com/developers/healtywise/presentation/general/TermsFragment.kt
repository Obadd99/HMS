package com.developers.healtywise.presentation.general

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.developers.healtywise.R
import com.developers.healtywise.common.helpers.UICommunicationHelper
import com.developers.healtywise.common.helpers.utils.snackbar
import com.developers.healtywise.data.local.dataStore.DataStoreManager
import com.developers.healtywise.databinding.FragmentTermsConditionBinding
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TermsFragment : Fragment() {
    private var _binding: FragmentTermsConditionBinding? = null
    private val binding get() = _binding!!
    private lateinit var uiCommunicationListener: UICommunicationHelper

    @Inject
    lateinit var auth: FirebaseAuth
    private val navController by lazy { findNavController() }

    @Inject
    lateinit var dataStoreManager: DataStoreManager
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        privcy_text.also {
//            binding.txtPageContent.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                Html.fromHtml(it, Html.FROM_HTML_MODE_COMPACT)
//            } else {
//                Html.fromHtml(it)
//            }
//        }
        binding.btnBack.setOnClickListener { navController.popBackStack() }
        binding.btnSave.setOnClickListener {
            if (binding.icCheckTerms.isChecked) {
                findNavController().popBackStack()
            } else {
                snackbar(getString(R.string.must_agre_the_terms_and_conditions))
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentTermsConditionBinding.inflate(inflater, container, false)
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