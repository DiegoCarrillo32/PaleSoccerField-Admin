package com.kosti.palesoccerfieldadmin.schedules

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ScheduleDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
val COLLECTION_NAME = "horario"
class ScheduleAdapter(private val dataSet: MutableList<ScheduleDataModel>) : RecyclerView.Adapter<ScheduleAdapter.ViewHolder>(), AddScheduleFragment.OnDismissListener {
    //TODO: Validar los campos, revisar por que las horas se ven raro, verificar bien las fechas
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTV: TextView
        val startTimeTV: TextView
        val endTimeTV: TextView
        val deleteBtn: Button
        val editBtn: Button
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

        holder.dateTV.text = dataSet[position].fecha?.toDate()?.day.toString() + "/" + dataSet[position].fecha?.toDate()?.month.toString() + "/" + dataSet[position].fecha?.toDate()?.year.toString()


        holder.startTimeTV.text = dataSet[position].tanda?.get(0)?.toDate()?.hours?.toString() + ":" + dataSet[position].tanda?.get(0)?.toDate()?.minutes.toString()
        holder.endTimeTV.text = dataSet[position].tanda?.get(1)?.toDate()?.hours.toString() + ":" + dataSet[position].tanda?.get(1)?.toDate()?.minutes.toString()

        holder.deleteBtn.setOnClickListener {
            FirebaseUtils().getCollectionByProperty("reservas", "horario", dataSet[position].id){
                result ->
                result.onSuccess {
                    if(it.size>0 && it[0]["horario"] == dataSet[position].id)   {
                        Toast.makeText(holder.itemView.context, "No se puede eliminar el horario porque una reserva depende de el", Toast.LENGTH_SHORT).show()
                    } else {
                        FirebaseUtils().deleteDocument(COLLECTION_NAME, dataSet[position].id)
                        dataSet.removeAt(position)
                        notifyItemRemoved(position)
                        notifyItemRangeChanged(position, dataSet.size)
                    }
                }
                result.onFailure {
                    Toast.makeText(holder.itemView.context, "Error al eliminar el horario", Toast.LENGTH_SHORT).show()
                }
            }

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
                        schedule["tanda"] as MutableList<Timestamp>
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