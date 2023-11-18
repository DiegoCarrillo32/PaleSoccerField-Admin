package com.kosti.palesoccerfieldadmin.aproveReservations

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class AproveReservations : AppCompatActivity() {

    private lateinit var reservationRecyclerView: RecyclerView
    private lateinit var reservationList: MutableList<ReservasDataModel>
    private lateinit var adapter: AproveReservationsListAdapter
    private lateinit var filteredList: MutableList<ReservasDataModel>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var reservationListProgressBar: ProgressBar
    private lateinit var toolbar: Toolbar


    private val reservationNameCollection = "reservas"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprove_reservations)

        reservationRecyclerView = findViewById(R.id.reservationListRecycler)
        reservationListProgressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbarAproveReservations)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutReservationList)
        filteredList = mutableListOf()
        reservationList = mutableListOf()
        fetchDataFromFirebase()

        // Add the LinearLayoutManager to  RecyclerView
        val layoutManager = LinearLayoutManager(this)
        reservationRecyclerView.layoutManager = layoutManager

        swipeRefreshLayout.setOnRefreshListener {
            fetchDataFromFirebase()
            swipeRefreshLayout.isRefreshing = false
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    private fun fetchDataFromFirebase() {
        Snackbar.make(
            reservationRecyclerView,
            "Buscando la informacion de las reservas",
            Snackbar.LENGTH_LONG
        )
            .show()
        reservationList.clear()
        reservationListProgressBar.visibility = View.VISIBLE
        FirebaseUtils().readCollectionStateFalse(reservationNameCollection) { result ->
            result.onSuccess { it ->


                for (reservation in it) {
                    if (reservation["encargado"] == null ||
                        reservation["horario"] == null
                    ) {
                        Toast.makeText(this, "Reserva con datos erroneos", Toast.LENGTH_LONG).show()
                        continue
                    }
                    FirebaseUtils().getCollectionByProperty(
                        "jugadores",
                        "UID",
                        reservation["encargado"].toString(),

                    ) { result ->
                        result.onSuccess { user ->


                            FirebaseUtils().getDocumentById(
                                "horario",
                                reservation["horario"].toString()
                            ) { result ->
                                result.onSuccess { schedule ->

                                    reservationList.add(
                                        ReservasDataModel(
                                            reservation["id"].toString(),
                                            user[0]["nombre"].toString(),
                                            schedule["fecha"] as Timestamp,
                                        )
                                    )
                                    adapter = AproveReservationsListAdapter(this, reservationList)
                                    reservationRecyclerView.adapter = adapter

                                }
                                result.onFailure {
                                    Snackbar.make(
                                        reservationRecyclerView,
                                        "Error al cargar los datos",
                                        Snackbar.LENGTH_LONG
                                    )
                                        .show()

                                }
                            }
                        }
                        result.onFailure {
                            Snackbar.make(
                                reservationRecyclerView,
                                "Error al cargar los datos",
                                Snackbar.LENGTH_LONG
                            )
                                .show()
                        }
                    }
                }

                reservationListProgressBar.visibility = View.GONE
            }
            result.onFailure {
                Snackbar.make(
                    reservationRecyclerView,
                    "Error al cargar los datos",
                    Snackbar.LENGTH_LONG
                )
                    .show()
            }
        }
    }

}