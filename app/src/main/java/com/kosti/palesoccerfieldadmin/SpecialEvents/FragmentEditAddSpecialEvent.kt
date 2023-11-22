package com.kosti.palesoccerfieldadmin.SpecialEvents

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.text.SimpleDateFormat

import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "name"
private const val ARG_PARAM3 = "description"
private const val ARG_PARAM4 = "date"
private const val ARG_PARAM5 = "status"
private const val ARG_PARAM6 = "imageUrl"


class FragmentEditAddSpecialEvent : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var id: String? = null
    private var name: String? = null
    private var description: String? = null
    private var date: Date? = null
    private var status: Boolean by Delegates.notNull()
    private var imageUrl: String = ""

    private var imageUri : Uri = Uri.EMPTY
    private var sd = ""

    private val calendar = Calendar.getInstance()
    private lateinit var btnDatePicker : TextView
    private var btnAddImage: ImageButton? = null
    private var selectedTime: Timestamp = Timestamp(Date())

    private var onDismissListener: OnDismissListener? = null

    private lateinit var radioGroup: RadioGroup
    private lateinit var radioButton: RadioButton
    private var estado by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_PARAM1)
            name = it.getString(ARG_PARAM2)
            description= it.getString(ARG_PARAM3)
            date = it.getSerializable(ARG_PARAM4) as Date
            status = it.getBoolean(ARG_PARAM5)
            imageUrl = it.getString(ARG_PARAM6) ?: ""
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_edit_add_special_event, container, false)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == 1){
            imageUri = data?.data ?: Uri.EMPTY
            sd = getFileName(context, imageUri)
            Toast.makeText(context, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // IMAGEN
        btnAddImage = view.findViewById(R.id.selectImageES)
        btnAddImage?.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            startActivityForResult(galleryIntent,1)
        }

        // FECHA
        btnDatePicker = view.findViewById(R.id.selectDateES)
        btnDatePicker.setOnClickListener{
            showDatePicker(btnDatePicker)
        }

        val btnNombre = view.findViewById<EditText>(R.id.selectNameES)
        val btnDescription = view.findViewById<EditText>(R.id.selectDescES)

        radioGroup = view.findViewById(R.id.rg_evento_especial_estado)
        radioGroup.setOnCheckedChangeListener {
                group, checkedId ->

            radioButton = view.findViewById(checkedId)
            if (radioButton.text == "Activo") {
                estado =true
            }else {
                estado =false
            }
            
        }

        if(id != null) {
            btnNombre.setText(name)
            btnDescription.setText(description)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = date?.let { dateFormat.format(it) }
            btnDatePicker.text= "Fecha seleccionada: $formattedDate"
            selectedTime = Timestamp(date!!)

            val radioBtnActivo = view.findViewById<RadioButton>(R.id.radio_btn_activo)
            val radioBtnInactiva = view.findViewById<RadioButton>(R.id.radio_btn_inactivo)

            if(status){
                radioBtnActivo.isChecked = true
            }else {
                radioBtnInactiva.isChecked = true
            }

        }

        val btnAddES = view.findViewById<TextView>(R.id.btn_addSpecialEvent)
        btnAddES.setOnClickListener {
            if(id != null) {
                EditToDb(btnNombre, btnDescription)
            } else {
                AddToDb(btnNombre, btnDescription)
            }
        }
    }

    @SuppressLint("Range")
    private fun getFileName(context: Context?, imageUri: Uri): String {
        if(imageUri.scheme == "content"){
            val cursor = context?.contentResolver?.query(imageUri, null, null, null, null)
            try {
                if(cursor != null && cursor.moveToFirst()){
                    if(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME) != -1)
                        return cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            } finally {
                cursor?.close()
            }
        }
        return imageUri.path?.lastIndexOf("/").let {
            it?.let { it1 -> imageUri.path?.substring(it1) } ?: ""
        }
    }

    private fun EditToDb(btnNombre: EditText?, btnDescription: EditText?) {

        val name = btnNombre?.text.toString()
        val desc = btnDescription?.text.toString()

        if(name.isEmpty() || desc.isEmpty()){
            Toast.makeText(context, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }


        // disable the isDraggable bottom sheet to avoid errors

        if(dialog is BottomSheetDialog) {
            (dialog as BottomSheetDialog).behavior.isDraggable = false

        }

        isCancelable = false

        if(imageUrl != ""){
            FirebaseUtils().deleteImage(imageUrl)
        }

        radioGroup.checkedRadioButtonId
        if(imageUri != Uri.EMPTY && sd != "") {
            FirebaseUtils().saveImage(imageUri, sd) {
                    result ->
                result.onSuccess {res ->
                    imageUrl = res
                    FirebaseUtils().updateDocument(
                        "evento_especial", id!!, hashMapOf(
                            "nombre" to name,
                            "descripcion" to desc,
                            "estado" to estado,
                            "fecha" to selectedTime,
                            "imagen_url" to imageUrl
                        )
                    )
                    dismiss()
                    Toast.makeText(context, "Evento editado", Toast.LENGTH_SHORT).show()
                }
                result.onFailure {
                    Toast.makeText(context, "Error al agregar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        } else{
            FirebaseUtils().updateDocument(
                "evento_especial", id!!, hashMapOf(
                    "nombre" to name,
                    "descripcion" to desc,
                    "estado" to estado,
                    "fecha" to selectedTime,
                    "imagen_url" to imageUrl
                )
            )
            dismiss()
            Toast.makeText(context, "Evento editado", Toast.LENGTH_SHORT).show()
        }



    }

    private fun AddToDb(btnNombre: EditText, btnDescription: EditText) {
        val name = btnNombre.text.toString()
        val desc = btnDescription.text.toString()

        if(name.isEmpty() || desc.isEmpty()){
            Toast.makeText(context, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if(imageUri == Uri.EMPTY || sd == ""){
            Toast.makeText(context, "Por favor seleccione una imagen", Toast.LENGTH_SHORT).show()
            return
        }
        if(imageUri == Uri.EMPTY){
            Toast.makeText(context, "Por favor seleccione una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        if(dialog is BottomSheetDialog) {
            (dialog as BottomSheetDialog).behavior.isDraggable = false

        }
        isCancelable = false

        try {
            FirebaseUtils().saveImage(imageUri, sd){
                    result ->
                result.onSuccess {res ->
                    imageUrl = res
                    FirebaseUtils().createDocument(
                        "evento_especial", hashMapOf(
                            "nombre" to name,
                            "descripcion" to desc,
                            "estado" to true,
                            "fecha" to selectedTime,
                            "imagen_url" to imageUrl
                        )
                    )
                    dismiss()
                    Toast.makeText(context, "Evento especial creado $imageUrl", Toast.LENGTH_SHORT).show()                }
                result.onFailure {
                    Toast.makeText(context, "Error al agregar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("Fragment Evento Especial", e.message.toString())
            Toast.makeText(context, "Error al crear el evento, por favor no cierre el formulario", Toast.LENGTH_SHORT).show()
        }
    }



    private fun showDatePicker(text: TextView) {
        // Create a DatePickerDialog

        val datePickerDialog = context?.let {
            DatePickerDialog(
                it, { DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                    // Create a new Calendar instance to hold the selected date
                    val selectedDate = Calendar.getInstance()
                    // Set the selected date using the values received from the DatePicker dialog
                    selectedDate.set(year, monthOfYear, dayOfMonth)
                    selectedDate.set(Calendar.HOUR_OF_DAY, 0)
                    selectedDate.set(Calendar.MINUTE, 0)
                    selectedDate.set(Calendar.SECOND, 0)
                    // Create a SimpleDateFormat to format the date as "dd/MM/yyyy"
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    // Format the selected date into a string
                    val formattedDate = dateFormat.format(selectedDate.time)
                    // Update the TextView to display the selected date with the "Selected Date: " prefix
                    text.text = "Fecha seleccionada: $formattedDate"

                    selectedTime = Timestamp(selectedDate.time)

                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        // Show the DatePicker dialog
        datePickerDialog?.show()
    }

    fun setOnDismissListener(listener: OnDismissListener) {
        onDismissListener = listener
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        onDismissListener?.onDismissOnActivity()
    }

    interface OnDismissListener {
        fun onDismissOnActivity()
    }
}