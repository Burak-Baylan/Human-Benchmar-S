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
    private var mCtx = context
    private var mActivity = activity
    private var mView = view

    private var storage : FirebaseStorage = FirebaseStorage.getInstance()
    private var storageReference : StorageReference
    private var firebase : FirebaseFirestore
    private var auth : FirebaseAuth
    private var currentUser : FirebaseUser?
    private var userId : String?
    private var firebaseManage : FirebaseManage

    init {
        storageReference = storage.reference
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        userId = currentUser?.uid
        firebaseManage = FirebaseManage(mCtx,mView,mActivity)
    }

    fun getProfilePhoto(ppImageView : ImageView){
        currentUser = auth.currentUser
        val profilePhotoUrl = currentUser?.photoUrl
        if (profilePhotoUrl != null) {
            Picasso.get().load(profilePhotoUrl).into(ppImageView)
            Picasso.get()
            //Toast.makeText(mCtx,"$profilePhotoUrl",Toast.LENGTH_SHORT).show()
        }
        else{
            ppImageView.setImageResource(R.drawable.questionmark)
            //Toast.makeText(mCtx,"$profilePhotoUrl",Toast.LENGTH_SHORT).show()
        }
    }

}