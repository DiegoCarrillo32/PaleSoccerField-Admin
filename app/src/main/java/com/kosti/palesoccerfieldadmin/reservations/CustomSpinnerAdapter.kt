package com.kosti.palesoccerfieldadmin.reservations
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.kosti.palesoccerfieldadmin.R

class CustomSpinnerAdapter(
    context: Context,
    resource: Int,
    objects: List<String>
) : ArrayAdapter<String>(context, resource, objects) {

    private val inflater: LayoutInflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, isDropdown = false)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createViewFromResource(position, convertView, parent, isDropdown = true)
    }

    private fun createViewFromResource(
        position: Int,
        convertView: View?,
        parent: ViewGroup,
        isDropdown: Boolean
    ): View {
        val view = convertView ?: inflater.inflate(R.layout.custom_spinner, parent, false)

        val textView = view.findViewById<TextView>(android.R.id.text1)
        val imageView = view.findViewById<ImageView>(R.id.spinnerIcon)

        textView.text = getItem(position)

        // Ocultar el icono en la vista desplegable
        imageView.visibility = if (isDropdown) View.GONE else View.VISIBLE

        return view
    }
}