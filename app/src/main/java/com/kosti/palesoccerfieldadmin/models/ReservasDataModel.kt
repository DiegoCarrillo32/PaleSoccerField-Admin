package com.kosti.palesoccerfieldadmin.models

import kotlin.properties.Delegates

/*
    Datos de una reserva

    - encargado -> Manager
    - equipo -> Team
    - estado -> Status
    - horario -> ScheduleID (ID del horario)
    - retadores -> opponents
    - tipo -> Type (publica / privada)
 */
class ReservasDataModel() {

    lateinit var Manager: String
    var Team: Boolean by Delegates.notNull()
    var Status: Boolean by Delegates.notNull()
    lateinit var ScheduleID: String
    lateinit var Opponents: Array<Int>
    var Type: Boolean by Delegates.notNull()
    constructor(
        manager: String,
        team: Boolean,
        status: Boolean,
        scheduleID: String,
        opponents: Array<Int>,
        type: Boolean
    ) : this(){
        Manager= manager
        Team = team
        Status = status
        ScheduleID= scheduleID
        Opponents = opponents
        Type = type
    }


}