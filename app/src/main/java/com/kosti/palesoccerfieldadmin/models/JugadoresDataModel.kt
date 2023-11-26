package com.kosti.palesoccerfieldadmin.models

import com.google.firebase.Timestamp
import kotlin.properties.Delegates


/*
*   Datos de un usuario

    - apodo -> Nickname
    - bloqueos -> bannedList
    - clasificacion -> Clasification
    - contrasena -> Password
    - correo -> Email
    - estado -> Status
    - fecha_nacimiento -> Age
    - nombre -> Name
    - posiciones -> Positions
    - rol -> Role
    - telefono -> Phone
    *d
* */
public class JugadoresDataModel() {

    lateinit var Nickname: String
    lateinit var BannedList: MutableList<String>
    lateinit var Clasification: String
    lateinit var Email: String
    var Status: Boolean by Delegates.notNull()
    lateinit var Age: String
    lateinit var Name: String
    lateinit var Positions: MutableList<String>
    lateinit var Role: String
    lateinit var Phone: String
    lateinit var Id: String
    lateinit var UID: String

    constructor(
        nick: String,
        bannedList: MutableList<String>,
        clasf: String,
        email: String,
        status: Boolean,
        name: String,
        pos: MutableList<String>,
        role: String,
        phone: String,
        date: String,
        id: String,
        uid: String
    ) : this() {

        Nickname = nick
        BannedList = bannedList
        Clasification = clasf
        Email = email
        Status = status
        Age = date
        Name = name
        Positions = pos
        Role = role
        Phone = phone
        Id = id
        UID = uid
    }

    constructor(
        name: String,
        clasf: String,
        pos: MutableList<String>,
        nick: String,
        phone: String,
        date: String,
        id: String,
    ) : this() {

        Nickname = nick
        Clasification = clasf
        Age = date
        Name = name
        Positions = pos
        Phone = phone
        Id = id
    }



    constructor(
        nombre: String,
        nickname: String,
        uid: String,
        id: String,
        mail: String,
    ) : this() {
        Name = nombre
        Nickname = nickname
        UID = uid
        Id = id
        Email = mail
    }
    constructor(
        name: String,
        nickname: String,
        clasification: String,
        pos: MutableList<String>,
        id: String,
        uid:String
    ) : this() {
        Name = name
        Nickname = nickname
        Clasification = clasification
        Positions= pos
        Id = id
        UID=uid
    }

    constructor(
        documentID : String,
        nombre : String,
        estado : Boolean
    ) : this(){
        UID = documentID
        Name = nombre
        Status = estado
    }
}