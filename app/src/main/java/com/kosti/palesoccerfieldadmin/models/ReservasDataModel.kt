package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp
import kotlin.properties.Delegates

/*
    Datos de una reserva

    - id -> id
    - encargado -> Manager
    - equipo -> Team
    - estado -> Status
    - hour -> reservation time
    - horario -> ScheduleID (ID del horario)
    - retadores -> opponents
    - tipo -> Type (publica / privada)
 */
class ReservasDataModel() {

    lateinit var id: String
    lateinit var Manager: String
    var Team: Boolean by Delegates.notNull()
    var Status: Boolean by Delegates.notNull()
    lateinit var hour: Timestamp
    lateinit var ScheduleID: String
    lateinit var Opponents: Array<Int>
    var Type: Boolean by Delegates.notNull()
    constructor(
        id: String,
        manager: String,
        team: Boolean,
        status: Boolean,
        hourTime: Timestamp,
        scheduleID: String,
        opponents: Array<Int>,
        type: Boolean
    ) : this(){
        this.id = id
        Manager= manager
        Team = team
        Status = status
        hour = hourTime
        ScheduleID= scheduleID
        Opponents = opponents
        Type = type
    }

    constructor(
        id:String,
        manager: String,
        scheduleHour: Timestamp
    ) : this() {
        this.id = id
        Manager= manager
        hour = scheduleHour
    }


}