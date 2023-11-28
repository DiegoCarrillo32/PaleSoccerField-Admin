package com.kosti.palesoccerfieldadmin.personBan

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel

class AdapterBanear(
    private var jugadoresList: List<JugadoresDataModel>,
    private val onBanearDesbanearClick: (JugadoresDataModel) -> Unit
) : RecyclerView.Adapter<AdapterBanear.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nombreTextView: TextView = itemView.findViewById(R.id.nombreUsuarioBAN)
        val estadoTextView: TextView = itemView.findViewById(R.id.estadoUsuarioBAN)
        val btnBanearDesbanear: Button = itemView.findViewById(R.id.banearDesbanearButton)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val jugador = jugadoresList[position]

        holder.nombreTextView.text = jugador.Name
        holder.estadoTextView.text = if (jugador.Status) "La cuenta se encuentra activa."  else "La cuenta se encuentra desactivada."
        holder.btnBanearDesbanear.text = if (jugador.Status) "Desactivar cuenta"  else "Activar Cuenta"

        holder.btnBanearDesbanear.setOnClickListener {
            onBanearDesbanearClick(jugador)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_banear_persona, parent, false)
        return ViewHolder(itemView)
    }

    fun updateData(newData: List<JugadoresDataModel>) {
        jugadoresList = newData
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return jugadoresList.size
    }
}

