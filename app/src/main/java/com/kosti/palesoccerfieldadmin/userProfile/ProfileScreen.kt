package com.kosti.palesoccerfieldadmin.userProfile

import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "name"
private const val ARG_PARAM2 = "age"
private const val ARG_PARAM3 = "phone"
private const val ARG_PARAM4 = "classification"
private const val ARG_PARAM5 = "positions"
private const val ARG_PARAM6 = "nickname"

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileScreen.newInstance] factory method to
 * create an instance of this fragment.
 */

class ProfileScreen : BottomSheetDialogFragment() {

    // TODO: Rename and change types of parameters
    private var name: String? = null
    private var age: Int? = null
    private var phone: String? = null
    private var classification: String? = null
    private var isEditingClassification: Boolean = false
    private var nickname: String? = null
    private lateinit var positions: MutableList<String>
    private var ratesList = listOf("Malo", "Bueno", "Regular")





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_PARAM1)
            age = it.getInt(ARG_PARAM2)
            phone = it.getString(ARG_PARAM3)
            classification = it.getString(ARG_PARAM4)
            positions = it.getStringArrayList(ARG_PARAM5)!!
            nickname = it.getString(ARG_PARAM6)

        }
    }

        override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            // Inflate the layout for this fragment
            val view = inflater.inflate(R.layout.fragment_profile_screen, container, false)

            spinnerPositions(view,)

            return view
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)

            val plyrNTV: TextView = view.findViewById(R.id.playerName)
            val ageTV: TextView = view.findViewById(R.id.edadText)
            val phoneTV: TextView = view.findViewById(R.id.telefonoText)
            val classTV: Spinner = view.findViewById(R.id.clasificacionSpinner)
            val nicknameTV: TextView = view.findViewById(R.id.nicknameText)
            val editClasiBtn: ImageButton = view.findViewById(R.id.editClassBtn)
            val rateAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, ratesList)
            rateAdapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item
            )

            ageTV.text = age.toString()
            plyrNTV.text = name
            phoneTV.text = phone
            nicknameTV.text = nickname
            classTV.isEnabled = false
            classTV.isClickable = false

            editClasiBtn.setOnClickListener {
                if (isEditingClassification) {
                    classTV.isEnabled = false
                    classTV.isClickable = false
                    isEditingClassification = false
                    // Enviar la data a firebase

                } else {
                    classTV.isEnabled = true
                    classTV.isClickable = true
                    isEditingClassification = true

                }
            }
            classTV.adapter = rateAdapter


        }

        override fun onStart() {
            super.onStart()
            if (dialog != null) {
                var bottomSheet: View =
                    dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
                bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;


            }
        }

        private fun spinnerPositions(view: View) {
            val elementos = positions

            val spinner: Spinner = view.findViewById(R.id.spinnerPositions)

            // Creates an ArrayAdapter using the default elements and layout for the spinner
            val adapter =
                this.context?.let {
                    ArrayAdapter(
                        it,
                        android.R.layout.simple_spinner_item,
                        elementos
                    )
                }

            // Specifies the layout to be used when the options are displayed.
            adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            // Attach the ArrayAdapter to the Spinner
            spinner.adapter = adapter
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


