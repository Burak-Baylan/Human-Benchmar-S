package com.burak.humanbenchmarks

import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_chat.*

class UserStatus {

    private val firebase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser = auth.currentUser

    fun statusUpdater(status : String){
        if (currentUser != null) {
            val uId = currentUser.uid
            firebase.collection("Users").document(uId).update("userStatus", status)
        }
    }

    fun addOnlineOrOfflineChangeListener(uid : String, onlineOrOfflineTextView : TextView?, onlineOrOfflineButton : Button?){
        println("gelen uid $uid")
        firebase.collection("Users").document(uid).addSnapshotListener { snapshot, error ->
            if (error == null){
                if (snapshot != null && snapshot.exists()){
                    val status : String = snapshot.get("userStatus") as String
                    println("statusHere $status")
                    if (status == "OFFLINE"){
                        val offlineSpannable = SpannableString("OFFLINE")
                        offlineSpannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            offlineSpannable.length,
                            0
                        )
                        if (onlineOrOfflineTextView != null) {
                            onlineOrOfflineTextView.setTextColor(Color.parseColor("#F44336"))
                            onlineOrOfflineTextView.text = offlineSpannable
                        }
                        else if (onlineOrOfflineButton != null){
                            onlineOrOfflineButton.setBackgroundResource(R.drawable.offline_red)
                        }
                        println("naburda: '$uid' OFFLINE")
                    }
                    else if (status == "ONLINE"){
                        val onlineSpannable = SpannableString("ONLINE")
                        onlineSpannable.setSpan(
                            StyleSpan(Typeface.BOLD),
                            0,
                            onlineSpannable.length,
                            0
                        )
                        if (onlineOrOfflineTextView != null) {
                            onlineOrOfflineTextView.setTextColor(Color.parseColor("#4CAF50"))
                            onlineOrOfflineTextView.text = onlineSpannable
                        }
                        else if (onlineOrOfflineButton != null){
                            onlineOrOfflineButton.setBackgroundResource(R.drawable.online_green)
                        }
                        println("naburda: '$uid' ONLINE")
                    }
                }
                else{
                    println("snapshotp null")
                }
            }
            else{
                println("errorda sourn var")
            }
        }
    }

}