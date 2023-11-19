package com.kosti.palesoccerfieldadmin.reservations

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import java.util.Date
import java.util.Locale


class CustomAdapter(private var dataSet: MutableList<ReservasDataModel>, private val context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tV_encargado: TextView
        val tV_fecha: TextView
        val btnEditar: ImageButton
        val btnEliminar: ImageButton

        init {
            // Define click listener for the ViewHolder's View
            tV_encargado = view.findViewById(R.id.tVEncargadoTR)
            tV_fecha = view.findViewById(R.id.tVFechaTR)
            btnEditar = view.findViewById(R.id.btn_editarReserva)
            btnEliminar = view.findViewById(R.id.btn_eliminarReserva)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.activity_reservation_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.tV_encargado.text = dataSet[position].Manager

        val date = Date(dataSet[position].Date.seconds * 1000)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)
        viewHolder.tV_fecha.text = formattedDate


        viewHolder.btnEditar.setOnClickListener {

            Toast.makeText(
                viewHolder.itemView.context,
                "Editar",
                Toast.LENGTH_SHORT,
            ).show()

        }

        viewHolder.btnEliminar.setOnClickListener {

            mostrarDialogConfirmarEliminarReserva(dataSet[position].Manager.toString(),formattedDate,position)


        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size


    private fun mostrarDialogConfirmarEliminarReserva(encargado: String, fecha: String,position: Int) {

        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_confirmar_eliminar_reserva, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()


        val btnCancelar: Button = dialogView.findViewById(R.id.btn_dialog_cancelar)
        val btnEliminar: Button = dialogView.findViewById(R.id.btn_dialog_eliminar)
        val tvEncargado: TextView = dialogView.findViewById(R.id.dialog_tVNombreTR)
        val tvFecha: TextView = dialogView.findViewById(R.id.dialog_tVFechaTR)

        tvEncargado.text = encargado
        tvFecha.text = fecha

        btnEliminar.setOnClickListener {
            FirebaseUtils().deleteDocument("reservas", dataSet[position].id.toString())
            dataSet.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, dataSet.size)
            Toast.makeText(
                context,
                "Reserva eliminada.",
                Toast.LENGTH_SHORT,
            ).show()
            dialog.dismiss()
        }

        btnCancelar.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

}
