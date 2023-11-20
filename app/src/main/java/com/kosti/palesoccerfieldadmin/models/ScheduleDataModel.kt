package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp

class ScheduleDataModel {
    var id: String = ""
    var fecha: Timestamp? = null
    var tanda: MutableList<Timestamp>? = null
    var reservado: Boolean = false
    private var textoHorario: String = ""

    constructor(ID: String, date: Timestamp?, round: MutableList<Timestamp>?, state:Boolean, txt:String) {
        id = ID
        fecha = date
        tanda = round
        reservado = state
        textoHorario = txt
    }

    constructor(ID: String, date: Timestamp?, round: MutableList<Timestamp>?, state:Boolean) {
        id = ID
        fecha = date
        tanda = round
        reservado = state
    }

    fun getTextoHorario() = textoHorario


    companion object {
        fun findByScheduleText(lista: List<ScheduleDataModel>, textoHorarioBuscado: String): ScheduleDataModel? {
            return lista.find { it.textoHorario == textoHorarioBuscado }
        }
    }


}