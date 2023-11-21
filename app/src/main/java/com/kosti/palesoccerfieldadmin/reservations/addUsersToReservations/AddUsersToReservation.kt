
package com.kosti.palesoccerfieldadmin.reservations.addUsersToReservations

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.reservations.createReservations.CreateReservations
import kotlin.properties.Delegates

class AddUsersToReservation : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterAddUsers: AddUsersToReservationAdapter
    private val playersNameCollection = "jugadores"
    private lateinit var idUser: String

    private var playersIds: ArrayList<String> = ArrayList()
    private var challengersIds: ArrayList<String> = ArrayList()
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar

    //This have users
    private var userList: MutableList<JugadoresDataModel> = ArrayList()

    private var usersForProposalTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private var usersForChallengingTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var whereAdd: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_users_to_reservation)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewAddUserToReservation)
        searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewAddUserToReservation)
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarAddUserToReservation)
        val btnAddPlayers:Button = findViewById(R.id.btnAddPlayersToReservation)
        whereAdd = intent.getStringExtra("textParameter").toString()
        playersIds = intent.getStringArrayListExtra("playersIds") ?: ArrayList()
        challengersIds = intent.getStringArrayListExtra("challengersIds") ?: ArrayList()
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterAddUsers = AddUsersToReservationAdapter(ArrayList(), this::addUserToReservation)
        recyclerView.adapter = adapterAddUsers

        btnAddPlayers.setOnClickListener{
            onAceptarButtonClick()
        }


        toolbar.setNavigationOnClickListener { onBackPressed() }

        // Obtener el ID del usuario actual
        idUser = intent.getStringExtra("textParameter").toString()

        // Llamar a la función para obtener la lista de usuarios desde Firebase
        getAllUsersFromCollection(playersNameCollection)
    }

    private fun getAllUsersFromCollection(collectionName: String) {
        val db = Firebase.firestore
        val usersCollectionRef = db.collection(collectionName)

        // Limpiar la lista antes de agregar los nuevos usuarios
        if (userList.isNotEmpty()) {
            userList.clear()
        }

        usersCollectionRef
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    for (document in result) {
                        val user = JugadoresDataModel(
                            document["nombre"].toString(),
                            document["apodo"].toString(),
                            document["clasificacion"].toString(),
                            document["posiciones"] as MutableList<String>,
                            document.id,
                            document["id"].toString()
                        )
                        findUserIdByNameAndNickname(user.Name, user.Nickname) { userId ->
                            if (userId != null) {
                                if(whereAdd=="proposalTeam") {
                                    if(playersIds.isNotEmpty()){
                                        if (userId in playersIds) {
                                            //
                                        } else {
                                            Log.d("encuentraID", "#$userId")
                                            userList.add(user)
                                        }
                                    }else{
                                        userList.add(user)
                                    }

                                }else if(whereAdd=="challengingTeam"){
                                    if(challengersIds.isNotEmpty()){
                                        if (userId in challengersIds) {
                                            //
                                        } else {
                                            Log.d("encuentraID", "#$userId")
                                            userList.add(user)
                                        }
                                    }else{
                                        userList.add(user)
                                    }
                                }
                            }
                            updateAdapterData(userList)
                        }
                    }
                } else {
                    Log.d("Usercito", "No se encontraron documentos en la colección $collectionName")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Usercito", "Error al obtener documentos: ", exception)
            }
    }
    private fun updateAdapterData(userList: List<JugadoresDataModel>) {
        // Actualizar los datos en el adaptador y notificar cambios
        adapterAddUsers.setData(userList)
    }
    private fun onAceptarButtonClick() {
        val selectedUsers = adapterAddUsers.getSelectedUsers()
        val userIds = mutableListOf<String>()

        // Variable para contar el número de respuestas obtenidas
        var responsesReceived = 0

        selectedUsers.forEach { user ->
            // Aquí deberías buscar el ID del usuario por nombre y apodo
            findUserIdByNameAndNickname(user.Name, user.Nickname) { userId ->
                // Agregar el ID a la lista si se encuentra
                if (userId != null) {
                    userIds.add(userId)
                }

                // Incrementar el contador de respuestas
                responsesReceived++

                // Verificar si se han recibido respuestas para todos los usuarios seleccionados
                if (responsesReceived == selectedUsers.size) {
                    // Todas las respuestas han sido recibidas, puedes continuar con el código
                    val intent = Intent(this, CreateReservations::class.java)
                    when (whereAdd) {
                        "proposalTeam" -> {
                            intent.putExtra("textParameter", "proposalTeam")
                            playersIds = playersIds.plus(userIds) as ArrayList<String>
                            intent.putStringArrayListExtra("playersIds", ArrayList(playersIds))
                            intent.putStringArrayListExtra("challengersIds", ArrayList(challengersIds))

                        }
                        "challengingTeam" -> {
                            intent.putExtra("textParameter", "challengingTeam")
                            challengersIds = challengersIds.plus(userIds) as ArrayList<String>
                            intent.putStringArrayListExtra("challengersIds", ArrayList(challengersIds))
                            intent.putStringArrayListExtra("playersIds", ArrayList(playersIds))
                        }
                    }
                    Log.d("seguimiento","challengersIds $challengersIds \nplayersIds $playersIds")
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }
    }
    private fun findUserIdByNameAndNickname(name: String, nickname: String, callback: (String?) -> Unit) {
        val db = Firebase.firestore
        val usersCollectionRef = db.collection(playersNameCollection)

        // Realizar una consulta para encontrar al usuario por nombre de usuario y nombre
        usersCollectionRef
            .whereEqualTo("apodo", nickname)
            .whereEqualTo("nombre", name)
            .get()
            .addOnSuccessListener { result ->
                if (result != null && !result.isEmpty) {
                    // Se encontró al usuario, obten el ID
                    val userId = result.documents[0].id
                    callback(userId)
                } else {
                    // No se encontró al usuario
                    callback(null)
                }
            }
            .addOnFailureListener { e ->
                Log.w("Usercito", "Error al buscar al usuario: $e")
                // Manejar el error
                callback(null)
            }
    }

    private fun addUserToReservation(userData: JugadoresDataModel) {
        when (whereAdd) {
            "proposalTeam" -> {
                // Agregar usuario a usersForProposalTeam
                usersForProposalTeam.add(userData)
            }
            "challengingTeam" -> {
                // Agregar usuario a usersForChallengingTeam
                usersForChallengingTeam.add(userData)
            }
        }
    }
}