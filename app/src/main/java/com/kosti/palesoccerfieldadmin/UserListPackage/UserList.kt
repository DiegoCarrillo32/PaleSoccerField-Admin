package com.kosti.palesoccerfieldadmin.UserListPackage

import UserListAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.widget.SearchView
import com.kosti.palesoccerfieldadmin.R

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
    private lateinit var userList: MutableList<UserListDataModel>
    private lateinit var adapter: UserListAdapter
    private lateinit var filteredList: MutableList<UserListDataModel>
    private var ratesList = listOf<String>("Todos","Malo", "Bueno", "Regular")
    private var positionList = listOf<String>("Todos","Atacante", "Defensor")
    private var selectedRate = "Todos"
    private var selectedPosition = "Todos"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        //  variables
        userListView = findViewById(R.id.users_list)
        userList = mutableListOf(
            UserListDataModel("Diego", "Bueno", "Defensor", "Kosti"),
            UserListDataModel("Mariana", "Malo", "Atacante", "Nana"),
            UserListDataModel("Felipe", "Regular", "Defensor", "Pipe"),
        )
        filteredList = mutableListOf()

        adapter = UserListAdapter(this, userList)
        userListView.adapter = adapter
        initSearchWidget()
        setupSpinners()

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
                .filter { it.Position == selectedPosition } as MutableList<UserListDataModel>
            userListView.adapter = UserListAdapter(applicationContext, filteredList);
            return
        }
        if(selectedPosition == "Todos"){
            filteredList = userList
                .filter { it.Clasification == selectedRate } as MutableList<UserListDataModel>
            userListView.adapter = UserListAdapter(applicationContext, filteredList);
            return
        }
        filteredList = userList
            .filter { it.Clasification == selectedRate }
            .filter { it.Position == selectedPosition } as MutableList<UserListDataModel>
        userListView.adapter = UserListAdapter(applicationContext, filteredList);
    }

    private fun initSearchWidget(){
        val searchView: SearchView = findViewById(R.id.searchViewUserList)
        searchView.setOnQueryTextListener( object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false;
            }

            override fun onQueryTextChange(p0: String?): Boolean {


                val filteredUsers = mutableListOf<UserListDataModel>()
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