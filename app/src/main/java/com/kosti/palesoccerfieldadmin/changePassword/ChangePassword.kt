package com.kosti.palesoccerfieldadmin.changePassword

import android.graphics.Typeface
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.kosti.palesoccerfieldadmin.R

class ChangePassword : AppCompatActivity() {

    private lateinit var btnToggleVisibilityCurrentPassword: ImageButton
    private lateinit var btnToggleVisibilityNewPassword: ImageButton
    private lateinit var btnToggleVisibilityConfirmNewPassword: ImageButton

    private lateinit var edtCurrentPassword: EditText
    private lateinit var edtNewPassword: EditText
    private lateinit var edtConfirmNewPassword: EditText

    private lateinit var btnRecoveryPassword: Button
    private lateinit var btnChange: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        btnToggleVisibilityCurrentPassword = findViewById(R.id.btnViewCurrentPassword)
        btnToggleVisibilityNewPassword = findViewById(R.id.btnViewNewPassword)
        btnToggleVisibilityConfirmNewPassword = findViewById(R.id.btnViewConfirmNewPassword)


        edtCurrentPassword = findViewById(R.id.edtCurrentPassword)
        edtNewPassword = findViewById(R.id.edtNewPassword)
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword)

        btnChange = findViewById(R.id.btnChangePassword)
        btnRecoveryPassword = findViewById(R.id.btnRecoveryPassword)

        btnToggleVisibilityCurrentPassword.setOnClickListener {
            if (edtCurrentPassword.text.toString() != ""){
                togglePasswordVisibility(edtCurrentPassword, btnToggleVisibilityCurrentPassword)
            }
        }

        btnToggleVisibilityNewPassword.setOnClickListener {
            if (edtNewPassword.text.toString() != ""){
                togglePasswordVisibility(edtNewPassword, btnToggleVisibilityNewPassword)
            }
        }

        btnToggleVisibilityConfirmNewPassword.setOnClickListener {
            if (edtConfirmNewPassword.text.toString() != ""){
                togglePasswordVisibility(edtConfirmNewPassword, btnToggleVisibilityConfirmNewPassword)
            }
        }

        btnChange.setOnClickListener {
            val successDialog = ChangedPasswordDialog()
            successDialog.show(supportFragmentManager, "dialog_changed_password") }

        btnRecoveryPassword.setOnClickListener {  }

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

