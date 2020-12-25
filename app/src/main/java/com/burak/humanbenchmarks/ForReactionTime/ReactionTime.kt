package com.burak.humanbenchmarks.ForReactionTime

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.burak.humanbenchmarks.*
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reaction_time.*
import java.io.Serializable
import java.util.concurrent.ThreadLocalRandom

class ReactionTime : AppCompatActivity() {

    private lateinit var auth : FirebaseAuth
    private var currentUser : FirebaseUser? = null
    private lateinit var firestore : FirebaseFirestore

    private var startTime : Long = 0L
    private var timeInMilliseconds : Long = 0L

    private var runnable : Runnable = Runnable {  }
    private var handler : Handler = Handler()
    private var countDownDegisken : CountDownTimer? = null
    private var besteKac : Int = 0
    private var besteKacTopla : Long = 0L
    private lateinit var firebaseManage : FirebaseManage
    private lateinit var viewReal : View
    private lateinit var snackbarCreater : PopupMessageCreator
    private lateinit var sqlHistories: SqlHistories
    private lateinit var achievementsControl: AchievementsControl
    private var mSQL : SQLiteDatabase? = null
    private var getUsername : String = "hehe"
    private var previouslyScore : String = ""
    private lateinit var oylesineTextView : TextView
    private var roundsCounter = 0

    private lateinit var row20TextView : TextView
    private lateinit var tooSlowTextView : TextView
    private lateinit var tooLuckTextView : TextView
    private lateinit var s1TextView : TextView
    private lateinit var turtleTextView : TextView
    private lateinit var robotOrTextView : TextView

    private lateinit var row20String : String
    private lateinit var tooSlowString : String
    private lateinit var tooLuckString : String
    private lateinit var s1String : String
    private lateinit var turtleString : String
    private lateinit var robotOrString : String

