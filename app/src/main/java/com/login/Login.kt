package com.login

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

class Login : AppCompatActivity() {

    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnLogin: Button
    private lateinit var auth: FirebaseAuth
    lateinit var progressBar: ProgressBar

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            toMain()
        }
    }
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
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(password)) {
                editTextPassword.error = "Se requiere una contraseña"
                return@setOnClickListener
            }

            // Verificar que exista e ingresar
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    progressBar.visibility = View.GONE
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Toast.makeText(
                            this@Login,
                            "Authentication Successful.",
                            Toast.LENGTH_SHORT,
                        ).show()
                        toMain()
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            this@Login,
                            "Authentication failed.",
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                }
        }
    }

    fun toMain() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}


