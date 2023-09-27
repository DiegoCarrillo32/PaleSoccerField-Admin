package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp

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
    description: String,
    status: Boolean,
    startDate: Timestamp,
    endDate: Timestamp,
    imageUrl: String,
    name: String
) {
    var Description: String = description
    var Status: Boolean = status
    var StartDate: Timestamp = startDate
    var EndDate: Timestamp = endDate
    var ImageUrl: String = imageUrl
    var Name: String = name
}