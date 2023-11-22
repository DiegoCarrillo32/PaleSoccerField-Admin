package com.kosti.palesoccerfieldadmin.macthBookings

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.reservations.CustomAdapter
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Calendar


class MatchBookings : AppCompatActivity() {

    private lateinit var customAdapter: MatchBookingsCustomAdapter
    private lateinit var listaReservas: MutableList<ReservasDataModel>
    private lateinit var reservasDelDia: MutableList<ReservasDataModel>
    private lateinit var recyclerView: RecyclerView

    init {
        getReservasFromFirebase()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_bookings)

        listaReservas = mutableListOf()

        reservasDelDia = mutableListOf()

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val noReservationsMessage = findViewById<TextView>(R.id.noReservationsMessage)
        recyclerView = findViewById(R.id.recycler)



        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->

            consultarDatosReservasFirebase(year, month, dayOfMonth)
            // Aquí debes cargar las reservas para la fecha seleccionada
            // Puedes utilizar una función o una llamada a una API, dependiendo de tu implementación

            // Ejemplo de cómo mostrar o ocultar el ListView y el mensaje según si hay reservas o no

            if (listaReservas.size > 0) {
                recyclerView.visibility = android.view.View.VISIBLE
                noReservationsMessage.visibility = android.view.View.GONE
                // Aquí deberías cargar las reservas en el listView
                cargarReservas()

            } else {
                recyclerView.visibility = android.view.View.GONE
                noReservationsMessage.visibility = android.view.View.VISIBLE
            }
        }
    }

    fun cargarReservas(){
        customAdapter = MatchBookingsCustomAdapter(listaReservas, this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = customAdapter
    }
    fun consultarDatosReservasFirebase(year: Int, month: Int, dayOfMonth: Int) {
        listaReservas.clear()
        FirebaseUtils().readCollectionByDate("reservas", year, month, dayOfMonth) { result ->
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


                            recyclerView.visibility = android.view.View.VISIBLE

                            customAdapter = MatchBookingsCustomAdapter(listaReservas, this)
                            recyclerView.layoutManager = LinearLayoutManager(this)
                            recyclerView.adapter = customAdapter
                           

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


    fun getReservasFromFirebase(){
        FirebaseUtils().readCollectionByDate("reservas", year, month, dayOfMonth) { result ->
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


                            recyclerView.visibility = android.view.View.VISIBLE

                            customAdapter = MatchBookingsCustomAdapter(listaReservas, this)
                            recyclerView.layoutManager = LinearLayoutManager(this)
                            recyclerView.adapter = customAdapter


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

