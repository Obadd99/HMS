package com.developers.healtywise.common.helpers.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import com.developers.healtywise.databinding.LogoutDialogLayoutBinding

object CustomDialog {


    fun showDialogForLogout(
        context: Context,
        logoutClickListener: (Boolean) -> Unit
    ) {
        val dialogBuilder: AlertDialog.Builder =
            AlertDialog.Builder(context)
        val dialog: Dialog
        val bind: LogoutDialogLayoutBinding =
            LogoutDialogLayoutBinding.inflate(LayoutInflater.from(context))

        if (bind.root != null) {
            (bind.root as ViewGroup).removeView(bind.root)
        }
        dialogBuilder
            .setView(bind.root)
            .setCancelable(true)
        dialog = dialogBuilder.create()
        dialog.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        dialog.show()

        bind.logoutBtnYes.setOnClickListener {
            // no button id = -1
            dialog.dismiss()
            logoutClickListener(true)
        }
        bind.logoutCancelBtn.setOnClickListener {
            // no button id = -1
            dialog.dismiss()
        }
    }
}