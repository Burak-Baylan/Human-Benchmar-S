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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.burak.humanbenchmarks.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_reaction_time_menu.*
import kotlinx.android.synthetic.main.activity_reaction_time_menu.leaderBoardLayout

class ReactionTimeMenu : AppCompatActivity() {

    private lateinit var viewReal : View
    private lateinit var snackCreater : PopupMessageCreator
    private lateinit var firebaseManage: FirebaseManage
    private lateinit var olmasiGerekTextView : TextView
    private lateinit var nameText : TextView
    private lateinit var achievementsControl: AchievementsControl
    private lateinit var auth : FirebaseAuth
    private var currentUser : FirebaseUser? = null
    private lateinit var firebase : FirebaseFirestore
    private val animationControl = animationControl(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reaction_time_menu)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Reaction Time"
        var achievementScrollViewVisibilityControlBool = false
        supportActionBar?.hide()
        animationControl.forOnCreate(savedInstanceState)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#393e46")))
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#393e46")
        
        fabListener()

        forLateInit()
        firebaseManage.getUser(nameText,viewReal,true)
        //achievementsControl.loadAchievements(achievementsLinearLayout)
        listeners()
        netConnect()

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

                snackCreater.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "You must be logged in if you want to show all achievements.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackCreater.showToastCenter(this, "You must be logged in if you want to show all achievements.")

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

