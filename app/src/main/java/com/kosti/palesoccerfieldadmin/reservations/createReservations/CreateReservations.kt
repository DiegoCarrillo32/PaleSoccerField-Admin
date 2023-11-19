package com.kosti.palesoccerfieldadmin.reservations.createReservations

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.widget.Toolbar
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.reservations.addUsersToReservations.AddUsersToReservation

class CreateReservations : AppCompatActivity() {

    private var usersForProposalTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private var usersForChallengingTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var toolbar: Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_reservations)
        val userIds = intent.getStringArrayListExtra("jugadoresIds")
        // Hacer algo con la lista de IDs recibidos
        Log.d("ReceivedUserIds", "wtf${userIds.toString()}")
        val btnAgregarJugadores: Button = findViewById(R.id.btnAgregarJugadorEquipo)
        val btnAgregarRetadpres: Button = findViewById(R.id.btnAgregarJugadorRetador)
        btnAgregarJugadores.setOnClickListener {
            val intent = Intent(this, AddUsersToReservation::class.java)
            intent.putExtra("textParameter", "proposalTeam")
            this.startActivity(intent)
        }
        btnAgregarRetadpres.setOnClickListener {
            val intent = Intent(this, AddUsersToReservation::class.java)
            intent.putExtra("textParameter", "challengingTeam")
            this.startActivity(intent)
        }
        toolbar = findViewById(R.id.toolbarCreateReservations)

        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    //TODO: METER LOGICA PARA QUE DEPENDIENDO DE LAS OPCIONES QUE SE SELECCIONEN SE DESACTIVEN UNAS U OTRAS EN EL UI

    //TODO: CONFIGURAR SPINNER HORARIOS CON LOS QUE ESTAN DISPONIBLES AHORITA SEGUN LA BASE DE DATOS

    //TODO: METER LOGICA PARA ABRIR ACITVITY PARA SELECCIONAR EL ENCARGADO

    //TODO: CONFIGURAR EL OTRO SPINNER CON VALORES QUEMADOS PERO USANDO LISTAS EN VALUES PARA RESERVA Privada o Publica

    //TODO: AGREGAR FUNCIONALIDAD PARA SELECCIONAR JUGADORES Y DEPENDE DEL BOTON SE AÑADAN A UN EQUIPO O A OTRO

    //TODO: CONFIGURAR UI PARA LOS ADAPTER CUSTOM DE LOS EQUIPOS --Andrik-- create_reservation_player_list_item lista

    //TODO: CREAR 2 CUSTOM ADAPTERS PARA LOS DOS RECYCLER VIEW

    //TODO: IMPORTANTEEEEEE FUNCIONALIDAD PARA EL BOTON DE CREAR RESERVA METER LAS 100000 VALIDACIONES


}