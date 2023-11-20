package com.kosti.palesoccerfieldadmin.aproveUsers

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class AproveUsers : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: MutableList<JugadoresDataModel>
    private lateinit var adapter: AproveUserListAdapter
    private lateinit var filteredList: MutableList<JugadoresDataModel>
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var userListProgressBar: ProgressBar
    private lateinit var toolbar: Toolbar


    private val playersNameCollection = "jugadores"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aprove_users)
        userRecyclerView = findViewById(R.id.userListRecycler)
        userListProgressBar = findViewById(R.id.progressBar)
        toolbar = findViewById(R.id.toolbarAproveUsers)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutUserList)
        filteredList = mutableListOf()
        userList = mutableListOf()
        fetchDataFromFirebase()

        // Add the LinearLayoutManager to  RecyclerView
        val layoutManager = LinearLayoutManager(this)
        userRecyclerView.layoutManager = layoutManager

        swipeRefreshLayout.setOnRefreshListener {
            fetchDataFromFirebase()
            swipeRefreshLayout.isRefreshing = false
        }

        toolbar.setNavigationOnClickListener { onBackPressed() }

    }
    private fun fetchDataFromFirebase() {
        Snackbar.make(userRecyclerView, "Buscando la informacion de los jugadores", Snackbar.LENGTH_LONG)
            .show()
        userList.clear()
        userListProgressBar.visibility = View.VISIBLE
        FirebaseUtils().readCollectionStateFalse(playersNameCollection, "estado") { result ->
            result.onSuccess {
                for (user in it){
                    if(user["posiciones"] == null ||
                        user["nombre"] == null ||
                        user["apodo"] == null ||
                        user["id"] == null
                        ){
                        Toast.makeText(this, "Usuario con datos erroneos", Toast.LENGTH_LONG).show()
                        continue
                    }
                    userList.add(
                        JugadoresDataModel(
                            user["nombre"].toString(),
                            user["apodo"].toString(),
                            user["id"].toString()
                        )
                    )
                }
                adapter = AproveUserListAdapter(this, userList)
                userRecyclerView.adapter = adapter
                userListProgressBar.visibility = View.GONE
            }
            result.onFailure {
                Snackbar.make(userRecyclerView, "Error al cargar los datos", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }




}