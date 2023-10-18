package com.kosti.palesoccerfieldadmin.login

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.MainActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.CryptograpyPasswordClass
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils



class Login : AppCompatActivity() {

    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var btnLogin: Button
    lateinit var btnGoogle: ImageButton
    lateinit var progressBar: ProgressBar

    private lateinit var auth: FirebaseAuth

    /* ----------sign in with google ---------- start */

    private lateinit var oneTapClient: SignInClient
    private lateinit var signUpRequest: BeginSignInRequest
    private val REQ_ONE_TAP = 2
    private var showOneTapUI = true
    private lateinit var emailFromGoogle: String
    private lateinit var passwordFromGoogle: String
    /* ----------sign in with google ---------- end */
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

        /* ----------sign in with google ---------- start */

        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.web_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()


        var activityResultLauncher: ActivityResultLauncher<IntentSenderRequest> =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()
            ) {
                if (it.resultCode == Activity.RESULT_OK) {
                    try {
                        val credential = oneTapClient.getSignInCredentialFromIntent(it.data)
                        val idToken = credential.googleIdToken
                        when {
                            idToken != null -> {
                                emailFromGoogle = credential.id
                                passwordFromGoogle = credential.password.toString()
                                Log.d(TAG, "Got ID token.")
                                Toast.makeText(
                                    this@Login,
                                    "Email: $emailFromGoogle",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                Toast.makeText(
                                    this@Login,
                                    "Pass: $passwordFromGoogle",
                                    Toast.LENGTH_SHORT,
                                ).show()
                                toMain()
                            }

                            else -> {
                                // Shouldn't happen.
                                Log.d(TAG, "No ID token!")
                            }
                        }
                    } catch (e: ApiException) {
                        e.printStackTrace()
                    }
                }
            }
        /* ---------- sign in with google ---------- end */

        editTextEmail = findViewById(R.id.email)
        editTextPassword = findViewById(R.id.password)
        btnLogin = findViewById(R.id.btn_login)
        btnGoogle = findViewById(R.id.btn_google)
        progressBar = findViewById(R.id.progress_bar_login)

        auth = Firebase.auth

        btnGoogle.setOnClickListener {
            oneTapClient.beginSignIn(signUpRequest)
                .addOnSuccessListener(this) { result ->

                    val intentSenderRequest: IntentSenderRequest =
                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
                    activityResultLauncher.launch(intentSenderRequest)
                    /*
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0
                        )
                    }catch (e: ApiException){
                        e.printStackTrace()
                    }*/


                }
                .addOnFailureListener(this) { e ->
                    // No Google Accounts found. Just continue presenting the signed-out UI.

                }

            Toast.makeText(
                this@Login,
                "Boton de Google.",
                Toast.LENGTH_SHORT,
            ).show()
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

            autentificacion(email,password)
        }
    }


    fun toMain() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun autentificacion(email:String, password:String){
        val cryptClass = CryptograpyPasswordClass()

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
                                    if (elem["rol"] == "Administrador") {
                                        val passDecrypt = cryptClass.decrypt(elem["contrasena"].toString())
                                        if (passDecrypt == password) {
                                            Toast.makeText(
                                                this@Login,
                                                "Authentication Successful. Usuario: ${elem["nombre"]}",
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                            toMain()
                                        } else {
                                            progressBar.visibility = View.GONE
                                            Toast.makeText(
                                                this@Login,
                                                "Contraseña invalida.",
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
                } else {
                    // If sign in fails, display a message to the user.

                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                    progressBar.visibility = View.GONE
                }
            }
    }
}