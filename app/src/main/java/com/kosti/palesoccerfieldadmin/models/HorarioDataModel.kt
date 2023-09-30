package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp
import kotlin.properties.Delegates

/*
    Datos de una horario

    - estado -> Status
    - fecha -> Date
    - id_reserva -> reservationID
    - tandas -> Choices

 */
class HorarioDataModel() {

    var Status: Boolean by Delegates.notNull()
    lateinit var Date: Timestamp
    lateinit  var ReservationID: String
    lateinit var Choices: MutableList<Timestamp>

    constructor(
        status: Boolean,
        date: Timestamp,
        reservationID: String,
        choices: MutableList<Timestamp>
    ) : this() {
        Status = status
        Date = date
        ReservationID = reservationID
        Choices = choices
    }


}