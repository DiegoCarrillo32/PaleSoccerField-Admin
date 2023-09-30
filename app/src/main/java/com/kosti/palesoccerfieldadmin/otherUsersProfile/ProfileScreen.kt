package com.kosti.palesoccerfieldadmin.otherUsersProfile

import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetBehavior
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
private const val ARG_PARAM7 = "id"
private const val COLLECTION_NAME = "jugadores"
private const val CLASIFICATION_FIELD = "clasificacion"

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
    private var id: String? = null
    private lateinit var positions: MutableList<String>
    private var ratesList = listOf("malo", "bueno", "regular")
    private var onDismissListener: OnDismissListener? = null
    private var didEditClassification: Boolean = false
    private lateinit var  bottomSheetBehaviour: BottomSheetBehavior<View>;





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            name = it.getString(ARG_PARAM1)
            age = it.getInt(ARG_PARAM2)
            phone = it.getString(ARG_PARAM3)
            classification = it.getString(ARG_PARAM4)
            positions = it.getStringArrayList(ARG_PARAM5)!!
            nickname = it.getString(ARG_PARAM6)
            id = it.getString(ARG_PARAM7)

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
        val closeButton: ImageButton = view.findViewById(R.id.backButton)

        //Sheet behaviour
        bottomSheetBehaviour = BottomSheetBehavior.from( view.parent as View);
        //set to behaviour to expanded and minimum height to parent layout
        bottomSheetBehaviour.state = BottomSheetBehavior.STATE_EXPANDED

        closeButton.setOnClickListener {
            closeBottomSheet()
        }

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
                if(id != null && classTV.selectedItem.toString().toLowerCase() != classification?.toLowerCase() ) {
                    FirebaseUtils().updateProperty(COLLECTION_NAME,
                        id!!, CLASIFICATION_FIELD, classTV.selectedItem.toString())
                    didEditClassification = true
                }

            } else {
                classTV.isEnabled = true
                classTV.isClickable = true
                isEditingClassification = true

            }
        }
        classTV.adapter = rateAdapter
        classTV.setSelection(rateAdapter.getPosition(classification))


    }

    override fun onStart() {
        super.onStart()
        if (dialog != null) {
            var bottomSheet: View =
                dialog!!.findViewById(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;


        }
    }

    private fun closeBottomSheet() {
        dismiss()
    }


    private fun spinnerPositions(view: View) {
        val elementos = positions

        val spinner: Spinner = view.findViewById(R.id.spinnerPositions)
        val adapter =
            this.context?.let {
                ArrayAdapter(
                    it,
                    android.R.layout.simple_spinner_item,
                    elementos
                )
            }

        adapter?.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }

    fun setOnDismissListener(listener: OnDismissListener) {
        onDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        if(didEditClassification){
            onDismissListener?.onDismissOnActivity()
            didEditClassification = false
        }

    }

    interface OnDismissListener {
        fun onDismissOnActivity()
    }

}


