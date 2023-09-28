package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp
import kotlin.properties.Delegates

/*
    Datos de una reseña ( resena en Firebase Storage)

    - comentario -> Feedback
    - estado -> Status
    - fecha -> Date
    - jugador -> PlayerID
 */
class OverviewDataModel() {

    lateinit var Feedback: String
    var Status by Delegates.notNull<Boolean>()
    lateinit var Date: Timestamp
    lateinit var PlayerID: String

    constructor(
        feedback: String,
        status: Boolean,
        date: Timestamp,
        playerID: String
    ) : this() {
        Feedback = feedback
        Status = status
        Date = date
        PlayerID = playerID
    }

}