package com.kosti.palesoccerfieldadmin.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.kosti.palesoccerfieldadmin.MainActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.UserViewModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils


class Login : AppCompatActivity() {

    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnLogin: Button
    lateinit var progressBar: ProgressBar
    lateinit var currentUserID: String

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
        btnLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar_login)
        var userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

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
            FirebaseUtils().getCollectionByProperty("jugadores", "correo", email) { result ->
                result.onSuccess {
                    if (it.isNotEmpty()) {
                        for (elem in it) {
                            // que sea usuario y que sea admin
                            if (elem["rol"] == "Administrador") {
                                if (elem["contrasena"] == password) {
                                    Toast.makeText(
                                        this@Login,
                                        "Authentication Successful. Usuario: ${elem["nombre"]}",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                    currentUserID = elem["id"].toString()
                                    userViewModel.setUserId(currentUserID)

                                    toMain()
                                } else {
                                    progressBar.visibility = View.GONE
                                    Toast.makeText(
                                        this@Login,
                                        "Contraseña invalida",
                                        Toast.LENGTH_SHORT,
                                    ).show()
                                }

                            } else {
                                progressBar.visibility = View.GONE
                                Toast.makeText(
                                    this@Login,
                                    "Acceso denegado.",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    } else {
                        progressBar.visibility = View.GONE
                        Toast.makeText(
                            this@Login,
                            "El usuario no existe.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
                result.onFailure { exception ->
                    val errorMessage = exception.message ?: "Error desconocido"
                    progressBar.visibility = View.GONE
                    Toast.makeText(
                        this@Login,
                        "Authentication failed FirebaseUtils. $errorMessage",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
        }
    }

    fun toMain() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}


