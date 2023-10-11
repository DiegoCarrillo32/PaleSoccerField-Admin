import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel

class BlockedUsersListAdapter(private val originalData: ArrayList<JugadoresDataModel>) : RecyclerView.Adapter<BlockedUsersListAdapter.ViewHolder>() {
    private var data = ArrayList(originalData)

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgProfile: ImageView = itemView.findViewById(R.id.user_img_Blocked_Users_list)
        val nameProfile: TextView = itemView.findViewById(R.id.user_name_Blocked_Users_list)
        val nickNameProfile: TextView = itemView.findViewById(R.id.user_nickname_Blocked_Users_list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.blocked_user_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userData = data[position]

        // Configura las vistas con los datos correspondientes
        holder.imgProfile.setImageResource(0)
        holder.nameProfile.text = userData.Name
        holder.nickNameProfile.text = userData.Nickname
    }

    override fun getItemCount(): Int = data.size

    fun filter(text: String) {
        val filteredList = ArrayList<JugadoresDataModel>()
        for (user in originalData) {
            if (user.Name.contains(text, ignoreCase = true) || user.Nickname.contains(text, ignoreCase = true)) {
                filteredList.add(user)
            }
        }
        data = filteredList
        notifyDataSetChanged()
    }

    fun restoreOriginalData() {
        data = ArrayList(originalData)
        notifyDataSetChanged()
    }
}
