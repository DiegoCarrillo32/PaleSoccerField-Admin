package com.kosti.palesoccerfieldadmin.blockedUserList

import BlockedUsersListAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import android.util.Log
import android.widget.SearchView
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
class BlockedUsersList : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BlockedUsersListAdapter
    private val playersNameCollection = "jugadores"
    private var listUsersBlocked: ArrayList<String> = ArrayList()
    private var listUsersBlockedFinded: ArrayList<JugadoresDataModel> = ArrayList()
    private lateinit var idUser: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.blocked_users_list)
        idUser = intent.getStringExtra("textParameter").toString()
        getListBlockedUsers(idUser)

        val searchView = findViewById<SearchView>(R.id.searchViewBUL)
        recyclerView = findViewById(R.id.recyclerViewBlockedUsers)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BlockedUsersListAdapter(listUsersBlockedFinded)
        recyclerView.adapter = adapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText.orEmpty())
                return true
            }
        })
    }

    private fun getListBlockedUsers(id: String) {
        FirebaseUtils().getDocumentById(playersNameCollection, id) { result ->
            result.onSuccess { data ->
                listUsersBlocked = data["bloqueos"] as ArrayList<String>

                if (listUsersBlocked.isEmpty()) {
                    Log.d("Usercito", "El array está vacío o su contenido es desconocido")
                } else {
                    for (elemento in listUsersBlocked) {
                        findBlockedUsers(elemento)
                    }
                    setupRecyclerView()
                }
            }
            result.onFailure {
                Log.d("UserNoFound", "User not found")
            }
        }
    }

    private fun setupRecyclerView() {
        if (listUsersBlockedFinded.isNotEmpty()) {
            adapter = BlockedUsersListAdapter(listUsersBlockedFinded)
            recyclerView.adapter = adapter
        } else {
            Log.d("Usercito", "La lista listUsersBlockedFinded está vacía")
        }
    }

    private fun findBlockedUsers(id: String) {
        FirebaseUtils().getDocumentById(playersNameCollection, id) { result ->
            result.onSuccess {
                listUsersBlockedFinded.add(
                    JugadoresDataModel(
                        it["nombre"].toString(),
                        it["apodo"].toString(),
                        it["id"].toString()
                    )
                )
                Log.d("Usercito", "Usuario bloqueado agregado: ${it["nombre"]}")

                if (listUsersBlockedFinded.size == listUsersBlocked.size) {
                    setupRecyclerView()
                }
            }
            result.onFailure {
                Log.d("UserNoFound", "User not found")
            }
        }
    }
}