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
import com.burak.humanbenchmarks.R
import com.burak.humanbenchmarks.SnackbarCreater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class AchievementsControl(context: Context, activity: Activity, view: View) {

    private var snackCreator : SnackbarCreater = SnackbarCreater()
    private var firebase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private var currentUser : FirebaseUser? = auth.currentUser
    private var mCtx = context
    private var mView = view
    private var howManyAchievements = 0
    private lateinit var alert : AlertDialog.Builder

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

                    //snackCreator.showToastTop(mCtx, "$allAchievementsCounter")

                    val h = TextView(mCtx)
                    h.text = "burada"
                    h.setTextColor(Color.rgb(255, 255, 255))


                    showAchievementsDetails(getLinear, row20Rounds, tooSlow, tooLuck, st1, turtle, robotOr, true, oylesineCardView, oylesineCardView2, oylesineCardView3, oylesineCardView4, oylesineCardView5, oylesineCardView6)

                } else {
                    snackCreator.createFailSnack("Boş", mView)
                }
            }
        }
        else{
            putText.text = "Achievements: ?/?"
        }
    }

    var achievementsLinearCounter = 0
    private fun textViewFun(text : String, color : String, lyt : LinearLayout){
        val txtView = TextView(mCtx)
        txtView.text = text
        txtView.textSize = 15f
        txtView.typeface = Typeface.DEFAULT_BOLD


        //Toast.makeText(mCtx, "Zibirop", Toast.LENGTH_SHORT).show()
        txtView.setTextColor(Color.parseColor(color))

        if (achievementsLinearCounter >= 1) {
            val imageForLine = TextView(mCtx)
            imageForLine.setBackgroundColor(Color.parseColor("#EDC755"))
            imageForLine.width = 900
            imageForLine.height = 3
            imageForLine.setPadding(0, 2, 0, 2)
            lyt.addView(imageForLine)
        }
        lyt.addView(txtView)
        achievementsLinearCounter++
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
                        achievementsControl.snackCreator.showToastShort(achievementsControl.mCtx, e.localizedMessage!!)
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
        val yesilRenk = "#61ff1e"
        val kirmiziRenk = "#FFFFFF"

        val cardIcınKırmızıRenk = "#AF4C4C"
        val cardIcınYesilRenk = "#4CAF50"
        achievementsLinearCounter = 0


        if (row20Rounds){ // Row 20 Rounds
            textViewFun("20 rounds in a row", yesilRenk, putLayout)
            card20Rounds.setTextColor(Color.parseColor(cardIcınYesilRenk))
            row20BckClr = R.style.GreenCustomAlertDialog
        }
        else{
            textViewFun("20 rounds in a row", kirmiziRenk, putLayout)
            card20Rounds.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            row20BckClr = R.style.RedCustomAlertDialog
        }

        if (tooSlow){ // Too Slow
            textViewFun("Too slow", yesilRenk, putLayout)
            cardTooSlow.setTextColor(Color.parseColor(cardIcınYesilRenk))
            tooSlowBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            textViewFun("Too slow", kirmiziRenk, putLayout)
            cardTooSlow.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            tooSlowBckClr = R.style.RedCustomAlertDialog
        }

        if (tooLuck){ // Too Luck
            textViewFun("Too lucky", yesilRenk, putLayout)
            cardTooLuck.setTextColor(Color.parseColor(cardIcınYesilRenk))
            tooLuckBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            textViewFun("Too Lucky", kirmiziRenk, putLayout)
            cardTooLuck.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            tooLuckBckClr = R.style.RedCustomAlertDialog
        }

        if (st1){ // Get in 1st
            textViewFun("Be Leader", yesilRenk, putLayout)
            card1St.setTextColor(Color.parseColor(cardIcınYesilRenk))
            st1BckClr = R.style.GreenCustomAlertDialog
        }
        else{
            textViewFun("Be Leader", kirmiziRenk, putLayout)
            card1St.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            st1BckClr = R.style.RedCustomAlertDialog
        }

        if (turtle){ // Turtle
            textViewFun("Turtle", yesilRenk, putLayout)
            cardTurtle.setTextColor(Color.parseColor(cardIcınYesilRenk))
            turtleBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            textViewFun("Turtle" , kirmiziRenk, putLayout)
            cardTurtle.setTextColor(Color.parseColor(cardIcınKırmızıRenk))
            turtleBckClr = R.style.RedCustomAlertDialog
        }

        if (robotOr){ // Are You Robot
            textViewFun("Are you robot?", yesilRenk, putLayout)
            cardAreYouRobob.setTextColor(Color.parseColor(cardIcınYesilRenk))
            robotBckClr = R.style.GreenCustomAlertDialog
        }
        else{
            textViewFun("Are you robot?",kirmiziRenk, putLayout)
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
            if (boolCounterHere < 0){
                boolCounterHere = 0
            }
        }
        howManyAchievements++
        return boolCounterHere
    }

    fun updateAchievements(hangiSey: String, updateBool: Boolean, updateMessage: String, updateTitle : String){
        val currentId = currentUser?.uid
        //firebaseManage.loadingScreenStarter(false)

        firebase.collection("Users").document(currentId!!).collection("Achievements").document("allAchievements").update(
            hangiSey,
            updateBool
        ).addOnSuccessListener {
            //firebaseManage.loadingScreenDestroyer(false)

            val getLayout = setLayout(updateMessage, updateTitle)

            alert = AlertDialog.Builder(mCtx, R.style.CustomAlertDialogForHistories)
            //alert.setTitle(updateTitle)
            alert.setView(getLayout)

            alert.setPositiveButton("DONE") {dialog : DialogInterface, _ : Int ->
                dialog.cancel()
            }

            alert.show()



            //snackCreator.createSuccessSnack(updateMessage, mView)
            val ring: MediaPlayer = MediaPlayer.create(mCtx, R.raw.ring)
            ring.start()
        }.addOnFailureListener{
            //firebaseManage.loadingScreenDestroyer(false)

            snackCreator.createFailSnack("Updatelenemedi", mView)
        }
    }

    fun setLayout(title : String, message : String) : LinearLayout{
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 0)


        val linearLayout = LinearLayout(mCtx)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50,10,50,20)

        val textForMessage1 = TextView(mCtx)
        textForMessage1.text = title
        textForMessage1.textSize = 25f
        textForMessage1.gravity = Gravity.CENTER
        textForMessage1.typeface = Typeface.DEFAULT_BOLD
        textForMessage1.setTextColor(Color.rgb(255,255,255))
        linearLayout.addView(textForMessage1)

        val cupImage = ImageView(mCtx)
        cupImage.maxHeight = 100
        cupImage.maxWidth = 100
        cupImage.setImageResource(R.drawable.cup_for_achievement)
        cupImage.minimumHeight = 100
        cupImage.minimumWidth = 100
        linearLayout.addView(cupImage)

        val textForMessage = TextView(mCtx)
        textForMessage.text = message
        textForMessage.textSize = 20f
        textForMessage.gravity = Gravity.CENTER
        textForMessage.setTextColor(Color.rgb(255,255,255))
        linearLayout.addView(textForMessage)

        return linearLayout
    }
}