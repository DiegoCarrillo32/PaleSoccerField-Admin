package com.kosti.palesoccerfieldadmin.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.MainActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.registro.Register
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class Login : AppCompatActivity() {

    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnLogin: Button
    lateinit var textViewCreateAccount: TextView
    lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    /*
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            toMain()
        }
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        textViewCreateAccount = findViewById(R.id.textViewCreateAccount)
        btnLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar_login)

        auth = Firebase.auth

        textViewCreateAccount.setOnClickListener {
            toRegister()
        }

        btnLogin.setOnClickListener {
            progressBar.visibility = View.VISIBLE
            val email: String = editTextEmail.text.toString()
            val password: String = editTextPassword.text.toString()

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

            // Verificar que exista e ingresar

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        Toast.makeText(
                            this@Login,
                            "Authentication Successful.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        getUser()
                        toMain()

                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@Login,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    fun getUser() {

        FirebaseUtils().filterBy("jugadores", "correo", auth.currentUser?.email.toString())
        { result ->
            result.onSuccess {
                if (it.isNotEmpty()) {
                    for (elem in it) {
                        Toast.makeText(
                            this@Login,
                            "Authentication Successful FirebaseUtils. usuario: ${elem["nombre"]}",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
            }
            result.onFailure { exception ->
                val errorMessage = exception.message ?: "Error desconocido"
                Toast.makeText(
                    this@Login,
                    "Authentication failed FirebaseUtils. $errorMessage",
                    Toast.LENGTH_SHORT,
                ).show()
            }
        }
    }

    fun toMain() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    fun toRegister() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, Register::class.java)
        startActivity(intent)
        finish()
    }
}


