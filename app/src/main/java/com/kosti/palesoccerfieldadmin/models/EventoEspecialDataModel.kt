package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp

/*
    Datos de un evento_especial

    - descripcion -> Description
    - estado -> Status
    - fecha -> Date
    - imagen_url -> ImageUrl
    - nombre -> Name
 */
class EventoEspecialDataModel(
    description: String,
    status: Boolean,
    date: Timestamp,
    imageUrl: String,
    name: String
) {

    var Description: String = description
    var Status: Boolean = status
    var Date: Timestamp = date
    var ImageUrl: String = imageUrl
    var Name: String = name
}