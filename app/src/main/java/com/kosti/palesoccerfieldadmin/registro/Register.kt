package com.kosti.palesoccerfieldadmin.registro

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.login.Login

class Register : AppCompatActivity() {

    lateinit var editTextNombre: EditText
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnRegister: Button

    lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        editTextNombre = findViewById(R.id.nombreRegister)
        editTextEmail = findViewById(R.id.emailRegister)
        editTextPassword = findViewById(R.id.passwordRegister)
        btnRegister = findViewById(R.id.btn_register)
        progressBar = findViewById(R.id.progress_bar_register)

        btnRegister.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            val nombre: String = editTextNombre.text.toString()
            val email: String = editTextEmail.text.toString()
            val password: String = editTextPassword.text.toString()

            auth = Firebase.auth

            if (TextUtils.isEmpty(nombre)) {
                editTextEmail.error = "Ingrese su nombre"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }
            if (TextUtils.isEmpty(email)) {
                editTextEmail.error = "Ingrese su correo"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.error = "Se requiere una contraseña"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            this@Register,
                            "Authentication successful.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        toLogin()
                    } else {
                        // If sign in fails, display a message to the user.

                        Toast.makeText(
                            this@Register,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()

                    }
                }
        }
    }

    private fun toLogin() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }
}