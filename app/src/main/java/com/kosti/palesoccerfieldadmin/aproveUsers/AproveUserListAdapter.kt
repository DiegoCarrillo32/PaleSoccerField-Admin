package com.kosti.palesoccerfieldadmin.aproveUsers

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.google.firebase.firestore.FirebaseFirestore
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class AproveUserListAdapter (private val context: Context, private val data: MutableList<JugadoresDataModel>):
    RecyclerView.Adapter<AproveUserListAdapter.ViewHolder>() {
    private lateinit var userName: TextView
    private lateinit var userNickname: TextView
    private lateinit var db: FirebaseFirestore
    private var playerToApproveOrDeny: String? = null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AproveUserListAdapter.ViewHolder {
        return AproveUserListAdapter.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.aprove_user_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AproveUserListAdapter.ViewHolder, position: Int) {
        val item = data[position]
        holder.userName.text = item.Name
        holder.userNickname.text = buildString {
            append("( ")
            append(item.Nickname)
            append(" )")
        }

        val context = holder.itemView.context  // Get the context from the ViewHolder's itemView

        // Approve Button Click
        holder.btnAprove.setOnClickListener {
            val db = FirebaseFirestore.getInstance()
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Aprobar usuario")
            builder.setMessage("¿Estás seguro de que quieres aprobar este usuario?")
            builder.setPositiveButton("Si") { dialogInterface: DialogInterface, i: Int ->
                val userApproved = FirebaseUtils().getDocumentReferenceById("jugadores", item.Id)
                val currentPosition = holder.adapterPosition // Guarda la posición actual
                db.runBatch() { batch ->
                    batch.update(userApproved, "estado", true)
                }.addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Usuario aprobado", Toast.LENGTH_SHORT).show()
                        playerToApproveOrDeny = item.Id
                        data.removeAt(currentPosition) // Usa la posición guardada
                        notifyItemRemoved(currentPosition)
                        showDialogClassifier()
                    } else {
                        Toast.makeText(context, "Error al aprobar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
            }
            builder.show()
        }

        // Deny Button Click
        holder.btnDeny.setOnClickListener {
            val db = FirebaseFirestore.getInstance()

            val builder = AlertDialog.Builder(context)
            builder.setTitle("Denegar usuario")
            builder.setMessage("¿Estás seguro de que quieres denegar este usuario?")
            builder.setPositiveButton("Si") { dialogInterface: DialogInterface, i: Int ->
                val userEliminated = FirebaseUtils().getDocumentReferenceById("jugadores", item.Id)
                val currentPosition = holder.adapterPosition // Guarda la posición actual
                db.runBatch() { batch ->
                    batch.delete(userEliminated)
                }.addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(context, "Usuario eliminado", Toast.LENGTH_SHORT).show()
                        playerToApproveOrDeny = null
                        data.removeAt(currentPosition)
                        notifyItemRemoved(currentPosition)
                    } else {
                        Toast.makeText(context, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            builder.setNegativeButton("Cancelar") { dialogInterface: DialogInterface, i: Int ->
                // TODO: do nothing or handle cancel action
            }
            builder.show()
        }
    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.tvUserName)
        val userNickname: TextView = itemView.findViewById(R.id.tvUserNickname)
        val btnAprove: ImageButton = itemView.findViewById(R.id.acceptBtn)
        val btnDeny: ImageButton = itemView.findViewById(R.id.denyBtn)
    }
    fun showDialogClassifier() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_classifier, null)
        val builder = AlertDialog.Builder(context)
        builder.setView(dialogView)
        val dialog = builder.create()
        val playerId = playerToApproveOrDeny
        val imageButtonBad: ImageButton = dialogView.findViewById(R.id.imageButtonBad)
        val imageButtonRegular: ImageButton = dialogView.findViewById(R.id.imageButtonRegular)
        val imageButtonGood: ImageButton = dialogView.findViewById(R.id.imageButtonGood)

        if (playerId != null) {
            imageButtonBad.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val userApproved = db.collection("jugadores").document(playerId)
                db.runBatch() { batch ->
                    batch.update(userApproved, "clasificacion", "Malo")
                }.addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        data.removeAll { it.Id == playerId }
                        playerToApproveOrDeny = null
                        Toast.makeText(context, "Usuario calificado", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Error al aprobar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            imageButtonRegular.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val userApproved = db.collection("jugadores").document(playerId)
                db.runBatch() { batch ->
                    batch.update(userApproved, "clasificacion", "Regular")
                }.addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        data.removeAll { it.Id == playerId }
                        playerToApproveOrDeny = null
                        Toast.makeText(context, "Usuario calificado", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Error al aprobar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            imageButtonGood.setOnClickListener {
                val db = FirebaseFirestore.getInstance()
                val userApproved = db.collection("jugadores").document(playerId)
                db.runBatch() { batch ->
                    batch.update(userApproved, "clasificacion", "Good")
                }.addOnCompleteListener() { task ->
                    if (task.isSuccessful) {
                        data.removeAll { it.Id == playerId }
                        playerToApproveOrDeny = null
                        Toast.makeText(context, "Usuario calificado", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    } else {
                        Toast.makeText(context, "Error al aprobar el usuario", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(context, "No hay usuario para aprobar/denegar", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }
}