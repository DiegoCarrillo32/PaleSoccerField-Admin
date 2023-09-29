package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp
import kotlin.properties.Delegates

/*
    Datos de un evento_especial

    - descripcion -> Description
    - estado -> Status
    - fecha -> Date
    - imagen_url -> ImageUrl
    - nombre -> Name
 */
class EventoEspecialDataModel() {

    lateinit var Description: String
    var Status: Boolean by Delegates.notNull()
    lateinit var Date: Timestamp
    lateinit var ImageUrl: String
    lateinit var Name: String
    constructor(
        description: String,
        status: Boolean,
        date: Timestamp,
        imageUrl: String,
        name: String
    ) : this() {
         Description = description
         Status = status
         Date= date
         ImageUrl = imageUrl
         Name = name
    }

}