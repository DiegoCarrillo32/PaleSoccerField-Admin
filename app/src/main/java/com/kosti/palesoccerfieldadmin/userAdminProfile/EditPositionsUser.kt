package com.kosti.palesoccerfieldadmin.userAdminProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils


class EditPositionsUser : AppCompatActivity() {

    lateinit var tvTittle: TextView
    lateinit var userId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_positions_user)

        val userPositions = intent.getStringArrayListExtra("positions")
        val type = intent.getStringExtra("type")
        userId = intent.getStringExtra("userId").toString()

        tvTittle = findViewById(R.id.tvTittle)

        tvTittle.setText(type)

        val allPositions = listOf("Delantero", "Mediocampista", "Portero", "Defensa", "Lateral Izquierdo", "Lateral Derecho")

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
                    if (userPositions?.contains(opcion) == false) {
                        userPositions.add(opcion)
                        println(userPositions.toString())
                    }

                } else {
                    // La opción ha sido deseleccionada
                    userPositions?.remove(opcion)
                    println(userPositions.toString())
                }
            }

            // Agrega el CheckBox al LinearLayout
            linearLayout.addView(checkBox)
        }

        val listo = findViewById<Button>(R.id.btnEditData)
        listo.setOnClickListener {
            if (userPositions != null) {
                if (userPositions.size != 0) {
                    FirebaseUtils().updateProperty("jugadores", userId,"posiciones", userPositions)
                    Toast.makeText(applicationContext, "Las posiciones han sido editadas", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra("posiciones", userPositions)
                    intent.putExtra("accion", "refrescarPosiciones")
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Débes de selccionar al menos una posición", Toast.LENGTH_SHORT).show()
                }
            }
        }
        val backButton = findViewById<ImageButton>(R.id.backButton)
        backButton.setOnClickListener { finish() }
    }

}