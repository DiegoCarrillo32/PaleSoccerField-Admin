package com.kosti.palesoccerfieldadmin.promotions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.PromotionDataModel
import com.kosti.palesoccerfieldadmin.models.ScheduleDataModel
import com.kosti.palesoccerfieldadmin.schedules.AddScheduleFragment
import com.kosti.palesoccerfieldadmin.schedules.ScheduleAdapter
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

class Promotions : AppCompatActivity(), AddEditPromotion.OnDismissListener {

    private val COLLECTION_NAME = "promocion"
    private lateinit var recyclerview: RecyclerView
    private lateinit var promotionList: MutableList<PromotionDataModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_promotions)

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

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.schedule_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                val bottomSheetFragment = AddEditPromotion()
                bottomSheetFragment.setOnDismissListener(this)
                bottomSheetFragment.show(supportFragmentManager, "AEPDialogFragment")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun getPromotionData(){
        FirebaseUtils().readCollection(COLLECTION_NAME) {
                result ->
            result.onSuccess {
                promotionList.clear()
                for (promotion in it){
                    if(promotion["estado"] != true) continue

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

            }

        }

    }

    override fun onDismissOnActivity() {
        getPromotionData()
    }
}