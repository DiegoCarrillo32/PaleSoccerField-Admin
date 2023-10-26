import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel

class BlockedUsersListAdapter(
    private var data: MutableList<JugadoresDataModel>,
    private var blockClickListener: (JugadoresDataModel) -> Unit
) : RecyclerView.Adapter<BlockedUsersListAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.user_img_Blocked_Users_list)
        val nameProfile: TextView = itemView.findViewById(R.id.user_name_Blocked_Users_list)
        val nickNameProfile: TextView = itemView.findViewById(R.id.user_nickname_Blocked_Users_list)
        val blockButton: ImageButton = itemView.findViewById(R.id.blockButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blocked_user_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = data[position]

        holder.imgProfile.setImageResource(0)
        holder.nameProfile.text = userData.Name
        holder.nickNameProfile.text = userData.Nickname

        holder.blockButton.setOnClickListener {
            blockClickListener(userData)
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

    fun restoreOriginalData() {
        // Implementar si es necesario
    }

    fun setData(newData: List<JugadoresDataModel>) {
        data.clear()
        data.addAll(newData)
        notifyDataSetChanged()
    }
}
