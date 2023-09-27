package com.kosti.palesoccerfieldadmin.registro

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.MainActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.login.Login
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class Register : AppCompatActivity() {

    lateinit var editTextNombre: EditText
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var textViewToLogin: TextView
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
        textViewToLogin = findViewById(R.id.textViewToLogin)

        btnRegister.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            val nombre: String = editTextNombre.text.toString()
            val email: String = editTextEmail.text.toString()

            val password: String = editTextPassword.text.toString()

            auth = Firebase.auth

            textViewToLogin.setOnClickListener {
                toLogin()
            }
            if (TextUtils.isEmpty(nombre)) {
                editTextNombre.error = "Ingrese su nombre"
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

            if(!isValidEmail(email.trim())) {
                editTextEmail.error = "Correo invalido"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if(password.length < 6) {
                editTextPassword.error = ""
                progressBar.visibility = View.GONE
                Toast.makeText(
                    this@Register,
                    "La contraseña debe tener al menos 6 caracteres.",
                    Toast.LENGTH_SHORT,
                ).show()
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
                        createUser(nombre, email, password)
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

    private fun createUser(nombre: String, email: String, password: String) {
        val jugadorPorDefecto: HashMap<String, Any> = HashMap<String, Any>()
        val bloqueosList = mutableListOf<String>()
        val posicionesList = mutableListOf<String>()

        val date = Timestamp.now()

        jugadorPorDefecto["apodo"] = nombre
        jugadorPorDefecto["bloqueos"] = bloqueosList
        jugadorPorDefecto["clasificacion"] = "bueno"
        jugadorPorDefecto["contrasena"] = password
        jugadorPorDefecto["correo"] = email
        jugadorPorDefecto["estado"] = true
        jugadorPorDefecto["fecha_nacimiento"] = date
        jugadorPorDefecto["nombre"] = nombre
        jugadorPorDefecto["posiciones"] = posicionesList
        jugadorPorDefecto["rol"] = "admin"
        jugadorPorDefecto["telefono"] = "00000000"

        FirebaseUtils().createDocument("jugadores", jugadorPorDefecto)
    }

    private fun toLogin() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, Login::class.java)
        startActivity(intent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }
}