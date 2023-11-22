package com.kosti.palesoccerfieldadmin

import com.kosti.palesoccerfieldadmin.SpecialEvents.SpecialEvents
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.kosti.palesoccerfieldadmin.approveReview.AprobarResenia
import com.kosti.palesoccerfieldadmin.reservations.aproveReservations.AproveReservations
import com.kosti.palesoccerfieldadmin.aproveUsers.AproveUsers
import com.kosti.palesoccerfieldadmin.deletePassword.DeleteAccount
import com.kosti.palesoccerfieldadmin.login.Login
import com.kosti.palesoccerfieldadmin.macthBookings.HistoryBookings
import com.kosti.palesoccerfieldadmin.macthBookings.MatchBookings
import com.kosti.palesoccerfieldadmin.promotions.Promotions
import com.kosti.palesoccerfieldadmin.registro.Register
import com.kosti.palesoccerfieldadmin.reservations.Reservations
import com.kosti.palesoccerfieldadmin.schedules.Schedules
import com.kosti.palesoccerfieldadmin.userAdminProfile.EditUserData
import com.kosti.palesoccerfieldadmin.userListPackage.UserList
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class MainActivity : AppCompatActivity() {

    lateinit var btnRegistrarUsuario: Button
    lateinit var btnGestionDeReservas: Button
    lateinit var btnEventosEspeciales: Button
    lateinit var btnLogOut: Button
    lateinit var usuarioNombreTV:TextView
    lateinit var usuarioCorreoTV: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val userId = intent.getStringExtra("userId").toString()
        val userName = intent.getStringExtra("userName").toString()
        val userMail = intent.getStringExtra("userMail").toString()
        btnRegistrarUsuario = findViewById(R.id.registrarUsuarios)
        btnGestionDeReservas = findViewById(R.id.btnGestionDeReservas)
        btnEventosEspeciales = findViewById(R.id.btnSpecialEvents)

        usuarioNombreTV = findViewById(R.id.nombreUsuarioTV)
        usuarioNombreTV.text = userName
        usuarioCorreoTV = findViewById(R.id.correoUsuarioTV)
        usuarioCorreoTV.text = userMail

        manageNotifications()


        val btnBooking = findViewById<Button>(R.id.btnReservas)
        btnBooking.setOnClickListener {
            val intent = Intent(this, MatchBookings::class.java)
            startActivity(intent)
        }

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

        val btnApproveReviews = findViewById<Button>(R.id.btnApproveReviews)
        btnApproveReviews.setOnClickListener {
            val intent = Intent(this, AprobarResenia::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        val btnPromotionManagement = findViewById<Button>(R.id.btnPromotionManagement)
        btnPromotionManagement.setOnClickListener {
            val intent = Intent(this, Promotions::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }

        val btnHistorialReservas = findViewById<Button>(R.id.btnHistorialReservas)
        btnHistorialReservas.setOnClickListener {
            val intent = Intent(this, HistoryBookings::class.java)
            intent.putExtra("userId", userId)
            startActivity(intent)
        }


        btnRegistrarUsuario.setOnClickListener{
            toRegister()
        }

        btnGestionDeReservas.setOnClickListener {
            toGestionDeReservas()
        }

        btnEventosEspeciales.setOnClickListener {
            toSpecialEvents()
        }

        btnLogOut = findViewById(R.id.btnLogOut)
        btnLogOut.setOnClickListener {
            // TODO: Hacer el logout en firebase o lo que sea
            toLogin()
        }
    }

    private fun toSpecialEvents() {
        val intent = Intent(this, SpecialEvents::class.java)
        startActivity(intent)
    }

    private fun manageNotifications() {
        FirebaseUtils().checkStatusAttr("jugadores") { success ->
            if(success){
                showNotificationDialog(R.layout.dialog_visitar_aprobar_usuarios)
            }
        }
    }
    private fun showNotificationDialog(dialogId: Int){
        val dialogView = LayoutInflater.from(this).inflate(dialogId, null)
        val builder = AlertDialog.Builder(this)
        builder.setView(dialogView)
        val dialog = builder.create()
        if(dialogId == R.layout.dialog_visitar_aprobar_usuarios){
            val btnCancelar = dialogView.findViewById<Button>(R.id.btn_dialog_cancelar)
            btnCancelar.setOnClickListener {
                dialog.dismiss()
            }

            val btnVisitar = dialogView.findViewById<Button>(R.id.btn_dialog_visitar)
            btnVisitar.setOnClickListener {
                val intent = Intent(this, AproveUsers::class.java)
                startActivity(intent)
                dialog.dismiss()
            }
        }
        dialog.show()

    }
    fun toGestionDeReservas() {
        val intent = Intent(this, Reservations::class.java)
        startActivity(intent)
        //finish()
    }

    fun toRegister() {
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
        //finish()
    }

    fun toLogin() {
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}