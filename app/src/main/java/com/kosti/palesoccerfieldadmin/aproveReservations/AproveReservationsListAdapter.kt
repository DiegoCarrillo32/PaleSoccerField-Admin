package com.kosti.palesoccerfieldadmin.aproveReservations

import android.content.Context
import android.content.DialogInterface
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Date
import java.util.Locale


class AproveReservationsListAdapter(
    private val context: Context, private val data: MutableList<ReservasDataModel>
) : RecyclerView.Adapter<AproveReservationsListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val reservationName: TextView = itemView.findViewById(R.id.tvUserName)
        val reservationSchedule: TextView = itemView.findViewById(R.id.tvSchedule)
        val btnAprove: ImageButton = itemView.findViewById(R.id.acceptBtn)
        val btnDeny: ImageButton = itemView.findViewById(R.id.denyBtn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return AproveReservationsListAdapter.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.aprove_reservation_list_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]


        holder.reservationName.text = item.Manager
        val date = Date(item.hour.seconds * 1000)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        holder.reservationSchedule.text = formattedDate
        val context = holder.itemView.context  // Get the context from the ViewHolder's itemView


        // Approve Button Click
        holder.btnAprove.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Aprobar reserva")
            builder.setMessage("¿Estás seguro de que quieres aprobar esta reserva?")
            builder.setPositiveButton("Si") { dialogInterface: DialogInterface, i: Int ->
                val reservationApproved = FirebaseUtils().getDocumentReferenceById("reservas", item.id)
                val currentPosition = holder.adapterPosition // Guarda la posición actual
                db.runBatch() { batch ->
                    batch.update(reservationApproved, "estado", true)
                }.addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Reserva aprobada", Toast.LENGTH_SHORT).show()
                        data.removeAt(currentPosition)
                        notifyItemRemoved(currentPosition)
                    } else {
                        Toast.makeText(context, "Error al aprobar la reserva", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
            builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            }
            builder.show()
        }

        // Deny Button Click
        holder.btnDeny.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Denegar Reserva")
            builder.setMessage("¿Estás seguro de que quieres rechazar este reserva?")
            builder.setPositiveButton("Si") { dialogInterface: DialogInterface, i: Int ->
                val reservationEliminated = FirebaseUtils().getDocumentReferenceById("reservas", item.id)
                val currentPosition = holder.adapterPosition
                db.runBatch() { batch ->
                    batch.delete(reservationEliminated)
                }.addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Reserva eliminada", Toast.LENGTH_SHORT).show()
                        data.removeAt(currentPosition)
                        notifyItemRemoved(currentPosition)
                    } else {
                        Toast.makeText(context, "Error al eliminar la reserva", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->

            }
            builder.show()
        }
    }
}