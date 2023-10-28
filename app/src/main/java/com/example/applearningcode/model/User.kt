package com.example.applearningcode.model

data class User (var correo: String,  var nameUser: String, var uid: String, var user: String ){
    constructor() : this("", "", "", "")
}