        goBackButtonInToolbar.setOnClickListener { finish() }

    }

    override fun onStart() {
        animationControl.forOnStart()
        if (firebaseManage.internetControl(this)){
            connectionIconReactionTime.visibility = View.INVISIBLE
        }
        else{
            connectionIconReactionTime.visibility = View.VISIBLE
            MainActivity.connectionImageAnimation(connectionIconReactionTime)
        }
        super.onStart()
    }

    private fun listeners(){
        startButtonReal.setOnClickListener {
            val intent = Intent(this, ReactionTime::class.java)
            startActivity(intent)
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

                        snackCreater.customToast(
                            this, this, null, Toast.LENGTH_SHORT, "Deleted",
                            R.drawable.custom_toast_success, R.drawable.ic_success_image
                        )
                        //snackCreater.createSuccessSnack("Deleted", viewReal)

                        deleteMeOnLeaderBoardImage.visibility = View.INVISIBLE
                    }.addOnFailureListener {

                        snackCreater.customToast(
                            this, this, null, Toast.LENGTH_SHORT, it.localizedMessage!!,
                            R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                        //snackCreater.createFailSnack(it.localizedMessage!!, viewReal)
                    }
                }
                catch (e : Exception){

                    snackCreater.customToast(
                        this, this, null, Toast.LENGTH_SHORT, "Could not be deleted.",
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    //snackCreater.createFailSnack("Could not be deleted.", viewReal)
                }
            }
            alert.setNegativeButton("Cancel") {dialog : DialogInterface, _ : Int ->
                dialog.cancel()
            }
            val dialog = alert.create()
            dialog.window!!.attributes!!.windowAnimations = R.style.CustomAlertDialog
            dialog.show()
        }
    }

    private fun forLateInit(){
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()

        viewReal = window.decorView.rootView
        snackCreater = PopupMessageCreator()
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
            connectionIconReactionTime.visibility = View.INVISIBLE
            tryConnectTextReal.visibility = View.INVISIBLE
        }
        else{
            leaderBoardLayout.visibility = View.INVISIBLE
            progressForLeaderReal.visibility = View.VISIBLE
            tryConnectTextReal.visibility = View.VISIBLE
            connectionIconReactionTime.visibility = View.VISIBLE
            MainActivity.connectionImageAnimation(connectionIconReactionTime)
            underlinedText("Try Again", tryConnectTextReal)
            /*snackCreater.customToast(
                this, this, null, Toast.LENGTH_SHORT, "No Connection",
                R.drawable.custom_toast_error, R.drawable.ic_error_image
            )*/
            //snackCreater.createFailSnack("No Connection",viewReal)
            //loadingImage.setBackgroundResource(Drawable.(R.layout.custom_loading_screen))
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
        firstFabReactionTime.setOnClickListener{ view ->
            if (!isFabOpen){//Kapalıysa
                showFabMenu()
            }
            else{//Açıksa
                closeFabMenu()
            }
        }

        refreshFabReactionTime.setOnClickListener {
            closeFabMenu()
            firebaseManage.loadLeaderScores(leaderBoardLayout,false, olmasiGerekTextView, deleteMeOnLeaderBoardImage, 15)
        }

        swapFabReactionTime.setOnClickListener {
            /****/
            val oylesineCardView  = TextView(this); val oylesineCardView2 = TextView(this); val oylesineCardView3 = TextView(this); val oylesineCardView4 = TextView(this); val oylesineCardView5 = TextView(this); val oylesineCardView6 = TextView(this)
            /***/
            currentUser = auth.currentUser
            if (currentUser != null) {
                if (swapControl) {
                    achievementsControl.getAchievementsForShowNumber(textView14, achievementsLinearLayout, oylesineCardView, oylesineCardView2, oylesineCardView3, oylesineCardView4, oylesineCardView5, oylesineCardView6)
                    swapControl = false
                    /************************** INVISIBLE LEADER BOARD ********************************/
                    deleteMeOnLeaderBoardImage.visibility = View.INVISIBLE
                    //textView14.visibility = View.GONE
                    textView15.visibility = View.GONE
                    reactionTimeLeadersScroll.visibility = View.INVISIBLE
                    /*progressForLeaderRealNumbersMemory.visibility = View.INVISIBLE
                tryConnectTextRealNumbersMemory.visibility = View.INVISIBLE*/
                    /*************************** VISIBLE ACHIEVEMENTS *********************************/
                    textView14.text = "Achievements"
                    achievementsScrollView.visibility = View.VISIBLE
                    refreshFabReactionTime.visibility = View.INVISIBLE
                    /**********************************************************************************/
                } else if (!swapControl) {
                    swapControl = true
                    /*************************** VISIBLE LEADER BOARD *********************************/
                    deleteMeOnLeaderBoardImage.visibility = View.VISIBLE
                    //textView14.visibility = View.VISIBLE
                    textView15.visibility = View.VISIBLE
                    reactionTimeLeadersScroll.visibility = View.VISIBLE
                    /*progressForLeaderRealNumbersMemory.refreshFab.isClickable = truevisibility = View.VISIBLE
                tryConnectTextRealNumbersMemory.visibility = View.VISIBLE*/
                    /**********************************************************************************/
                    /************************** INVISIBLE ACHIEVEMENTS ********************************/
                    textView14.text = "Leaders"
                    achievementsScrollView.visibility = View.INVISIBLE
                    refreshFabReactionTime.visibility = View.VISIBLE
                    /**********************************************************************************/
                }
                closeFabMenu()
            }
            else {
                snackCreater.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "If you want to see your own achievements, you must logged in.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackCreater.showToastCenter(this, "If you want to see your own achievements, you must logged in.")
            }
        }

    }
    
    private var isFabOpen : Boolean = false
    private fun showFabMenu(){
        isFabOpen = true
        firstFabReactionTime.setImageResource(R.drawable.ic_up_arrow_reaction_time)

        swapFabReactionTime.animate().translationY(+resources.getDimension(R.dimen.standard_55))
        refreshFabReactionTime.animate().translationY(+resources.getDimension(R.dimen.standard_105))
    }

    private fun closeFabMenu(){
        isFabOpen = false
        firstFabReactionTime.setImageResource(R.drawable.ic_down_arrow_reaction_time)
        swapFabReactionTime.animate().translationY(0F)
        refreshFabReactionTime.animate().translationY(0F)
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