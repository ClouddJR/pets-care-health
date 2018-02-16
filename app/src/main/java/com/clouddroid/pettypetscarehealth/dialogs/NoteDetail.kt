package com.clouddroid.pettypetscarehealth.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.app.DialogFragment
import android.os.Bundle
import com.clouddroid.pettypetscarehealth.R

/**
 * Created by arkadiusz on 15.02.18.
 */
class NoteDetail : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.dialog_note_detail, null))
        return builder.create()
    }
}