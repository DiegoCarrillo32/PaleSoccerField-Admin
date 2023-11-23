package com.kosti.palesoccerfieldadmin.SpecialEvents

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
import com.kosti.palesoccerfieldadmin.models.EventoEspecialDataModel
import com.kosti.palesoccerfieldadmin.promotions.AddEditPromotion
import com.kosti.palesoccerfieldadmin.reservations.CustomAdapter
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class SpecialEvents : AppCompatActivity(), FragmentEditAddSpecialEvent.OnDismissListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var listaEventosEspeciales: MutableList<EventoEspecialDataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_events)

        recyclerView = findViewById(R.id.reciclerSpecialEvents)
        recyclerView.layoutManager = LinearLayoutManager(this)
        listaEventosEspeciales = mutableListOf()
        consultarDatosEventosEspecialesFirebase()

        val toolbar: Toolbar = findViewById(R.id.toolbarSpecialEvents)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_special_event, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addSpecialEvent -> {
                val bottomSheetFragment = FragmentEditAddSpecialEvent()
                bottomSheetFragment.setOnDismissListener(this)
                bottomSheetFragment.show(supportFragmentManager, "AEPDialogFragment")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun consultarDatosEventosEspecialesFirebase() {
        FirebaseUtils().readCollection("evento_especial") { result ->
            result.onSuccess {
                listaEventosEspeciales.clear()
                for(eventoEspecial in it){
                    //if (eventoEspecial["estado"] != true ) continue

                    listaEventosEspeciales.add(
                        EventoEspecialDataModel(
                            eventoEspecial["id"].toString(),
                            eventoEspecial["descripcion"].toString(),
                            eventoEspecial["estado"] as Boolean,
                            eventoEspecial["fecha"] as Timestamp,
                            eventoEspecial["imagen_url"].toString(),
                            eventoEspecial["nombre"].toString()
                        )
                    )
                }

                val adapter = SpecialEventsCustomAdapter(listaEventosEspeciales, this)
                recyclerView.adapter = adapter
                recyclerView.adapter?.notifyDataSetChanged()
            }

            result.onFailure {
                Toast.makeText(
                    this,
                    "Error al cargar los datos del evento.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
    override fun onDismissOnActivity() {
        consultarDatosEventosEspecialesFirebase()
    }

}