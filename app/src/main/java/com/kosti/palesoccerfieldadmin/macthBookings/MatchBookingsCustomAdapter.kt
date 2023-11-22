package com.kosti.palesoccerfieldadmin.macthBookings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.ReservasDataModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MatchBookingsCustomAdapter(private var dataSet: MutableList<ReservasDataModel>, private val context: Context) :
    RecyclerView.Adapter<MatchBookingsCustomAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tV_encargado: TextView
        val tV_fecha: TextView
        val tV_estado: TextView


        init {
            // Define click listener for the ViewHolder's View
            tV_encargado = view.findViewById(R.id.tVNombreTR)
            tV_fecha = view.findViewById(R.id.tVFechaTR)
            tV_estado = view.findViewById(R.id.tVEstadoTR)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_item_booking, viewGroup, false)

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

        viewHolder.tV_estado.text = if (dataSet[position].Status) {
            "Activa"
        } else {
            // Puedes asignar otro texto en caso de que la condición sea falsa
            "Inactiva"
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
