package com.kosti.palesoccerfieldadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kosti.palesoccerfieldadmin.aproveUsers.AproveUsers
import com.kosti.palesoccerfieldadmin.userListPackage.UserList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var btnNavegar = findViewById<Button>(R.id.btnBuscarUsuarios)
        btnNavegar.setOnClickListener {
            val intent = Intent(this, UserList::class.java)
            startActivity(intent)
        }

        val btnNavegarAprobar = findViewById<Button>(R.id.btnAproveUsers)
        btnNavegarAprobar.setOnClickListener {
            val intent = Intent(this, AproveUsers::class.java)
            startActivity(intent)
        }

    }
}