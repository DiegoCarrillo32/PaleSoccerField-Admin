package com.kosti.palesoccerfieldadmin.SpecialEvents

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.EventoEspecialDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import com.squareup.picasso.Picasso
import java.util.Date
import java.util.Locale

class SpecialEventsCustomAdapter(private var dataSet: MutableList<EventoEspecialDataModel>, private val context: Context) :
    RecyclerView.Adapter<SpecialEventsCustomAdapter.ViewHolder>(),
    FragmentEditAddSpecialEvent.OnDismissListener {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tV_nombre: TextView
        val tV_descripcion: TextView
        val tV_estado: TextView
        val tV_fecha: TextView
        val btnEditar: ImageButton
        val btnEliminar: ImageButton
        val imagen: ImageView

        init {
            // Define click listener for the ViewHolder's View
            tV_nombre = view.findViewById(R.id.tV_nombre_evento_especial)
            tV_fecha = view.findViewById(R.id.tV_fecha_evento_especial)
            tV_descripcion = view.findViewById(R.id.tV_descripcion_evento_especial)
            tV_estado = view.findViewById(R.id.tV_estado_evento_especial)
            btnEditar = view.findViewById(R.id.btn_editar_evento_especial)
            btnEliminar = view.findViewById(R.id.btn_eliminar_evento_especial)
            imagen = view.findViewById(R.id.imagen_evento_especial)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.special_events_list_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.tV_nombre.text = dataSet[position].Name
        viewHolder.tV_descripcion.text = dataSet[position].Description
        viewHolder.tV_fecha.text = dataSet[position].Date.toDate().toString()

        val date = Date(dataSet[position].Date.seconds * 1000)
        val dateFormat = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(date)

        viewHolder.tV_fecha.text = formattedDate

        if (dataSet[position].Status){
            viewHolder.tV_estado.text = "Activa"
        }else{
            viewHolder.tV_estado.text = "Inactiva"
        }

        if(dataSet[position].ImageUrl != "")Picasso.get().load(dataSet[position].ImageUrl).into(viewHolder.imagen)

        viewHolder.btnEditar.setOnClickListener {

            val bundle = Bundle()
            val fragmentSpecialEvent = FragmentEditAddSpecialEvent()
            fragmentSpecialEvent.setOnDismissListener(this)

            bundle.putString("id", dataSet[position].id)
            bundle.putString("name", dataSet[position].Name)
            bundle.putString("description", dataSet[position].Description)
            bundle.putSerializable("date", dataSet[position].Date.toDate())
            bundle.putBoolean("status", dataSet[position].Status)
            bundle.putString("imageUrl",dataSet[position].ImageUrl)
            fragmentSpecialEvent.arguments = bundle
            fragmentSpecialEvent.show((context as SpecialEvents).supportFragmentManager, "BSDialogFragment")

        }

        viewHolder.btnEliminar.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(context)
            builder.setTitle("Eliminar evento especial")
            builder.setMessage("¿Estas seguro que deseas eliminar este evento especial?")
            builder.setPositiveButton("Si") { dialog, which ->
                FirebaseUtils().deleteImage(dataSet[position].ImageUrl);
                FirebaseUtils().deleteDocument("evento_especial", dataSet[position].id)
                dataSet.removeAt(position)
                notifyItemRemoved(position)
                notifyItemRangeChanged(position, dataSet.size)
            }
            builder.setNeutralButton("Cancelar") {_,_ ->
                //Toast.makeText(context,"You cancelled the dialog.",Toast.LENGTH_SHORT).show()
            }
            builder.show()
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size
    override fun onDismissOnActivity() {
        consultarDatosEventosEspecialesFirebase()
    }

    fun consultarDatosEventosEspecialesFirebase() {
        FirebaseUtils().readCollection("evento_especial") { result ->
            result.onSuccess {
                dataSet.clear()
                for(eventoEspecial in it){
                    //if (eventoEspecial["estado"] != true)continue
                    dataSet.add(
                        EventoEspecialDataModel(
                            eventoEspecial["id"].toString(),
                            eventoEspecial["descripcion"].toString(),
                            eventoEspecial["estado"] as Boolean,
                            eventoEspecial["fecha"] as Timestamp,
                            eventoEspecial["imagen_url"].toString(),
                            eventoEspecial["nombre"].toString()
                        )
                    )

                }
                notifyDataSetChanged()
            }
            result.onFailure {
            }
        }
    }
}
