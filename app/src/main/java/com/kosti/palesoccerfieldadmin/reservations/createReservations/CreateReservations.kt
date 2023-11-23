package com.kosti.palesoccerfieldadmin.reservations.createReservations

import android.app.Dialog
import android.content.Intent
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
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
    private var usersForProposalTeam: MutableList<JugadoresDataModel> = ArrayList()
    private var usersForChallengingTeam: MutableList<JugadoresDataModel> = ArrayList()
    private lateinit var startForResult : ActivityResultLauncher<Intent>
    private lateinit var whereAdd: String
    private var originalBossList: List<JugadoresDataModel> = ArrayList()
    private var reservationsSchedules = mutableListOf<ScheduleDataModel>()
    private lateinit var spinnerSchedules: Spinner
    private lateinit var spinnerTypesOfRsrv: Spinner
    private lateinit var types: List<String>

    private lateinit var recyclerViewPlayers: RecyclerView
    private lateinit var recyclerViewChallenging: RecyclerView
    private lateinit var toolbar: Toolbar
    private lateinit var btnSelectBoss:Button
    private lateinit var btnAgregarJugadores: Button
    private lateinit var btnAgregarRetadores: Button
    private lateinit var btnCrearReserva: Button
    private lateinit var spinnerTipoReserva: Spinner
    private lateinit var checkTengoEquipo: CheckBox
    private lateinit var scheduleSelected:ScheduleDataModel
    private lateinit var auxScheduleSelected: String
    private lateinit var tvBoss: TextView

    private lateinit var boss: JugadoresDataModel
    private var tengoEquipoChecked = false



    private lateinit var reservationIdToEdit: String // Agrega esta variable
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservations)
        reservationIdToEdit = intent.getStringExtra("reservationIdToEdit") ?: ""
        types = resources.getStringArray(R.array.typesOfReservation).toList()
        spinnerTypesOfRsrv = findViewById(R.id.spinnerTipoReserva)
        spinnerSchedules = findViewById(R.id.spinnerHorario)
        btnCrearReserva = findViewById(R.id.btnCrearReserva)
        recyclerViewPlayers = findViewById(R.id.recyclerJugadoresEquipo)
        recyclerViewPlayers.layoutManager = LinearLayoutManager(this)
        adapterRemoveUsersPlayersTeamAdapter = RemoveUsersPlayersTeamAdapter(ArrayList(), this::userPlayersTeam)
        recyclerViewPlayers.adapter = adapterRemoveUsersPlayersTeamAdapter
        recyclerViewChallenging = findViewById(R.id.recyclerJugadoresRetadores)
        recyclerViewChallenging.layoutManager = LinearLayoutManager(this)
        adapterRemoveUsersChallengingTeamAdapter = RemoveUsersChallengingTeamAdapter(ArrayList(), this::userChallengingTeam)
        recyclerViewChallenging.adapter = adapterRemoveUsersChallengingTeamAdapter


        if (reservationIdToEdit != "") {
            val title = findViewById<TextView>(R.id.tvToolbarTitle)
            title.text = "Editar Reserva"
            // Si reservationIdToEdit no está vacío, estás en modo edición
            Toast.makeText(this, "Estás en modo edición", Toast.LENGTH_SHORT).show()
            cargarJugadoresDesdeFirebase()
            cargarDatosReservaParaEdicion(reservationIdToEdit)
            btnCrearReserva.text = "Editar" // Cambia el texto del botón
        } else {
            // Estás en modo creación
            supportActionBar?.title = "Crear Reserva" // Cambia el título de la barra de acciones
        }

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

        btnAgregarJugadores = findViewById(R.id.btnAgregarJugadorEquipo)
        btnAgregarRetadores = findViewById(R.id.btnAgregarJugadorRetador)
        // Reemplaza tu código actual donde inicias la actividad AddUsersToReservation
        btnAgregarJugadores.setOnClickListener {
            val intent = Intent(this, AddUsersToReservation::class.java)
            intent.putExtra("textParameter", "proposalTeam")
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

        btnCrearReserva.setOnClickListener{
            createOrUpdateReservation()
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

    private fun createReservation(){

        val reservacion: HashMap<String, Any> = HashMap<String, Any>()

        if(::boss.isInitialized && ::scheduleSelected.isInitialized){//Validamos que haya un encargado y un horario seleccionado
            reservacion["encargado"] = boss.UID

            reservacion["horario"] = scheduleSelected.id
            reservacion["fecha"] = scheduleSelected.tanda?.get(0) as Timestamp
            reservacion["jugadores"] = playersIds
            reservacion["retadores"] = challengersIds
            reservacion["estado"] = true
            if(checkTengoEquipo.isEnabled){//Validamos que esté disponible el check para decir que tengo equipo
                reservacion["tipo"] = "Publica"
                reservacion["equipo"] = checkTengoEquipo.isChecked
            }else{
                reservacion["tipo"] = "Privada"
                reservacion["equipo"] = false
            }
            FirebaseUtils().createDocument("reservas", reservacion)
            Toast.makeText(this,"Se creó la reserva exitosamente",Toast.LENGTH_LONG).show()
            FirebaseUtils().updateDocument("horario",scheduleSelected.id, hashMapOf("reservado" to true))
            finish()
        }else{
            Toast.makeText(this,"Recuerda seleccionar un horario y un encargado.",Toast.LENGTH_LONG).show()
        }
    }

    private fun spinnerTypeReservation() {



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

    private fun spinnerSchedules(idHorario: String = "" ) {

        FirebaseFirestore.getInstance()
        FirebaseUtils().readCollection("horario") {
            result ->
                result.onSuccess {
                    reservationsSchedules.clear()
                    for (schedule in it) {
                        if (schedule["reservado"] == null ||
                            schedule["tanda"] == null) {
                            Toast.makeText(this, "Horario con datos erroneos", Toast.LENGTH_LONG).show()
                            continue
                        }
                        // si el horario está reservado y es el horario que se está editando agrega el horario a la lista
                        if((idHorario != "") && (schedule["id"].toString() == idHorario)){
                            Toast.makeText(this, "Horario reservado $idHorario", Toast.LENGTH_LONG).show()
                            var horario = ScheduleDataModel(
                                schedule["id"].toString(),
                                schedule["fecha"] as Timestamp,
                                schedule["tanda"] as MutableList<Timestamp>,
                                schedule["reservado"] as Boolean,
                                convertTime(schedule["tanda"] as MutableList<Timestamp> )
                            )

                            reservationsSchedules.add(horario)
                            continue
                        }
                        // si el horario está reservado y no es el horario que se está editando no agrega el horario a la lista
                        if(schedule["reservado"] == true ){
                            continue
                        }

                        var horario = ScheduleDataModel(
                            schedule["id"].toString(),
                            schedule["fecha"] as Timestamp,
                            schedule["tanda"] as MutableList<Timestamp>,
                            schedule["reservado"] as Boolean,
                            convertTime(schedule["tanda"] as MutableList<Timestamp> )
                        )

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
                            document.id,
                            document["UID"].toString()
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

        // Limpiar la lista antes de agregar los nuevos usuarios
        originalBossList = ArrayList()

        usersCollectionRef
            .get()
            .addOnSuccessListener { result ->
                val userList: ArrayList<JugadoresDataModel> = ArrayList()

                for (document in result) {
                    val user = JugadoresDataModel(
                        document["nombre"].toString(),
                        document["apodo"].toString(),
                        document["clasificacion"].toString(),
                        document["posiciones"] as MutableList<String>,
                        document.id,
                        document["UID"].toString()
                    )
                    userList.add(user)
                }
                originalBossList = userList.toList()
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
                if (newText.isNullOrBlank()) {
                    // Si el texto está vacío, utiliza los datos originales
                    adapter.setData(originalBossList)
                } else {
                    // Si hay texto de búsqueda, filtra la lista
                    adapter.filter(newText ?: "")
                }
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


    /*
    * Activity Editar
    * */

    private fun cargarDatosReservaParaEdicion(reservationId: String) {

        val db = Firebase.firestore
        val reservasCollectionRef = db.collection("reservas")

        // Realizar la consulta a Firebase para obtener los datos de la reserva
        reservasCollectionRef.document(reservationId).get()
            .addOnSuccessListener { documentSnapshot ->
                // Verificar si el documento existe
                if (documentSnapshot.exists()) {
                    // Obtener los datos del documento
                    val encargado = documentSnapshot.getString("encargado")
                    if(encargado != null){
                        originalBossList.forEach { bossesito ->
                            if (bossesito.UID == encargado){
                                boss = bossesito
                                tvBoss = findViewById(R.id.tvBoss)
                                tvBoss.text = boss.Name
                            }else{
                                Log.d("ERROR EDIT","El encargado no existe")
                            }
                        }
                    }
                    val idHorario = documentSnapshot.getString("horario")


                    if(idHorario != null){
                        auxScheduleSelected = idHorario
                        spinnerSchedules(idHorario) // settea la lista, tomando en cuenta solo el horario actual
                        reservationsSchedules.forEach{ schedulito->
                            if(schedulito.id == idHorario){
                                scheduleSelected = schedulito
                                val scheduleIds = reservationsSchedules.map { it.id }

                                // Encontrar el índice de scheduleSelected en la lista de identificadores
                                val indexOfSelected = scheduleIds.indexOf(scheduleSelected.id)

                                // Verificar si el índice es válido y establecerlo como el elemento seleccionado en el Spinner
                                if (indexOfSelected != -1) {
                                    spinnerSchedules.setSelection(indexOfSelected)

                                    // Configurar el listener del Spinner para manejar selecciones
                                    spinnerSchedules.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                                            scheduleSelected = reservationsSchedules[position]
                                            // Puedes realizar acciones adicionales al seleccionar un elemento en el spinner
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>?) {
                                            // Acciones adicionales cuando no se selecciona nada en el spinner
                                        }
                                    }
                                }
                            }
                        }
                    }
                    spinnerTypeReservation()

                    val equipo = documentSnapshot.getBoolean("equipo")
                    val listaJugadores = documentSnapshot.get("jugadores") as MutableList<*>
                    val listaRetadores = documentSnapshot.get("retadores") as MutableList<*>

                    challengersIds = listaRetadores as ArrayList<String>
                    playersIds = listaJugadores as ArrayList<String>
                    loadTeamsFromFirebase(playersIds, isProposalTeam = true)
                    loadTeamsFromFirebase(challengersIds, isProposalTeam = false)
                    checkTengoEquipo.isChecked = equipo?: false

                    val TipoDeReserva = documentSnapshot.getString("tipo")

                    if (TipoDeReserva != null) {
                        // Obtener el índice del elemento en la lista
                        val index = types.indexOf(TipoDeReserva)

                        // Establecer la selección en el Spinner
                        if (index != -1) {
                            spinnerTypesOfRsrv.setSelection(index)
                        }

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
                } else {
                    // El documento no existe
                    // Puedes manejar este caso según tus necesidades
                }
            }
            .addOnFailureListener { e ->
                // Manejar el error en la consulta a Firebase
                // Puedes mostrar un mensaje de error o realizar otras acciones
            }
    }

    private fun cargarJugadoresDesdeFirebase() {
        val db = Firebase.firestore
        val jugadoresCollectionRef = db.collection("jugadores")

        // Limpiar la lista antes de agregar los nuevos usuarios
        originalBossList = ArrayList()

        jugadoresCollectionRef
            .get()
            .addOnSuccessListener { result ->
                val userList: MutableList<JugadoresDataModel> = ArrayList()

                for (document in result) {
                    val user = JugadoresDataModel(
                        document.getString("nombre").orEmpty(),
                        document.getString("apodo").orEmpty(),
                        document.getString("clasificacion").orEmpty(),
                        document.get("posiciones") as? MutableList<String> ?: mutableListOf(),
                        document.id,
                        document.getString("UID").orEmpty()
                    )
                    userList.add(user)
                }
                // Almacena la lista completa
                originalBossList = userList
                // Ahora tienes la lista completa de jugadores en originalBossList
                // Puedes realizar consultas a esta lista según tus necesidades
            }
            .addOnFailureListener { e ->
                // Manejar el error en la consulta a Firebase
                // Puedes mostrar un mensaje de error o realizar otras acciones
            }
    }

    private fun createOrUpdateReservation() {
        if (reservationIdToEdit.isNotEmpty()) {
            // Estás en modo edición, implementa la lógica de actualización
            updateReservation(reservationIdToEdit)
        } else {
            // Estás en modo creación, implementa la lógica de creación
            createReservation()
        }
    }

    private fun updateReservation(reservationId: String) {
        val reservacion: HashMap<String, Any> = HashMap<String, Any>()
        if(::boss.isInitialized && ::scheduleSelected.isInitialized){//Validamos que haya un encargado y un horario seleccionado
            reservacion["encargado"] = boss.UID

            reservacion["horario"] = scheduleSelected.id
            reservacion["fecha"] = scheduleSelected.tanda?.get(0) as Timestamp

            reservacion["jugadores"] = playersIds
            reservacion["retadores"] = challengersIds
            reservacion["estado"] = true
            if(checkTengoEquipo.isEnabled){//Validamos que esté disponible el check para decir que tengo equipo
                reservacion["tipo"] = "Publica"
                reservacion["equipo"] = checkTengoEquipo.isChecked
            }else{
                reservacion["tipo"] = "Privada"
                reservacion["equipo"] = false
            }
            if(scheduleSelected.id != auxScheduleSelected){
                FirebaseUtils().updateDocument("horario",auxScheduleSelected, hashMapOf("reservado" to false))
                FirebaseUtils().updateDocument("horario",scheduleSelected.id, hashMapOf("reservado" to true))
            }
            FirebaseUtils().updateDocument("reservas",reservationId,reservacion)
            Toast.makeText(this,"Se Actualizó la reserva exitosamente",Toast.LENGTH_LONG).show()
            finish()
        }else{
            Toast.makeText(this,"Recuerda seleccionar un horario y un encargado.",Toast.LENGTH_LONG).show()
        }
    }


}