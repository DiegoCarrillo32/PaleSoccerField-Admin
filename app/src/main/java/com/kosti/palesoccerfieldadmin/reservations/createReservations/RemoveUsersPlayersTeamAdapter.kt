package com.kosti.palesoccerfieldadmin.reservations.createReservations

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel

class RemoveUsersPlayersTeamAdapter(
    private var data: MutableList<JugadoresDataModel>,
    private var removeClickListener: (JugadoresDataModel) -> Unit
) : RecyclerView.Adapter<RemoveUsersPlayersTeamAdapter.ViewHolder>() {


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameProfile: TextView = itemView.findViewById(R.id.user_list_reservation_Remove_user_name)
        val nickNameProfile: TextView = itemView.findViewById(R.id.user_list_reservation_Remove_user_nickname)
        val positionProfile: TextView = itemView.findViewById(R.id.user_list_reservation_Remove_user_position)
        val classifierProfile: TextView = itemView.findViewById(R.id.user_list_reservation_Remove_user_classifier)
        val btnRemove: ImageButton = itemView.findViewById(R.id.bnt_user_list_reservation_Remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.users_list_to_remove_in_reservation, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = data[position]

        holder.nameProfile.text = userData.Name
        holder.nickNameProfile.text = userData.Nickname
        holder.positionProfile.text = toConvertListtoString(userData.Positions)
        holder.classifierProfile.text = userData.Clasification



        // Configurar el clic en el elemento para llamar a la función de clic externa
        holder.btnRemove.setOnClickListener {
            Log.d("RemoveUsers", "Aqui llegamos a tocar el btn")
            removeClickListener(userData)
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
        data = filteredList
        notifyDataSetChanged()
    }

    fun toConvertListtoString(list: MutableList<String>): String {
        return list.joinToString(separator = "\n")
    }

    fun setData(newData: List<JugadoresDataModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
}
