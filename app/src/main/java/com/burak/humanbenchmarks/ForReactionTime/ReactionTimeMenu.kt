package com.burak.humanbenchmarks.ForReactionTime

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.burak.humanbenchmarks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reaction_time_menu.*
import kotlinx.android.synthetic.main.activity_reaction_time_menu.leaderBoardLayout

class ReactionTimeMenu : AppCompatActivity() {

    private lateinit var viewReal : View
    private lateinit var snackCreater : SnackbarCreater
    private lateinit var firebaseManage: FirebaseManage
    private lateinit var olmasiGerekTextView : TextView
    private lateinit var nameText : TextView
    private lateinit var achievementsControl: AchievementsControl
    private lateinit var auth : FirebaseAuth
    private var currentUser : FirebaseUser? = null
    private lateinit var firebase : FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_time_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reaction Time"
        var achievementScrollViewVisibilityControlBool = false

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#393e46")))
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#393e46")

        forLateInit()
        firebaseManage.getUser(nameText,viewReal,true)
        //achievementsControl.loadAchievements(achievementsLinearLayout)
        listeners()
        netConnect()

        /****/
        val oylesineCardView  = TextView(this)
        val oylesineCardView2 = TextView(this)
        val oylesineCardView3 = TextView(this)
        val oylesineCardView4 = TextView(this)
        val oylesineCardView5 = TextView(this)
        val oylesineCardView6 = TextView(this)
        /***/

        achievementsControl.getAchievementsForShowNumber(achievementsCounterText, achievementsLinearLayout, oylesineCardView, oylesineCardView2, oylesineCardView3, oylesineCardView4, oylesineCardView5, oylesineCardView6)

        achievementsScrollView.visibility = View.INVISIBLE

        if (currentUser == null) {
            achievementsCounterText.setBackgroundResource(R.drawable.achievements_background_null)
        }

        achievementsCounterText.setOnClickListener {
            if (currentUser != null) {
                if (!achievementScrollViewVisibilityControlBool) {
                    achievementsCounterText.setBackgroundResource(R.drawable.cust_start_button_no_stroke)
                    achievementsScrollView.visibility = View.VISIBLE
                    achievementScrollViewVisibilityControlBool = true
                } else if (achievementScrollViewVisibilityControlBool) {
                    achievementsCounterText.setBackgroundResource(R.drawable.achievements_background)
                    achievementsScrollView.visibility = View.INVISIBLE
                    achievementScrollViewVisibilityControlBool = false
                }
            }
            else{
                snackCreater.showToastCenter(this, "You must be logged in if you want to show all achievements.")
                achievementsCounterText.setBackgroundResource(R.drawable.achievements_background_null)
            }
        }

        achievementsScrollView.setOnClickListener {
            val intent = Intent(this, ShowAchievements::class.java)
            startActivity(intent)
        }

        achievementsLinearLayout.setOnClickListener {
            val intent = Intent(this, ShowAchievements::class.java)
            startActivity(intent)
        }

    }

    private fun listeners(){
        startButtonReal.setOnClickListener {
            val intent = Intent(this, ReactionTime::class.java)
            startActivity(intent)
            finish()
        }
        tryConnectTextReal.setOnClickListener {
            netConnect()
        }

        deleteMeOnLeaderBoardImage.setOnClickListener {
            val userId = currentUser?.uid
            val alert = AlertDialog.Builder(this, R.style.CustomAlertDialogForHistories)
            alert.setTitle("Delete Me On Leader Board")
            alert.setMessage("If you delete. You cant get it back.")
            alert.setPositiveButton("Delete") {_ : DialogInterface, _ : Int ->
                try {
                    firebase.collection("Scores").document(userId!!).update("ScoreAverage", 0).addOnSuccessListener {
                        snackCreater.createSuccessSnack("Deleted", viewReal)
                        deleteMeOnLeaderBoardImage.visibility = View.INVISIBLE
                    }.addOnFailureListener {
                        snackCreater.createFailSnack(it.localizedMessage!!, viewReal)
                    }
                }
                catch (e : Exception){
                    snackCreater.createFailSnack("Could not be deleted.", viewReal)
                }
            }
            alert.setNegativeButton("Cancel") {dialog : DialogInterface, _ : Int ->
                dialog.cancel()
            }
            alert.show()
        }
    }

    private fun forLateInit(){
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()

        viewReal = window.decorView.rootView
        snackCreater = SnackbarCreater()
        firebaseManage = FirebaseManage(this,viewReal,this)
        olmasiGerekTextView = TextView(this)
        nameText = TextView(this)
        achievementsControl = AchievementsControl(this,this, viewReal)
    }

    private fun netConnect(){
        val netControl = firebaseManage.internetControl(this)
        if (netControl) {
            firebaseManage.loadLeaderScores(leaderBoardLayout,false, olmasiGerekTextView, deleteMeOnLeaderBoardImage, 15)
            leaderBoardLayout.visibility = View.VISIBLE
            progressForLeaderReal.visibility = View.INVISIBLE
            tryConnectTextReal.visibility = View.INVISIBLE
        }
        else{
            leaderBoardLayout.visibility = View.INVISIBLE
            progressForLeaderReal.visibility = View.VISIBLE
            tryConnectTextReal.visibility = View.VISIBLE
            underlinedText("Try Again", tryConnectTextReal)
            snackCreater.createFailSnack("No Connection",viewReal)
            //loadingImage.setBackgroundResource(Drawable.(R.layout.custom_loading_screen))
        }
    }

    private fun underlinedText(text: String, textView: TextView){
        var spannableString = SpannableString(text)
        spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        textView.text = spannableString
    }
}