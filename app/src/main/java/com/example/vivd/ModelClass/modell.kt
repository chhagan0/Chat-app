package com.example.vivd.ModelClass

import android.net.Uri
import com.google.android.gms.tasks.Task

class modell {
    var uid:String?=null
    var name:String?=null
    var phoneNumber:String?=null
    var profileImage:String?=null
    constructor(){}
    constructor(
        uid: String?,
        name:String?,
        phoneNumber:String?,
        profileImage:String?
    ){
        this.uid=uid
        this.name=name
        this.phoneNumber=phoneNumber
        this.profileImage=profileImage
    }

}