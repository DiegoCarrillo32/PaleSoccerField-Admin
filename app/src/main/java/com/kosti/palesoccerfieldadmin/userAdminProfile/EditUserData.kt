package com.kosti.palesoccerfieldadmin.userAdminProfile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.UserViewModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils


class EditUserData : AppCompatActivity() {

    private lateinit var currentUserId: String

    //Layouts declarations
    private lateinit var linearLayoutPositions: LinearLayout
    private lateinit var linearLayoutName: LinearLayout
    private lateinit var linearLayoutNickname: LinearLayout
    private lateinit var linearLayoutPhone: LinearLayout
    private lateinit var linearLayoutAge: LinearLayout

    //Buttons declarations
    private lateinit var btnEditPositions: ImageButton
    private lateinit var btnEditName: ImageButton
    private lateinit var btnEditNickname: ImageButton
    private lateinit var btnEditPhone: ImageButton
    private lateinit var btnEditAge: ImageButton

    //User data declarations
    private lateinit var positions: MutableList<String>
    private var name: String? = null
    private var nickname: String? = null
    private var phone: String? = null
    private var age: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_data)

        val userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)

        userViewModel.userId.observe(this, Observer { userId ->

            Toast.makeText(applicationContext, userId, Toast.LENGTH_SHORT).show()
        })

        //Layouts asignations
        linearLayoutPositions = findViewById(R.id.layoutPositions)
        linearLayoutName = findViewById(R.id.layoutName)
        linearLayoutNickname = findViewById(R.id.layoutNickname)
        linearLayoutPhone= findViewById(R.id.layoutPhone)
        linearLayoutAge = findViewById(R.id.layoutAge)

        //Buttons asignations
        btnEditPositions = findViewById(R.id.btnEditPositions)
        btnEditName = findViewById(R.id.btnEditName)
        btnEditNickname = findViewById(R.id.btnEditNickname)
        btnEditPhone = findViewById(R.id.btnEditPhone)
        btnEditAge = findViewById(R.id.btnEditAge)

        //User data asignations
        currentUserId = "vnCw2ctK4hPa06RmsSpm"

        positions = mutableListOf("Delantero", "Medio Campista")
        age = "21"

        val nameTV : TextView = findViewById(R.id.nameText)
        val nicknameTV : TextView = findViewById(R.id.nicknameText)
        val phoneTV : TextView = findViewById(R.id.phoneText)

        FirebaseUtils().getDocumentById("jugadores" , currentUserId) { user ->
            user.onSuccess {
                name = user.getOrThrow()["nombre"].toString()
                nickname = user.getOrThrow()["apodo"].toString()
                phone = user.getOrThrow()["telefono"].toString()

                nameTV.text = name
                nicknameTV.text = nickname
                phoneTV.text = phone
                Toast.makeText(applicationContext, user.getOrThrow()["positions"].toString(), Toast.LENGTH_SHORT).show()

            }
            user.onFailure {
                Toast.makeText(applicationContext, "Error al cargar el usuario", Toast.LENGTH_SHORT).show()
            }
        }



        val spinner = findViewById<Spinner>(R.id.spinnerPositions)

        val ageTV : TextView = findViewById(R.id.ageText)


        ageTV.text = age

        //Events setClickOnListener
        btnEditPositions.setOnClickListener { editPositions() }
        btnEditName.setOnClickListener { editName() }
        btnEditNickname.setOnClickListener { editNickname() }
        btnEditPhone.setOnClickListener { editPhone() }
        btnEditAge.setOnClickListener { editAge() }

        val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, positions)

        // Establece el diseño que se usará cuando se despliegue la lista de opciones
        adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Asigna el adaptador al Spinner
        spinner.adapter = adaptador

        // Opcional: Agrega un oyente de selección para manejar las selecciones del usuario
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val opcionSeleccionada = positions[position]
                Toast.makeText(applicationContext, opcionSeleccionada, Toast.LENGTH_SHORT).show()
                // Realiza alguna acción con la opción seleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Maneja la situación en la que no se seleccionó ninguna opción
            }
        }
    }






    fun activityEditField(type: String, data: String){
        val intent = Intent(this, EditField::class.java)

        // Agregar datos al Intent
        intent.putExtra("type", type)
        intent.putExtra("data", data)

        // Iniciar la nueva actividad
        startActivity(intent)
    }

    fun activityEditPositions(type: String){
        val intent = Intent(this, EditPositionsUser::class.java)

        // Agregar datos al Intent
        intent.putStringArrayListExtra("positions", ArrayList(positions))
        intent.putExtra("type", type)

        // Iniciar la nueva actividad
        startActivity(intent)
    }

    fun editPositions() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutPositions.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutPositions.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        activityEditPositions("Posiciones");
    }


    fun editName() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutName.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutName.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        name?.let { activityEditField("Nombre" , it) };
    }

    fun editNickname() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutNickname.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutNickname.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        nickname?.let { activityEditField("Apodo" , it) };
    }

    fun editPhone() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutPhone.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutPhone.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        phone?.let { activityEditField("Teléfono" , it) };
    }

    fun editAge() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutAge.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutAge.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        age?.let { activityEditField("Edad" , it) };
    }

}