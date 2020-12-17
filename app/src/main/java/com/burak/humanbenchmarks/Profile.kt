package com.burak.humanbenchmarks

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_profile.*


private lateinit var firebaseManage : FirebaseManage
private lateinit var viewReal : View
private lateinit var firestore : FirebaseFirestore
private lateinit var auth : FirebaseAuth
private lateinit var currentUser : FirebaseUser
private lateinit var clickListeners: ProfileActivityClickListeners
private lateinit var currentUsername : String

class Profile : AppCompatActivity() {

    private var animationControl : animationControl = animationControl(this)

    override fun onStart() {
        animationControl.forOnStart()
        super.onStart()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_profile)
        animationControl.forOnCreate(savedInstanceState)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#0E573D")))
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#0E573D")

        forLateInit()
        firebaseManage.getUser(usernameText, viewReal, welcomeControl = true)
        onClickListeners()

        /** Get Profile Photo **/
        val getProfilePhoto = GetProfilePhoto(this,this, viewReal)
        getProfilePhoto.getProfilePhoto(ppImage)
    }

    private fun forLateInit(){
        supportActionBar?.hide()
        firestore = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        emailTextView.text = currentUser.email
        viewReal = window.decorView.rootView
        firebaseManage = FirebaseManage(this, viewReal, this)
        clickListeners = ProfileActivityClickListeners(this, this, viewReal)
    }

    private fun onClickListeners (){
        clickListeners.historyCardView(myHistoriesCard)
        clickListeners.changePpCardView(changePpCard)
        clickListeners.changeUsernameCardView(changeUsernameCard)
        clickListeners.changePasswordCardView(changePasswordCard)
        clickListeners.changeEmailCardView(changeEmailCard)
        clickListeners.deleteAccountCardView(deleteAccountCard)
        clickListeners.achievementsCardView(myAchievementsCard)

        ppImage.setOnClickListener {
            val intent = Intent(this, ChangePp::class.java)
            startActivity(intent)
        }
    }
}