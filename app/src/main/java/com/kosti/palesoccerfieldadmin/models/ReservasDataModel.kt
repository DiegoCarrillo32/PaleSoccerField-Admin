package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp
import kotlin.properties.Delegates

/*
    Datos de una reserva

    - idReserva -> id
    - encargado -> Manager
    - equipo -> Team
    - estado -> Status
    - fecha -> Date
    - IDhorario -> ScheduleID (ID del horario)
    - retadores -> opponents
    - jugadores -> players
    - tipo -> Type (Publica / Privada)
 */
class ReservasDataModel() {

    lateinit var id: String
    lateinit var Manager: String
    var Team: Boolean by Delegates.notNull()
    var Status: Boolean by Delegates.notNull()
    lateinit var Date: Timestamp
    lateinit var ScheduleID: String
    lateinit var Opponents: Array<JugadoresDataModel>
    lateinit var Players: Array<JugadoresDataModel>
    lateinit var Type: String

    constructor(
        id: String,
        manager: String,
        team: Boolean,
        status: Boolean,
        date: Timestamp,
        scheduleID: String,
        opponents: Array<JugadoresDataModel>,
        players: Array<JugadoresDataModel>,
        type: String
    ) : this(){
        this.id = id
        Manager= manager
        Team = team
        Status = status
        Date = date
        ScheduleID= scheduleID
        Opponents = opponents
        Players = players
        Type = type
    }

    constructor(
        id:String,
        manager: String,
        date: Timestamp
    ) : this() {
        this.id = id
        Manager= manager
        Date = date
    }

    constructor(
        id:String,
        manager: String,
        date: Timestamp,
        status: Boolean
    ) : this() {
        this.id = id
        Manager= manager
        Date = date
        Status = status
    }
}