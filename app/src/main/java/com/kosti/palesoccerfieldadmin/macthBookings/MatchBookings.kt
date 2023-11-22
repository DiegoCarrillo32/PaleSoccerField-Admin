package com.kosti.palesoccerfieldadmin.macthBookings

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.CalendarView
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Calendar

class MatchBookings : AppCompatActivity() {

    private lateinit var customAdapter: MatchBookingsCustomAdapter
    private lateinit var listaReservas: MutableList<ReservasDataModel>
    private lateinit var recyclerView: RecyclerView

    init {
        listaReservas = mutableListOf()
        getReservasFromFirebase()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_bookings)

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val noReservationsMessage = findViewById<TextView>(R.id.noReservationsMessage)
        recyclerView = findViewById(R.id.recycler)


        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->


            val reservasFiltradas = filtrarReservas(year, month, dayOfMonth)

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


    fun filtrarReservas(year: Int, month: Int, dayOfMonth: Int): MutableList<ReservasDataModel> {
        val fechaFiltrada = listaReservas.filter { reserva ->
            val reservaDate = reserva.Date.toDate()
            val reservaCalendar = Calendar.getInstance().apply {
                time = reservaDate
            }

            // Comparar año, mes y día
            reservaCalendar.get(Calendar.YEAR) == year &&
                    reservaCalendar.get(Calendar.MONTH) == month &&
                    reservaCalendar.get(Calendar.DAY_OF_MONTH) == dayOfMonth
        }.toMutableList()

        return fechaFiltrada
    }

    fun cargarReservas(reservasFiltradas: MutableList<ReservasDataModel>){
        customAdapter = MatchBookingsCustomAdapter(reservasFiltradas, this)
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
                                    reserva["estado"] as Boolean
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

