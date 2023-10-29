package com.kosti.palesoccerfieldadmin.userAdminProfile

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils


class EditField : AppCompatActivity() {

    lateinit var editTextData: EditText
    lateinit var tvTittle: TextView
    lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_field)

        editTextData = findViewById(R.id.data)

        tvTittle = findViewById(R.id.tvTittle)

        backButton = findViewById(R.id.backButton)

        val intent = intent

        if (intent != null) {
            val type = intent.getStringExtra("type")
            val data = intent.getStringExtra("data")

            // Usar la variable "tipo" en esta actividad
            editTextData.setHint(type)
            editTextData.setText(data)
            tvTittle.setText(type)
            editTextData.requestFocus()

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(editTextData, InputMethodManager.SHOW_IMPLICIT)

            val btnEditData = findViewById<Button>(R.id.btnEditData)
            btnEditData.setOnClickListener {
                println(data)
                println(editTextData.text.toString())
                if (editTextData.text.toString() == data) {
                    println("gg")
                    Toast.makeText(
                        applicationContext,
                        "El ${type} debe ser diferente",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (type == "Nombre") {
                        updateData("nombre")
                    } else if (type == "Apodo") {
                        updateData("apodo")
                    } else if (type == "Teléfono") {
                        updateData("telefono")
                    }
                }
            }
        }
        backButton.setOnClickListener { finish() }
    }

    fun updateData(field: String){
        FirebaseUtils().updateProperty("jugadores","QNLLg9OhQ1z8uc2yELWn", field , editTextData.text.toString())
    }


}