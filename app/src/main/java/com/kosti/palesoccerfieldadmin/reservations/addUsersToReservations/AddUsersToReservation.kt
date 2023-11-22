
package com.kosti.palesoccerfieldadmin.reservations.addUsersToReservations

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
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
    private var originalUserList: List<JugadoresDataModel> = ArrayList()
    private var playersIds: ArrayList<String> = ArrayList()
    private var challengersIds: ArrayList<String> = ArrayList()
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private var selectedFilterOption: String = "all"

    //This have users
    private var userList: MutableList<JugadoresDataModel> = ArrayList()

    private var usersForProposalTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private var usersForChallengingTeam: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var whereAdd: String

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioAll: RadioButton
    private lateinit var radioBad: RadioButton
    private lateinit var radioRegular: RadioButton
    private lateinit var radioGood: RadioButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_users_to_reservation)
        radioGroup = findViewById<RadioGroup>(R.id.radioGroupFilterAddUserToReservation)
        radioAll = findViewById<RadioButton>(R.id.radioAllAddUserToReservation)
        radioBad = findViewById<RadioButton>(R.id.radioBadAddUserToReservation)
        radioRegular = findViewById<RadioButton>(R.id.radioRegularAddUserToReservation)
        radioGood = findViewById<RadioButton>(R.id.radioGoodAddUserToReservation)
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
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText.orEmpty())
                return true
            }
        })

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            // Obtener la opción seleccionada
            selectedFilterOption = when (checkedId) {
                R.id.radioAllAddUserToReservation -> "all"
                R.id.radioBadAddUserToReservation -> "bad"
                R.id.radioRegularAddUserToReservation -> "regular"
                R.id.radioGoodAddUserToReservation -> "good"
                else -> "all" // Opción predeterminada
            }

            // Filtrar la lista según la opción seleccionada y la cadena de búsqueda actual
            filterList(searchView.query.toString())
        }
    }


    private fun getAllUsersFromCollection(collectionName: String) {
        val db = Firebase.firestore
        val usersCollectionRef = db.collection(collectionName)

        // Limpia la lista antes de agregar los nuevos usuarios
        userList.clear()

        usersCollectionRef
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    // Limpia la lista antes de agregar nuevos usuarios
                    userList.clear()

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
                                val isAlreadyAdded = userId in playersIds || userId in challengersIds

                                if (!isAlreadyAdded) {
                                    Log.d("encuentraID", "#$userId")
                                    userList.add(user)
                                }
                            }
                            // Actualiza el adaptador con la nueva lista de usuarios
                            updateAdapterData(userList)
                            // Guarda los datos originales al principio
                            originalUserList = userList.toList()
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
    private fun restoreOriginalData() {
        userList.clear()
        userList.addAll(originalUserList)
        updateAdapterData(userList)
    }

    private fun filterList(query: String) {
        val filteredList = when (selectedFilterOption) {
            "all" -> originalUserList.filter { user ->
                user.Name.contains(query, ignoreCase = true) ||
                        user.Nickname.contains(query, ignoreCase = true) ||
                        user.Clasification.contains(query, ignoreCase = true)
            }
            "bad" -> originalUserList.filter { user ->
                user.Clasification.equals("Malo", ignoreCase = true) &&
                        (user.Name.contains(query, ignoreCase = true) || user.Nickname.contains(query, ignoreCase = true))
            }
            "regular" -> originalUserList.filter { user ->
                user.Clasification.equals("Regular", ignoreCase = true) &&
                        (user.Name.contains(query, ignoreCase = true) || user.Nickname.contains(query, ignoreCase = true))
            }
            "good" -> originalUserList.filter { user ->
                user.Clasification.equals("Bueno", ignoreCase = true) &&
                        (user.Name.contains(query, ignoreCase = true) || user.Nickname.contains(query, ignoreCase = true))
            }
            else -> originalUserList // Opción desconocida, muestra todos
        }

        userList.clear()
        userList.addAll(filteredList)
        updateAdapterData(userList)
    }


    private fun onAceptarButtonClick() {
        val selectedUsers = adapterAddUsers.getSelectedUsers()
        val userIds = mutableListOf<String>()

        // Variable para contar el número de respuestas obtenidas
        var responsesReceived = 0


        if (selectedUsers.size<=6){
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
                                val comparacion: Int = selectedUsers.size + playersIds.size
                                if ((comparacion)<=6){
                                    intent.putExtra("textParameter", "proposalTeam")
                                    playersIds = playersIds.plus(userIds) as ArrayList<String>
                                    intent.putStringArrayListExtra("playersIds", ArrayList(playersIds))
                                    intent.putStringArrayListExtra("challengersIds", ArrayList(challengersIds))
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }else{
                                    Toast.makeText(this,"No se pueden agregar más de 6 jugadores",Toast.LENGTH_SHORT).show()
                                }
                            }
                            "challengingTeam" -> {
                                val comparacion: Int = selectedUsers.size + challengersIds.size
                                if ((comparacion)<=6){
                                    intent.putExtra("textParameter", "challengingTeam")
                                    challengersIds = challengersIds.plus(userIds) as ArrayList<String>
                                    intent.putStringArrayListExtra("challengersIds", ArrayList(challengersIds))
                                    intent.putStringArrayListExtra("playersIds", ArrayList(playersIds))
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                }else{
                                    Toast.makeText(this,"No se pueden agregar más de 6 jugadores",Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                    }
                }
            }
        }else{
            Toast.makeText(this,"No se pueden agregar más de 6 jugadores",Toast.LENGTH_SHORT).show()
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