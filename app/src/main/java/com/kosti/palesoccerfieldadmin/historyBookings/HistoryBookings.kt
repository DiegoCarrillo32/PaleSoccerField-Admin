package com.kosti.palesoccerfieldadmin.macthBookings

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Calendar

class HistoryBookings : AppCompatActivity() {

    private lateinit var customAdapter: HistoryBookingsCustomAdapter
    private lateinit var listaReservas: MutableList<ReservasDataModel>
    private lateinit var recyclerView: RecyclerView
    private var selectedOption: Boolean = true

    init {
        listaReservas = mutableListOf()
        getReservasFromFirebase()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_bookings)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val noReservationsMessage = findViewById<TextView>(R.id.noReservationsMessage)
        recyclerView = findViewById(R.id.recycler)

        val spinner: Spinner = findViewById(R.id.spinner)

        val opciones = listOf("Sí", "No")

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner.adapter = adapter

        // Manejar la selección
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>?, selectedItemView: View?, position: Int, id: Long) {
                selectedOption = opciones[position] == "Sí"
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {
                // Manejar caso en que no se selecciona nada
            }
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            val reservasFiltradas = filtrarReservas(year, month, dayOfMonth, selectedOption)

            if (reservasFiltradas.size > 0) {
                recyclerView.visibility = android.view.View.VISIBLE
                noReservationsMessage.visibility = android.view.View.GONE
                // Aquí deberías cargar las reservas en el listView
                cargarReservas(reservasFiltradas)

            } else {
                recyclerView.visibility = android.view.View.GONE
                noReservationsMessage.visibility = android.view.View.VISIBLE
            }
        }

        val btnBack = findViewById<ImageButton>(R.id.backButton)
        btnBack.setOnClickListener { finish() }
    }

    fun filtrarReservas(year: Int, month: Int, dayOfMonth: Int, estado: Boolean): MutableList<ReservasDataModel> {
        val fechaFiltrada = listaReservas.filter { reserva ->
            val reservaDate = reserva.Date.toDate()
            val reservaCalendar = Calendar.getInstance().apply {
                time = reservaDate
            }

            // Comparar año, mes y día
            val fechaCoincide = reservaCalendar.get(Calendar.YEAR) == year &&
                    reservaCalendar.get(Calendar.MONTH) == month &&
                    reservaCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth

            // Comparar estado
            val estadoCoincide = reserva.Status == estado

            // Retornar true si ambas condiciones son verdaderas
            fechaCoincide && estadoCoincide
        }.toMutableList()
        return fechaFiltrada
    }

    fun cargarReservas(reservasFiltradas: MutableList<ReservasDataModel>){
        customAdapter = HistoryBookingsCustomAdapter(reservasFiltradas, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }

    private fun getReservasFromFirebase(){
        FirebaseUtils().readCollection("reservas") { result ->
            result.onSuccess {
                for (reserva in it) {
                    if (reserva["encargado"] == null ||
                        reserva["fecha"] == null
                    ) {
                        Toast.makeText(this, "Reserva con datos erroneos", Toast.LENGTH_LONG).show()
                        continue
                    }
                    FirebaseUtils().getCollectionByProperty(
                        "jugadores",
                        "UID",
                        reserva["encargado"].toString()
                    ) { result ->
                        result.onSuccess { user ->
                            listaReservas.add(
                                ReservasDataModel(
                                    reserva["id"].toString(),
                                    user[0]["nombre"].toString(),
                                    reserva["fecha"] as Timestamp,
                                    reserva["estado"] as Boolean,
                                    reserva["equipo"] as Boolean,
                                    reserva["tipo"].toString()
                                )
                            )
                        }
                        result.onFailure {
                            Toast.makeText(
                                this,
                                "Error al cargar los datos del encargado",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            }
            result.onFailure {
                Toast.makeText(this, "Error al cargar los datos", Toast.LENGTH_LONG).show()

            }
        }
    }

}

