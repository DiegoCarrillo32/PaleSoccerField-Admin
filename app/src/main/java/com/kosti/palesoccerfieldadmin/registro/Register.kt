package com.kosti.palesoccerfieldadmin.registro

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kosti.palesoccerfieldadmin.MainActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.login.Login
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Calendar
import java.time.LocalDate
import java.time.ZoneOffset
import java.util.Locale

class Register : AppCompatActivity() {

    lateinit var editTextNombre: EditText
    lateinit var editTextApodo: EditText
    lateinit var editTextTelefono: EditText
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var textViewToLogin: TextView

    lateinit var textViewFechanacimiento: TextView
    lateinit var fechaCovertida: String

    lateinit var btnRegister: Button
    lateinit var progressBar: ProgressBar

    lateinit var radioGroupUserType: RadioGroup
    lateinit var radioButtonUserType: RadioButton
    lateinit var rol:String

    lateinit var radioGroupClasificacion: RadioGroup
    lateinit var radioButtonClasificacion: RadioButton
    lateinit var clasificacion:String

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
        editTextApodo = findViewById(R.id.apodoRegister)
        editTextTelefono = findViewById(R.id.telefonoRegister)
        textViewFechanacimiento = findViewById(R.id.fechaNacimientoRegister)
        radioGroupUserType = findViewById(R.id.rg_tipoDeUsuario)
        radioGroupClasificacion = findViewById(R.id.rg_clasificacion)
        rol = ""
        clasificacion = ""

        auth = Firebase.auth

        textViewToLogin.setOnClickListener {
            toMain()
        }

        radioGroupUserType.setOnCheckedChangeListener{
            group, checkedId ->

            radioButtonUserType = findViewById(checkedId)
            rol = radioButtonUserType.text.toString()

        }

        radioGroupClasificacion.setOnCheckedChangeListener{
                group, checkedId ->

            radioButtonClasificacion = findViewById(checkedId)
            clasificacion = radioButtonClasificacion.text.toString()

        }

        textViewFechanacimiento.setOnClickListener {
            var calendar: Calendar = Calendar.getInstance()
            var year = calendar.get(Calendar.YEAR)
            var month = calendar.get(Calendar.MONTH)
            var day = calendar.get(Calendar.DAY_OF_MONTH)

            // Crea una instancia del DatePickerDialog y muestra el selector de fecha

            val datePickerDialog = DatePickerDialog(
                this, android.R.style.Theme_Material_Dialog,
                { view, year, month, dayOfMonth ->
                    // La fecha seleccionada por el usuario
                    val fechaSeleccionada = "$dayOfMonth/${month + 1}/$year"
                    // Aquí puedes hacer algo con la fecha seleccionada, por ejemplo, mostrarla en un TextView


                    // Crea una instancia de Calendar y configura la fecha seleccionada
                    val calendario = Calendar.getInstance()
                    calendario.set(Calendar.YEAR, year)
                    calendario.set(Calendar.MONTH, month) // Ten en cuenta que los meses comienzan desde 0 en Calendar
                    calendario.set(Calendar.DAY_OF_MONTH, dayOfMonth)

                    // Obtiene el Timestamp de la fecha seleccionada
                    textViewFechanacimiento.text = fechaSeleccionada
                    fechaCovertida = calendario.timeInMillis.toString()

                },
                year, month, day
            )

            datePickerDialog.show()
        }


        btnRegister.setOnClickListener {

            progressBar.visibility = View.VISIBLE
            val nombre: String = editTextNombre.text.toString()
            val email: String = editTextEmail.text.toString()
            val password: String = editTextPassword.text.toString()
            var apodo: String = editTextApodo.text.toString()
            val telefono: String = editTextTelefono.text.toString()
            val fechaNac: String = textViewFechanacimiento.text.toString()


            if(TextUtils.isEmpty(nombre) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(telefono)
                || TextUtils.isEmpty(fechaNac) || TextUtils.isEmpty(clasificacion)
                || TextUtils.isEmpty(rol)
            ){

                if (TextUtils.isEmpty(nombre)) {
                    editTextNombre.error = "Ingrese su nombre"
                }
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.error = "Ingrese su correo"
                }
                if (TextUtils.isEmpty(telefono)) {
                    editTextTelefono.error = "Ingrese su numero de telefono"
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.error = "Se requiere una contraseña"
                }
                if (TextUtils.isEmpty(fechaNac)) {
                    textViewFechanacimiento.error = "Ingrese su fecha de nacimiento"
                }

                if (TextUtils.isEmpty(clasificacion)) {
                    Toast.makeText(
                        this@Register,
                       "Por favor, seleccione la clasificacion.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }

                if (TextUtils.isEmpty(rol)) {
                    Toast.makeText(
                        this@Register,
                        "Por favor, seleccione el rol.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(apodo)) {
                apodo = " "
                apodo = editTextNombre.text.toString()
                progressBar.visibility = View.GONE

            }

            if (!isValidEmail(email.trim())) {
                editTextEmail.error = "Correo invalido"
                progressBar.visibility = View.GONE
                return@setOnClickListener
            }

            if (password.length < 6) {
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
                        createUser(nombre, email, password,apodo, telefono, fechaCovertida, clasificacion, rol)
                        toMain()
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

    private fun createUser(nombre: String,
                           email: String,
                           password: String,
                           apodo: String,
                           telefono: String,
                           fechaNac: String,
                           clasf: String,
                           rol: String
    ) {
        val jugadorPorDefecto: HashMap<String, Any> = HashMap<String, Any>()
        val bloqueosList = mutableListOf<String>()
        val posicionesList = mutableListOf<String>()

        jugadorPorDefecto["apodo"] = apodo.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        jugadorPorDefecto["bloqueos"] = bloqueosList
        jugadorPorDefecto["clasificacion"] = clasf.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        jugadorPorDefecto["contrasena"] = password
        jugadorPorDefecto["correo"] = email
        jugadorPorDefecto["estado"] = true
        jugadorPorDefecto["fecha_nacimiento"] = fechaNac
        jugadorPorDefecto["nombre"] = nombre.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        jugadorPorDefecto["posiciones"] = posicionesList
        jugadorPorDefecto["rol"] = rol.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        jugadorPorDefecto["telefono"] = telefono

        FirebaseUtils().createDocument("jugadores", jugadorPorDefecto)
    }

    private fun toMain() {
        progressBar.visibility = View.GONE
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun isValidEmail(email: String): Boolean {
        val pattern = Patterns.EMAIL_ADDRESS
        return pattern.matcher(email).matches()
    }

}