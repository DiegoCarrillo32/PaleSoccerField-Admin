package com.kosti.palesoccerfieldadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.kosti.palesoccerfieldadmin.aproveUsers.AproveUsers
import com.kosti.palesoccerfieldadmin.registro.Register
import com.kosti.palesoccerfieldadmin.userListPackage.UserList

class MainActivity : AppCompatActivity() {

    lateinit var btnRegistrarUsuario: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRegistrarUsuario = findViewById(R.id.registrarUsuarios)

        val btnNavegar = findViewById<Button>(R.id.btnBuscarUsuarios)
        btnNavegar.setOnClickListener {
            val intent = Intent(this, UserList::class.java)
            startActivity(intent)
        }

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
    }
}