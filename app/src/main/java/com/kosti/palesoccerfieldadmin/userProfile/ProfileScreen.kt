package com.kosti.palesoccerfieldadmin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "name"
private const val ARG_PARAM2 = "age"
private const val ARG_PARAM3 = "phone"
private const val ARG_PARAM4 = "classification"
private const val ARG_PARAM5 = "positions"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileScreen.newInstance] factory method to
 * create an instance of this fragment.
 */

class ProfileScreen : Fragment() {

    // TODO: Rename and change types of parameters
    private var name: String? = null
    private var age: Int? = null
    private var phone: String? = null
    private var classification: String? = null
    private lateinit var positions: MutableList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_PARAM1)
            age = it.getInt(ARG_PARAM2)
            phone = it.getString(ARG_PARAM3)
            classification= it.getString(ARG_PARAM4)
            positions= it.getStringArrayList(ARG_PARAM5)!!

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_screen, container, false)

        spinnerPositions(view, )

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val plyrNTV : TextView = view.findViewById(R.id.playerName)
        val ageTV : TextView = view.findViewById(R.id.edadText)
        val phoneTV : TextView = view.findViewById(R.id.telefonoText)
        val classTV : TextView = view.findViewById(R.id.clasificacionText)
        ageTV.text = age.toString()
        plyrNTV.text = name
        phoneTV.text = phone
        classTV.text = classification
    }

    private fun spinnerPositions(view: View){
        val elementos = positions

        val spinner: Spinner = view.findViewById(R.id.spinnerPositions)

        // Crea un ArrayAdapter usando los elementos y el diseño predeterminado para el spinner
        val adapter =
            this.context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, elementos) }

        // Especifica el diseño que se usará cuando se desplieguen las opciones
        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Une el ArrayAdapter al Spinner
        spinner.adapter = adapter

        // Opcionalmente, puedes configurar un escuchador para detectar la selección del usuario
        spinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val itemSeleccionado = elementos[position]
                // Realiza alguna acción con el elemento seleccionado
                Toast.makeText(view?.context, "Seleccionaste: $itemSeleccionado", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Se llama cuando no se ha seleccionado nada en el Spinner (opcional)
                Toast.makeText(view.context, "Nada", Toast.LENGTH_SHORT).show()
            }
        }
    }


//    companion object {
//
//        @JvmStatic
//        fun newInstance(name: String, age: String) =
//            ProfileScreen().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, name)
//                    putString(ARG_PARAM2, age)
//                }
//            }
//    }
}


