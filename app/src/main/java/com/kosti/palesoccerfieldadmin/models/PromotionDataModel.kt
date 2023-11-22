package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp
import kotlin.properties.Delegates

/*
    Datos de una promocion

    - descripcion -> Description
    - estado -> Status
    - fecha_inicial -> StartDate
    - fecha_final -> EndDate
    - imagen_url -> ImageUrl
    - nombre -> Name
 */
class PromotionDataModel(
) {
    lateinit var Id: String
    lateinit var Description: String
    var Status: Boolean by Delegates.notNull()
    lateinit var StartDate: Timestamp
    lateinit var EndDate: Timestamp
    lateinit var ImageUrl: String
    lateinit var Name: String

    constructor(
        id: String,
        description: String,
        status: Boolean,
        startDate: Timestamp,
        endDate: Timestamp,
        imageUrl: String,
        name: String
    ) : this() {
         Id = id
         Description = description
         Status = status
         StartDate = startDate
         EndDate = endDate
         ImageUrl = imageUrl
         Name = name
    }

    constructor(
        description: String,
        status: Boolean,
        startDate: Timestamp,
        endDate: Timestamp,
        imageUrl: String,
        name: String
    ) : this() {
         Description = description
         Status = status
         StartDate = startDate
         EndDate = endDate
         ImageUrl = imageUrl
         Name = name
    }


}