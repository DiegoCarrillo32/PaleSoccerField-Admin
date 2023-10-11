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
    lateinit var Password: String
    lateinit var Email: String
    var Status: Boolean by Delegates.notNull()
    lateinit var Age: String
    lateinit var Name: String
    lateinit var Positions: MutableList<String>
    lateinit var Role: String
    lateinit var Phone: String
    lateinit var Id: String

    constructor(nick: String,
                bannedList: MutableList<String>,
                clasf:String,
                password: String,
                email: String,
                status: Boolean,
                name: String,
                pos: MutableList<String>,
                role: String,
                phone: String,
                date: String,
                id: String
    ): this () {

         Nickname = nick
         BannedList = bannedList
         Clasification = clasf
         Password= password
         Email = email
         Status = status
         Age = date
         Name = name
         Positions = pos
         Role = role
         Phone = phone
         Id = id
    }

    constructor(name: String,
                clasf:String,
                pos: MutableList<String>,
                nick: String,
                phone: String,
                date: String,
                id: String
                ): this () {

         Nickname  = nick
         Clasification = clasf
         Age = date
         Name= name
         Positions = pos
         Phone = phone
         Id = id
    }
    constructor(name: String,
                nick: String,
                id: String
    ): this () {

         Nickname  = nick
         Name= name
         Id = id
    }
}