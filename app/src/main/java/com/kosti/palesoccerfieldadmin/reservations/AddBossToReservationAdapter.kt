package com.kosti.palesoccerfieldadmin.reservations.addUsersToReservations

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel

class AddBossToReservationAdapter(
    private var data: MutableList<JugadoresDataModel>,
    private var addClickListener: (JugadoresDataModel) -> Unit
) : RecyclerView.Adapter<AddBossToReservationAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameProfile: TextView = itemView.findViewById(R.id.user_list_reservation_boss_name)
        val classifierProfile: TextView =
            itemView.findViewById(R.id.user_list_reservation_boss_classifier)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.user_list_add_to_boss_in_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = data[position]

        holder.nameProfile.text = userData.Name
        holder.classifierProfile.text = userData.Clasification

        // Configurar el clic en el elemento para llamar a la función de clic externa
        holder.itemView.setOnClickListener {
            addClickListener(userData)
        }
    }

    override fun getItemCount(): Int = data.size

    fun filter(text: String) {
        val filteredList = ArrayList<JugadoresDataModel>()
        for (user in data) {
            if (user.Name.contains(text, ignoreCase = true) || user.Nickname.contains(
                    text,
                    ignoreCase = true
                )
            ) {
                filteredList.add(user)
            }
        }
        data = filteredList
        notifyDataSetChanged()
    }
    fun setData(newData: List<JugadoresDataModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()

        Log.d("Adapter", "Número de elementos después de setData: ${data.size}")
    }

}
