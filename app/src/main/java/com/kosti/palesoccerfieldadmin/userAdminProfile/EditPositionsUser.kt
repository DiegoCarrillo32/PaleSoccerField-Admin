package com.kosti.palesoccerfieldadmin.userAdminProfile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.kosti.palesoccerfieldadmin.R

class EditPositionsUser : AppCompatActivity() {

    lateinit var tvTittle: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_positions_user)

        val userPositions = intent.getStringArrayListExtra("positions")
        val type = intent.getStringExtra("type")

        tvTittle = findViewById(R.id.tvTittle)

        tvTittle.setText(type)

        val allPositions = listOf("Delantero", "Medio Campista", "Portero", "Defensa", "Lateral Izquierdo", "Lateral Derecho")

        val linearLayout = findViewById<LinearLayout>(R.id.checkBoxContainer)

        for (opcion in allPositions) {
            val checkBox = CheckBox(this)
            checkBox.text = opcion

            if (userPositions != null) {
                if (opcion in userPositions) { // Establece la segunda opción como preseleccionada
                    checkBox.isChecked = true
                }
            }

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                // Manejar la selección/deselección aquí si es necesario
                if (isChecked) {
                    // La opción ha sido seleccionada
                    // Realiza acciones basadas en la selección
                } else {
                    // La opción ha sido deseleccionada
                    // Realiza acciones basadas en la deselección
                }
            }

            // Agrega el CheckBox al LinearLayout
            linearLayout.addView(checkBox)
        }
    }
}