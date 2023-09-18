package com.kosti.palesoccerfieldadmin.UserListPackage

import UserListAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
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
    private lateinit var userList: List<UserListDataModel>
    private lateinit var adapter: UserListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)

        //  variables
        userListView = findViewById(R.id.users_list)
        userList = listOf(
            UserListDataModel("Diego"),
            UserListDataModel("Mariana"),
            UserListDataModel("Felipe"),
        )
        adapter = UserListAdapter(this, userList)
        userListView.adapter = adapter



    }
}