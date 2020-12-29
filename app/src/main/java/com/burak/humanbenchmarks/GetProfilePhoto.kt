package com.burak.humanbenchmarks

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class GetProfilePhoto (context : Context, activity : Activity, view : View) {
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser : FirebaseUser? = auth.currentUser


    fun getProfilePhoto(ppImageView : ImageView){
        val profilePhotoUrl = currentUser?.photoUrl
        if (profilePhotoUrl != null) {
            Picasso.get().load(profilePhotoUrl).into(ppImageView)
            Picasso.get()
        }
        else{
            ppImageView.setImageResource(R.drawable.questionmark)
        }
    }

}