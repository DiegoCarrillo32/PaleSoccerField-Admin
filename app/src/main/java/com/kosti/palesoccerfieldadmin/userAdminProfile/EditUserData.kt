package com.kosti.palesoccerfieldadmin.userAdminProfile

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.changePassword.ChangePassword
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Date


class EditUserData : AppCompatActivity() {

    companion object {
        const val REQUEST_CODE_EDIT_FIELD = 1
    }

    private lateinit var userId: String
    
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
    private lateinit var btnEditDOB: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var btnChangePassword: Button

    //User data declarations
    private var positions: MutableList<String> = mutableListOf()
    private var name: String? = null
    private var nickname: String? = null
    private var phone: String? = null
    private var dateDOB: String? = null
    private var timesTampString: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user_data)

        userId = intent.getStringExtra("userId").toString()


        loadData() // Carga todos los datos desde firebase

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
        btnEditDOB = findViewById(R.id.btnEditDOB)
        btnBack = findViewById(R.id.backButton)
        btnChangePassword = findViewById(R.id.btnChangePassword)

        //Events setClickOnListener
        btnEditPositions.setOnClickListener { editPositions() }
        btnEditName.setOnClickListener { editName() }
        btnEditNickname.setOnClickListener { editNickname() }
        btnEditPhone.setOnClickListener { editPhone() }
        btnEditDOB.setOnClickListener { editDOB() }
        btnBack.setOnClickListener { finish() }
        btnChangePassword.setOnClickListener { activityChangePassword() }

    }

    private fun loadData(){

        val spinner = findViewById<Spinner>(R.id.spinnerPositions)

        FirebaseUtils().getDocumentById("jugadores" , userId) { user ->
            user.onSuccess {
                val userData: HashMap<String, Any> = user.getOrThrow()


                val nameTV : TextView = findViewById(R.id.nameText)
                val nicknameTV : TextView = findViewById(R.id.nicknameText)
                val phoneTV : TextView = findViewById(R.id.phoneText)
                val fechaNtoTV : TextView = findViewById(R.id.ageText)

                name = userData["nombre"].toString()
                nickname = userData["apodo"].toString()
                phone = userData["telefono"].toString()
                timesTampString = userData["fecha_nacimiento"].toString()
                positions.clear()
                val list = userData["posiciones"] as List<String>?
                if (list != null) {
                    for (item in list) {
                        positions.add(item)
                        println(item)
                    }
                } else {
                    println("El campo 'miLista' está vacío o no contiene una lista de cadenas.")
                }

                nameTV.text = name
                nicknameTV.text = nickname
                phoneTV.text = phone
                dateDOB = timesTampString!!.toLong()?.let { FirebaseUtils().transformEpochToDateWithFormat(it) }

                fechaNtoTV.text = dateDOB


                val adaptador = ArrayAdapter(this, android.R.layout.simple_spinner_item, positions)

                // Establece el diseño que se usará cuando se despliegue la lista de opciones
                adaptador.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                // Asigna el adaptador al Spinner
                spinner.adapter = adaptador

            }
            user.onFailure {
                Toast.makeText(applicationContext, "Error al cargar el usuario", Toast.LENGTH_SHORT).show()
            }
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val opcionSeleccionada = positions[position]
                // Realiza alguna acción con la opción seleccionada
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Maneja la situación en la que no se seleccionó ninguna opción
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE_EDIT_FIELD && resultCode == RESULT_OK) {
            loadData()
        }
    }

    private fun activityEditField(type: String, data: String){
        val intent = Intent(this, EditField::class.java)

        // Agregar datos al Intent
        intent.putExtra("type", type)
        intent.putExtra("data", data)
        intent.putExtra("userId", userId)

        // Iniciar la nueva actividad
        startActivityForResult(intent, REQUEST_CODE_EDIT_FIELD)
    }

    private fun activityEditPositions(type: String){
        val intent = Intent(this, EditPositionsUser::class.java)

        // Agregar datos al Intent
        intent.putStringArrayListExtra("positions", ArrayList(positions))
        intent.putExtra("type", type)
        intent.putExtra("userId", userId)
        startActivityForResult(intent, REQUEST_CODE_EDIT_FIELD)
    }

    private fun activityChangePassword(){
        val intent = Intent(this, ChangePassword::class.java)
        intent.putExtra("userId", userId)
        startActivity(intent)
    }


    private fun editPositions() {
        activityEditPositions("Posiciones");
    }


    private fun editName() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutName.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutName.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        name?.let { activityEditField("Nombre" , it) };
    }

    private fun editNickname() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutNickname.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutNickname.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        nickname?.let { activityEditField("Apodo" , it) };
    }

    private fun editPhone() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutPhone.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutPhone.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        phone?.let { activityEditField("Teléfono" , it) };
    }

    private fun editDOB() {
        // Cambia el fondo del LinearLayout cuando se hace clic en el botón
        linearLayoutAge.setBackgroundResource(R.drawable.edit_field_layout) // Cambia el color de fondo según tu preferencia
        val handler = Handler()
        handler.postDelayed({
            linearLayoutAge.setBackgroundColor(Color.TRANSPARENT) // Restaura el fondo al estado original (transparente)
        }, 200)

        dateDOB?.let { activityEditField("Fecha de Nacimiento" , it) };
    }
}