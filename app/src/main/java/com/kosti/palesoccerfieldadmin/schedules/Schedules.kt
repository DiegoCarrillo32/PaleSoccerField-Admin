package com.kosti.palesoccerfieldadmin.schedules


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.kosti.palesoccerfieldadmin.R
import com.kosti.palesoccerfieldadmin.models.JugadoresDataModel
import com.kosti.palesoccerfieldadmin.models.ScheduleDataModel
import com.kosti.palesoccerfieldadmin.utils.FirebaseUtils

import java.util.Calendar


class Schedules : AppCompatActivity(), AddScheduleFragment.OnDismissListener {
    private val COLLECTION_NAME = "horario"


    private lateinit var recyclerview: RecyclerView
    private lateinit var scheduleList: MutableList<ScheduleDataModel>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedules)
        recyclerview = findViewById(R.id.recyclerView)
        recyclerview.layoutManager = LinearLayoutManager(this)

        val toolbar: Toolbar = findViewById(R.id.toolbarSchedule)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener {
            finish()
        }


        scheduleList = mutableListOf()
        getScheduleData()

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.schedule_toolbar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                val bottomSheetFragment = AddScheduleFragment()
                bottomSheetFragment.setOnDismissListener(this)
                bottomSheetFragment.show(supportFragmentManager, "BSDialogFragment")
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }



    fun getScheduleData(){
        FirebaseUtils().readCollection(COLLECTION_NAME) {
            result ->
            result.onSuccess {
                for (schedule in it){
                    if(schedule["fecha"] == null || schedule["tanda"] == null) continue
                    val scheduleData = ScheduleDataModel(
                        schedule["id"] as String,
                        schedule["fecha"] as Timestamp,
                        schedule["tanda"] as MutableList<Timestamp>
                    )
                    scheduleList.add(scheduleData)

                }
                val adapter = ScheduleAdapter(scheduleList, this)
                recyclerview.adapter = adapter
            }
            result.onFailure {

            }

        }

    }

    override fun onDismissOnActivity() {
        FirebaseUtils().readCollection(COLLECTION_NAME) {
                result ->
            result.onSuccess {
                scheduleList.clear()
                for (schedule in it){
                    if(schedule["fecha"] == null || schedule["tanda"] == null) continue
                    val scheduleData = ScheduleDataModel(
                        schedule["id"] as String,
                        schedule["fecha"] as Timestamp,
                        schedule["tanda"] as MutableList<Timestamp>
                    )
                    scheduleList.add(scheduleData)

                }
                recyclerview.adapter?.notifyDataSetChanged()
            }
            result.onFailure {

            }

        }
        Toast.makeText(this, "Se actualizo la informacion", Toast.LENGTH_LONG).show()
    }


}