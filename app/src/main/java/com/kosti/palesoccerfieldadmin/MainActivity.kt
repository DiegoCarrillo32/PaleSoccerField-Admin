package com.kosti.palesoccerfieldadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import com.kosti.palesoccerfieldadmin.userAdminProfile.EditUserData
import com.kosti.palesoccerfieldadmin.userListPackage.UserList

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        var btnNavegar = findViewById<Button>(R.id.btnBuscarUsuarios)
        var btnCuenta = findViewById<Button>(R.id.btnCuenta)

        btnNavegar.setOnClickListener {
            val intent = Intent(this, UserList::class.java)
            startActivity(intent)
        }

        btnCuenta.setOnClickListener {
            val intent = Intent(this, EditUserData::class.java)
            startActivity(intent)
        }

    }
}