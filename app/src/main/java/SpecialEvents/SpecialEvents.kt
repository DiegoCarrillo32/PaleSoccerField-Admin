package SpecialEvents

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
import com.kosti.palesoccerfieldadmin.reservations.CustomAdapter
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class SpecialEvents : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var customAdapter: SpecialEventsCustomAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var listaEventosEspeciales: MutableList<EventoEspecialDataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_events)

        listaEventosEspeciales = mutableListOf()

        toolbar = findViewById(R.id.toolbarSpecialEvents)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            //val intent = Intent(this, MainActivity::class.java)
            //startActivity(intent)
            finish()
        }
        consultarDatosEventosEspecialesFirebase()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_special_event, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.addSpecialEvent -> {
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    fun consultarDatosEventosEspecialesFirebase() {
        FirebaseUtils().readCollection("evento_especial") { result ->
            result.onSuccess {
                for(eventoEspecial in it){
                    if (eventoEspecial["id"] == null ||
                        eventoEspecial["nombre"] == null ||
                        eventoEspecial["fecha"] == null ||
                        eventoEspecial["descripcion"] == null ||
                        eventoEspecial["estado"] == null
                    ){
                        Toast.makeText(this, "Reserva con datos erroneos", Toast.LENGTH_LONG).show()
                        continue
                    }
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
                customAdapter = SpecialEventsCustomAdapter(listaEventosEspeciales, this)
                recyclerView = findViewById(R.id.reciclerSpecialEvents)
                recyclerView.layoutManager = LinearLayoutManager(this)
                recyclerView.adapter = customAdapter
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
}