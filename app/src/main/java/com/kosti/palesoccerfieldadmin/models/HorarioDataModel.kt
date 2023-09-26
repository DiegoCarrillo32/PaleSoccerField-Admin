package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp

/*
    Datos de una horario

    - estado -> Status
    - fecha -> Date
    - id_reserva -> reservationID
    - tandas -> Choices

 */
class HorarioDataModel(
    status: Boolean,
    date: Timestamp,
    reservationID: String,
    choices: MutableList<Timestamp>
) {

    var Status: Boolean = status
    var Date: Timestamp = date
    var ReservationID: String = reservationID
    var Choices: MutableList<Timestamp> = choices

}