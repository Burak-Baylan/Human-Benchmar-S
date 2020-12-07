package com.burak.humanbenchmarks.ForNumbersMemory

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.media.MediaPlayer
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.burak.humanbenchmarks.ForReactionTime.AchievementsControl
import com.burak.humanbenchmarks.R
import com.burak.humanbenchmarks.SnackbarCreater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class NumbersMemoryAchievementsUpdater (context : Context, activity: Activity, view : View){

    private val mCtx = context
    private val mActivity = activity
    private val viewReal = view

    private val firebase : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val currentUser : FirebaseUser? = auth.currentUser
    private val snackCreator : SnackbarCreater = SnackbarCreater()
    private val achievementsControl : AchievementsControl = AchievementsControl(mCtx, mActivity, viewReal)

    fun updateTrueAnAchievements(whichAchievement : String, title : String){
        if (currentUser != null){
            val userId = currentUser.uid

            firebase.collection("Users").document(userId).collection("Achievements").document("numbersMemoryAchievements").update(whichAchievement, true).addOnSuccessListener {
                //snackCreator.showToastCenter(mCtx, "UPDATED")


                val getLayout = achievementsControl.setLayout(title, whichAchievement)

                val alert = AlertDialog.Builder(mCtx, R.style.CustomAlertDialogForHistories)
                //alert.setTitle(updateTitle)
                alert.setView(getLayout)
                alert.show()

                //snackCreator.createSuccessSnack(updateMessage, mView)
                val ring: MediaPlayer = MediaPlayer.create(mCtx, R.raw.ring)
                ring.start()

            }.addOnFailureListener {
                snackCreator.showToastCenter(mCtx, "NO UPDATED")
            }
        }
        else{
            snackCreator.showToastCenter(mCtx, "USER NULL")
        }
    }

    companion object{
        lateinit var giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements : HashMap<String, Boolean>
        var brainStormClr = 0
        var impatientClr = 0
        var rookieClr = 0
        var smartClr = 0

        var howManyAchievementNumbersMemory = 0
        var allAchievementNumberMemory = 0
    }

    var brainStorm = false
    var impatient = false
    var rookie = false
    var smart = false

    fun giveAchievements(showAchievementsNumberTextView : TextView, brainStormColorTextView : TextView, impatientColorTextView : TextView, rookieColorTextView : TextView, smartColorTextView : TextView, youAddThisOnAchievementsBoard : Boolean){

        if (currentUser != null) {
            val userId = currentUser.uid


            firebase.collection("Users").document(userId).collection("Achievements")
                .document("numbersMemoryAchievements").addSnapshotListener { snapshot, e ->

                allAchievementNumberMemory = 0
                howManyAchievementNumbersMemory = 0
                var brainStormCounter = 0
                var impatientCounter = 0
                var rookieCounter = 0
                var smartCounter = 0

                if (e != null) {
                    /********/
                }

                if (snapshot != null && snapshot.exists()) {
                    brainStorm = snapshot.get("brainStorm") as Boolean
                    impatient = snapshot.get("impatient") as Boolean
                    rookie = snapshot.get("rookie") as Boolean
                    smart = snapshot.get("smart") as Boolean

                    giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements = hashMapOf(
                        "brainStorm" to brainStorm,
                        "impatient" to impatient,
                        "rookie" to rookie,
                        "smart" to smart
                    )

                    brainStormCounter = achievementsCountBooleans(brainStorm, brainStormCounter)
                    impatientCounter = achievementsCountBooleans(impatient, impatientCounter)
                    rookieCounter = achievementsCountBooleans(rookie, rookieCounter)
                    smartCounter = achievementsCountBooleans(smart, smartCounter)

                    allAchievementNumberMemory =
                        brainStormCounter + impatientCounter + rookieCounter + smartCounter

                    showAchievementsNumberTextView.text =
                        "Achievements: $allAchievementNumberMemory/$howManyAchievementNumbersMemory"

                    giveColors(
                        brainStorm,
                        impatient,
                        rookie,
                        smart,
                        brainStormColorTextView,
                        impatientColorTextView,
                        rookieColorTextView,
                        smartColorTextView
                    )

                    if (youAddThisOnAchievementsBoard) {
                        layoutForAchievements.removeAllViews()
                        achievementsLinearCounter = 0
                        val yesilRenk = "#61ff1e"
                        val kirmiziRenk = "#FFFFFF"

                        if (brainStorm) {
                            textViewFun("Brain Storm", yesilRenk, layoutForAchievements)
                        } else if (!brainStorm) {
                            textViewFun("Brain Storm", kirmiziRenk, layoutForAchievements)
                        }

                        if (impatient) {
                            textViewFun("Impatient", yesilRenk, layoutForAchievements)
                        } else if (!impatient) {
                            textViewFun("Impatient", kirmiziRenk, layoutForAchievements)
                        }

                        if (rookie) {
                            textViewFun("Rookie", yesilRenk, layoutForAchievements)
                        } else if (!rookie) {
                            textViewFun("Rookie", kirmiziRenk, layoutForAchievements)
                        }

                        if (smart) {
                            textViewFun("Smart", yesilRenk, layoutForAchievements)
                        } else if (!smart) {
                            textViewFun("Smart", kirmiziRenk, layoutForAchievements)
                        }
                    }
                }
            }
        }
    }

    private fun achievementsCountBooleans(getBool : Boolean, getCount : Int) : Int{
        var getCountHere = getCount
        if (getBool){
            getCountHere++
        }
        else if (!getBool){
            getCountHere--
            if(getCountHere < 0){
                getCountHere = 0
            }
        }
        howManyAchievementNumbersMemory++
        return getCountHere
    }

    private fun giveColors(brainStormBool : Boolean, impatientBool : Boolean, rookieBool : Boolean, smartBool : Boolean, brainStormText : TextView, impatientText : TextView, rookieText : TextView, smartText : TextView){
        val kırmızıRenk = "#AF4C4C"
        val yeşilRenk = "#4CAF50"

        if (brainStormBool){
            brainStormClr = R.style.GreenCustomAlertDialog
            brainStormText.setTextColor(Color.parseColor(yeşilRenk))
        }
        else if (!brainStormBool){
            brainStormClr = R.style.RedCustomAlertDialog
            brainStormText.setTextColor(Color.parseColor(kırmızıRenk))
        }

        if (impatientBool){
            impatientClr = R.style.GreenCustomAlertDialog
            impatientText.setTextColor(Color.parseColor(yeşilRenk))
        }
        else if (!impatientBool){
            impatientClr = R.style.RedCustomAlertDialog
            impatientText.setTextColor(Color.parseColor(kırmızıRenk))
        }

        if (rookieBool){
            rookieClr = R.style.GreenCustomAlertDialog
            rookieText.setTextColor(Color.parseColor(yeşilRenk))
        }
        else if (!rookieBool){
            rookieClr = R.style.RedCustomAlertDialog
            rookieText.setTextColor(Color.parseColor(kırmızıRenk))
        }

        if (smartBool){
            smartClr = R.style.GreenCustomAlertDialog
            smartText.setTextColor(Color.parseColor(yeşilRenk))
        }
        else if (!smartBool){
            smartClr = R.style.RedCustomAlertDialog
            smartText.setTextColor(Color.parseColor(kırmızıRenk))
        }
    }

    lateinit var layoutForAchievements : LinearLayout
    fun getAchievementsForShowAchievementListInMenu(showNumberTextView : TextView, layout : LinearLayout){

        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            val gerekliAmaBuradaKullanılmayanTextView2 = TextView(mCtx)
            val gerekliAmaBuradaKullanılmayanTextView3 = TextView(mCtx)
            val gerekliAmaBuradaKullanılmayanTextView4 = TextView(mCtx)
            val gerekliAmaBuradaKullanılmayanTextView5 = TextView(mCtx)

            this.layoutForAchievements = layout

            giveAchievements(
                showNumberTextView,
                gerekliAmaBuradaKullanılmayanTextView2,
                gerekliAmaBuradaKullanılmayanTextView3,
                gerekliAmaBuradaKullanılmayanTextView4,
                gerekliAmaBuradaKullanılmayanTextView5,
                true
            )
        }
    }

    var achievementsLinearCounter = 0
    private fun textViewFun(text : String, color : String, lyt : LinearLayout){
        val txtView = TextView(mCtx)
        txtView.text = text
        txtView.textSize = 15f
        txtView.typeface = Typeface.DEFAULT_BOLD

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
}