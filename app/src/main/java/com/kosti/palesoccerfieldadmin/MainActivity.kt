package com.kosti.palesoccerfieldadmin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.kosti.palesoccerfieldadmin.reservations.aproveReservations.AproveReservations
import com.kosti.palesoccerfieldadmin.aproveUsers.AproveUsers
import com.kosti.palesoccerfieldadmin.deletePassword.DeleteAccount
import com.kosti.palesoccerfieldadmin.login.Login
import com.kosti.palesoccerfieldadmin.registro.Register
import com.kosti.palesoccerfieldadmin.schedules.Schedules
import com.kosti.palesoccerfieldadmin.userAdminProfile.EditUserData
import com.kosti.palesoccerfieldadmin.userListPackage.UserList

class MainActivity : AppCompatActivity() {

    lateinit var btnRegistrarUsuario: Button
    lateinit var btnLogOut: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnRegistrarUsuario = findViewById(R.id.registrarUsuarios)

        val userId = intent.getStringExtra("userId").toString()


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

        val btnCuenta = findViewById<Button>(R.id.btnCuenta)
        btnCuenta.setOnClickListener {
            val intent = Intent(this, EditUserData::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        val btnDeleteAccount = findViewById<Button>(R.id.btnSend)
        btnDeleteAccount.setOnClickListener {
            val intent = Intent(this, DeleteAccount::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        val btnRegisterSchedule = findViewById<Button>(R.id.registerSchedule)
        btnRegisterSchedule.setOnClickListener {
            val intent = Intent(this, Schedules::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        val btnAproveReservation = findViewById<Button>(R.id.btnAproveReservations)
        btnAproveReservation.setOnClickListener {
            val intent = Intent(this, AproveReservations::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }



        btnRegistrarUsuario.setOnClickListener{
            toRegister()
        }

        btnLogOut = findViewById(R.id.btnLogOut)
        btnLogOut.setOnClickListener {
            // TODO: Hacer el logout en firebase o lo que sea
            toLogin()
        }

    }

    fun toRegister() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
        finish()
    }

    fun toLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}