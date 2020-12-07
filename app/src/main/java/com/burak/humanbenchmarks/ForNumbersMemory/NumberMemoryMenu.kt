package com.burak.humanbenchmarks.ForNumbersMemory

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
import com.burak.humanbenchmarks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_number_memory_menu.*

class NumberMemoryMenu : AppCompatActivity() {


    /**
     * @drawable/numbers_memory_background
     *
     */


    private lateinit var firebaseManage: FirebaseManage
    private lateinit var viewReal : View
    private lateinit var snackCreator : SnackbarCreater
    private lateinit var auth : FirebaseAuth
    private var currentUser : FirebaseUser? = null
    private lateinit var numbersMemoryAchievementsUpdater: NumbersMemoryAchievementsUpdater

    var achievementScrollViewVisibilityControlBool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_memory_menu)
        supportActionBar?.title = "Numbers Memory"

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#393e46")))
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#393e46")

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        viewReal = window.decorView.rootView

        numbersMemoryAchievementsUpdater = NumbersMemoryAchievementsUpdater(this,this,viewReal)
        numbersMemoryAchievementsUpdater.getAchievementsForShowAchievementListInMenu(achievementsCounterText2, achievementsLinearLayoutNumberMemory)

        firebaseManage = FirebaseManage(this,viewReal,this)
        snackCreator = SnackbarCreater()
        internetControl()

        achievementsCounterText2.setBackgroundResource(R.drawable.achievements_background)
        numbersAchievementsScrollView.visibility = View.INVISIBLE

        if (currentUser != null) {
            firebaseManage.getNumbersMemoryLeader(leaderBoardLayoutNumberMemory, false, 15, deleteMeOnLeaderBoardImageNumbersMemory)
        }
        else if (currentUser == null){
            firebaseManage.getNumbersMemoryLeader(leaderBoardLayoutNumberMemory, true, 15, deleteMeOnLeaderBoardImageNumbersMemory)
            achievementsCounterText2.setBackgroundResource(R.drawable.achievements_background_null)
        }

        achievementsCounterText2.setOnClickListener {
            if (currentUser != null) {
                if (!achievementScrollViewVisibilityControlBool) {
                    achievementsCounterText2.setBackgroundResource(R.drawable.cust_start_button_no_stroke)
                    numbersAchievementsScrollView.visibility = View.VISIBLE
                    achievementScrollViewVisibilityControlBool = true
                } else if (achievementScrollViewVisibilityControlBool) {
                    achievementsCounterText2.setBackgroundResource(R.drawable.achievements_background)
                    numbersAchievementsScrollView.visibility = View.INVISIBLE
                    achievementScrollViewVisibilityControlBool = false
                }
            }
            else{
                snackCreator.showToastCenter(this, "You must be logged in if you want to show all achievements.")
                achievementsCounterText2.setBackgroundResource(R.drawable.achievements_background_null)
            }
        }

        tryConnectTextRealNumbersMemory.setOnClickListener {
            internetControl()
        }
    }

    fun startClick(v : View){
        val intent = Intent(this, NumberMemory::class.java)
        startActivity(intent)
    }

    private fun internetControl(){
        val netControl = firebaseManage.internetControl(this)
        if (netControl) {
            leaderBoardLayoutNumberMemory.visibility = View.VISIBLE
            progressForLeaderRealNumbersMemory.visibility = View.INVISIBLE
            tryConnectTextRealNumbersMemory.visibility = View.INVISIBLE
        }
        else{
            leaderBoardLayoutNumberMemory.visibility = View.INVISIBLE
            progressForLeaderRealNumbersMemory.visibility = View.VISIBLE
            tryConnectTextRealNumbersMemory.visibility = View.VISIBLE
            underlinedText("Try Again", tryConnectTextRealNumbersMemory)
            snackCreator.createFailSnack("No Connection",viewReal)
        }
    }

    private fun underlinedText(text: String, textView: TextView){
        var spannableString = SpannableString(text)
        spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        textView.text = spannableString
    }
}