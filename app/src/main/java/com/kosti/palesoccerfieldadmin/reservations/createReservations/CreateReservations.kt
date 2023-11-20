package com.kosti.palesoccerfieldadmin.reservations.createReservations

import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.models.ScheduleDataModel
import com.kosti.palesoccerfieldadmin.reservations.addUsersToReservations.AddUsersToReservation
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Date
import java.util.Locale

class CreateReservations : AppCompatActivity() {

    private var usersForProposalTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private var usersForChallengingTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var toolbar: Toolbar
    private lateinit var btnAgregarJugadores: Button
    private lateinit var btnAgregarRetadores: Button
    private lateinit var spinnerTipoReserva: Spinner
    private lateinit var checkTengoEquipo: CheckBox
    private lateinit var scheduleSelected:ScheduleDataModel

    private var tengoEquipoChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservations)
        val userIds = intent.getStringArrayListExtra("jugadoresIds")
        // Hacer algo con la lista de IDs recibidos
        Log.d("ReceivedUserIds", "wtf${userIds.toString()}")
        btnAgregarJugadores = findViewById(R.id.btnAgregarJugadorEquipo)
        btnAgregarRetadores = findViewById(R.id.btnAgregarJugadorRetador)
        btnAgregarJugadores.setOnClickListener {
            val intent = Intent(this, AddUsersToReservation::class.java)
            intent.putExtra("textParameter", "proposalTeam")
            this.startActivity(intent)
        }
        btnAgregarRetadores.setOnClickListener {
            val intent = Intent(this, AddUsersToReservation::class.java)
            intent.putExtra("textParameter", "challengingTeam")
            this.startActivity(intent)
        }

        checkTengoEquipo = findViewById(R.id.checkTengoEquipo)
        spinnerTipoReserva = findViewById(R.id.spinnerTipoReserva)

        // Listener para el CheckBox
        checkTengoEquipo.setOnCheckedChangeListener { _, isChecked ->
            tengoEquipoChecked = isChecked
            // Desactivar el botón si el CheckBox está marcado
            btnAgregarJugadores.isEnabled = !isChecked
        }


        spinnerTipoReserva.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Verificar el valor seleccionado y actualizar la visibilidad del botón
                actualizarVisibilidadBoton(
                    btnAgregarJugadores,
                    btnAgregarRetadores,
                    checkTengoEquipo,
                    spinnerTipoReserva.selectedItem.toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No es necesario la implementacion con la cosa
            }
        }

        spinnerTypeReservation()
        spinnerSchedules()


        toolbar = findViewById(R.id.toolbarCreateReservations)


        toolbar.setNavigationOnClickListener { onBackPressed() }
    }


    private fun spinnerTypeReservation() {
        val types = resources.getStringArray(R.array.typesOfReservation)
        val spinnerTypesOfRsrv: Spinner = findViewById(R.id.spinnerTipoReserva)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)

        // Especifica el diseño que se usará cuando se desplieguen las opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Une el ArrayAdapter al Spinner
        spinnerTypesOfRsrv.adapter = adapter

        // Opcionalmente, puedes configurar un escuchador para detectar la selección del usuario
        spinnerTypesOfRsrv.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val itemSeleccionado = types[position]
                actualizarVisibilidadBoton(
                    btnAgregarJugadores,
                    btnAgregarRetadores,
                    checkTengoEquipo,
                    itemSeleccionado.toString()
                )
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }
    }

    private fun spinnerSchedules() {

        val reservationsSchedules = mutableListOf<ScheduleDataModel>()

        val spinnerSchedules: Spinner = findViewById(R.id.spinnerHorario)

        // Configurar Firestore
        val db = FirebaseFirestore.getInstance()


        FirebaseUtils().readCollectionStateFalse("horario", "reservado") { result ->
            result.onSuccess { it ->

                for (schedule in it) {
                    if (schedule["reservado"] == null ||
                        schedule["tanda"] == null
                    ) {
                        Toast.makeText(this, "Horario con datos erroneos", Toast.LENGTH_LONG).show()
                        continue
                    }

                    var horario = ScheduleDataModel(
                        schedule["id"].toString(),
                        schedule["fecha"] as Timestamp,
                        schedule["tanda"] as MutableList<Timestamp>,
                        schedule["reservado"] as Boolean,
                        convertTime(schedule["tanda"] as MutableList<Timestamp> )
                    )

                    Log.d("REVISANDO", horario.getTextoHorario())
                    reservationsSchedules.add(horario)
                }

                val scheduleText = reservationsSchedules.map { horario ->
                    horario.getTextoHorario()
                }

                // Crear un ArrayAdapter y establecerlo en el Spinner
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, scheduleText)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSchedules.adapter = adapter

                // Opcionalmente, puedes configurar un escuchador para detectar la selección del usuario
                spinnerSchedules.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                       scheduleSelected = reservationsSchedules[position]

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // No es necesario implementar nada aquí si no se selecciona nada
                    }
                }
            }
            result.onFailure {
                Toast.makeText(this, "Error al cargar horarios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun convertTime(list:MutableList<Timestamp>): String {


        val date1 = Date(list[0].seconds * 1000)
        val date2 = Date(list[1].seconds * 1000)

        // Formato para obtener solo la hora en formato de 12 horas
        val hourFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        // Obtener las horas formateadas
        val formattedTime1 = hourFormat.format(date1)
        val formattedTime2 = hourFormat.format(date2)

        // Obtener la fecha en formato "dd/MM/yyyy"
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date1)


        return "$formattedDate, $formattedTime1 - $formattedTime2"
    }


    private fun actualizarVisibilidadBoton(
        btn: Button,
        btn2: Button,
        chckBox: CheckBox,
        tipoReserva: String
    ) {
        // Desactivar el botón si el tipo de reserva es "Privada" o si el CheckBox está marcado
        btn.isEnabled = tipoReserva != "Privada" && !tengoEquipoChecked
        btn2.isEnabled = tipoReserva != "Privada" && !tengoEquipoChecked
        chckBox.isEnabled = tipoReserva != "Privada"

    }

    //TODO: METER LOGICA PARA QUE DEPENDIENDO DE LAS OPCIONES QUE SE SELECCIONEN SE DESACTIVEN UNAS U OTRAS EN EL UI lista

    //TODO: CONFIGURAR SPINNER HORARIOS CON LOS QUE ESTAN DISPONIBLES AHORITA SEGUN LA BASE DE DATOS

    //TODO: METER LOGICA PARA ABRIR ACITVITY PARA SELECCIONAR EL ENCARGADO

    //TODO: CONFIGURAR EL OTRO SPINNER CON VALORES QUEMADOS PERO USANDO LISTAS EN VALUES PARA RESERVA Privada o Publica lista

    //TODO: AGREGAR FUNCIONALIDAD PARA SELECCIONAR JUGADORES Y DEPENDE DEL BOTON SE AÑADAN A UN EQUIPO O A OTRO

    //TODO: CONFIGURAR UI PARA LOS ADAPTER CUSTOM DE LOS EQUIPOS --Andrik-- create_reservation_player_list_item lista

    //TODO: CREAR 2 CUSTOM ADAPTERS PARA LOS DOS RECYCLER VIEW

    //TODO: IMPORTANTEEEEEE FUNCIONALIDAD PARA EL BOTON DE CREAR RESERVA METER LAS 100000 VALIDACIONES


}