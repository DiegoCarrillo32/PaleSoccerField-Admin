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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.otherUsersProfile.ProfileScreen
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils


private const val ADMIN_ROLE = "Administrador"
private const val PLAYER_ROLE = "Jugador"
private const val ALL_FILTER = "Todos"
private const val PLAYER_NAME_COLLECTION = "jugadores"

class UserList : AppCompatActivity(), ProfileScreen.OnDismissListener {

    private lateinit var userListView:ListView
    private lateinit var userList: MutableList<JugadoresDataModel>
    private lateinit var adapter: UserListAdapter
    private lateinit var filteredList: MutableList<JugadoresDataModel>
    private lateinit var userListProgressBar: ProgressBar
    private lateinit var toolbar: Toolbar
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private var ratesList = listOf(ALL_FILTER, "Malo", "Bueno", "Regular")
    private var positionList = listOf(ALL_FILTER,"Defensa", "Arquero", "Medio campista", "Delantero")
    private val rolesList = listOf(ALL_FILTER, PLAYER_ROLE, ADMIN_ROLE)
    private var selectedRate = ALL_FILTER
    private var selectedPosition = ALL_FILTER
    private var selectedRole = ALL_FILTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        //  variables
        userListView = findViewById(R.id.users_list)
        userListProgressBar = findViewById(R.id.userListProgressBar)
        toolbar = findViewById(R.id.toolbarUserList)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutUserList)
        filteredList = mutableListOf()
        userList = mutableListOf()
        adapter = UserListAdapter(this, userList)

        fetchDataFromFirebase()
        initSearchWidget()
        setupSpinners()
        filteredList = userList
        toolbar.setNavigationOnClickListener { onBackPressed() }

        swipeRefreshLayout.setOnRefreshListener {
            fetchDataFromFirebase()
            filteredList = userList
            swipeRefreshLayout.isRefreshing = false
        }

        userListView.onItemClickListener = object: AdapterView.OnItemClickListener {
            override fun onItemClick(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val fragmentProfileScreen = ProfileScreen()

                fragmentProfileScreen.setOnDismissListener(this@UserList)
                var data = Bundle()
                data.putString("name", filteredList[p2].Name)
                data.putString("classification", filteredList[p2].Clasification)
                data.putString("nickname", filteredList[p2].Nickname)

                data.putLong("age", (filteredList[p2].Age.toLong()))
                data.putString("phone", filteredList[p2].Phone)
                data.putStringArrayList("positions", filteredList[p2].Positions as ArrayList<String>)
                data.putString("id", filteredList[p2].Id)
                fragmentProfileScreen.arguments = data

                fragmentProfileScreen.show(supportFragmentManager, "ProfileScreen")
            }
        }

    }

    private fun fetchDataFromFirebase() {
        Snackbar.make(userListView, "Buscando la informacion de los jugadores", Snackbar.LENGTH_LONG)
            .show()
        userList.clear()
        userListProgressBar.visibility = View.VISIBLE
        FirebaseUtils().readCollection(PLAYER_NAME_COLLECTION) { result ->
            result.onSuccess {
                for (user in it){
                    if(user["posiciones"] == null ||
                        user["nombre"] == null ||
                        user["clasificacion"] == null ||
                        user["apodo"] == null ||
                        user["telefono"] == null ||
                        user["id"] == null ||
                        user["fecha_nacimiento"] == null){
                        Toast.makeText(this, "Usuario con datos erroneos", Toast.LENGTH_LONG).show()
                        continue
                    }

                    if(user["estado"] == false) {
                        continue
                    }
                    var player = JugadoresDataModel(
                        user["nombre"].toString(),
                        user["clasificacion"].toString(),
                        user["posiciones"] as MutableList<String> ,
                        user["apodo"].toString(),
                        user["telefono"].toString(),
                        user["fecha_nacimiento"].toString(),
                        user["id"].toString(),
                        )
                    player.Role = user["rol"].toString()
                    userList.add(
                        player
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
        val roleSpinner = findViewById<Spinner>(R.id.roleSpinner)

        val roleAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, rolesList)
        val rateAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, ratesList)
        val positionAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, positionList)

        roleAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        rateAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        positionAdapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        roleSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedRole = rolesList[p2]
                filterList()
                adapter.notifyDataSetChanged()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }

        rateSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedRate = ratesList[p2]
                filterList()
                adapter.notifyDataSetChanged()


            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        positionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                selectedPosition = positionList[p2]
                filterList()
                adapter.notifyDataSetChanged()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }

        rateSpinner.adapter = rateAdapter;
        positionSpinner.adapter = positionAdapter;
        roleSpinner.adapter = roleAdapter;
    }
    fun filterList(){

        if(selectedRole != ALL_FILTER){
            filteredList = userList.filter {
                it.Role.equals(selectedRole, ignoreCase = true)
            } as MutableList<JugadoresDataModel>
            userListView.adapter = UserListAdapter(applicationContext, filteredList);
            adapter.notifyDataSetChanged()
            return
        }
        if(selectedRate == ALL_FILTER && selectedPosition == ALL_FILTER){
            userListView.adapter = UserListAdapter(applicationContext, userList);
            adapter.notifyDataSetChanged()
            return
        }
        if(selectedRate == ALL_FILTER){
            filteredList = userList.filter {
                it.Positions.contains(selectedPosition)
            } as MutableList<JugadoresDataModel>
            userListView.adapter = UserListAdapter(applicationContext, filteredList);
            adapter.notifyDataSetChanged()
            return
        }
        if(selectedPosition == ALL_FILTER){
            filteredList = userList.filter {
                it.Clasification.equals(selectedRate, ignoreCase = true)
            } as MutableList<JugadoresDataModel>
            userListView.adapter = UserListAdapter(applicationContext, filteredList);
            adapter.notifyDataSetChanged()
            return
        }

        filteredList = userList.filter {
            it.Clasification.equals(selectedRate, ignoreCase = true) ||
                    it.Positions.contains(selectedPosition)
        } as MutableList<JugadoresDataModel>
        userListView.adapter = UserListAdapter(applicationContext, filteredList);
        adapter.notifyDataSetChanged()

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
                adapter.notifyDataSetChanged()

                return false;
            }
        } )

    }

    override fun onDismissOnActivity() {
        Toast.makeText(this, "Se actualizo la informacion", Toast.LENGTH_LONG).show()
        fetchDataFromFirebase()

    }
}