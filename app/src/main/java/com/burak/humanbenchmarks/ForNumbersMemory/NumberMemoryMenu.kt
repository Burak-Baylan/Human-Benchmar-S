package com.burak.humanbenchmarks.ForNumbersMemory

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
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.burak.humanbenchmarks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_number_memory_menu.*

class NumberMemoryMenu : AppCompatActivity() {


    /**
     * @drawable/numbers_memory_background
     *
     */


    private lateinit var firebaseManage : FirebaseManage
    private lateinit var firebase : FirebaseFirestore
    private lateinit var viewReal : View
    private lateinit var snackCreator : PopupMessageCreator
    private lateinit var auth : FirebaseAuth
    private var currentUser : FirebaseUser? = null
    private lateinit var numbersMemoryAchievementsUpdater: NumbersMemoryAchievementsUpdater
    private var animationControl : animationControl = animationControl(this)

    var onStartCount = 0

    var achievementScrollViewVisibilityControlBool = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_memory_menu)
        supportActionBar?.title = "Numbers Memory"
        supportActionBar?.hide()

        animationControl.forOnCreate(savedInstanceState)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#393e46")))
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#393e46")

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        viewReal = window.decorView.rootView
        fabListener()

        numbersMemoryAchievementsUpdater = NumbersMemoryAchievementsUpdater(this,this,viewReal)

        firebaseManage = FirebaseManage(this,viewReal,this)
        snackCreator = PopupMessageCreator()
        internetControl(false)

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
                snackCreator.customToast(
                    this, this, null, Toast.LENGTH_LONG,
                    "You must be logged in if you want to show all achievements.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackCreator.showToastCenter(this, "You must be logged in if you want to show all achievements.")
                achievementsCounterText2.setBackgroundResource(R.drawable.achievements_background_null)
            }
        }

        tryConnectTextRealNumbersMemory.setOnClickListener {
            internetControl(true)
        }

        deleteMeOnLeaderBoardImageNumbersMemory.setOnClickListener {
            if (firebaseManage.internetControl(this)) {
                firebase = FirebaseFirestore.getInstance()
                val userId = currentUser?.uid
                val alert =
                    AlertDialog.Builder(this, R.style.CustomAlertDialogForHistoriesNumbersMemory)
                alert.setTitle("Delete Me On Leader Board")
                alert.setMessage("If you delete. You cant get it back.")
                alert.setPositiveButton("Delete") { _: DialogInterface, _: Int ->
                    try {
                        firebase.collection("Scores").document(userId!!).update("NumbersScore", 0, "after18Count", 0)
                            .addOnSuccessListener {

                                snackCreator.customToast(
                                    this,
                                    this,
                                    null,
                                    Toast.LENGTH_SHORT,
                                    "Deleted",
                                    R.drawable.custom_toast_success,
                                    R.drawable.ic_success_image
                                )
                                //snackCreator.showToastShort(this, "Deleted")

                                deleteMeOnLeaderBoardImageNumbersMemory.visibility = View.INVISIBLE
                            }.addOnFailureListener {

                            snackCreator.customToast(
                                this, this, null, Toast.LENGTH_LONG,
                                it.localizedMessage!!,
                                R.drawable.custom_toast_error, R.drawable.ic_error_image
                            )
                            //snackCreator.showToastShort(this, it.localizedMessage!!)
                        }
                    } catch (e: Exception) {

                        snackCreator.customToast(
                            this, this, null, Toast.LENGTH_LONG,
                            "Could not be deleted.",
                            R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                        //snackCreator.showToastShort(this, "Could not be deleted.")
                    }
                }
                alert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                    dialog.cancel()
                }
                val dialog = alert.create()
                dialog.window!!.attributes!!.windowAnimations = R.style.CustomAlertDialog
                dialog.show()
            }
            else{
                snackCreator.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "Internet connection required.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
            }
        }
        goBackButtonInToolbar2.setOnClickListener { finish() }
    }

    override fun onStart() {
        animationControl.forOnStart()
        if (firebaseManage.internetControl(this)){
            connectionIconNumbersMemory.visibility = View.INVISIBLE
        }
        else{
            connectionIconNumbersMemory.visibility = View.VISIBLE
            MainActivity.connectionImageAnimation(connectionIconNumbersMemory)
        }
        super.onStart()
    }

    fun startClick(v : View){
        val intent = Intent(this, NumberMemory::class.java)
        startActivity(intent)
    }

    private fun internetControl(messageControl : Boolean){
        val netControl = firebaseManage.internetControl(this)
        if (netControl) {
            leaderBoardLayoutNumberMemory.visibility = View.VISIBLE
            progressForLeaderRealNumbersMemory.visibility = View.INVISIBLE
            connectionIconNumbersMemory.visibility = View.INVISIBLE
            tryConnectTextRealNumbersMemory.visibility = View.INVISIBLE
            swapGoLeaders()
        }
        else{
            leaderBoardLayoutNumberMemory.visibility = View.INVISIBLE
            achievementsLinearLayoutNumberMemory.visibility = View.INVISIBLE
            progressForLeaderRealNumbersMemory.visibility = View.VISIBLE
            connectionIconNumbersMemory.visibility = View.VISIBLE
            MainActivity.connectionImageAnimation(connectionIconNumbersMemory)
            tryConnectTextRealNumbersMemory.visibility = View.VISIBLE
            underlinedText("Try Again", tryConnectTextRealNumbersMemory)
            if (messageControl){
                snackCreator.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "No Connection",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
            }
        }
    }

    private fun underlinedText(text: String, textView: TextView){
        var spannableString = SpannableString(text)
        spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        textView.text = spannableString
    }
    var swapControl = true
    private fun fabListener(){
        firstFab.setOnClickListener{ view ->
            if (!isFabOpen){//Kapalıysa
                showFabMenu()
            }
            else{//Açıksa
                closeFabMenu()
            }
        }

        refreshFab.setOnClickListener {
            val netControl = firebaseManage.internetControl(this)
            internetControl(true)
            if (netControl) {
                closeFabMenu()
                firebaseManage.getNumbersMemoryLeader(leaderBoardLayoutNumberMemory, false, 15, deleteMeOnLeaderBoardImageNumbersMemory)
            }
            else {
                snackCreator.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "No Connection", R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
            }
        }

        swapFab.setOnClickListener {
            if (currentUser != null) {
                if (swapControl) { // Achievements'i göster
                    swapGoAchievements()
                } else if (!swapControl) { // Lider göster
                    swapGoLeaders()
                }
                closeFabMenu()
            } else {
                snackCreator.customToast(
                    this, this, null, Toast.LENGTH_LONG,
                    "If you want to see your own achievements, you must logged in.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackCreator.showToastCenter(this, "If you want to see your own achievements, you must logged in.")
            }
        }
    }

    private fun swapGoAchievements(){
        val netControl = firebaseManage.internetControl(this)
        /**********************************************************************************/
        currentUser = auth.currentUser
        swapControl = false
        numbersMemoryAchievementsUpdater.getAchievementsForShowAchievementListInMenu(
            textView13,
            achievementsLinearLayoutNumberMemory
        )
        /************************** INVISIBLE LEADER BOARD ********************************/
        deleteMeOnLeaderBoardImageNumbersMemory.visibility = View.INVISIBLE
        //textView13.visibility = View.GONE
        textView16.visibility = View.GONE
        numbersMemoryTimeLeadersScroll.visibility = View.INVISIBLE
        /*progressForLeaderRealNumbersMemory.visibility = View.INVISIBLE
        tryConnectTextRealNumbersMemory.visibility = View.INVISIBLE*/
        /**********************************************************************************/
        /*************************** VISIBLE ACHIEVEMENTS *********************************/
        textView13.text = "Achievements"
        numbersAchievementsScrollView.visibility = View.VISIBLE
        achievementsLinearLayoutNumberMemory.visibility = View.VISIBLE
        refreshFab.visibility = View.INVISIBLE
        progressForLeaderRealNumbersMemory.visibility = View.INVISIBLE
        tryConnectTextRealNumbersMemory.visibility = View.INVISIBLE
        println("orrrrada")
        /**********************************************************************************/
        if (!netControl){
            tryConnectTextRealNumbersMemory.visibility = View.VISIBLE
            progressForLeaderRealNumbersMemory.visibility = View.VISIBLE
            achievementsLinearLayoutNumberMemory.visibility = View.INVISIBLE
            println("burrrrada")
        }
        /**********************************************************************************/
    }

    private fun swapGoLeaders(){
        val netControl = firebaseManage.internetControl(this)
        swapControl = true
        /*************************** VISIBLE LEADER BOARD *********************************/
        deleteMeOnLeaderBoardImageNumbersMemory.visibility = View.VISIBLE
        textView13.visibility = View.VISIBLE
        textView16.visibility = View.VISIBLE
        numbersMemoryTimeLeadersScroll.visibility = View.VISIBLE
        /*progressForLeaderRealNumbersMemory.refreshFab.isClickable = truevisibility = View.VISIBLE
        tryConnectTextRealNumbersMemory.visibility = View.VISIBLE*/
        /**********************************************************************************/
        /************************** INVISIBLE ACHIEVEMENTS ********************************/
        textView13.text = "Leaders"
        numbersAchievementsScrollView.visibility = View.INVISIBLE
        refreshFab.visibility = View.VISIBLE
        /**********************************************************************************/
        if (!netControl) {
            tryConnectTextRealNumbersMemory.visibility = View.VISIBLE
            progressForLeaderRealNumbersMemory.visibility = View.VISIBLE
            leaderBoardLayoutNumberMemory.visibility = View.INVISIBLE
            println()
        }
        /**********************************************************************************/
    }

    private var isFabOpen : Boolean = false
    private fun showFabMenu(){
        isFabOpen = true
        firstFab.setImageResource(R.drawable.ic_up_arrow)
        swapFab.animate().translationY(+resources.getDimension(R.dimen.standard_55))
        refreshFab.animate().translationY(+resources.getDimension(R.dimen.standard_105))
    }

    private fun closeFabMenu(){
        isFabOpen = false
        firstFab.setImageResource(R.drawable.ic_down_arrow)
        swapFab.animate().translationY(0F)
        refreshFab.animate().translationY(0F)
    }

    private val userStatusUpdater = UserStatus()
    override fun onPause() {
        super.onPause()
        userStatusUpdater.statusUpdater("OFFLINE")
    }

    override fun onResume() {
        super.onResume()
        userStatusUpdater.statusUpdater("ONLINE")
    }

}