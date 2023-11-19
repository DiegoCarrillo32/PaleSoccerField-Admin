package com.kosti.palesoccerfieldadmin.reservations

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import java.util.Date
import java.util.Locale


class CustomAdapter(private var dataSet: List<ReservasDataModel>, private val context: Context) :
    RecyclerView.Adapter<CustomAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tV_encargado: TextView
        val tV_fecha: TextView
        val btnEditar: ImageButton
        val btnEliminar: ImageButton

        init {
            // Define click listener for the ViewHolder's View
            tV_encargado = view.findViewById(R.id.tVNombreTR)
            tV_fecha = view.findViewById(R.id.tVDescripcionTR)
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

            Toast.makeText(
                viewHolder.itemView.context,
                "Eliminar.",
                Toast.LENGTH_SHORT,
            ).show()

        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
