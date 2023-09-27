package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp

/*
    Datos de una reseña ( resena en Firebase Storage)

    - comentario -> Feedback
    - estado -> Status
    - fecha -> Date
    - jugador -> PlayerID
 */
class OverviewDataModel(
    feedback: String,
    status: Boolean,
    date: Timestamp,
    playerID: String
) {
    var Feedback: String = feedback
    var Status: Boolean = status
    var Date: Timestamp = date
    var PlayerID: String = playerID
}