package com.developers.healtywise.common.helpers.utils

import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import com.developers.goldady.common.helpers.utils.hideKeyboard
import com.google.android.material.snackbar.Snackbar


fun Fragment.snackbar(message: String) {
    requireView().hideKeyboard()
    Snackbar.make(
        requireView(),
        message,
        Snackbar.LENGTH_LONG
    ).show()

}

infix fun View.snackbar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_LONG
    ).show()

}
 fun Float.fess():Float =(  this* 10) / 100
 fun Float.toEGP():Float =(this) * 100
 fun Float.toCent():Float =(  this) / 100


fun deleteBackStakeAfterNavigate(fragmentId: Int) = NavOptions.Builder()
        .setPopUpTo(fragmentId, true)
        .build()

fun deleteBackStakeAfterNavigate2(fragmentId1: Int,fragmentId2: Int) = NavOptions.Builder()
    .setPopUpTo(fragmentId1, true)
    .setPopUpTo(fragmentId2, true)
    .build()






