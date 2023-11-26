package com.kosti.palesoccerfieldadmin.blockedUserList

import BlockedUsersListAdapter
import UnBlockedUsersListAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlin.properties.Delegates

class BlockedUsersList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapterBlocked: BlockedUsersListAdapter
    private lateinit var adapterUnBlocked: UnBlockedUsersListAdapter
    private val playersNameCollection = "jugadores"
    private var listUsersBlocked: ArrayList<String> = ArrayList()
    private var listUsersBlockedFinded: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var idUser: String
    private var userList: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var textViewNoBlockedUsers: TextView
    private lateinit var progressBarLoading: ProgressBar
    private lateinit var radioProgressBar: ProgressBar
    private lateinit var searchView: androidx.appcompat.widget.SearchView
    private var selected by Delegates.notNull<Boolean>()
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blocked_users_list)

        idUser = intent.getStringExtra("textParameter").toString()
        radioProgressBar = findViewById(R.id.radioProgressBar)
        val loadingMessage = findViewById<TextView>(R.id.loadingMessage)
        val radioGroupFilter = findViewById<RadioGroup>(R.id.radioGroupFilter)
        textViewNoBlockedUsers = findViewById(R.id.textViewNoBlockedUsers)
        progressBarLoading = findViewById(R.id.progressBarLoading)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerViewBlockedUsers)
        searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchViewBUL)
        toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbarBlockedUsers)

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapterBlocked = BlockedUsersListAdapter(ArrayList(), this::blockUsers)
        adapterUnBlocked = UnBlockedUsersListAdapter(ArrayList(), this::unblockUsers)
        recyclerView.adapter = adapterUnBlocked

        recyclerView.visibility = View.GONE
        textViewNoBlockedUsers.visibility = View.GONE
        progressBarLoading.visibility = View.VISIBLE
        selected = true
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText.orEmpty())
                return true
            }
        })

        radioGroupFilter.setOnCheckedChangeListener { group, checkedId ->
            radioProgressBar.visibility = View.VISIBLE
            loadingMessage.visibility = View.VISIBLE // Opcional: Muestra un mensaje de carga

            when (checkedId) {
                R.id.radioBlocked -> {
                    selected = true
                    setupRecyclerView(showBlocked = true)
                    radioProgressBar.visibility = View.GONE
                    loadingMessage.visibility = View.GONE
                }
                R.id.radioAll -> {
                    selected = false
                    getAllUsersFromCollection(playersNameCollection, true)
                    setupRecyclerView(showBlocked = false)
                    radioProgressBar.visibility = View.GONE
                    loadingMessage.visibility = View.GONE
                }
            }
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
        // Llamar a getListBlockedUsers(idUser) después de inicializar el adaptador
        getListBlockedUsers(idUser)
    }

    private fun filterList(query: String) {
        if(selected){
            val filteredList = listUsersBlockedFinded.filter { user ->
                user.Name.contains(query, ignoreCase = true) || user.Nickname.contains(query, ignoreCase = true)
            }.toMutableList()
            adapterBlocked.setData(filteredList)
            adapterUnBlocked.setData(filteredList)

            if (filteredList.isEmpty()) {
                textViewNoBlockedUsers.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                textViewNoBlockedUsers.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }else{
            val filteredList = userList.filter { user ->
                user.Name.contains(query, ignoreCase = true) || user.Nickname.contains(query, ignoreCase = true)
            }.toMutableList()
            adapterBlocked.setData(filteredList)
            adapterUnBlocked.setData(filteredList)

            if (filteredList.isEmpty()) {
                textViewNoBlockedUsers.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            } else {
                textViewNoBlockedUsers.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            }
        }

    }

    private fun getListBlockedUsers(id: String) {
        //listUsersBlocked.clear()
        listUsersBlockedFinded.clear()
        FirebaseUtils().getDocumentById(playersNameCollection, id) { result ->
            result.onSuccess { data ->
                listUsersBlocked = data["bloqueos"] as ArrayList<String>
                if (listUsersBlocked.isEmpty()) {
                    Log.d("Usercito", "El array está vacío o su contenido es desconocido")
                } else {
                    for (elemento in listUsersBlocked) {
                        findBlockedUsers(elemento)
                    }
                }

                // Llama a setupRecyclerView después de que los datos se hayan llenado
                setupRecyclerView(showBlocked = true)
            }
            result.onFailure {
                Log.d("UserNoFound", "User not found")
                setupRecyclerView(showBlocked = true)
            }
        }
    }


    private fun getAllUsersFromCollection(collectionName: String, isAll:Boolean) {
        val db = Firebase.firestore
        val usersCollectionRef = db.collection(collectionName)
        userList.clear()
        usersCollectionRef
            .get()
            .addOnSuccessListener { result ->
                if (result != null) {
                    for (document in result) {
                        val user = JugadoresDataModel(
                            document["nombre"].toString(),
                            document["apodo"].toString(),
                            document["uid"].toString(),
                            document.id,
                            document["correo"].toString()
                        )

                        userList.add(user)
                    }
                    if(isAll){
                        setupRecyclerView(showBlocked = false)
                    }
                } else {
                    Log.d("Usercito", "No se encontraron documentos en la colección $collectionName")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("Usercito", "Error al obtener documentos: ", exception)
            }
    }

    private fun findBlockedUsers(id: String) {
        FirebaseUtils().getDocumentById(playersNameCollection, id) { result ->
            result.onSuccess {
                listUsersBlockedFinded.add(
                    JugadoresDataModel(
                        it["nombre"].toString(),
                        it["apodo"].toString(),
                        it["uid"].toString(),
                        it["id"].toString(),
                        it["correo"].toString()
                    )
                )

                if (listUsersBlockedFinded.size == listUsersBlocked.size) {
                    setupRecyclerView(showBlocked = true)
                }
            }
            result.onFailure {
                Log.d("UserNoFound", "User not found")
                setupRecyclerView(showBlocked = true)
            }
        }
    }

    private fun setupRecyclerView(showBlocked: Boolean) {
        if (showBlocked) {
            if (listUsersBlockedFinded.isNotEmpty()) {
                recyclerView.adapter = adapterUnBlocked
                adapterUnBlocked.setData(listUsersBlockedFinded)
                textViewNoBlockedUsers.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                Log.d("Usercito", "La lista listUsersBlockedFinded está vacía")
                textViewNoBlockedUsers.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            progressBarLoading.visibility = View.GONE // Oculta la ProgressBar cuando se han cargado los datos
        } else {
            if (userList.isNotEmpty()) {
                recyclerView.adapter = adapterBlocked
                adapterBlocked.setData(userList)
                textViewNoBlockedUsers.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE
            } else {
                Log.d("Usercito", "La lista userList está vacía")
                textViewNoBlockedUsers.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }
            progressBarLoading.visibility = View.GONE // Oculta la ProgressBar cuando se han cargado los datos
        }
    }

    private fun blockUsers(userData: JugadoresDataModel) {
        val userNick = userData.Nickname
        val userName = userData.Name

        if (userNick != null && userName != null) {
            // Verifica si el usuario ya está bloqueado
            // Obtener el ID del usuario basado en su nombre de usuario y nombre
            val db = Firebase.firestore
            val usersCollectionRef = db.collection(playersNameCollection)

            // Realizar una consulta para encontrar al usuario por nombre de usuario y nombre
            usersCollectionRef
                .whereEqualTo("apodo", userNick)
                .whereEqualTo("nombre", userName)
                .get()
                .addOnSuccessListener { result ->
                    if (result != null && !result.isEmpty) {
                        // Se encontró al usuario, obten el ID
                        val userId = result.documents[0].id
                        if (listUsersBlocked.contains(userData.Id)) {
                            Toast.makeText(this, "$userName ya está bloqueado.", Toast.LENGTH_LONG).show()
                        }else{
                            // Agregar el ID a la lista de usuarios bloqueados
                            listUsersBlocked.add(userId)

                            // Actualizar la lista de bloqueados en Firebase
                            val userDocRef = db.collection(playersNameCollection).document(idUser)
                            userDocRef.update("bloqueos", listUsersBlocked).addOnSuccessListener {
                                Toast.makeText(this, "$userName bloqueado con éxito.", Toast.LENGTH_LONG).show()
                                getListBlockedUsers(idUser)
                                val radioGroupFilter = findViewById<RadioGroup>(R.id.radioGroupFilter)
                                radioGroupFilter.check(R.id.radioBlocked)
                            }.addOnFailureListener { e ->
                                Log.w("Usercito", "Error al bloquear usuario: $e")
                            }
                        }
                    } else {
                        Log.d("Usercito", "No se encontró al usuario con el nombre de usuario y nombre proporcionados.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Usercito", "Error al buscar al usuario: $e")
                }

        } else {
            Log.d("Usercito", "El nombre de usuario o nombre es nulo.")
        }
    }



    private fun unblockUsers(userData: JugadoresDataModel) {
        val userId = userData.Id
        Log.d("Usercito","xD $userId")
        // Lógica para desbloquear un usuario de la lista de bloqueados
        val db = Firebase.firestore
        val userDocRef = db.collection(playersNameCollection).document(idUser)

        // Obtén la lista actual de usuarios bloqueados
        userDocRef.get().addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                val currentBlocked = documentSnapshot.get("bloqueos") as? ArrayList<String>

                // Si la lista existe y el usuario se encuentra en ella, elimínalo
                if (currentBlocked != null && currentBlocked.contains(userId)) {
                    currentBlocked.remove(userId)
                    // Actualiza el documento en Firebase con la nueva lista de bloqueados
                    userDocRef.update("bloqueos", currentBlocked).addOnSuccessListener {
                        Toast.makeText(this, "${userData.Name} desbloqueado con éxito.", Toast.LENGTH_LONG).show()
                        // Elimina el usuario de la lista listUsersBlockedFinded
                        listUsersBlocked.remove(userId)
                        listUsersBlockedFinded.remove(userData)
                        setupRecyclerView(showBlocked = true)
                    }.addOnFailureListener { e ->
                        Log.w("Usercito", "Error al desbloquear usuario: $e")
                    }
                }
            }
        }
    }
}
