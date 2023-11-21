package com.kosti.palesoccerfieldadmin.reservations.createReservations

import android.app.Dialog
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
import android.widget.ListView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.models.ScheduleDataModel
import com.kosti.palesoccerfieldadmin.reservations.CustomSpinnerAdapter
import com.kosti.palesoccerfieldadmin.reservations.addUsersToReservations.AddBossToReservationAdapter
import com.kosti.palesoccerfieldadmin.reservations.addUsersToReservations.AddUsersToReservation
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Date
import java.util.Locale

class CreateReservations : AppCompatActivity() {

    private lateinit var adapterRemoveUsersPlayersTeamAdapter: RemoveUsersPlayersTeamAdapter
    private lateinit var adapterRemoveUsersChallengingTeamAdapter: RemoveUsersChallengingTeamAdapter
    private var playersIds: ArrayList<String> = ArrayList()
    private var challengersIds: ArrayList<String> = ArrayList()
    private var usersForProposalTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private var usersForChallengingTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var startForResult : ActivityResultLauncher<Intent>
    private lateinit var whereAdd: String

    private lateinit var recyclerViewPlayers: RecyclerView
    private lateinit var recyclerViewChallenging: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var btnSelectBoss:Button
    private lateinit var btnAgregarJugadores: Button
    private lateinit var btnAgregarRetadores: Button
    private lateinit var spinnerTipoReserva: Spinner
    private lateinit var checkTengoEquipo: CheckBox
    private lateinit var scheduleSelected:ScheduleDataModel
    private lateinit var tvBoss: TextView

