package com.kosti.palesoccerfieldadmin.changePassword

import android.annotation.SuppressLint
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class ChangePassword : AppCompatActivity() {

    private lateinit var btnSend: Button
    private lateinit var btnBack: ImageButton
    private lateinit var edtEmail: EditText
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        userId = intent.getStringExtra("userId").toString()

        btnSend = findViewById(R.id.btnSend)
        edtEmail = findViewById(R.id.edtEmail)
        btnBack = findViewById(R.id.backButton)

        btnSend.setOnClickListener {
            val email = edtEmail.text.toString()
            if (email != "") {
                val auth = FirebaseAuth.getInstance()
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val successDialog = ChangedPasswordDialog()
                            successDialog.show(supportFragmentManager, "dialog_changed_password")
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Error de servidor",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(applicationContext, "No se encontro tu correo", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        btnBack.setOnClickListener { finish() }
    }
}

