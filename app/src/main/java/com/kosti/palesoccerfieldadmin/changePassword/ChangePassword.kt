package com.kosti.palesoccerfieldadmin.changePassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.ImageButton
import com.kosti.palesoccerfieldadmin.R

class ChangePassword : AppCompatActivity() {

    private lateinit var btnToggleVisibilityPassword: ImageButton

    private lateinit var edtPassword: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        btnToggleVisibilityPassword = findViewById(R.id.btnViewPassword)

        edtPassword = findViewById(R.id.edtPassword)

        btnToggleVisibilityPassword.setOnClickListener {
            if (edtPassword.text.toString() != ""){
                togglePasswordVisibility()
            }
        }
    }


    private fun togglePasswordVisibility(){
        if (edtPassword.inputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
            // Oculta la contraseña
            edtPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            btnToggleVisibilityPassword.setImageResource(R.drawable.baseline_visibility_24)
        } else {
            // Muestra la contraseña
            edtPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            btnToggleVisibilityPassword.setImageResource(R.drawable.baseline_visibility_off_24)
        }

        // Asegúrate de que el cursor se posicione al final del texto
        edtPassword.setSelection(edtPassword.text.length)
    }
}

