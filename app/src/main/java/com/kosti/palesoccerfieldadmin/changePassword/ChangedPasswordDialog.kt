package com.kosti.palesoccerfieldadmin.changePassword

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.kosti.palesoccerfieldadmin.R

class ChangedPasswordDialog : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_changed_password, container, false)
        // Configura el contenido del diálogo aquí

        val btnCloseDialog: Button = view.findViewById(R.id.btnConfirmChangedPassword)

        // Configura el clic del botón para cerrar el diálogo
        btnCloseDialog.setOnClickListener {
            dismiss() // Este método cierra el diálogo
        }
        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }
}