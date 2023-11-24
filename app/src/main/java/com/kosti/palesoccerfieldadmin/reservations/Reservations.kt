package com.kosti.palesoccerfieldadmin.reservations

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.MainActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.reservations.aproveReservations.AproveReservations
import com.kosti.palesoccerfieldadmin.reservations.createReservations.CreateReservations
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class Reservations : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var customAdapter: CustomAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listaReservas: MutableList<ReservasDataModel>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reservations)

        listaReservas = mutableListOf()

        toolbar = findViewById(R.id.toolbarGestionDeReservas)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        toolbar.setNavigationOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            finish()
        }
        consultarDatosReservasFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addReservation -> {
                val intent = Intent(this, CreateReservations::class.java)
                startActivityForResult(intent, 1)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        listaReservas.clear()
        consultarDatosReservasFirebase()
    }
    fun consultarDatosReservasFirebase() {
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
                            val res =ReservasDataModel(
                                reserva["id"].toString(),
                                user[0]["nombre"].toString(),
                                reserva["fecha"] as Timestamp
                            )
                            res.ScheduleID = reserva["horario"].toString()
                            listaReservas.add(
                                res
                            )
                            customAdapter = CustomAdapter(listaReservas, this)
                            recyclerView = findViewById(R.id.recicler)
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