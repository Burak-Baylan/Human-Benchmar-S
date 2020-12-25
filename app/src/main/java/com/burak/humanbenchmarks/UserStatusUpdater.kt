package com.burak.humanbenchmarks

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class UserStatusUpdater {

    private val firebase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    fun statusUpdater(status : String){
        if (currentUser != null) {
            val uId = currentUser.uid
            firebase.collection("Users").document(uId).update("userStatus", status)
        }
    }

}