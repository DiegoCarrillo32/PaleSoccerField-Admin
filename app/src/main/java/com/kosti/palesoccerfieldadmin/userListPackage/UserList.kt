package com.kosti.palesoccerfieldadmin.userListPackage

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.userProfile.ProfileScreen
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Date

/*
* datos de un usuario
*
* apodo
* clasificacion
* bloqueos
* contrasena
* correo
* estado
* fecha_nacimiento
* nombre
* posiciones
* rol
* telefono
* */
class UserList : AppCompatActivity() {
    private lateinit var userListView:ListView
    private lateinit var userList: MutableList<JugadoresDataModel>
    private lateinit var adapter: UserListAdapter
    private lateinit var filteredList: MutableList<JugadoresDataModel>
    private lateinit var userListProgressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private var ratesList = listOf<String>("Todos", "Malo", "Bueno", "Regular")
    private var positionList = listOf<String>("Todos","Defensa", "Arquero", "Medio campista", "Delantero")
    private var selectedRate = "Todos"
    private var selectedPosition = "Todos"

    private val playersNameCollection = "jugadores"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        //  variables
        userListView = findViewById(R.id.users_list)
        userListProgressBar = findViewById(R.id.userListProgressBar)
        toolbar = findViewById(R.id.toolbarUserList)
        filteredList = mutableListOf()
        userList = mutableListOf()
        fetchDataFromFirebase()

        initSearchWidget()
        setupSpinners()

        toolbar.setNavigationOnClickListener { onBackPressed() }

        userListView.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {

                var data = Bundle()
                data.putString("name", userList[p2].Name)
                data.putString("classification", userList[p2].Clasification)
                data.putString("nickname", userList[p2].Nickname)
                // Calculate age from Timestamp
                data.putString("age", ((Date().time - userList[p2].Age.toDate().time) / (1000 * 60 * 60 * 24 * 365)).toInt().toString())
                data.putString("phone", userList[p2].Phone)
                data.putStringArrayList("positions", userList[p2].Positions as ArrayList<String>)
                var fragmentProfileScreen = ProfileScreen()
                fragmentProfileScreen.arguments = data
                fragmentProfileScreen.show(supportFragmentManager, "ProfileScreen")
            }

        }

    }

    private fun fetchDataFromFirebase() {
        Snackbar.make(userListView, "Buscando la informacion de los jugadores", Snackbar.LENGTH_LONG)
            .show()
        userListProgressBar.visibility = View.VISIBLE
        FirebaseUtils().readCollection(playersNameCollection) { result ->
            result.onSuccess {
                for (user in it){
                    if(user["posiciones"] == null ||
                        user["nombre"] == null ||
                        user["clasificacion"] == null ||
                        user["apodo"] == null ||
                        user["telefono"] == null ||
                        user["fecha_nacimiento"] == null){
                        Toast.makeText(this, "Usuario con datos erroneos", Toast.LENGTH_LONG).show()
                        continue
                    }
                    userList.add(
                        JugadoresDataModel(
                            user["nombre"].toString(),
                        user["clasificacion"].toString(),
                        user["posiciones"] as MutableList<String> ,
                        user["apodo"].toString(),
                        user["telefono"].toString(),
                        user["fecha_nacimiento"] as Timestamp,
                    )


                    )
                }
                adapter = UserListAdapter(this, userList)
                userListView.adapter = adapter
                userListProgressBar.visibility = View.GONE
            }
            result.onFailure {
                Snackbar.make(userListView, "Error al cargar los datos", Snackbar.LENGTH_LONG)
                    .show()
            }
        }
    }

    private fun setupSpinners() {
        val rateSpinner = findViewById<Spinner>(R.id.rateSpinner)
        val positionSpinner = findViewById<Spinner>(R.id.positionSpinner)
        val rateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratesList)
        val positionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, positionList)
        rateAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        positionAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        rateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedRate = ratesList[p2]
                filterList()

            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }
        positionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedPosition = positionList[p2]
                filterList()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }

        }

        rateSpinner.adapter = rateAdapter;
        positionSpinner.adapter = positionAdapter;
    }
    fun filterList(){
        if(selectedRate == "Todos" && selectedPosition == "Todos"){
            userListView.adapter = UserListAdapter(applicationContext, userList);
            return
        }
        if(selectedRate == "Todos"){
            filteredList = userList
                .filter { it.Positions.contains(selectedPosition)  } as MutableList<JugadoresDataModel>
            userListView.adapter = UserListAdapter(applicationContext, filteredList);
            return
        }
        if(selectedPosition == "Todos"){
            filteredList = userList
                .filter { it.Clasification.toLowerCase() == selectedRate.toLowerCase() } as MutableList<JugadoresDataModel>
            userListView.adapter = UserListAdapter(applicationContext, filteredList);
            return
        }
        filteredList = userList
            .filter { it.Clasification.toLowerCase() == selectedRate.toLowerCase() }
            .filter { it.Positions.contains(selectedPosition)  } as MutableList<JugadoresDataModel>
        userListView.adapter = UserListAdapter(applicationContext, filteredList);
    }

    private fun initSearchWidget(){
        val searchView: SearchView = findViewById(R.id.searchViewUserList)
        searchView.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(p0: String?): Boolean {


                val filteredUsers = mutableListOf<JugadoresDataModel>()
                if(selectedRate != "Todos" || selectedPosition != "Todos"){
                    for (user in filteredList){
                        if (p0 != null) {
                            if(user.Name.toLowerCase().contains(p0.toLowerCase(), ignoreCase = true) || user.Nickname.toLowerCase().contains(p0.toLowerCase(), ignoreCase = true) ){
                                filteredUsers.add(user)
                            }
                        }
                    }
                    userListView.adapter = UserListAdapter(applicationContext, filteredUsers);
                    return false
                }
                for(user in userList){
                    if (p0 != null) {
                        if(user.Name.toLowerCase().contains(p0.toLowerCase(), ignoreCase = true) || user.Nickname.toLowerCase().contains(p0.toLowerCase(), ignoreCase = true) ){
                            filteredUsers.add(user)
                        }
                    }
                }

                userListView.adapter = UserListAdapter(applicationContext, filteredUsers);
                return false;

            }

        } )

    }
}