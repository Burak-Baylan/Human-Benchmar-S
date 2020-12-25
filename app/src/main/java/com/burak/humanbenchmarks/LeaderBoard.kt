package com.burak.humanbenchmarks

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_leader_board.*

class LeaderBoard : AppCompatActivity() {

    private var firebase = FirebaseFirestore.getInstance()
    private var auth = FirebaseAuth.getInstance()
    private lateinit var viewReal : View
    private var animationControl : animationControl = animationControl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_leader_board)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#252a34")))
        animationControl.forOnCreate(savedInstanceState)

        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#252a34")

        supportActionBar?.title = "Scoreboard"
        supportActionBar?.hide()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        viewReal = window.decorView.rootView
        getNumbersMemoryLeader()
        getReactionTimeLeader()
        goBackButtonInToolbar3.setOnClickListener { finish() }
    }

    private fun getReactionTimeLeader(){
        val snackbarCreater = PopupMessageCreator()
        firebase.collection("Scores").orderBy("ScoreAverage", Query.Direction.ASCENDING).limit(3).addSnapshotListener{ snapshot, exception ->
            if (exception != null) {
                snackbarCreater.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "Leader Board cannot bet installed.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackbarCreater.createFailSnack("Leader Board cannot be installed.", viewReal)
            }
            else {
                if (snapshot != null) {
                    val documents = snapshot.documents
                    var counter = 1
                    for (document in documents) {
                        val usernameCurrent : String = document.get("Username") as String
                        val averageScore: Number = document.get("ScoreAverage") as Number
                        val uidHere : String = document.get("Uid") as String
                        if (averageScore.toInt() != 0) {
                            when (counter) {
                                1 -> {
                                    leaderNullControl(usernameCurrent, averageScore, st1Username, st1Score, "ms"
                                    )
                                    getReactionTimeProfilePhotos(st1Image, uidHere)
                                    counter++
                                }
                                2 -> {
                                    leaderNullControl(usernameCurrent, averageScore, st2Username, st2Score, "ms"
                                    )
                                    getReactionTimeProfilePhotos(st2Image, uidHere)
                                    counter++
                                }
                                3 -> {
                                    leaderNullControl(usernameCurrent, averageScore, st3Username, st3Score, "ms"
                                    )
                                    getReactionTimeProfilePhotos(st3Image, uidHere)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getReactionTimeProfilePhotos(imageView : ImageView, uid : String){

        firebase.collection("ProfilePhotos").document(uid).addSnapshotListener{snapshot, e ->
            if (e != null){
                /** HATA **/
            }
            else{
                if (snapshot != null && snapshot.exists()){
                    val ppUrlHere = snapshot.get("ppurl") as String
                    Picasso.get().load(ppUrlHere).into(imageView)
                }
            }
        }
    }

    private fun getNumbersMemoryLeader(){
        val snackbarCreator = PopupMessageCreator()
        firebase.collection("Scores").orderBy("NumbersScore", Query.Direction.DESCENDING).limit(3).addSnapshotListener{ snapshot, exception ->
            if (exception != null) {
                snackbarCreator.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "Leader Board cannot bet installed.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackbarCreator.createFailSnack("Leader Board cannot be installed.", viewReal)
            }
            else {
                if (snapshot != null) {
                    val documents = snapshot.documents
                    var counter = 1
                    for (document in documents) {
                        val usernameCurrent : String = document.get("Username") as String
                        val lastInit: Number = document.get("NumbersScore") as Number
                        val uidHere : String = document.get("Uid") as String
                        
                        if (lastInit.toInt() != 0) {
                            when (counter) {
                                1 -> {
                                    leaderNullControl(usernameCurrent, lastInit, st1UsernameText, st1ScoreNumbers, "digit"
                                    )
                                    getNumbersMemoryProfilePhotos(st1ImageNumbersMemory, uidHere)
                                    counter++
                                }
                                2 -> {
                                    leaderNullControl(usernameCurrent, lastInit, st2UsernaneText, st2ScoreNumbers, "digit"
                                    )
                                    getNumbersMemoryProfilePhotos(st2ImageNumbersMemory, uidHere)
                                    counter++
                                }
                                3 -> {
                                    leaderNullControl(usernameCurrent, lastInit, st3UsernameText, st3ScoreNumbers, "digit"
                                    )
                                    getNumbersMemoryProfilePhotos(st3ImageNumbersMemory, uidHere)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getNumbersMemoryProfilePhotos(imageView : ImageView, uid : String){

        firebase.collection("ProfilePhotos").document(uid).addSnapshotListener{snapshot, e ->
            if (e != null){
                /** HATA **/
            }
            else{
                if (snapshot != null && snapshot.exists()){
                    val ppUrlHere = snapshot.get("ppurl") as String
                    Picasso.get().load(ppUrlHere).into(imageView)
                }
            }
        }
    }

    private fun leaderNullControl(usernameCurrent : String?, lastInit: Number?, usernamePutText : TextView, scoreTextView : TextView, msOrDigit : String){
        if (usernameCurrent != null){
            usernamePutText.text = usernameCurrent
        }
        if (lastInit != null){
            scoreTextView.text = "$lastInit-$msOrDigit"
        }
    }

    private val userStatusUpdater = UserStatusUpdater()
    override fun onPause() {
        super.onPause()
        userStatusUpdater.statusUpdater("OFFLINE")
    }

    override fun onResume() {
        super.onResume()
        userStatusUpdater.statusUpdater("ONLINE")
    }

}