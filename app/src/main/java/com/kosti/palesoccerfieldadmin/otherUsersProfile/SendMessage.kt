package com.kosti.palesoccerfieldadmin.otherUsersProfile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.kosti.palesoccerfieldadmin.R
import org.checkerframework.checker.units.qual.Length
import java.net.URLEncoder

class SendMessage : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private lateinit var userName: String
    private lateinit var userPhone: String
    private lateinit var messageEditText:EditText
    private lateinit var btnSend:Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_message)

        userName = intent.getStringExtra("userName").toString()
        userPhone = intent.getStringExtra("userPhone").toString()
        messageEditText = findViewById(R.id.et_Message_container)
        btnSend = findViewById(R.id.button_send_message)

        var title:TextView = findViewById(R.id.tvUserName)
        title.text = "Enviando mensaje a $userName"


        btnSend.setOnClickListener {
            sendMessage()
        }

        toolbar = findViewById(R.id.toolbarSendMessage)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }


    private fun sendMessage(){

        try {

            if(messageEditText.text.isEmpty()) {
                Toast.makeText(this,"Por favor escriba un mensaje", Toast.LENGTH_SHORT).show()
                return;

            } else if (!isValidPhoneNumber(userPhone.trim())) {
                Toast.makeText(this, "Número de teléfono no válido", Toast.LENGTH_SHORT).show()
                return
            } else {

                val intent = Intent(Intent.ACTION_VIEW)
                val url =  "https://api.whatsapp.com/send?phone=+506" + userPhone.trim() + "&text=" + URLEncoder.encode(
                    messageEditText.text.toString()
                )
                intent.setPackage("com.whatsapp")
                intent.data = Uri.parse(url)
                startActivity(intent)

            }
        } catch (e:Exception){
            Toast.makeText(this, ""+e.stackTraceToString(), Toast.LENGTH_SHORT).show()
        }


    }

    fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // El patrón acepta números de teléfono en el formato "+50612345678"
        val regexPattern = Regex("^\\d{8}\$")

        return regexPattern.matches(phoneNumber)
    }
}