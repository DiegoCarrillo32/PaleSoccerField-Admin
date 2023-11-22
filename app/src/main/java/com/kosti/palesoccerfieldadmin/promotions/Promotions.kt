package com.kosti.palesoccerfieldadmin.promotions

import android.annotation.SuppressLint
import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.PromotionDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class Promotions : AppCompatActivity(), AddEditPromotion.OnDismissListener {

    private val COLLECTION_NAME = "promocion"
    private lateinit var recyclerview: RecyclerView
    private lateinit var promotionList: MutableList<PromotionDataModel>

    private lateinit var tvDateSelected: TextView
    private lateinit var linearLayoutSearchDate: LinearLayout
    private lateinit var searchView: SearchView
    private lateinit var btnShowDialog: Button
    private lateinit var radioGroup: RadioGroup
    private lateinit var radioNamePromotion: RadioButton
    private lateinit var radioDatePromotion: RadioButton
    private var selectedFilterOption: String = "all"
    private var selectedDate: String = "all" // Inicializar con un valor predeterminado

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotions)
        tvDateSelected = findViewById(R.id.tvDateSelected)
        linearLayoutSearchDate = findViewById(R.id.linearLayoutSearchDate)
        btnShowDialog = findViewById(R.id.btnOpenToSelectDate)
        searchView = findViewById(R.id.searchViewPromotion)
        radioGroup = findViewById(R.id.radioGroupFilterPromotion)
        radioNamePromotion = findViewById(R.id.radioNamePromotion)
        radioDatePromotion = findViewById(R.id.radioDatePromotion)

        recyclerview = findViewById(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)
        promotionList = mutableListOf()
        getPromotionData()

        val toolbar: Toolbar = findViewById(R.id.toolbarPromotions)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText.orEmpty(), selectedDate)
                return true
            }
        })

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radioNamePromotion -> {
                    linearLayoutSearchDate.visibility = View.GONE
                    selectedDate = "all" // Restablecer la fecha seleccionada
                    filterList(searchView.query.toString(), selectedDate)
                }
                R.id.radioDatePromotion -> {
                    if(tvDateSelected.text==""){
                        tvDateSelected.text = "..."
                    }
                    fadeInWithTransition(linearLayoutSearchDate)
                }
            }
        }

        btnShowDialog.setOnClickListener {
            mostrarDialogo()
        }
    }
    private fun fadeInWithTransition(view: View) {
        val transition = Fade()
        transition.duration = 500
        TransitionManager.beginDelayedTransition(linearLayoutSearchDate, transition)
        view.visibility = View.VISIBLE
    }

    private fun filterList(query: String, selectedDate: String) {
        val filteredList = when (selectedFilterOption) {
            "all" -> promotionList.filter { promotion ->
                val timestamp: Timestamp = promotion.StartDate
                val date = Date(timestamp.seconds * 1000) // Convierte los segundos a milisegundos
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val formattedDate: String = dateFormat.format(date)
                promotion.Name.contains(query, ignoreCase = true) &&
                        (selectedDate == "all" || formattedDate == selectedDate)
            }
            else -> promotionList // Opción desconocida, muestra todos
        }

        updateAdapterData(filteredList.toMutableList())
    }

    private fun mostrarDialogo() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_select_date)

        val datePicker = dialog.findViewById<DatePicker>(R.id.datePickerPromotion)
        val btnSearch = dialog.findViewById<Button>(R.id.btnSearchDatePickerPromotion)

        btnSearch.setOnClickListener {
            val day = datePicker.dayOfMonth
            val month = datePicker.month + 1
            val year = datePicker.year
            selectedDate = String.format("%02d/%02d/%d", day, month, year)
            tvDateSelected.text = selectedDate
            filterList(searchView.query.toString(), selectedDate)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun getPromotionData() {
        FirebaseUtils().readCollection(COLLECTION_NAME) { result ->
            result.onSuccess {
                promotionList.clear()
                for (promotion in it) {
                    if (promotion["estado"] != true) continue
                    val promotionData = PromotionDataModel(
                        promotion["id"].toString(),
                        promotion["descripcion"].toString(),
                        promotion["estado"] as Boolean,
                        promotion["fecha_final"] as Timestamp,
                        promotion["fecha_inicio"] as Timestamp,
                        promotion["imagen_url"].toString(),
                        promotion["nombre"].toString(),
                    )
                    promotionList.add(promotionData)
                }
                val adapter = PromotionAdapter(promotionList, this)
                recyclerview.adapter = adapter
                recyclerview.adapter?.notifyDataSetChanged()
            }
            result.onFailure {
                // Manejar el error
            }
        }
    }

    private fun updateAdapterData(promotionList: MutableList<PromotionDataModel>) {
        val adapter = PromotionAdapter(promotionList, this)
        recyclerview.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    override fun onDismissOnActivity() {
        getPromotionData()
    }
}
