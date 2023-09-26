package com.kosti.palesoccerfieldadmin.userListPackage

import com.google.firebase.Timestamp
import java.util.Date


/*
* datos de un usuario
*
* apodo
* clasificacion
* bloqueos
* contrasena
* correo
* estado
* fecha_nacimiento
* nombre
* posiciones
* rol
* telefono
* */
public class UserListDataModel(name: String, clasf:String, pos: MutableList<String>, nick: String, phone: String, date: Timestamp) {
    var Name:String = name
    var Nickname: String = nick
    var Clasification: String = clasf;
    var Positions: MutableList<String> = pos;
    var Phone: String = phone
    var Age: Timestamp = date


}