package com.kosti.palesoccerfieldadmin.schedules

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.otherUsersProfile.ProfileScreen
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

private const val ARG_PARAM1 = "date"
private const val ARG_PARAM2 = "start"
private const val ARG_PARAM3 = "end"
private const val ARG_PARAM4 = "id"
// TODO : Agregar el dismiss lsitener para saber cuando se cierra el dialogo y actualizar la lista de horarios
class AddScheduleFragment: BottomSheetDialogFragment() {

    private val COLLECTION_NAME = "horario"
    private var selectedDate: Date? = null
    private var selectedStart: Date? = null
    private var selectedEnd: Date? = null
    private var id: String? = null



    private var selectedDateTimestamp: Timestamp = Timestamp(Date())
    private var selectedTimeStart: Timestamp? = null
    private var selectedTimeEnd: Timestamp? = null

    private lateinit var btnDatePicker: TextView
    private lateinit var btnSelectTimeStart: TextView
    private lateinit var btnSelectTimeEnd: TextView
    private lateinit var btnCreateSchedule: Button

    private val calendar = Calendar.getInstance()
    private var onDismissListener: AddScheduleFragment.OnDismissListener? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            selectedDate =  it.getSerializable(ARG_PARAM1) as Date
            selectedStart =  it.getSerializable(ARG_PARAM2) as Date
            selectedEnd =  it.getSerializable(ARG_PARAM3) as Date
            id = it.getString(ARG_PARAM4)
            // convert the received string to a HashMap of type <Timestamp, mutableList<Timestamp> >


        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_schedule, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        btnDatePicker = view.findViewById(R.id.selectDate)


        if(selectedDate != null){
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val formattedDate = selectedDate?.let { dateFormat.format(it) }
            btnDatePicker.text = "Fecha seleccionada: $formattedDate"
            selectedDateTimestamp = Timestamp(selectedDate!!)
        }

        btnSelectTimeStart = view.findViewById(R.id.selectTimeStart)


        if(selectedStart != null){
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = selectedStart?.let { timeFormat.format(it) }
            btnSelectTimeStart.text = "Hora seleccionada: $formattedTime"
            selectedTimeStart = Timestamp(selectedStart!!)
        }



        btnSelectTimeEnd = view.findViewById(R.id.selectTimeEnd)


        if(selectedEnd != null){
            val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
            val formattedTime = selectedEnd?.let { timeFormat.format(it) }
            btnSelectTimeEnd.text = "Hora seleccionada: $formattedTime"
            selectedTimeEnd = Timestamp(selectedEnd!!)
        }

        btnCreateSchedule = view.findViewById(R.id.btn_addSchedule)

        btnCreateSchedule.setOnClickListener {

            if(selectedTimeStart == null || selectedTimeEnd == null){
                Toast.makeText(context, "Por favor seleccione una fecha y hora", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(id != null){ // edit mode
                editSchedule()
                dismiss()
            }
            else{
                saveToDb()
                dismiss()
            }

        }

        btnSelectTimeStart.setOnClickListener {
            showTimePicker(true)
        }

        btnSelectTimeEnd.setOnClickListener {
            showTimePicker(false)
        }

        btnDatePicker.setOnClickListener {
            showDatePicker()
        }

    }

    private fun showTimePicker(select_flag: Boolean){

        val timePickerDialog = TimePickerDialog(
            context, {timePicker, hourOfDay, minute ->

                // Create a new Calendar instance to hold the selected time
                val selectedTime = Calendar.getInstance()
                // Set the selected time using the values received from the TimePicker dialog
                if(!select_flag && selectedTimeStart != null && selectedTimeStart!!.toDate().hours > hourOfDay){
                    Toast.makeText(context, "La hora final no puede ser menor a la hora inicial", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog

                }

                if(!select_flag && selectedTimeStart == null) {
                    Toast.makeText(context, "Por favor seleccione la hora inicial", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }



                // if the selected time is 1 hour or less than the selected start time, show an error message
                if(!select_flag && hourOfDay < selectedTimeStart!!.toDate().hours+1){
                    Toast.makeText(context, "La cantidad minima de reserva es 1 hora", Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog

                }
                selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                selectedTime.set(Calendar.MINUTE, minute)

                // Create a SimpleDateFormat to format the time as "HH:mm"
                val timeFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                // Format the selected time into a string
                val formattedTime = timeFormat.format(selectedTime.time)

                // Update the TextView to display the selected time with the "Selected Time: " prefix
                if(select_flag){
                    btnSelectTimeStart.text = "Hora seleccionada: $formattedTime"
                    selectedTimeStart = Timestamp(selectedTime.time)
                }
                else{
                    btnSelectTimeEnd.text = "Hora seleccionada: $formattedTime"
                    selectedTimeEnd = Timestamp(selectedTime.time)
                }

            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )


        if(selectedTimeStart != null && !select_flag)
            selectedTimeStart?.toDate()
                ?.let {
                    timePickerDialog.updateTime(it.hours+1, selectedTimeStart!!.toDate().minutes)

                }


        timePickerDialog.show()
    }

    private fun showDatePicker() {
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
                    btnDatePicker.text = "Fecha seleccionada: $formattedDate"
                    selectedDateTimestamp = Timestamp(selectedDate.time)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }

        // Show the DatePicker dialog
        datePickerDialog?.show()
    }
    private fun editSchedule(){
        FirebaseUtils().updateDocument(COLLECTION_NAME, id!!, hashMapOf(
            "fecha" to selectedDateTimestamp,
            "tanda" to mutableListOf(
                selectedTimeStart,
                selectedTimeEnd
            )
        ))
    }
    private fun saveToDb(){
        FirebaseUtils().createDocument(COLLECTION_NAME, hashMapOf(
            "fecha" to selectedDateTimestamp,
            "tanda" to mutableListOf(
                selectedTimeStart,
                selectedTimeEnd
            ),
            "reservado" to false

        ))
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