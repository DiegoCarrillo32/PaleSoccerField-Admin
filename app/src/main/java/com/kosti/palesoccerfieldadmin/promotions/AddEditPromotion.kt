package com.kosti.palesoccerfieldadmin.promotions

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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.PromotionDataModel
import com.kosti.palesoccerfieldadmin.schedules.AddScheduleFragment
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "id"
private const val ARG_PARAM2 = "name"
private const val ARG_PARAM3 = "description"
private const val ARG_PARAM4 = "startDate"
private const val ARG_PARAM5 = "endDate"
private const val ARG_PARAM6 = "imageUrl"
private const val ARG_PARAM7 = "status"

class AddEditPromotion : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var id: String? = null
    private var name: String? = null
    private var desc: String? = null
    private var startDate: Date? = null
    private var endDate: Date? = null
    private var imageUrl: String = ""
    private var status: String? = null

    private var imageUri : Uri = Uri.EMPTY
    private var sd = ""

    private val calendar = Calendar.getInstance()
    private lateinit var btnDatePicker : TextView
    private lateinit var btnDatePicker2  : TextView
    private var btnAddImage: ImageButton? = null

    private var selectedTimeStart: Timestamp = Timestamp(Date())
    private var selectedTimeEnd: Timestamp = Timestamp(Date())

    private var onDismissListener: OnDismissListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_PARAM1)
            name = it.getString(ARG_PARAM2)
            desc = it.getString(ARG_PARAM3)
            startDate = it.getSerializable(ARG_PARAM4) as Date
            endDate = it.getSerializable(ARG_PARAM5) as Date
            imageUrl = it.getString(ARG_PARAM6) ?: ""
            status = it.getString(ARG_PARAM7)

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_edit_promotion, container, false)
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


        btnAddImage = view.findViewById<ImageButton>(R.id.selectImage)
        btnAddImage?.setOnClickListener {
            val galleryIntent = Intent(Intent.ACTION_PICK)
            galleryIntent.type = "image/*"
            // start the activiy in the FRAGMENT and get the result in the fragment
            // get the result of the activity
            startActivityForResult(galleryIntent, 1)
        }

        btnDatePicker = view.findViewById<TextView>(R.id.selectDateStart)
        btnDatePicker.setOnClickListener {
            showDatePicker(btnDatePicker, true)
        }
        btnDatePicker2 = view.findViewById<TextView>(R.id.selectDateEnd)
        btnDatePicker2.setOnClickListener {
            showDatePicker(btnDatePicker2, false)
        }
        val btnName = view.findViewById<EditText>(R.id.selectName)
        val btnDescription = view.findViewById<EditText>(R.id.selectDesc)

        if(id != null){
            btnName.setText(name)
            btnDescription.setText(desc)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = startDate?.let { dateFormat.format(it) }
            btnDatePicker.text = "Fecha seleccionada: $formattedDate"
            selectedTimeStart = Timestamp(startDate!!)
            val formattedDate2 = endDate?.let { dateFormat.format(it) }
            btnDatePicker2.text = "Fecha seleccionada: $formattedDate2"
            selectedTimeEnd = Timestamp(endDate!!)
        }

        val btnAdd = view.findViewById<TextView>(R.id.btn_addPromotion)
        btnAdd.setOnClickListener {
            if(id != null) {
                EditToDb(btnName, btnDescription)
            } else {
                AddToDb(btnName, btnDescription)
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

    private fun EditToDb(btnName: EditText?, btnDescription: EditText?) {
        val name = btnName?.text.toString()
        val desc = btnDescription?.text.toString()

        if(name.isEmpty() || desc.isEmpty()){
            Toast.makeText(context, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if(selectedTimeStart.seconds > selectedTimeEnd.seconds){
            Toast.makeText(context, "La fecha de inicio no puede ser mayor a la fecha de fin", Toast.LENGTH_SHORT).show()
            return
        }

        if(imageUri == Uri.EMPTY){
            Toast.makeText(context, "Por favor seleccione una imagen", Toast.LENGTH_SHORT).show()
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


        FirebaseUtils().saveImage(imageUri, sd){
                result ->
            result.onSuccess {res ->
                imageUrl = res
                FirebaseUtils().updateDocument(
                    "promocion", id!!, hashMapOf(
                        "nombre" to name,
                        "descripcion" to desc,
                        "estado" to true,
                        "fecha_final" to selectedTimeStart,
                        "fecha_inicio" to selectedTimeEnd,
                        "imagen_url" to imageUrl
                    )
                )
                dismiss()
                Toast.makeText(context, "Promocion editada", Toast.LENGTH_SHORT).show()
            }
            result.onFailure {
                Toast.makeText(context, "Error al agregar imagen", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun AddToDb(btnName: EditText, btnDescription: EditText) {
        val name = btnName.text.toString()
        val desc = btnDescription.text.toString()
        if(name.isEmpty() || desc.isEmpty()){
            Toast.makeText(context, "Por favor llene todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        if(selectedTimeStart.seconds > selectedTimeEnd.seconds){
            Toast.makeText(context, "La fecha de inicio no puede ser mayor a la fecha de fin", Toast.LENGTH_SHORT).show()
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
        // create a coroutine to save the image and get the url




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
                        "promocion", hashMapOf(
                            "nombre" to name,
                            "descripcion" to desc,
                            "estado" to true,
                            "fecha_final" to selectedTimeStart,
                            "fecha_inicio" to selectedTimeEnd,
                            "imagen_url" to imageUrl
                        )
                    )
                    dismiss()
                    Toast.makeText(context, "Promocion agregada $imageUrl", Toast.LENGTH_SHORT).show()                }
                result.onFailure {
                    Toast.makeText(context, "Error al agregar imagen", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Log.e("AddEditPromotion", e.message.toString())
            Toast.makeText(context, "Error al crear la promocion, por favor no cierre el formulario", Toast.LENGTH_SHORT).show()
        }


    }

    private fun showDatePicker(text: TextView, flag: Boolean) {
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
                    if(flag){
                        selectedTimeStart = Timestamp(selectedDate.time)
                    }
                    else{
                        selectedTimeEnd = Timestamp(selectedDate.time)
                    }

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