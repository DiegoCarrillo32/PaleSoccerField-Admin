package com.kosti.palesoccerfieldadmin.SpecialEvents

import android.app.Dialog
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.EventoEspecialDataModel
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.promotions.AddEditPromotion
import com.kosti.palesoccerfieldadmin.reservations.CustomAdapter
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.math.log

class SpecialEvents : AppCompatActivity(), FragmentEditAddSpecialEvent.OnDismissListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var tvDateSelected: TextView
    private lateinit var linearLayoutSearchDate: LinearLayout
    private lateinit var listaEventosEspeciales: MutableList<EventoEspecialDataModel>
    private lateinit var searchView: SearchView
    private lateinit var radioGroup: RadioGroup
    private lateinit var btnShowDialog: Button
    private lateinit var radioNamePromotion: RadioButton
    private lateinit var radioDatePromotion: RadioButton
    private var selectedFilterOption: String = "all"
    private var selectedDate: String = "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_special_events)

        searchView = findViewById(R.id.searchViewPromotion)
        tvDateSelected = findViewById(R.id.tvDateSelected)
        linearLayoutSearchDate = findViewById(R.id.linearLayoutSearchDate)
        recyclerView = findViewById(R.id.reciclerSpecialEvents)
        radioGroup = findViewById(R.id.radioGroupFilterPromotion)
        btnShowDialog = findViewById(R.id.btnOpenToSelectDate)
        radioNamePromotion = findViewById(R.id.radioNamePromotion)
        radioDatePromotion = findViewById(R.id.radioDatePromotion)


        recyclerView.layoutManager = LinearLayoutManager(this)
        listaEventosEspeciales = mutableListOf()
        consultarDatosEventosEspecialesFirebase()

        val toolbar: Toolbar = findViewById(R.id.toolbarSpecialEvents)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                val eventosFiltrados = filterList(newText.orEmpty(), selectedDate)
                //Toast.makeText(applicationContext, eventosFiltrados.size.toString(), Toast.LENGTH_SHORT).show()
                cargarEventosFiltrados(eventosFiltrados)
                return true
            }
        })

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioNamePromotion -> {
                    linearLayoutSearchDate.visibility = View.GONE
                    selectedDate = "all" // Restablecer la fecha seleccionada
                    val eventosFiltrados = filterList(searchView.query.toString(), selectedDate)
                    cargarEventosFiltrados(eventosFiltrados)
                }
                R.id.radioDatePromotion -> {
                    if(tvDateSelected.text==""){
                        tvDateSelected.text = "..."
                    }
                    fadeInWithTransition(linearLayoutSearchDate)
                }
            }
        }

        btnShowDialog.setOnClickListener {
            mostrarDialogo()
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

    private fun filterList(query: String, selectedDate: String) : MutableList<EventoEspecialDataModel> {
        val filteredList = when (selectedFilterOption) {
            "all" -> listaEventosEspeciales.filter { event ->
                val timestamp: Timestamp = event.Date
                val date = Date(timestamp.seconds * 1000) // Convierte los segundos a milisegundos
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate: String = dateFormat.format(date)
                event.Name.contains(query, ignoreCase = true) &&
                        (selectedDate == "all" || formattedDate == selectedDate)
            }
            else -> listaEventosEspeciales // Opción desconocida, muestra todos
        }.toMutableList()

        return filteredList
    }

    private fun fadeInWithTransition(view: View) {
        val transition = Fade()
        transition.duration = 500
        TransitionManager.beginDelayedTransition(linearLayoutSearchDate, transition)
        view.visibility = View.VISIBLE
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

    private fun mostrarDialogo() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_select_date)

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePickerPromotion)
        val btnSearch = dialog.findViewById<Button>(R.id.btnSearchDatePickerPromotion)

        btnSearch.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1
            val year = datePicker.year
            selectedDate = String.format("%02d/%02d/%d", day, month, year)
            tvDateSelected.text = selectedDate
            val eventosFiltrados = filterList(searchView.query.toString(), selectedDate)
            cargarEventosFiltrados(eventosFiltrados)
            dialog.dismiss()
        }

        dialog.show()
    }

    fun cargarEventosFiltrados(eventosFiltrados: MutableList<EventoEspecialDataModel>){
        val adapter = SpecialEventsCustomAdapter(eventosFiltrados, this)
        recyclerView.adapter = adapter
        recyclerView.adapter?.notifyDataSetChanged()
    }


    override fun onDismissOnActivity() {
        consultarDatosEventosEspecialesFirebase()
    }

}