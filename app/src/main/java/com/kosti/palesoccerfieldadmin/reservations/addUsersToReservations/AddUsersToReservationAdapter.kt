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

class AddUsersToReservationAdapter(
    private var data: MutableList<JugadoresDataModel>,
    private var addClickListener: (JugadoresDataModel) -> Unit
) : RecyclerView.Adapter<AddUsersToReservationAdapter.ViewHolder>() {

    // Lista de usuarios seleccionados
    private val selectedUsers: MutableList<JugadoresDataModel> = mutableListOf()
    private var originalData: MutableList<JugadoresDataModel> = data.toMutableList()
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameProfile: TextView = itemView.findViewById(R.id.user_list_reservation_user_name)
        val nickNameProfile: TextView = itemView.findViewById(R.id.user_list_reservation_user_nickname)
        val positionProfile: TextView = itemView.findViewById(R.id.user_list_reservation_user_position)
        val classifierProfile: TextView = itemView.findViewById(R.id.user_list_reservation_user_classifier)
        val checkBox: CheckBox = itemView.findViewById(R.id.user_list_reservation_checkbox)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.users_list_to_add_in_reservations, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = data[position]

        holder.nameProfile.text = userData.Name
        holder.nickNameProfile.text = userData.Nickname
        holder.positionProfile.text = toConvertListtoString(userData.Positions)
        holder.classifierProfile.text = userData.Clasification

        // Establecer el estado del CheckBox según si el usuario está seleccionado o no
        holder.checkBox.isChecked = selectedUsers.contains(userData)

        // Configurar el listener del CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Agregar usuario a la lista de usuarios seleccionados
                selectedUsers.add(userData)
            } else {
                // Eliminar usuario de la lista de usuarios seleccionados
                selectedUsers.remove(userData)
            }
        }

        // Configurar el clic en el elemento para llamar a la función de clic externa
        holder.itemView.setOnClickListener {
            addClickListener(userData)
        }
    }

    override fun getItemCount(): Int = data.size

    fun filter(text: String) {
        val filteredList = ArrayList<JugadoresDataModel>()
        for (user in data) {
            if (user.Name.contains(text, ignoreCase = true) || user.Nickname.contains(text, ignoreCase = true)) {
                filteredList.add(user)
            }
        }
        setData(filteredList)
    }


    fun toConvertListtoString(list: MutableList<String>): String {
        return list.joinToString(separator = "\n")
    }

    fun setData(newData: List<JugadoresDataModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }

    // Método para obtener la lista de usuarios seleccionados
    fun getSelectedUsers(): List<JugadoresDataModel> {
        return selectedUsers.toList()
    }

}
