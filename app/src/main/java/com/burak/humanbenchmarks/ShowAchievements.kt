package com.burak.humanbenchmarks

import android.content.DialogInterface
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.burak.humanbenchmarks.ForNumbersMemory.NumbersMemoryAchievementsUpdater
import com.burak.humanbenchmarks.ForReactionTime.AchievementsControl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_show_achievements.*

class ShowAchievements : AppCompatActivity() {

    private lateinit var achievementsControl : AchievementsControl
    private lateinit var viewReal : View
    private lateinit var snackCreator : PopupMessageCreator
    private lateinit var firebase : FirebaseFirestore
    private lateinit var auth : FirebaseAuth
    private lateinit var currentUser : FirebaseUser
    private lateinit var numbersMemoryAchievementsUpdater: NumbersMemoryAchievementsUpdater
    private var animationControl : animationControl = animationControl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_achievements)

        animationControl.forOnCreate(savedInstanceState)

        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        viewReal = window.decorView.rootView

        achievementsControl = AchievementsControl(this,this, viewReal)
        numbersMemoryAchievementsUpdater = NumbersMemoryAchievementsUpdater(this, this, viewReal)
        numbersMemoryAchievementsUpdater.giveAchievements(numbersMemoryAchievementsText, brainStormText, impatientText, rookieText, smartText, false)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#803918")))
        window.decorView.apply { // Hide navigation bar.
            systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        }
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#803918")
        supportActionBar?.hide()
        snackCreator = PopupMessageCreator()

        val zıptırıkLayout = LinearLayout(this)
        achievementsControl.getAchievementsForShowNumber(achievementText, zıptırıkLayout, place1StText, row20Text, robotText, tooLuckyText, tooSlowText, turtleText)



        clickListeners()
    }

    private fun clickListeners(){
        place1StCard.setOnClickListener {
            alertCreator("Be Leader", "Be rank 1 on the leader board.", AchievementsControl.st1BckClr, false)
        }

        row20Card.setOnClickListener {
            alertCreator("Consecutive Player", "Play 20 rounds in a row.", AchievementsControl.row20BckClr, true)
        }

        robotCard.setOnClickListener {
            alertCreator("Be Robot", "100 ms or less average.", AchievementsControl.robotBckClr, false)
        }

        tooLuckCard.setOnClickListener {
            alertCreator("Too Lucky", "80 ms or less score.", AchievementsControl.tooLuckBckClr, true)
        }

        tooSlowCard.setOnClickListener {
            alertCreator("Too Slow", "Wait 10 seconds or more.", AchievementsControl.tooSlowBckClr, false)
        }

        turtleCard.setOnClickListener {
            alertCreator("Turtle", "Wait 50 seconds or more.", AchievementsControl.turtleBckClr, true)
        }

        brainStormCard.setOnClickListener {
            alertCreator("Brain Storm", "Know an 18-digit number.", NumbersMemoryAchievementsUpdater.brainStormClr, false)
        }

        impatientCard.setOnClickListener {
            alertCreator("Impatient", "Pass before the time is up and know the number.", NumbersMemoryAchievementsUpdater.impatientClr, true)
        }

        rookieCard.setOnClickListener {
            alertCreator("Rookie", "Know an 7-digit number.", NumbersMemoryAchievementsUpdater.rookieClr, false)
        }

        smartCard.setOnClickListener {
            alertCreator("Smart", "Know an 10-digit number.", NumbersMemoryAchievementsUpdater.smartClr, true)
        }

        deleteAllAchievementsButton.setOnClickListener {
            val alert = AlertDialog.Builder(this, R.style.CustomAlertDialogForHistories)
            alert.setTitle("Delete All Achievements")
            alert.setMessage("Are you sure you want to delete all your achievements.")
            alert.setPositiveButton("Delete") {_ : DialogInterface, _ : Int ->
                val userId = currentUser.uid

                val achievementsMap = hashMapOf(
                    "20roundsRow" to false, // 20 round arka arkaya
                    "tooSlow" to false, // 10 saniyeden fazla
                    "tooLucky" to false, // 80ms den az -SKOR-
                    "1st" to false, // 1 sıraya yerleşmiş
                    "turtle" to false, // 50 saniyeden fazla
                    "areYouRobot" to false // 80ms den daha az -ORTALAMA-
                )
                firebase.collection("Users").document(userId).collection("Achievements").document("allAchievements").set(achievementsMap).addOnSuccessListener {
                    snackCreator.customToast(
                        this, this, null, Toast.LENGTH_SHORT,
                        "Achievements deleted.",
                        R.drawable.custom_toast_success, R.drawable.ic_success_image
                    )
                    //snackCreator.createSuccessSnack("Achievements deleted.", viewReal)
                }.addOnFailureListener{
                    snackCreator.customToast(
                        this, this, null, Toast.LENGTH_SHORT,
                        "Achievements could not be deleted.",
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    //snackCreator.createFailSnack("Achievements could not be deleted.",viewReal)
                }
            }
            alert.setNegativeButton("Cancel") {dialog : DialogInterface, _ : Int ->
                dialog.cancel()
            }
            alert.show()
        }

        deleteNumbersMemoryButton.setOnClickListener {
            val alert = AlertDialog.Builder(this, R.style.CustomAlertDialogForHistories)
            alert.setTitle("Delete All Achievements")
            alert.setMessage("Are you sure you want to delete all your achievements.")
            alert.setPositiveButton("Delete") {_ : DialogInterface, _ : Int ->
                val userId = currentUser.uid

                val achievementsMap = hashMapOf(
                    "brainStorm" to false, // 20 round arka arkaya
                    "impatient" to false, // 10 saniyeden fazla
                    "rookie" to false, // 80ms den az -SKOR-
                    "smart" to false, // 1 sıraya yerleşmiş
                )
                firebase.collection("Users").document(userId).collection("Achievements").document("numbersMemoryAchievements").set(achievementsMap).addOnSuccessListener {
                    snackCreator.customToast(
                        this, this, null, Toast.LENGTH_SHORT,
                        "Achievements deleted",
                        R.drawable.custom_toast_success, R.drawable.ic_success_image
                    )
                    //snackCreator.createSuccessSnack("Achievements deleted.", viewReal)
                }.addOnFailureListener{
                    snackCreator.customToast(
                        this, this, null, Toast.LENGTH_SHORT,
                        "Achievements could not be deleted.",
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    //snackCreator.createFailSnack("Achievements could not be deleted.",viewReal)
                }
            }
            alert.setNegativeButton("Cancel") {dialog : DialogInterface, _ : Int ->
                dialog.cancel()
            }
            alert.show()
        }
    }

    private fun alertCreator (title : String, message : String, style : Int, rightOrLeft : Boolean){
        val alert = AlertDialog.Builder(this, style)
        alert.setTitle(title)
        alert.setMessage(message)
        if (rightOrLeft) {
            alert.setPositiveButton("Okay") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
        }
        else if (!rightOrLeft){
            alert.setNeutralButton("Okay") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
        }
        alert.show()
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