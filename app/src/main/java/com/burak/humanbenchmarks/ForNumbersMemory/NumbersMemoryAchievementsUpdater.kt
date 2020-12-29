package com.burak.humanbenchmarks.ForNumbersMemory

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
import com.burak.humanbenchmarks.*
import com.burak.humanbenchmarks.ForReactionTime.AchievementsControl
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
    private val snackCreator : PopupMessageCreator = PopupMessageCreator()
    private val achievementsControl : AchievementsControl = AchievementsControl(mCtx, mActivity, viewReal)
    private val leadersBoardAndAchievementsScreenDesign = LeadersBoardAndAchievementsScreenDesign(mCtx)

    private val firebaseManage = FirebaseManage(mCtx, viewReal, mActivity)

    fun updateTrueAnAchievements(whichAchievement : String, title : String, message : String){
        firebaseManage.loadingScreenDestroyer(false)
        firebaseManage.loadingScreenStarter(false)
        if (currentUser != null){
            val userId = currentUser.uid
            firebase.collection("Users").document(userId).collection("Achievements").document("numbersMemoryAchievements").update(whichAchievement, true).addOnSuccessListener {

                firebaseManage.loadingScreenDestroyer(false)

                val getLayout = leadersBoardAndAchievementsScreenDesign.setLayout(title, message)

                val alert = AlertDialog.Builder(mCtx, R.style.CustomAlertDialogForHistories)
                //alert.setTitle(updateTitle)
                alert.setView(getLayout)
                //alert.show()
                builder = alert.show()

                val ring: MediaPlayer = MediaPlayer.create(mCtx, R.raw.ring)
                ring.start()

            }.addOnFailureListener {
                snackCreator.customToast(
                    mActivity, mCtx, null, Toast.LENGTH_SHORT, "Achievement could not update.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                firebaseManage.loadingScreenDestroyer(false)
                //snackCreator.showToastCenter(mCtx, "NO UPDATED")
            }
        }
        else{
            snackCreator.customToast(
                mActivity, mCtx, null, Toast.LENGTH_SHORT, "User Null",
                R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
            firebaseManage.loadingScreenDestroyer(false)
            //snackCreator.showToastCenter(mCtx, "USER NULL")
        }
    }

    companion object{
        lateinit var giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements : HashMap<String, Boolean>
        var brainStormClr = 0
        var impatientClr = 0
        var rookieClr = 0
        var smartClr = 0

        lateinit var builder : AlertDialog

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
                        val yesilRenk = "#387E3B"
                        val siyahRenk = "#000000"

                        if (brainStorm) {
                            addAchievements("Brain Storm", yesilRenk, layoutForAchievements, R.string.brainStorm, R.style.GreenCustomAlertDialog)
                        } else if (!brainStorm) {
                            addAchievements("Brain Storm", siyahRenk, layoutForAchievements, R.string.brainStorm, R.style.RedCustomAlertDialog)
                        }

                        if (impatient) {
                            addAchievements("Impatient", yesilRenk, layoutForAchievements, R.string.impatient, R.style.GreenCustomAlertDialog)
                        } else if (!impatient) {
                            addAchievements("Impatient", siyahRenk, layoutForAchievements, R.string.impatient, R.style.RedCustomAlertDialog)
                        }

                        if (rookie) {
                            addAchievements("Rookie", yesilRenk, layoutForAchievements, R.string.rookie, R.style.GreenCustomAlertDialog)
                        } else if (!rookie) {
                            addAchievements("Rookie", siyahRenk, layoutForAchievements, R.string.rookie, R.style.RedCustomAlertDialog)
                        }

                        if (smart) {
                            addAchievements("Smart", yesilRenk, layoutForAchievements, R.string.smart, R.style.GreenCustomAlertDialog)
                        } else if (!smart) {
                            addAchievements("Smart", siyahRenk, layoutForAchievements, R.string.smart, R.style.RedCustomAlertDialog)
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

    private var achievementsLinearCounter = 0
    private fun addAchievements(text : String, color : String, lyt : LinearLayout, detail : Int, style : Int){
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
            "Brain Storm" -> { imgView.setImageResource(R.drawable.brain_storm_ic) }
            "Impatient" -> { imgView.setImageResource(R.drawable.impatient_ic) }
            "Rookie" -> { imgView.setImageResource(R.drawable.rookie_ic) }
            "Smart" -> { imgView.setImageResource(R.drawable.smart_ic) }
        }
        /******************************************************************************************/
        txtView.setTextColor(Color.parseColor(color))
        txtView.gravity = Gravity.CENTER
        /******************************************************************************************/
        if (achievementsLinearCounter >= 1) { // Boşluk için.
            val imageForLine = TextView(mCtx)
            imageForLine.setBackgroundColor(Color.parseColor("#B06558"))
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
        createClickListenerForAchievementsDetail(mainLinearLayoutForAchievements, detail, style)
        /******************************************************************************************/
    }

    private fun createClickListenerForAchievementsDetail(layout : LinearLayout, detail : Int, style : Int){
        layout.setOnClickListener {
            println("$detail")
            val alert = AlertDialog.Builder(mCtx, style)
            alert.setTitle("Detail")
            alert.setMessage(detail)
            alert.setPositiveButton("Okay") { dialog : DialogInterface, _ : Int ->
                dialog.cancel()
            }
            val dialog = alert.create()
            dialog.window!!.attributes!!.windowAnimations = R.style.CustomAlertDialog
            dialog.show()
        }
    }
}