package com.example.pocketstatistician.convenience

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pocketstatistician.R
import com.example.pocketstatistician.activities.TableActivity

class YouChooseDialog(private val header: CharSequence, private val positiveButtonText: CharSequence, private val negativeButtonText: CharSequence,
                      private val neutralButtonText: CharSequence = ""): DialogFragment() {

    var dialogEventHandler: DialogClickListener? = null
    var dialogNeutralButtonListener: DialogNeutralClickListener? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(header)
            .setNegativeButton(
                negativeButtonText
            ) { dialog, which ->
                dialogEventHandler?.onNegativeButtonClick()
                dismiss()
            }
            .setPositiveButton(
                positiveButtonText
            ) { dialog, which ->
                dialogEventHandler?.onPositiveButtonClick()
            }

        if (neutralButtonText.isNotBlank()) {
            builder.setNeutralButton(neutralButtonText) { dialog, which ->
                dialogNeutralButtonListener?.onNeutralButtonClick()
            }
        }

        return builder.create()
    }

    interface DialogClickListener {
        fun onPositiveButtonClick()
        fun onNegativeButtonClick()
    }

    interface DialogNeutralClickListener {
        fun onNeutralButtonClick()
    }
}