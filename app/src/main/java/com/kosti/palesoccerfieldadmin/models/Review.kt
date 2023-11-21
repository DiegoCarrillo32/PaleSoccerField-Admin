package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp

data class Review (
    val id: String,
    val comentario: String,
    val estado: Boolean,
    val fecha: Timestamp,
    val jugador: String,


)