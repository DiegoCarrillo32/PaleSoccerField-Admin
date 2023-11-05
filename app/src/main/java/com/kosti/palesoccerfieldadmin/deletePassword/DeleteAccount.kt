package com.kosti.palesoccerfieldadmin.deletePassword

import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.kosti.palesoccerfieldadmin.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class DeleteAccount : AppCompatActivity() {

    private lateinit var btnDelete: Button
    private lateinit var btnBack: ImageButton
    private lateinit var btnToggleVisibilityCurrentPassword: ImageButton
    private lateinit var checkBox: CheckBox
    private lateinit var edtPassword: EditText
    private lateinit var edtEmail: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_account)

        val userId = intent.getStringExtra("userId").toString()

        btnDelete = findViewById(R.id.btnSend)
        btnBack = findViewById(R.id.backButton)
        btnToggleVisibilityCurrentPassword = findViewById(R.id.btnViewCurrentPassword)

        checkBox = findViewById(R.id.checkBoxDeleteAccount)

        edtPassword = findViewById(R.id.edtCurrentPassword)
        edtEmail = findViewById(R.id.edtEmail)

        btnBack.setOnClickListener { finish() }

        btnDelete.setOnClickListener {
            val password = edtPassword.text.toString()
            val email = edtEmail.text.toString()
            if (password != "" && email != "") {
                if (checkBox.isChecked) {
                    reauthenticateUser(email, password, userId)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Confirma el que deseas eliminar tu cuenta",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Escriba sus credenciales",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        btnToggleVisibilityCurrentPassword.setOnClickListener {
            if (edtPassword.text.toString() != ""){
                togglePasswordVisibility(edtPassword, btnToggleVisibilityCurrentPassword)
            }
        }
    }

    private fun reauthenticateUser(email: String, password: String, userId: String){
        val user = FirebaseAuth.getInstance().currentUser
        val credential = EmailAuthProvider.getCredential(email, password)
        user?.reauthenticate(credential)
            ?.addOnCompleteListener{ task ->
                if (task.isSuccessful){
                    deleteAccount(user, userId)
                } else {
                    Toast.makeText(applicationContext, "Los credenciales son incorrectos", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun deleteAccount(user: FirebaseUser?, userId: String) {
        if (user != null) {
            FirebaseUtils().deleteUserFromFirestore("jugadores", userId) { success ->
                if (success) {
                    deleteUserFromFirebaseAuth(user)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error de eliminando el usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            Toast.makeText(
                applicationContext,
                "Error de servidor, intente mas tarde",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun deleteUserFromFirebaseAuth(user: FirebaseUser) {
        user.delete()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val successDialog = DeletedAccountDialog()
                    successDialog.show(supportFragmentManager, "dialog_deleted_account")
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Error de eliminando el usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun togglePasswordVisibility(edtPassword: EditText, btnVisibilityPassword: ImageButton){
        if (edtPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Oculta la contraseña
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            edtPassword.typeface = Typeface.SANS_SERIF
            btnVisibilityPassword.setImageResource(R.drawable.baseline_visibility_24)
        } else {
            // Muestra la contraseña
            edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            edtPassword.typeface = Typeface.SANS_SERIF
            btnVisibilityPassword.setImageResource(R.drawable.baseline_visibility_off_24)
        }

        // Asegúrate de que el cursor se posicione al final del texto
        edtPassword.setSelection(edtPassword.text.length)
    }
}