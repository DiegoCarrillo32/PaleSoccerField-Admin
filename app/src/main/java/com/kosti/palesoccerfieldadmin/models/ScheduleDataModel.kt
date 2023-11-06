package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp

class ScheduleDataModel(var id: String, var fecha: Timestamp?, var tanda: MutableList<Timestamp>?) {

}