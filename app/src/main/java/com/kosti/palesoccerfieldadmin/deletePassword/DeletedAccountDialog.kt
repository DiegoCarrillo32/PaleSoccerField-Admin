package com.kosti.palesoccerfieldadmin.deletePassword

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.login.Login
import com.kosti.palesoccerfieldadmin.userListPackage.UserList

class DeletedAccountDialog  : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_deleted_account, container, false)

        val button = view.findViewById<Button>(R.id.btnBackLogin)
        button.setOnClickListener {
            val intent = Intent(activity, Login::class.java)
            startActivity(intent)
            dismiss()
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Evita que el diálogo se cierre haciendo clic fuera de él
        isCancelable = false
    }
}