package com.example.pocketstatistician.convenience

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pocketstatistician.R
import com.example.pocketstatistician.fragments.table.DataFragment

class NoteDialog(val dataFragment: DataFragment, val notePos: Int): DialogFragment() {
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(context!!)
        builder.setMessage(context?.getString(R.string.note, notePos))
            .setPositiveButton(
                R.string.move_note
            ) { dialog, which ->
                moveNote()
            }
            .setNeutralButton(
                R.string.delete_note
            ) { dialog, which ->
                deleteNote()
            }
            .setNegativeButton(
                R.string.cancel
            ) { dialog, which ->
                dialog.dismiss()
            }

        return builder.create()
    }

    private fun deleteNote() {
        dataFragment.deleteNote()
    }

    private fun moveNote() {

    }
}