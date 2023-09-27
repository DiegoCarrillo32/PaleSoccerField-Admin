package com.kosti.palesoccerfieldadmin.models

/*
    Datos de una reserva

    - encargado -> Manager
    - equipo -> Team
    - estado -> Status
    - horario -> ScheduleID (ID del horario)
    - retadores -> opponents
    - tipo -> Type (publica / privada)
 */
public class ReservasDataModel(
    manager: String,
    team: Boolean,
    status: Boolean,
    scheduleID: String,
    opponents: Array<Int>,
    type: Boolean
) {

    var Manager: String = manager
    var Team: Boolean = team
    var Status: Boolean = status
    var ScheduleID: String = scheduleID
    var Opponents: Array<Int> = opponents
    var Type: Boolean = type
}