package com.burak.humanbenchmarks

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore

class GetLinkForShareApp (context : Context, view : View, activity : Activity){

    private var mCtx = context
    private var viewReal = view
    private var mActivity = activity

    private var firebase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var firebaseManage : FirebaseManage = FirebaseManage(mCtx, viewReal, mActivity)


    fun share (text : String, tittle : String){

        firebaseManage.loadingScreenStarter(false)

        firebase.collection("Links (Private)").document("appLink").addSnapshotListener { snapshot, error ->

            if (error != null){
                /** PAYLAŞILAMADI **/
                firebaseManage.loadingScreenDestroyer(false)
            }
            else{

                val link = snapshot?.get("link") as String
                val sendIntent = Intent()
                sendIntent.action = Intent.ACTION_SEND
                sendIntent.type = "text/plain"
                val text = text //"Human Benchmarks S in the play store."
                sendIntent.putExtra(Intent.EXTRA_TEXT, "$text\n\n$link")
                mCtx.startActivity(Intent.createChooser(sendIntent,tittle))
                firebaseManage.loadingScreenDestroyer(false)

            }
        }
    }
    
}