    private val animationControl = animationControl(this)


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_time)

        animationControl.forOnCreate(savedInstanceState)
        supportActionBar?.hide()
        snackbarCreater = PopupMessageCreator()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        currentUser = auth.currentUser
        viewReal = window.decorView.rootView
        achievementsControl = AchievementsControl(this,this, viewReal)
        firebaseManage = FirebaseManage(this,viewReal,this)
        allInvisible()
        infoLayout.visibility = View.VISIBLE
        oylesineTextView = TextView(this)
        firebaseManage.getUser(oylesineTextView,viewReal,welcomeControl = true)
        sqlHistories = SqlHistories(this,this,viewReal)

        /******************************************************************************************/
        row20TextView = TextView(this)
        tooSlowTextView = TextView(this)
        tooLuckTextView = TextView(this)
        s1TextView = TextView(this)
        turtleTextView = TextView(this)
        robotOrTextView = TextView(this)

        AchievementsControl.getAchievementsForPutTextViews(
            achievementsControl,
            row20TextView,
            tooSlowTextView,
            tooLuckTextView,
            s1TextView,
            turtleTextView,
            robotOrTextView
        )
        /******************************************************************************************/

        if (currentUser != null){
            val currentEmail = currentUser!!.email
            if (currentEmail == "dsjkadnas@gmail.com"){
                saydir.visibility = View.VISIBLE
            }
        }

        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        try {
            val currentIdString = currentUser?.uid

            mSQL = this.openOrCreateDatabase(currentIdString, MODE_PRIVATE, null)
            mSQL?.execSQL("CREATE TABLE IF NOT EXISTS reactionhistory (id INTEGER PRIMARY KEY, history VARCHAR)")

            mSQL?.execSQL("CREATE TABLE IF NOT EXISTS reactionhistoryaverage (id INTEGER PRIMARY KEY, history VARCHAR)")
        }
        catch (e : Exception){}

        infoLayout.setOnClickListener {
            allInvisible()
            redLayout.visibility = View.VISIBLE

            val getedRandom = getRandomSecond()
            startCountDown(getedRandom)
        }

        redLayout.setOnClickListener {
            allInvisible()
            toSoonLayout.visibility = View.VISIBLE

            stopGreenSecond()
            if (countDownDegisken != null){
                countDownDegisken!!.cancel()
            }
        }

        greenLayout.setOnClickListener {
            msText.text = "$timeInMilliseconds ms"

            besteKac++
            besteKacTopla += timeInMilliseconds
            roundsCounter++

            /** ACHIEVEMENTS CONTROLS **/
            currentUser = auth.currentUser
            val currentId = currentUser?.uid

            row20String = row20TextView.text.toString()
            tooSlowString = tooSlowTextView.text.toString()
            tooLuckString = tooLuckTextView.text.toString()
            s1String = s1TextView.text.toString()
            turtleString = turtleTextView.text.toString()
            robotOrString = robotOrTextView.text.toString()

            if (currentId != null) {
                val reactionTimeAchievementsAvailableControl : ReactionTimeAchievementsAvailableControl = ReactionTimeAchievementsAvailableControl(this, this, viewReal)
                reactionTimeAchievementsAvailableControl.controlAvailableAchievements(
                    row20String, tooSlowString, tooLuckString, turtleString, robotOrString,
                    besteKac, besteKacTopla, roundsCounter, timeInMilliseconds,
                    row20TextView, tooSlowTextView, tooLuckTextView, s1TextView,turtleTextView,robotOrTextView
                )
            }
            /*********************************************************************/

            if (besteKac == 5){
                previouslyScore += "\nScore $besteKac:  $timeInMilliseconds"
                besteKacTopla /= 5
                averageText.visibility = View.VISIBLE
                averageText.text = "Average: $besteKacTopla ms"
                saveScoreButton.visibility = View.VISIBLE
                previouslyScore += "\n\nAverage Score: $besteKacTopla"
                saveScoreButton.visibility = View.VISIBLE
                returnMenuButton2.visibility = View.GONE
                sqlHistories.saveReactionHistory(previouslyScore)
                saveSQLAverages()
            }
            else if (besteKac > 5){
                previouslyScore = ""
                averageText.visibility = View.INVISIBLE
                besteKac = 0
                besteKac++
                besteKacTopla = 0
                saveScoreButton.visibility = View.GONE
                saveScoreButton.visibility = View.INVISIBLE
                returnMenuButton2.visibility = View.VISIBLE
                besteKacTopla += timeInMilliseconds
            }

            previouslyScore += "\nScore $besteKac:  $timeInMilliseconds"

            besteKacText.text = "$besteKac/5"

            allInvisible()
            showMsLayout.visibility = View.VISIBLE

            stopGreenSecond()
        }

        toSoonLayout.setOnClickListener {

            allInvisible()
            redLayout.visibility = View.VISIBLE

            val getedRandom = getRandomSecond()
            startCountDown(getedRandom)
        }

        showMsLayout.setOnClickListener{
            allInvisible()
            redLayout.visibility = View.VISIBLE

            val getedRandom = getRandomSecond()
            startCountDown(getedRandom)
        }

        returnMenuButton2.setOnClickListener {
            finish()
        }

        returnMenuButton.setOnClickListener {
            finish()
        }

        saveScoreButton.setOnClickListener {
            val connectControl = firebaseManage.internetControl(this)
            currentUser = auth.currentUser
            val currentEmail = currentUser?.email
            val currentId = currentUser?.uid

            getUsername = oylesineTextView.text.toString()

            if (currentId != null) {
                val alert = AlertDialog.Builder(this, R.style.saveScoreCustomAlertDialog)
                alert.setTitle("Overwrite Your Score")
                alert.setCancelable(false)
                window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                alert.setMessage("If you overwrite this score, you cannot get it back!")
                alert.setPositiveButton("Save") { _: DialogInterface, _: Int ->

                    if (connectControl) {
                        firestore.collection("Scores").document(currentId).update("ScoreAverage", besteKacTopla.toInt()).addOnSuccessListener {
                            snackbarCreater.customToast(
                                this, this, null, Toast.LENGTH_SHORT, "Score Saved",
                                R.drawable.custom_toast_success, R.drawable.ic_success_image
                            )
                        }.addOnFailureListener {
                            val addScoreAverage: HashMap<String, Serializable?> = hashMapOf(
                                "Email" to currentEmail,
                                "Uid" to currentId,
                                "ScoreAverage" to besteKacTopla.toInt(),
                                "Username" to getUsername
                            )
                            firebaseManage.firestoreAddWNoUuid(
                                addScoreAverage,
                                "Scores",
                                currentId,
                                "Score Saved",
                                "Score Save Fail"
                            )
                        }

                    } else if (!connectControl) {

                        snackbarCreater.customToast(
                            this, this, null, Toast.LENGTH_SHORT,
                            "Internet connection required to save.",
                            R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                    }
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                }
                alert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                    window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
                    dialog.cancel()
                }
                alert.show()
            }
            else{
                firebaseManage.loginAlertDialog(oylesineTextView)
                snackbarCreater.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "You must be logged in to save.",
                    R.drawable.custom_toast_warning, R.drawable.ic_warning_image
                )
            }
        }
    }

    @SuppressLint("Recycle")
    private fun saveSQLAverages(){
        var gelenString = ""
        var idString = ""
        try {
            try {
                val cursor = mSQL?.rawQuery("SELECT * FROM reactionhistoryaverage", null)

                val historyIx = cursor?.getColumnIndex("history")
                val historyId = cursor?.getColumnIndex("id")

                while (cursor!!.moveToNext()) {
                    gelenString = cursor.getString(historyIx!!)
                    idString = cursor.getString(historyId!!)
                }
            } catch (e: Exception) {}

            gelenString += "\n$idString- $besteKacTopla"

            mSQL?.execSQL("INSERT INTO reactionhistoryaverage (history) VALUES (?)", arrayOf(gelenString))
        }
        catch (e : Exception){

        }
    }

    private fun startCountDown(second : Long){

        if (countDownDegisken != null){
            countDownDegisken!!.cancel()
        }

        countDownDegisken = object : CountDownTimer(second,1) {
            override fun onTick(millisUntilFinished: Long) {
                saydir.text = "$millisUntilFinished"
            }

            override fun onFinish() {
                startTime = SystemClock.uptimeMillis()
                allInvisible()
                greenLayout.visibility = View.VISIBLE
                startGreenSecond()
            }
        }.start()
    }

    private fun getRandomSecond () : Long{
        val waitSecond = ThreadLocalRandom.current().nextInt(3000,6000)
        return waitSecond.toLong()
    }

    private fun allInvisible(){
        redLayout.visibility = View.GONE
        greenLayout.visibility = View.GONE
        toSoonLayout.visibility = View.GONE
        infoLayout.visibility = View.GONE
        showMsLayout.visibility = View.GONE
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun startGreenSecond(){
        handler.postDelayed(runnable,0)
        runnable = Runnable {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime
            handler.postDelayed(runnable,0)
        }
        handler.post(runnable)
    }
    private fun stopGreenSecond(){
        handler.removeCallbacks(runnable)
        timeInMilliseconds = 0L
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