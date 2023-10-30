package com.kosti.palesoccerfieldadmin.userAdminProfile

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Calendar


class EditField : AppCompatActivity() {

    lateinit var editTextData: EditText
    lateinit var tvTittle: TextView
    lateinit var backButton: ImageButton
    lateinit var dateInMillis: String
    lateinit var userId: String

    @SuppressLint("ClickableViewAccessibility")
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
            userId = intent.getStringExtra("userId").toString()

            // Usar la variable "tipo" en esta actividad
            editTextData.setHint(type)
            editTextData.setText(data)
            tvTittle.setText(type)
            editTextData.requestFocus()

            if (type == "Teléfono"){
                editTextData.inputType = InputType.TYPE_CLASS_PHONE //Asigna el edit text como phone
            } else if(type == "Fecha de Nacimiento"){
                editTextData.inputType = InputType.TYPE_CLASS_DATETIME or InputType.TYPE_DATETIME_VARIATION_DATE

                editTextData.setOnTouchListener { v, event ->
                    if (event.action == MotionEvent.ACTION_UP) {
                        val calendar = Calendar.getInstance()
                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH)
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                            val selectedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear" // Formato dd-MM-yyyy
                            editTextData.setText(selectedDate)

                            val calendar = Calendar.getInstance()
                            calendar.set(Calendar.YEAR, selectedYear)
                            calendar.set(Calendar.MONTH, selectedMonth)
                            calendar.set(Calendar.DAY_OF_MONTH, selectedDay)

                            dateInMillis = calendar.timeInMillis.toString()
                        }, year, month, day)

                        datePickerDialog.datePicker.maxDate = calendar.timeInMillis
                        datePickerDialog.show()
                        return@setOnTouchListener true
                    }
                    false
                }
            }

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
                    }else {
                        updateDateOfBirth("fecha_nacimiento")
                    }
                }
                setResult(RESULT_OK, intent)
                finish()
            }
        }
        backButton.setOnClickListener { finish() }
    }

    private fun updateData(field: String){
        FirebaseUtils().updateProperty("jugadores", userId, field , editTextData.text.toString())
    }

    private fun updateDateOfBirth(field: String){
        FirebaseUtils().updateProperty("jugadores", userId, field , dateInMillis)
    }
}