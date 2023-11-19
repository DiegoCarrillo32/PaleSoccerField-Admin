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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.MainActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils



class Login : AppCompatActivity() {

    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnLogin: Button
    lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btn_login)
        progressBar = findViewById(R.id.progress_bar_login)

        auth = Firebase.auth


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

            autentificacion(email,password)
        }
    }


    fun toMain(userId: String, userName: String, userMail:String) {
        progressBar.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("userId", userId)
        intent.putExtra("userName", userName)
        intent.putExtra("userMail", userMail)
        println("El id de usuario es: $userId")
        startActivity(intent)
        finish()
    }

    fun autentificacion(email:String, password:String){
        //val cryptClass = CryptograpyPasswordClass()

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Verificar que exista e ingresar
                    FirebaseUtils().getCollectionByProperty(
                        "jugadores",
                        "correo",
                        email
                    ) { result ->
                        result.onSuccess {
                            if (it.isNotEmpty()) {
                                for (elem in it) {
                                    // que sea usuario y que sea admin
                                    if (elem["rol"] == "Administrador" && elem["UID"] == auth.currentUser?.uid ) {
                                        toMain(elem["id"].toString(),
                                            elem["nombre"].toString(),
                                            elem["correo"].toString())
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
                                "Acceso bloqueado a la base de datos. $errorMessage",
                                Toast.LENGTH_SHORT,
                            ).show()
                        }
                    }
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        baseContext,
                        "Autentificacion fallida.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }
    }
}