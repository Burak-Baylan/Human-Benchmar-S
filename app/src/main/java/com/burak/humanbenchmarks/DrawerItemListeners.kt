package com.burak.humanbenchmarks

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import com.burak.humanbenchmarks.ForNumbersMemory.NumberMemoryMenu
import com.burak.humanbenchmarks.ForReactionTime.ReactionTimeMenu
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class DrawerItemListeners (val ctx : Context, val viewReal : View, val activity : Activity){
    private var snackCreater : SnackbarCreater = SnackbarCreater()
    private var loadingDialog : LoadingDialog = LoadingDialog(activity)
    private var firebaseManage : FirebaseManage = FirebaseManage(ctx, viewReal, activity)
    private lateinit var navigationView : NavigationView

    private lateinit var nameText : TextView
    private lateinit var loginText : TextView
    private lateinit var signupText : TextView
    private lateinit var nullLayoutReal : ConstraintLayout
    private lateinit var noNullLayoutReal : ConstraintLayout

    /** 2 **/
    fun putAllItems(nameText : TextView, loginText : TextView, signupText : TextView, nullLayoutReal : ConstraintLayout, noNullLayoutReal : ConstraintLayout){
        this.nameText = nameText
        this.loginText = loginText
        this.signupText = signupText
        this.nullLayoutReal = nullLayoutReal
        this.noNullLayoutReal = noNullLayoutReal
    }

    /** 1 **/
    fun findNavigationView(getNavView : NavigationView){
        this.navigationView = getNavView
    }

    private lateinit var currentEmail : Any
    private lateinit var currentId : String
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private fun getCurrentItems(){
        val currentUser : FirebaseUser? = auth.currentUser
        if (currentUser != null) {
            this.currentId = currentUser.uid
            this.currentEmail = currentUser.email!!
        }
    }

    private fun goReactionTime(){
        val intent = Intent(ctx, ReactionTimeMenu::class.java)
        ctx.startActivity(intent)
    }

    /** 3 **/
    fun drawerLayoutListener(navView : NavigationView){
        getCurrentItems()
        navView.setNavigationItemSelectedListener {
            when(it.itemId){
                R.id.reactionTime -> {
                    goReactionTime()
                }
                R.id.numberMemory -> {
                    val intent = Intent(ctx, NumberMemoryMenu::class.java)
                    ctx.startActivity(intent)
                }

                R.id.profile -> {
                    val intent = Intent(ctx, Profile::class.java)
                    ctx.startActivity(intent)
                }

                R.id.scoreboard -> {
                    val intent = Intent(ctx, LeaderBoard::class.java)
                    ctx.startActivity(intent)
                }

                R.id.myScores -> {
                    try {
                        val marketUri: Uri = Uri.parse("market://details?id=com.burak.humanbenchmarks")
                        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
                        ctx.startActivity(marketIntent)
                    } catch (e: ActivityNotFoundException) {
                        val marketUri: Uri =
                            Uri.parse("https://play.google.com/store/apps/details?id=com.burak.humanbenchmarks")
                        val marketIntent = Intent(Intent.ACTION_VIEW, marketUri)
                        ctx.startActivity(marketIntent)
                    }
                    snackCreater.showToastCenter(ctx, "Soon!")
                    //sqlHistories.myHistories()
                }

                R.id.feedback -> {
                    val feedbackLinearLayout = LinearLayout(ctx)
                    feedbackLinearLayout.orientation = LinearLayout.VERTICAL
                    feedbackLinearLayout.setPadding(0, 20, 10, 10)

                    val feedbackEditText = EditText(ctx)
                    feedbackEditText.setBackgroundResource(R.drawable.custom_input_edittext)
                    //feedbackEditText.hint = "What did we do wrong :)"
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    params.setMargins(30, 20, 30, 0)
                    feedbackEditText.layoutParams = params
                    feedbackEditText.setPadding(5, 25, 0, 25)
                    feedbackEditText.setHintTextColor(Color.parseColor("#2B2B2B"))
                    feedbackEditText.setTextColor(Color.parseColor("#FFFFFF"))
                    feedbackEditText.setCompoundDrawablesWithIntrinsicBounds(
                        R.drawable.ic_feedback,
                        0,
                        0,
                        0
                    )
                    feedbackEditText.compoundDrawablePadding = 5
                    feedbackEditText.width = 900
                    feedbackLinearLayout.addView(feedbackEditText)

                    val alert = AlertDialog.Builder(ctx, R.style.CustomAlertDialog)
                    alert.setTitle("Send Feedback")
                    alert.setView(feedbackLinearLayout)
                    alert.setCancelable(false)
                    alert.setPositiveButton("Send") { dialog: DialogInterface, i: Int ->
                        loadingDialog.loadingAlertDialog()
                        if (!feedbackEditText.text.isEmpty()) {
                            val feedbackString = feedbackEditText.text.toString()

                            val feedbackAddHashMap = hashMapOf(
                                "Email" to currentEmail.toString(),
                                "Uid" to currentId,
                                "Feedback" to feedbackString
                            )
                            firebaseManage.firestoreAdd(
                                feedbackAddHashMap,
                                "Feedbacks",
                                currentId,
                                "Thanks for Feedback :)",
                                "Feedback Fail!"
                            )
                            loadingDialog.dismissDialog()
                        } else {
                            snackCreater.createFailSnack("Message cannot be empty.", viewReal)
                            loadingDialog.dismissDialog()
                        }
                    }
                    alert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                    }
                    alert.show()
                }

                R.id.bossMenuItem -> {
                    val intent = Intent(ctx, Boss::class.java)
                    ctx.startActivity(intent)
                }

                R.id.share -> {
                    val getLinkForShareApp = GetLinkForShareApp(ctx, viewReal, activity)
                    getLinkForShareApp.share(
                        "Human Benchmarks S in play store!",
                        "Share Human Benchmark S"
                    )
                }

                R.id.logout -> {
                    var alert = AlertDialog.Builder(ctx, R.style.CustomAlertDialog)
                    alert.setTitle("Log Out")
                    alert.setMessage("Are you sure?")
                    alert.setPositiveButton("Yes") { _: DialogInterface, _: Int ->
                        auth.signOut()
                        loadingDialog.loadingAlertDialog()
                        logoutFun(true)
                        //snackCreater.createSuccessSnack("Log Out Success", viewReal)
                    }
                    alert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                        dialog.cancel()
                    }
                    alert.show()
                }

                R.id.achievementsInMenu -> {
                    val intent = Intent(ctx, ShowAchievements::class.java)
                    ctx.startActivity(intent)
                }
            }
            true
        }
    }

    private fun logoutFun(snackControl: Boolean){
        auth.signOut()
        firebaseManage.getUser(nameText, viewReal, welcomeControl = true)
        underlinedText("Login", loginText)
        underlinedText("Sign Up", signupText)
        navigationView.menu.findItem(R.id.profileGroup).isVisible = false
        navigationView.menu.findItem(R.id.logout).isVisible = false
        navigationView.menu.findItem(R.id.feedback).isVisible = false
        navigationView.menu.findItem(R.id.myScores).isVisible = false
        navigationView.menu.findItem(R.id.bossMenuItem).isVisible = false
        loadingDialog.dismissDialog()
        if (snackControl){
            snackCreater.createSuccessSnack("Log Out Success", viewReal)
        }
        nullLayoutReal.visibility = View.VISIBLE
        noNullLayoutReal.visibility = View.GONE
    }

    private fun underlinedText(text: String, textView: TextView){
        var spannableString = SpannableString(text)
        spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        textView.text = spannableString
    }

}