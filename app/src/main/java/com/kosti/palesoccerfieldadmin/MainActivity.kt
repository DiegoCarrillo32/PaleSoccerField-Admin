package com.kosti.palesoccerfieldadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
<<<<<<< HEAD
import android.widget.ImageButton
import com.kosti.palesoccerfieldadmin.userAdminProfile.EditUserData
=======
import com.kosti.palesoccerfieldadmin.aproveUsers.AproveUsers
import com.kosti.palesoccerfieldadmin.registro.Register
>>>>>>> develop
import com.kosti.palesoccerfieldadmin.userListPackage.UserList

class MainActivity : AppCompatActivity() {

    lateinit var btnRegistrarUsuario: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRegistrarUsuario = findViewById(R.id.registrarUsuarios)

<<<<<<< HEAD
        var btnNavegar = findViewById<Button>(R.id.btnBuscarUsuarios)
        var btnCuenta = findViewById<Button>(R.id.btnCuenta)

=======
        val btnNavegar = findViewById<Button>(R.id.btnBuscarUsuarios)
>>>>>>> develop
        btnNavegar.setOnClickListener {
            val intent = Intent(this, UserList::class.java)
            startActivity(intent)
        }

<<<<<<< HEAD
        btnCuenta.setOnClickListener {
            val intent = Intent(this, EditUserData::class.java)
            startActivity(intent)
        }

=======
        val btnNavegarAprobar = findViewById<Button>(R.id.btnAproveUsers)
        btnNavegarAprobar.setOnClickListener {
            val intent = Intent(this, AproveUsers::class.java)
            startActivity(intent)
        }

        btnRegistrarUsuario.setOnClickListener{
            toRegister()
        }



    }

    fun toRegister() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
        finish()
>>>>>>> develop
    }
}