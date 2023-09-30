package com.kosti.palesoccerfieldadmin.aproveUsers

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel

class AproveUserListAdapter (private val context: Context, private val data: List<JugadoresDataModel>): BaseAdapter() {
    private lateinit var userName: TextView
    private lateinit var userNickname: TextView


    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(position: Int): Any {
        return data[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        convertView = LayoutInflater.from(context).inflate(R.layout.aprove_user_list_item, parent, false)
        userName = convertView.findViewById(R.id.tvUserName)
        userName.text = data[position].Name;
        userNickname = convertView.findViewById(R.id.tvUserNickname)
        return convertView
    }
}