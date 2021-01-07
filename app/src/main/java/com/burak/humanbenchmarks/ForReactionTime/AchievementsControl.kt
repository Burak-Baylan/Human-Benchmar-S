package com.burak.humanbenchmarks.ForReactionTime

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.burak.humanbenchmarks.FirebaseManage
import com.burak.humanbenchmarks.LeadersBoardAndAchievementsScreenDesign
import com.burak.humanbenchmarks.R
import com.burak.humanbenchmarks.PopupMessageCreator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AchievementsControl(context: Context, activity: Activity, view: View) {

    private var snackCreator : PopupMessageCreator = PopupMessageCreator()
    private var firebase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser : FirebaseUser? = auth.currentUser
    private var mCtx = context
    private var mActivity = activity
    private var mView = view
    private var howManyAchievements = 0
    private lateinit var alert : AlertDialog.Builder
    private val leadersBoardAndAchievementsScreenDesign = LeadersBoardAndAchievementsScreenDesign(mCtx)

    fun getAchievementsForShowNumber(putText: TextView, getLinear : LinearLayout, oylesineCardView : TextView, oylesineCardView2 : TextView, oylesineCardView3 : TextView, oylesineCardView4 : TextView, oylesineCardView5 : TextView, oylesineCardView6 : TextView){
        if (currentUser != null) {
            val userId = currentUser?.uid

            firebase.collection("Users").document(userId!!).collection("Achievements")
                .document("allAchievements").addSnapshotListener { snapshot, e ->
                var allAchievementsCounter = 0
                var row20Counter = 0
                var tooSlowCounter = 0
                var tooLuckCounter = 0
                var st1Counter = 0
                var turtleCounter = 0
                var robotOrCounter = 0
                howManyAchievements = 0

                if (e != null) {
                    snackCreator.showToastShort(mCtx, e.localizedMessage!!)
                }

                if (snapshot != null && snapshot.exists()) {
                    val row20Rounds = snapshot.get("20roundsRow") as Boolean // 20 round arka arkaya
                    val tooSlow = snapshot.get("tooSlow") as Boolean // 10 saniyeden fazla
                    val tooLuck = snapshot.get("tooLucky") as Boolean // 80ms den az -SKOR-
                    val st1 = snapshot.get("1st") as Boolean // 1 sıraya yerleşmiş
                    val turtle = snapshot.get("turtle") as Boolean // 50 saniyeden fazla
                    val robotOr = snapshot.get("areYouRobot") as Boolean // 80ms den az -ORTALAMA-

                    row20Counter = counterCounter(row20Rounds, row20Counter)
                    tooSlowCounter = counterCounter(tooSlow, tooSlowCounter)
                    tooLuckCounter = counterCounter(tooLuck, tooLuckCounter)
                    st1Counter = counterCounter(st1, st1Counter)
                    turtleCounter = counterCounter(turtle, turtleCounter)
                    robotOrCounter = counterCounter(robotOr, robotOrCounter)

                    allAchievementsCounter =
                        row20Counter + tooSlowCounter + tooLuckCounter + st1Counter + turtleCounter + robotOrCounter

                    putText.text = "Achievements: $allAchievementsCounter/$howManyAchievements"

                    val h = TextView(mCtx)
                    h.text = "burada"
                    h.setTextColor(Color.rgb(255, 255, 255))


                    showAchievementsDetails(getLinear, row20Rounds, tooSlow, tooLuck, st1, turtle, robotOr, true, oylesineCardView, oylesineCardView2, oylesineCardView3, oylesineCardView4, oylesineCardView5, oylesineCardView6)

                }
            }
        }
        else{
            putText.text = "Achievements: ?/?"
        }
    }

    private fun createClickListenerForAchievementsDetail(layout : LinearLayout, detail : Int, style : Int){
        layout.setOnClickListener {
            println("$detail")
            val alert = AlertDialog.Builder(mCtx, style)
            alert.setTitle("Detail")
            alert.setMessage(detail)
            alert.setPositiveButton("Okay") {dialog : DialogInterface, _ : Int ->
                dialog.cancel()
            }
            val dialog = alert.create()
            dialog.window!!.attributes!!.windowAnimations = R.style.CustomAlertDialog
            dialog.show()
        }
    }

    var achievementsLinearCounter = 0
    private fun addAchievements(text : String, color : String, lyt : LinearLayout, achievementDetail : Int, style : Int){
        /******************************************************************************************/
        val linearLayoutForImage = LinearLayout(mCtx)
        /******************************************************************************************/
        val linearLayoutForTextView = LinearLayout(mCtx)
        linearLayoutForTextView.gravity = Gravity.CENTER
        val params2 = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        linearLayoutForTextView.layoutParams = params2
        /******************************************************************************************/
        val mainLinearLayoutForAchievements = LinearLayout(mCtx)
        mainLinearLayoutForAchievements.setBackgroundResource(R.drawable.numbers_memory_linear_layout_for_persons)
        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT)
        mainLinearLayoutForAchievements.layoutParams = params
        mainLinearLayoutForAchievements.setPadding(20, 15, 20, 15)
        /******************************************************************************************/
        val txtView = TextView(mCtx)
        txtView.text = text
        txtView.textSize = 25f
        txtView.gravity = Gravity.CENTER
        txtView.typeface = Typeface.DEFAULT_BOLD
        /******************************************************************************************/
        val imgView = ImageView(mCtx)
        imgView.setPadding(20, 0, 0, 0)
        /******************************************************************************************/
        when (text){
            "Be Leader" -> { imgView.setImageResource(R.drawable.st1_place_ic) }
            "20 rounds in a row" -> { imgView.setImageResource(R.drawable.in_a_row_ic) }
            "Are you robot?" -> { imgView.setImageResource(R.drawable.robot_ic) }
            "Too Lucky" -> { imgView.setImageResource(R.drawable.too_luck_ic) }
            "Too slow" -> { imgView.setImageResource(R.drawable.too_slow_ic) }
            "Turtle" -> { imgView.setImageResource(R.drawable.turtle_ic) }
        }
        /******************************************************************************************/
        txtView.setTextColor(Color.parseColor(color))
        txtView.gravity = Gravity.CENTER
        /******************************************************************************************/
        if (achievementsLinearCounter >= 1) { // Boşluk için.
            val imageForLine = TextView(mCtx)
            imageForLine.setBackgroundColor(Color.parseColor("#00adb5"))
            imageForLine.width = 900
            imageForLine.height = 15
            imageForLine.setPadding(0, 2, 0, 2)
            lyt.addView(imageForLine)
        }
        /******************************************************************************************/
        linearLayoutForImage.addView(imgView)
        linearLayoutForTextView.addView(txtView)
        /******************************************************************************************/
        mainLinearLayoutForAchievements.addView(linearLayoutForImage)
        mainLinearLayoutForAchievements.addView(linearLayoutForTextView)
        /******************************************************************************************/
        imgView.layoutParams.height = 100
        imgView.layoutParams.width = 100
        /******************************************************************************************/
        lyt.addView(mainLinearLayoutForAchievements)
        achievementsLinearCounter++
        /******************************************************************************************/
        createClickListenerForAchievementsDetail(mainLinearLayoutForAchievements, achievementDetail, style)
        /******************************************************************************************/
    }



    /** Javadaki static ile aynı **/
    companion object{
        var row20BckClr = 0
        var tooSlowBckClr = 0
        var tooLuckBckClr = 0
        var st1BckClr = 0
        var turtleBckClr = 0
        var robotBckClr = 0
        fun getAchievementsForPutTextViews(
            achievementsControl: AchievementsControl, row20TextView: TextView,
            tooSlowTextView: TextView,
            tooLuckTextView: TextView,
            s1TextView: TextView,
            turtleTextView: TextView,
            robotOrTextView: TextView
        ){
            if (achievementsControl.currentUser != null) {

                val userId = achievementsControl.currentUser?.uid
                achievementsControl.firebase.collection("Users").document(userId!!).collection("Achievements")
                    .document("allAchievements").addSnapshotListener { snapshot, e ->

                    if (e != null) {

                        achievementsControl.snackCreator.customToast(
                            achievementsControl.mActivity, achievementsControl.mCtx, null, Toast.LENGTH_SHORT,
                            e.localizedMessage!!, R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                    }

                    if (snapshot != null && snapshot.exists()) {
                        val row20Rounds = snapshot.get("20roundsRow") as Boolean // 20 round arka arkaya
                        val tooSlow = snapshot.get("tooSlow") as Boolean // 10 saniyeden fazla
                        val tooLuck = snapshot.get("tooLucky") as Boolean // 80ms den az -SKOR-
                        val st1 = snapshot.get("1st") as Boolean // 1 sıraya yerleşmiş
                        val turtle = snapshot.get("turtle") as Boolean // 50 saniyeden fazlal
                        val robotOr = snapshot.get("areYouRobot") as Boolean // 80ms den az -ORTALAMA-

                        row20TextView.text = "$row20Rounds"
                        tooSlowTextView.text = "$tooSlow"
                        tooLuckTextView.text = "$tooLuck"
                        s1TextView.text = "$st1"
                        turtleTextView.text = "$turtle"
                        robotOrTextView.text = "$robotOr"
                    }
                }
            }
        }
    }

    // Yeşil   07b229
    // Kırmızı a50629
    private fun showAchievementsDetails (putLayout : LinearLayout, row20Rounds : Boolean, tooSlow : Boolean, tooLuck : Boolean, st1 : Boolean, turtle : Boolean, robotOr : Boolean, colorControl : Boolean, card1St : TextView, card20Rounds : TextView, cardAreYouRobob : TextView, cardTooLuck : TextView, cardTooSlow : TextView, cardTurtle : TextView){
        putLayout.removeAllViews()
        val yesilRenk = "#387E3B"
        val kirmiziRenk = "#000000"

        val cardIcınKırmızıRenk = "#AF4C4C"
        val cardIcınYesilRenk = "#4CAF50"
        achievementsLinearCounter = 0

        if (row20Rounds){ // Row 20 Rounds
            addAchievements("20 rounds in a row", yesilRenk, putLayout, R.string.rounds20Row, R.style.GreenCustomAlertDialog)
            card20Rounds.setTextColor(Color.parseColor(cardIcınYesilRenk))
            row20BckClr = R.style.GreenCustomAlertDialog
        }
        else{
            addAchievements("20 rounds in a row", kirmiziRenk, putLayout, R.string.rounds20Row, R.style.RedCustomAlertDialog)
            card20Rounds.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            row20BckClr = R.style.RedCustomAlertDialog
        }

        if (tooSlow){ // Too Slow
            addAchievements("Too slow", yesilRenk, putLayout, R.string.tooSlow, R.style.GreenCustomAlertDialog)
            cardTooSlow.setTextColor(Color.parseColor(cardIcınYesilRenk))
            tooSlowBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            addAchievements("Too slow", kirmiziRenk, putLayout, R.string.tooSlow, R.style.RedCustomAlertDialog)
            cardTooSlow.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            tooSlowBckClr = R.style.RedCustomAlertDialog
        }

        if (tooLuck){ // Too Luck
            addAchievements("Too lucky", yesilRenk, putLayout, R.string.tooLucky, R.style.GreenCustomAlertDialog)
            cardTooLuck.setTextColor(Color.parseColor(cardIcınYesilRenk))
            tooLuckBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            addAchievements("Too Lucky", kirmiziRenk, putLayout, R.string.tooLucky, R.style.RedCustomAlertDialog)
            cardTooLuck.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            tooLuckBckClr = R.style.RedCustomAlertDialog
        }

        if (st1){ // Get in 1st
            addAchievements("Be Leader", yesilRenk, putLayout, R.string.beLeaderDetail, R.style.GreenCustomAlertDialog)
            card1St.setTextColor(Color.parseColor(cardIcınYesilRenk))
            st1BckClr = R.style.GreenCustomAlertDialog
        }
        else{
            addAchievements("Be Leader", kirmiziRenk, putLayout, R.string.beLeaderDetail, R.style.RedCustomAlertDialog)
            card1St.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            st1BckClr = R.style.RedCustomAlertDialog
        }

        if (turtle){ // Turtle
            addAchievements("Turtle", yesilRenk, putLayout, R.string.turtle, R.style.GreenCustomAlertDialog)
            cardTurtle.setTextColor(Color.parseColor(cardIcınYesilRenk))
            turtleBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            addAchievements("Turtle" , kirmiziRenk, putLayout, R.string.turtle, R.style.RedCustomAlertDialog)
            cardTurtle.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            turtleBckClr = R.style.RedCustomAlertDialog
        }

        if (robotOr){ // Are You Robot
            addAchievements("Are you robot?", yesilRenk, putLayout, R.string.areYouRobot, R.style.GreenCustomAlertDialog)
            cardAreYouRobob.setTextColor(Color.parseColor(cardIcınYesilRenk))
            robotBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            addAchievements("Are you robot?",kirmiziRenk, putLayout, R.string.areYouRobot, R.style.RedCustomAlertDialog)
            cardAreYouRobob.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            robotBckClr = R.style.RedCustomAlertDialog
        }
    }

    private fun counterCounter(bool: Boolean, boolCounter: Int) : Int {
        var boolCounterHere = boolCounter
        if (bool){
            boolCounterHere++
        }
        else if (!bool){
            boolCounterHere--
            if (boolCounterHere < 0) {
                boolCounterHere = 0
            }
        }
        howManyAchievements++
        return boolCounterHere
    }
    private val firebaseManage = FirebaseManage(mCtx, mView, mActivity)
    fun updateAchievements(hangiSey: String, updateBool: Boolean, updateMessage: String, updateTitle : String){
        val currentId = currentUser?.uid
        firebaseManage.loadingScreenDestroyer(false)
        firebaseManage.loadingScreenStarter(false)

        firebase.collection("Users").document(currentId!!).collection("Achievements").document("allAchievements").update(
            hangiSey,
            updateBool
        ).addOnSuccessListener {

            firebaseManage.loadingScreenDestroyer(false)

            val getLayout = leadersBoardAndAchievementsScreenDesign.setLayout(updateMessage, updateTitle)

            alert = AlertDialog.Builder(mCtx, R.style.CustomAlertDialogForHistories)

            alert.setView(getLayout)

            val dialog = alert.create()
            dialog.window!!.attributes!!.windowAnimations = R.style.CustomAlertDialog
            dialog.show()

            val ring: MediaPlayer = MediaPlayer.create(mCtx, R.raw.ring)
            ring.start()
        }.addOnFailureListener{
            snackCreator.customToast(
                mActivity, mCtx, null, Toast.LENGTH_SHORT, "Achievement could not update.",
                R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
            firebaseManage.loadingScreenDestroyer(false)
        }
    }
}