package com.kosti.palesoccerfieldadmin.reservations

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
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

        consultarDatosReservasFirebase()

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                // Acción para el elemento de búsqueda
                // funcion que levante la vista de agregar
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
    /*
    fun actualizarCustomAdapter(){

        // falta lista nueva
        customAdapter = CustomAdapter(datasetProductos,this)
        customAdapter.notifyDataSetChanged()
        recyclerView.adapter = customAdapter
    }
    */

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
                            listaReservas.add(
                                ReservasDataModel(
                                    reserva["id"].toString(),
                                    user[0]["nombre"].toString(),
                                    reserva["fecha"] as Timestamp
                                )
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