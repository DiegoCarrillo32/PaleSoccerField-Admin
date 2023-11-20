package com.kosti.palesoccerfieldadmin.schedules

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ScheduleDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Date
import java.util.Locale
import kotlin.time.Duration.Companion.hours

val COLLECTION_NAME = "horario"
class ScheduleAdapter(private val dataSet: MutableList<ScheduleDataModel>, private val context: Context) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(), AddScheduleFragment.OnDismissListener {
    //TODO: Validar los campos, revisar por que las horas se ven raro, verificar bien las fechas
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTV: TextView
        val startTimeTV: TextView
        val endTimeTV: TextView
        val deleteBtn: ImageButton
        val editBtn: ImageButton
        init {
            dateTV = view.findViewById(R.id.dateTV)
            startTimeTV = view.findViewById(R.id.startTimeTV)
            endTimeTV = view.findViewById(R.id.endTimeTV)
            deleteBtn = view.findViewById(R.id.deleteBtn)
            editBtn = view.findViewById(R.id.editBtn)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.schedule_row_item, parent, false)

        return ViewHolder(view)
    }

    override fun getItemCount() = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val date = Date((dataSet[position].fecha?.seconds ?: 0) * 1000)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        holder.dateTV.text      = "Fecha: $formattedDate"

        val initialHour = Date((dataSet[position].tanda?.get(0) ?.seconds ?: 0) * 1000)
        val finalHour = Date((dataSet[position].tanda?.get(1)?.seconds ?: 0) * 1000)
        val dateFormatHour = SimpleDateFormat("HH:mm", Locale.getDefault())
        val formattedHourInit = dateFormatHour.format(initialHour)
        val formattedHourFinal = dateFormatHour.format(finalHour)

        holder.startTimeTV.text = "Hora inicio: $formattedHourInit"

        holder.endTimeTV.text   = "Hora final: $formattedHourFinal"

        holder.deleteBtn.setOnClickListener {
            mostrarDialogConfirmarEliminarReserva(holder.dateTV.text.toString(), holder.endTimeTV.text.toString(), holder.startTimeTV.text.toString(), position)

        }

        holder.editBtn.setOnClickListener {

            val bundle = Bundle()
            // convert timestamp to date
            val date = dataSet[position].fecha?.toDate()
            val start = dataSet[position].tanda?.get(0)?.toDate()
            val end = dataSet[position].tanda?.get(1)?.toDate()
            val id = dataSet[position].id
            bundle.putString("id", id)
            bundle.putSerializable("date", date)
            bundle.putSerializable("start", start)
            bundle.putSerializable("end", end)
            val bottomSheetFragment = AddScheduleFragment()
            bottomSheetFragment.setOnDismissListener(this)
            bottomSheetFragment.arguments =  bundle
            bottomSheetFragment.show((holder.itemView.context as Schedules).supportFragmentManager, "BSDialogFragment")

        }

    }

    private fun mostrarDialogConfirmarEliminarReserva(fechaHorario: String, horaFinal: String,horaInicio:String, position: Int) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirmar_eliminar_horario, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()


        val btnCancelar: Button = dialogView.findViewById(R.id.btn_dialog_cancelar)
        val btnEliminar: Button = dialogView.findViewById(R.id.btn_dialog_eliminar)
        val tvFechaHorario: TextView = dialogView.findViewById(R.id.dialog_tVFecha)
        val tvHoraI: TextView = dialogView.findViewById(R.id.dialog_tVHoraI)
        val tvHoraF: TextView = dialogView.findViewById(R.id.dialog_tVHoraF)

        tvFechaHorario.text = fechaHorario
        tvHoraF.text = horaFinal
        tvHoraI.text = horaInicio

        btnEliminar.setOnClickListener {
            //TODO: si el horario reservacion es true, entonces cancele los cambios

            if(dataSet[position].reservado){
                Toast.makeText(context, "No se puede eliminar el horario porque una reserva depende de el", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                return@setOnClickListener
            }

            FirebaseUtils().getCollectionByProperty("reservas", "horario", dataSet[position].id){
                    result ->

                result.onSuccess {
                    if(it.size>0 && it[0]["horario"] == dataSet[position].id)   {
                        Toast.makeText(context, "No se puede eliminar el horario porque una reserva depende de el", Toast.LENGTH_SHORT).show()
                    } else {
                        FirebaseUtils().deleteDocument(COLLECTION_NAME, dataSet[position].id)
                        dataSet.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, dataSet.size)
                    }
                }

                result.onFailure {
                    Toast.makeText(context, "Error al eliminar el horario", Toast.LENGTH_SHORT).show()
                }
            }
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onDismissOnActivity() {
        FirebaseUtils().readCollection(COLLECTION_NAME) {
                result ->
            result.onSuccess {
                dataSet.clear()
                for (schedule in it){
                    if(schedule["fecha"] == null || schedule["tanda"] == null) continue
                    val scheduleData = ScheduleDataModel(
                        schedule["id"] as String,
                        schedule["fecha"] as Timestamp,
                        schedule["tanda"] as MutableList<Timestamp>,
                        schedule["reservado"] as Boolean,
                    )
                    dataSet.add(scheduleData)

                }
                notifyDataSetChanged()
            }
            result.onFailure {

            }

        }
    }
}