    private lateinit var boss: JugadoresDataModel
    private var tengoEquipoChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservations)

        recyclerViewPlayers = findViewById(R.id.recyclerJugadoresEquipo)
        recyclerViewPlayers.layoutManager = LinearLayoutManager(this)
        adapterRemoveUsersPlayersTeamAdapter = RemoveUsersPlayersTeamAdapter(ArrayList(), this::userPlayersTeam)
        recyclerViewPlayers.adapter = adapterRemoveUsersPlayersTeamAdapter
        recyclerViewChallenging = findViewById(R.id.recyclerJugadoresRetadores)
        recyclerViewChallenging.layoutManager = LinearLayoutManager(this)
        adapterRemoveUsersChallengingTeamAdapter = RemoveUsersChallengingTeamAdapter(ArrayList(), this::userChallengingTeam)
        recyclerViewChallenging.adapter = adapterRemoveUsersChallengingTeamAdapter

        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                usersForProposalTeam.clear()
                usersForChallengingTeam.clear()
                whereAdd = data?.getStringExtra("textParameter").toString()
                playersIds = data?.getStringArrayListExtra("playersIds") ?: ArrayList()
                challengersIds = data?.getStringArrayListExtra("challengersIds") ?: ArrayList()

                // Limpia las listas y vuelve a cargar desde Firebase
                usersForProposalTeam.clear()
                usersForChallengingTeam.clear()
                loadTeamsFromFirebase(playersIds, isProposalTeam = true)
                loadTeamsFromFirebase(challengersIds, isProposalTeam = false)
            }
        }
        Log.d("playersIds","$playersIds")
        btnAgregarJugadores = findViewById(R.id.btnAgregarJugadorEquipo)
        btnAgregarRetadores = findViewById(R.id.btnAgregarJugadorRetador)
        // Reemplaza tu código actual donde inicias la actividad AddUsersToReservation
        btnAgregarJugadores.setOnClickListener {
            val intent = Intent(this, AddUsersToReservation::class.java)
            intent.putExtra("textParameter", "proposalTeam")
            Log.d("seguimiento","challengersIds $challengersIds \nplayersIds $playersIds")
            intent.putStringArrayListExtra("playersIds", ArrayList(playersIds))
            intent.putStringArrayListExtra("challengersIds", ArrayList(challengersIds))
            startForResult.launch(intent)
        }

        btnAgregarRetadores.setOnClickListener {
            val intent = Intent(this, AddUsersToReservation::class.java)
            intent.putExtra("textParameter", "challengingTeam")
            intent.putStringArrayListExtra("playersIds", ArrayList(playersIds))
            intent.putStringArrayListExtra("challengersIds", ArrayList(challengersIds))
            startForResult.launch(intent)
        }

        btnSelectBoss = findViewById(R.id.btnSeleccionarEncargado)

        btnSelectBoss.setOnClickListener{
            mostrarDialogo()
        }

        checkTengoEquipo = findViewById(R.id.checkTengoEquipo)
        spinnerTipoReserva = findViewById(R.id.spinnerTipoReserva)

        // Listener para el CheckBox
        checkTengoEquipo.setOnCheckedChangeListener { _, isChecked ->
            tengoEquipoChecked = isChecked
            // Desactivar el botón si el CheckBox está marcado
            btnAgregarJugadores.isEnabled = !isChecked

            // Limpiar el RecyclerView y la lista cuando el CheckBox se marca
            if (isChecked) {
                limpiarListaYRecycler()
            }
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
        val types = resources.getStringArray(R.array.typesOfReservation).toList()
        val spinnerTypesOfRsrv: Spinner = findViewById(R.id.spinnerTipoReserva)

        val adapter = CustomSpinnerAdapter(this,R.layout.custom_spinner, types)

        // Especifica el diseño que se usará cuando se desplieguen las opciones
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Une el ArrayAdapter al Spinner
        spinnerTypesOfRsrv.adapter = adapter


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
        FirebaseFirestore.getInstance()


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
                val adapter = CustomSpinnerAdapter(this,R.layout.custom_spinner, scheduleText)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSchedules.adapter = adapter


                spinnerSchedules.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                       scheduleSelected = reservationsSchedules[position]

                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

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
        limpiarListaYRecycler()
    }

    //TODO: METER LOGICA PARA QUE DEPENDIENDO DE LAS OPCIONES QUE SE SELECCIONEN SE DESACTIVEN UNAS U OTRAS EN EL UI lista

    //TODO: CONFIGURAR SPINNER HORARIOS CON LOS QUE ESTAN DISPONIBLES AHORITA SEGUN LA BASE DE DATOS

    //TODO: METER LOGICA PARA ABRIR ACITVITY PARA SELECCIONAR EL ENCARGADO

    //TODO: CONFIGURAR EL OTRO SPINNER CON VALORES QUEMADOS PERO USANDO LISTAS EN VALUES PARA RESERVA Privada o Publica lista

    //TODO: AGREGAR FUNCIONALIDAD PARA SELECCIONAR JUGADORES Y DEPENDE DEL BOTON SE AÑADAN A UN EQUIPO O A OTRO

    //TODO: CONFIGURAR UI PARA LOS ADAPTER CUSTOM DE LOS EQUIPOS --Andrik-- create_reservation_player_list_item lista

    //TODO: CREAR 2 CUSTOM ADAPTERS PARA LOS DOS RECYCLER VIEW

    //TODO: IMPORTANTEEEEEE FUNCIONALIDAD PARA EL BOTON DE CREAR RESERVA METER LAS 100000 VALIDACIONES

    private fun loadTeamsFromFirebase(list: ArrayList<String>, isProposalTeam: Boolean) {
        // Verificar si la carga desde Firebase ya se realizó
        val usersList = if (isProposalTeam) usersForProposalTeam else usersForChallengingTeam
        if ((isProposalTeam && usersForProposalTeam.isNotEmpty()) || (!isProposalTeam && usersForChallengingTeam.isNotEmpty())) {
            return
        }

        val db = Firebase.firestore
        val jugadoresCollection = db.collection("jugadores")

        for (playerId in list) {
            db.collection("jugadores").document(playerId).get().addOnSuccessListener { document ->
            }
            jugadoresCollection.document(playerId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        val userData = JugadoresDataModel(
                            document["nombre"].toString(),
                            document["apodo"].toString(),
                            document["clasificacion"].toString(),
                            document["posiciones"] as MutableList<String>,
                            document.id
                        )

                        usersList.add(userData)

                        if (isProposalTeam) {
                            adapterRemoveUsersPlayersTeamAdapter.setData(usersList)
                        } else {
                            adapterRemoveUsersChallengingTeamAdapter.setData(usersList)
                        }
                    } else {
                        Log.d("Firestore", "No such document")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("Firestore", "Error getting document", exception)
                }
        }
    }

    private fun mostrarDialogo() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_select_boss) // Asegúrate de reemplazar con el layout correcto

        val recyclerViewD = dialog.findViewById<RecyclerView>(R.id.recyclerSelectBoss)
        val searchView = dialog.findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewSelectBoss)
        recyclerViewD.layoutManager = LinearLayoutManager(this)
        // Configurar el RecyclerView y cargar los jugadores desde Firebase
        val adapter = AddBossToReservationAdapter(ArrayList()) { jugadorSeleccionado ->
            Log.d("JugadorSeleccionado", jugadorSeleccionado.Name)
            boss = jugadorSeleccionado
            tvBoss = findViewById(R.id.tvBoss)
            tvBoss.text = boss.Name
            dialog.dismiss()
        }
        recyclerViewD.adapter = adapter

        // Configurar la lógica para cargar los jugadores desde Firebase
        cargarJugadoresDesdeFirebase(adapter, searchView)

        // Mostrar el diálogo
        dialog.show()
    }

    private fun cargarJugadoresDesdeFirebase(adapter: AddBossToReservationAdapter, searchView: androidx.appcompat.widget.SearchView) {
        val db = Firebase.firestore
        val usersCollectionRef = db.collection("jugadores")
        val userList: ArrayList<JugadoresDataModel> = ArrayList()

        usersCollectionRef
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val user = JugadoresDataModel(
                        document["nombre"].toString(),
                        document["apodo"].toString(),
                        document["clasificacion"].toString(),
                        document["posiciones"] as MutableList<String>,
                        document.id
                    )
                    userList.add(user)
                }
                Log.d("JugadorSeleccionado", "Nombre.${userList}" )
                // Actualizar el adaptador con la lista de usuarios obtenida de Firebase
                adapter.setData(userList)
            }
            .addOnFailureListener { exception ->
                Log.w("Firebase", "Error getting documents.", exception)
            }

        // Configurar la lógica de búsqueda si es necesario
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Lógica de búsqueda si se envía el texto
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Lógica de búsqueda mientras se escribe
                adapter.filter(newText ?: "")
                return true
            }
        })
    }



    private fun limpiarListaYRecycler() {
        if (checkTengoEquipo.isChecked) {
            usersForProposalTeam.clear()
            playersIds.clear()
            adapterRemoveUsersPlayersTeamAdapter.setData(usersForProposalTeam)
        }
        if (!checkTengoEquipo.isEnabled){
            usersForProposalTeam.clear()
            playersIds.clear()
            usersForChallengingTeam.clear()
            challengersIds.clear()
            adapterRemoveUsersPlayersTeamAdapter.setData(usersForProposalTeam)
            adapterRemoveUsersChallengingTeamAdapter.setData(usersForChallengingTeam)
        }


    }
    private fun userPlayersTeam(userData: JugadoresDataModel) {
        if (userData.Id in playersIds) {
            playersIds.remove(userData.Id)
        }
        usersForProposalTeam.remove(userData)

        adapterRemoveUsersPlayersTeamAdapter.setData(usersForProposalTeam)
    }

    private fun userChallengingTeam(userData: JugadoresDataModel) {
        if(userData.Id in challengersIds){
            challengersIds.remove(userData.Id)
        }
        usersForChallengingTeam.remove(userData)
        adapterRemoveUsersChallengingTeamAdapter.setData(usersForChallengingTeam)
    }
}