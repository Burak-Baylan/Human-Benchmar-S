package com.burak.humanbenchmarks

import android.annotation.SuppressLint
import android.content.*
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.InputType
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.burak.humanbenchmarks.ForNumbersMemory.NumberMemoryMenu
import com.burak.humanbenchmarks.ForReactionTime.ReactionTimeMenu
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.custom_toast.*
import kotlinx.android.synthetic.main.custom_toast.view.*
import kotlinx.android.synthetic.main.nav_header.*


class MainActivity : AppCompatActivity() {

    private lateinit var toggle : ActionBarDrawerToggle
    private lateinit var nameTextReal:TextView
    private lateinit var emailTextReal:TextView
    private lateinit var loginTextReal:TextView
    private lateinit var signupTextReal:TextView
    private lateinit var ppImageOnNav : ImageView
    private lateinit var nullLayoutReal : ConstraintLayout
    private lateinit var noNullLayoutReal : ConstraintLayout
    private lateinit var viewReal : View
    private var currentUser : FirebaseUser? = null
    private lateinit var snackCreater : PopupMessageCreator
    private lateinit var auth : FirebaseAuth
    private lateinit var firebase : FirebaseFirestore
    private lateinit var firebaseManage : FirebaseManage
    private lateinit var navigationView : NavigationView
    private lateinit var sqlHistories: SqlHistories
    private var currentEmail : String? = null
    private var currentId : String? = null
    private lateinit var loadingDialog : LoadingDialog
    private lateinit var olmasiGerekTextView : TextView
    private var welcomeControl : Boolean = false
    private var animationControl : animationControl = animationControl(this)

    private lateinit var mAdView : AdView
    override fun onStart() {
        animationControl.forOnStart()
        super.onStart()
    }

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        animationControl.forOnCreate(savedInstanceState)

        MobileAds.initialize(this) {}
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder().build()
        mAdView.loadAd(adRequest)

        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#264653")))
        val window : Window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = Color.parseColor("#264653")

        viewReal = window.decorView.rootView
        menuLayout.visibility = View.VISIBLE
        olmasiGerekTextView = TextView(this)
        sqlHistories = SqlHistories(this, this, viewReal)
        loadingDialog = LoadingDialog(this)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()
        currentEmail = currentUser?.email
        currentId = currentUser?.uid
        val intent = intent
        welcomeControl = intent.getBooleanExtra("welcomeControl", false)
        firebaseManage = FirebaseManage(this, viewReal, this)
        snackCreater = PopupMessageCreator()
        navigationView = findViewById(R.id.navView)
        val headerView = navigationView.getHeaderView(0)
        nameTextReal = headerView.findViewById(R.id.nameString)
        emailTextReal = headerView.findViewById(R.id.emailText)
        loginTextReal = headerView.findViewById(R.id.loginText)
        signupTextReal = headerView.findViewById(R.id.signupText)
        nullLayoutReal = headerView.findViewById(R.id.nullLayout)
        noNullLayoutReal = headerView.findViewById(R.id.noNullLayout)
        ppImageOnNav = headerView.findViewById(R.id.ppImage)
        loginTextReal.setOnClickListener {loginAlertDialog()}
        signupTextReal.setOnClickListener {signupAlertDialog()}
        ppImageOnNav.setOnClickListener {val intent1 = Intent(this, Profile::class.java)
        startActivity(intent1)}
        isFirstEnter()



        /*snackCreater.customToast(
            this, this, Gravity.TOP, Toast.LENGTH_LONG,
            "Welcome $", R.drawable.custom_toast_error, null
        )*/

        if (currentUser != null){
            logInFun(welcomeControl)
            val getPp = GetProfilePhoto(this, this, viewReal)
            val ppImageNav : ImageView = headerView.findViewById(R.id.ppImage)
            getPp.getProfilePhoto(ppImageNav)
        }
        else{
            //loadingDialog.loadingAlertDialog()
            logoutFun(false)
        }

        reactionTimeConstraintLayoutListeners()
        numbersMemoryConstraintLayoutListeners()

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val drawerItemListeners = DrawerItemListeners(this, viewReal, this)
        drawerItemListeners.findNavigationView(navigationView)
        drawerItemListeners.putAllItems(nameTextReal, loginTextReal, signupTextReal, nullLayoutReal, noNullLayoutReal)
        drawerItemListeners.drawerLayoutListener(navigationView)
        netConnect()
    }


    override fun onStop() {
        println("gitti stop")
        super.onStop()
    }

    private fun numbersMemoryConstraintLayoutListeners(){

        numberMemoryConstraint.setOnClickListener {
            val intent = Intent(this, NumberMemoryMenu::class.java)
            startActivity(intent)
        }

        numberMemoryConstraint.setOnLongClickListener {

            val alert = AlertDialog.Builder(
                this,
                R.style.CustomAlertDialogForHistoriesNumbersMemory
            )
            alert.setTitle("Hint")
                alert.setMessage("For the 1st row. More than anyone else, you need to memorize the numbers that appear on the screen.")
            alert.setPositiveButton("Understood") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.show()

            true
        }
    }

    private fun reactionTimeConstraintLayoutListeners(){

        reactionTimeConstraint.setOnClickListener {
            val intent = Intent(this, ReactionTimeMenu::class.java)
            startActivity(intent)
        }

        reactionTimeConstraint.setOnLongClickListener {

            val alert = AlertDialog.Builder(this, R.style.CustomAlertDialogForHistories)
            alert.setTitle("Hint")
            alert.setMessage("When the red color turns green, click on the screen as fast as possible. If you want to reach the 1st row!")
            alert.setPositiveButton("Understood") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.show()

            true
        }
    }


    private fun netConnect(){
        val netControl = firebaseManage.internetControl(this)
        if (!netControl){
            snackCreater.customToast(
                this, this, null, Toast.LENGTH_SHORT, "No Connection",
                R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
            //snackCreater.createFailSnack("No Connection", viewReal)
        }
    }

    private fun loginAlertDialog(){

        val loginLinearLayout = LinearLayout(this)
        loginLinearLayout.orientation = LinearLayout.VERTICAL

        loginLinearLayout.setPadding(10, 20, 10, 10)

        val emailEditText = EditText(this)
        emailEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        emailEditText.maxLines = 1
        emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailEditText.setHintTextColor(Color.parseColor("#2B2B2B"))
        emailEditText.setTextColor(Color.parseColor("#FFFFFF"))
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0)
        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params1.setMargins(10, 20, 10, 0)
        emailEditText.setPadding(5, 25, 0, 25)
        emailEditText.layoutParams = params1
        emailEditText.width = 900
        emailEditText.compoundDrawablePadding = 5
        loginLinearLayout.addView(emailEditText)


        val passwordEditText = EditText(this)
        passwordEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        //passwordEditText.hint = "Password"
        passwordEditText.maxLines = 1
        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params2.setMargins(10, 20, 10, 0)
        passwordEditText.layoutParams = params2
        passwordEditText.setHintTextColor(Color.parseColor("#2B2B2B"))
        passwordEditText.setTextColor(Color.parseColor("#FFFFFF"))
        passwordEditText.width = 900
        passwordEditText.setPadding(5, 25, 0, 25)
        passwordEditText.compoundDrawablePadding = 5
        //passwordEditText.setTransformationMethod(PasswordTransformationMethod.getInstance())
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0)
        loginLinearLayout.addView(passwordEditText)

        val loginAlert = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        loginAlert.setTitle("LOGIN")
        loginAlert.setView(loginLinearLayout)
        loginAlert.setCancelable(false)
        loginAlert.setPositiveButton("LOGIN") { _: DialogInterface, _: Int ->
            loadingDialog.loadingAlertDialog()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            val getPp = GetProfilePhoto(this, this, viewReal)

            try {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    logInFun(welcomeControl)
                    loadingDialog.dismissDialog()
                    getPp.getProfilePhoto(ppImage)
                }.addOnFailureListener {
                    loadingDialog.dismissDialog()
                    snackCreater.customToast(
                        this, this, null, Toast.LENGTH_SHORT, it.localizedMessage!!,
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    snackCreater.createFailSnack(it.localizedMessage!!, viewReal)
                }
            }
            catch (e: Exception){
                loadingDialog.dismissDialog()
                snackCreater.customToast(
                    this, this, null, Toast.LENGTH_SHORT, "Somethings went wrong. Try again.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackCreater.createFailSnack("Somethings went wrong. Try again.", viewReal)
            }

        }
        loginAlert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        loginAlert.setNeutralButton("Forgot\nPassword") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            forgotPasswordAlertDialog()
        }
        loginAlert.show()
    }

    private fun forgotPasswordAlertDialog(){
        val forgotLayout = LinearLayout(this)
        forgotLayout.orientation = LinearLayout.VERTICAL
        forgotLayout.setPadding(10, 20, 10, 0)

        val emailEditText = EditText(this)
        emailEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        emailEditText.width = 900
        emailEditText.setPadding(5, 25, 0, 25)
        emailEditText.maxLines = 10
        emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailEditText.setTextColor(Color.parseColor("#FFFFFF"))
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(10, 20, 10, 0)
        emailEditText.layoutParams = params
        emailEditText.compoundDrawablePadding = 5
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0)
        forgotLayout.addView(emailEditText)

        val alert = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        alert.setTitle("Reset Password")
        alert.setView(forgotLayout)
        alert.setPositiveButton("Send"){ _: DialogInterface, _: Int ->
            if (emailEditText.text != null){
                val email = emailEditText.text.toString()
                firebaseManage.resetPasswordWithEmail(email)
            }
            else{
                snackCreater.customToast(
                    this, this, null, Toast.LENGTH_SHORT, "Somethings went wrong. Try again.",
                    R.drawable.custom_toast_warning, R.drawable.ic_warning_image
                )
                snackCreater.createFailSnack("Email could not be blank.", viewReal)
            }
        }
        alert.setNegativeButton("Cancel"){ dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        alert.setNeutralButton("Back"){ dialog: DialogInterface, _: Int ->
            dialog.cancel()
            loginAlertDialog()
        }
        alert.show()
    }

    private fun signupAlertDialog(){
        val loginLinearLayout = LinearLayout(this)
        loginLinearLayout.orientation = LinearLayout.VERTICAL

        loginLinearLayout.setPadding(10, 20, 10, 10)


        val usernameEditText = EditText(this)
        usernameEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        //usernameEditText.hint = "Username"
        usernameEditText.maxLines = 1
        usernameEditText.setPadding(5, 25, 0, 25)
        usernameEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        usernameEditText.setHintTextColor(Color.parseColor("#2B2B2B"))
        usernameEditText.setTextColor(Color.parseColor("#FFFFFF"))
        val params3 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params3.setMargins(10, 20, 10, 0)
        usernameEditText.layoutParams = params3
        usernameEditText.width = 900
        usernameEditText.compoundDrawablePadding = 5
        usernameEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_username, 0, 0, 0)
        loginLinearLayout.addView(usernameEditText)

        val emailEditText = EditText(this)
        emailEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        //emailEditText.hint = "E-Mail"
        emailEditText.setPadding(5, 25, 0, 25)
        emailEditText.maxLines = 1
        emailEditText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        emailEditText.setHintTextColor(Color.parseColor("#2B2B2B"))
        emailEditText.setTextColor(Color.parseColor("#FFFFFF"))
        val params1 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params1.setMargins(10, 20, 10, 0)
        emailEditText.layoutParams = params1
        emailEditText.width = 900
        emailEditText.compoundDrawablePadding = 5
        emailEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0)
        loginLinearLayout.addView(emailEditText)


        val passwordEditText = EditText(this)
        passwordEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        //passwordEditText.hint = "Password"
        passwordEditText.maxLines = 1
        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params2.setMargins(10, 20, 10, 0)
        passwordEditText.layoutParams = params2
        passwordEditText.setPadding(5, 25, 0, 25)
        passwordEditText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
        passwordEditText.setHintTextColor(Color.parseColor("#2B2B2B"))
        passwordEditText.setTextColor(Color.parseColor("#FFFFFF"))
        passwordEditText.width = 900
        passwordEditText.compoundDrawablePadding = 5
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0)
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        loginLinearLayout.addView(passwordEditText)

        val loginAlert = AlertDialog.Builder(this, R.style.CustomAlertDialog)
        loginAlert.setTitle("SIGN UP")
        loginAlert.setView(loginLinearLayout)
        loginAlert.setCancelable(false)
        loginAlert.setPositiveButton("SIGN UP") { _: DialogInterface, _: Int ->
            loadingDialog.loadingAlertDialog()
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (usernameEditText.text.isNotEmpty() && emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()) {
                auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener {

                    val currentUser = auth.currentUser
                    val profileUpdates = userProfileChangeRequest {
                        displayName = username
                        photoUri = null
                    }
                    currentUser?.updateProfile(profileUpdates)?.addOnSuccessListener {

                        loadingDialog.dismissDialog()
                        //snackCreater.createSuccessSnack("Sign Up Success.", viewReal)
                        loginAlertDialog()
                        firebaseManage.addUserFirestore(email, password, username)

                    }?.addOnFailureListener{ exception ->
                        currentUser.delete().addOnCompleteListener {

                            snackCreater.customToast(
                                this, this, null, Toast.LENGTH_SHORT, exception.localizedMessage!!,
                                R.drawable.custom_toast_error, R.drawable.ic_error_image
                            )
                            /*snackCreater.createFailSnack(
                                exception.localizedMessage!!/*"User cannot be created. Try again."*/,
                                viewReal
                            )*/
                        }
                    }
                }.addOnFailureListener {
                    loadingDialog.dismissDialog()
                    snackCreater.customToast(
                        this, this, null, Toast.LENGTH_SHORT, it.localizedMessage!!,
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    //snackCreater.createFailSnack(it.localizedMessage!!, viewReal)
                }
            }
            else{
                loadingDialog.dismissDialog()
                snackCreater.customToast(
                    this, this, null, Toast.LENGTH_SHORT, "No place can be left blank.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackCreater.createFailSnack("No place can be left blank.", viewReal)
            }
        }
        loginAlert.setNegativeButton("CANCEL") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        loginAlert.setNeutralButton("LOGIN") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
            loginAlertDialog()
        }
        loginAlert.show()
    }

    private fun logInFun(welcomeControl: Boolean){
        currentUser = auth.currentUser
        val getEmail = currentUser?.email
        emailTextReal.text = getEmail
        currentId = currentUser?.uid

        layout = layoutInflater.inflate(R.layout.custom_toast,  custom_toast_layout)
        firebaseManage.getUser(nameTextReal, viewReal, welcomeControl)

        nullLayoutReal.visibility = View.GONE
        noNullLayoutReal.visibility = View.VISIBLE
        navigationView.menu.findItem(R.id.profileGroup).isVisible = true
        navigationView.menu.findItem(R.id.logout).isVisible = true
        navigationView.menu.findItem(R.id.feedback).isVisible = true
        navigationView.menu.findItem(R.id.myScores).isVisible = true
        this@MainActivity.welcomeControl = false

        currentEmail = currentUser!!.email
        if (currentEmail!! == "dsjkadnas@gmail.com") /** Boss item'a erişim için kontrol **/
        {
            navigationView.menu.findItem(R.id.bossMenuItem).isVisible = true
        }
    }

    companion object{
        lateinit var layout : View
    }

    private fun underlinedText(text: String, textView: TextView){
        val spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        textView.text = spannableString
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logoutFun(snackControl: Boolean){
        auth.signOut()
        firebaseManage.getUser(nameTextReal, viewReal, welcomeControl = true)
        underlinedText("Login", loginTextReal)
        underlinedText("Sign Up", signupTextReal)
        /*nullLayoutReal.visibility = View.VISIBLE
        noNullLayoutReal.visibility = View.GONE*/
        navigationView.menu.findItem(R.id.profileGroup).isVisible = false
        navigationView.menu.findItem(R.id.logout).isVisible = false
        navigationView.menu.findItem(R.id.feedback).isVisible = false
        navigationView.menu.findItem(R.id.myScores).isVisible = false
        navigationView.menu.findItem(R.id.bossMenuItem).isVisible = false
        loadingDialog.dismissDialog()
        if (snackControl){

            snackCreater.customToast(
                this, this, null, Toast.LENGTH_SHORT, "Log Out Success",
                R.drawable.custom_toast_success, R.drawable.ic_success_image
            )
            //snackCreater.createSuccessSnack("Log Out Success", viewReal)
        }
        nullLayoutReal.visibility = View
            .VISIBLE
        noNullLayoutReal.visibility = View.GONE
    }

    private fun isFirstEnter(){
        val preferences = getSharedPreferences("com.burak.humanbenchmarks", Context.MODE_PRIVATE)
        val firstEnterIsTrue = preferences.getBoolean("firstenter", true)
        if (firstEnterIsTrue){
            val intent = Intent(this, FirstScreen::class.java)
            startActivity(intent)
            finish()
        }
    }
}