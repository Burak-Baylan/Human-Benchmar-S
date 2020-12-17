package com.burak.humanbenchmarks

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.text.Editable
import android.text.InputType
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.*
import androidx.cardview.widget.CardView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProfileActivityClickListeners(activity: Activity, context: Context, view: View) {

    private lateinit var snackbarCreater: PopupMessageCreator
    private lateinit var sqlHistories: SqlHistories
    private var firebaseManage: FirebaseManage
    private var usernameText : TextView = TextView(context)
    private lateinit var changeWithEmailTextView : TextView
    private lateinit var changeWithOldPasswordTextView : TextView
    private lateinit var changePasswordWithOldPasswordEditText : EditText
    private lateinit var newPasswordEditText : EditText
    private var auth : FirebaseAuth
    private var currentUser : FirebaseUser
    private var oldPasswordTextView : TextView
    private lateinit var deleteAccountPasswordControl : EditText
    private lateinit var deleteControlEditText : EditText
    private lateinit var changePasswordWithEmailEditTExt : EditText

    private var mActivity : Activity = activity
    private var mCtx : Context = context
    private var viewReal : View = view

    init {
        firebaseManage = FirebaseManage(context,view,activity)
        firebaseManage.getUser(usernameText,view,true)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!
        oldPasswordTextView = TextView(mCtx)
        var emailNow = currentUser.email

        firebaseManage.getOldPassword(oldPasswordTextView, emailNow)


    }


    fun historyCardView (cardView : CardView){
        forLateInits()
        cardView.setOnClickListener {
            snackbarCreater.customToast(
                mActivity, mCtx, null, Toast.LENGTH_SHORT,
                "SOON!", R.drawable.custom_toast_info, R.drawable.ic_info_image
            )
            //snackbarCreater.showToastCenter(mCtx, "Soon!")
            //sqlHistories.myHistories()
        }
    }

    fun achievementsCardView (cardView : CardView){
        forLateInits()
        cardView.setOnClickListener {
            val intent = Intent(mCtx, ShowAchievements::class.java)
            mActivity.startActivity(intent)
        }
    }

    fun changePpCardView (cardView : CardView){
        forLateInits()
        cardView.setOnClickListener {
            val intent = Intent(mCtx, ChangePp::class.java)
            mActivity.startActivity(intent)
        }
    }

    fun changePasswordCardView (cardView : CardView){
        forLateInits()
        firebaseManage = FirebaseManage(mCtx,viewReal,mActivity)
        cardView.setOnClickListener {
            changeWithOldPasswordFun()
        }
    }

    private fun changeWithEmailFun(){
        val comingLinearLayout = createLayoutForChangePasswordWithEmail()

        val alert = AlertDialog.Builder(mCtx, R.style.CustomAlertDialog)
        alert.setTitle("Change Password")
        alert.setCancelable(false)
        alert.setView(comingLinearLayout)
        alert.setPositiveButton("Send") { _: DialogInterface, _: Int ->

            val netControl = firebaseManage.internetControl(mActivity)

            if (netControl) {
                val email = changePasswordWithEmailEditTExt.text.toString()
                firebaseManage.resetPasswordWithEmail(email)
            }
            else if (!netControl){

                snackbarCreater.customToast(
                    mActivity, mCtx, null, Toast.LENGTH_SHORT,
                    "You must be connected to the Internet.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackbarCreater.createFailSnack("You must be connected to the Internet.", viewReal)
            }
        }
        alert.setNegativeButton("Cancel") { dialog : DialogInterface, _: Int ->
            dialog.cancel()
        }
        alert.setNeutralButton("CHANGE W.\nOLD PASSWORD") { dialog : DialogInterface, _: Int ->
            dialog.cancel()
            changeWithOldPasswordFun()
        }
        alert.show()
    }

    private fun createLayoutForChangePasswordWithEmail() : LinearLayout{
        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params2.setMargins(10, 20, 10, 0)

        val mLayout = LinearLayout(mCtx)
        mLayout.setPadding(30,5,30,5)
        mLayout.orientation = LinearLayout.VERTICAL


        changePasswordWithEmailEditTExt = EditText(mCtx)
        changePasswordWithEmailEditTExt.setBackgroundResource(R.drawable.custom_input_edittext)
        changePasswordWithEmailEditTExt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_email, 0, 0, 0)
        changePasswordWithEmailEditTExt.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        changePasswordWithEmailEditTExt.maxLines = 1
        changePasswordWithEmailEditTExt.width = 900
        changePasswordWithEmailEditTExt.hint = "E-Mail"
        changePasswordWithEmailEditTExt.setHintTextColor(Color.parseColor("#FFFFFF"))
        changePasswordWithEmailEditTExt.setTextColor(Color.parseColor("#FFFFFF"))
        changePasswordWithEmailEditTExt.compoundDrawablePadding = 5
        changePasswordWithEmailEditTExt.setPadding(5, 25, 0, 25)
        changePasswordWithEmailEditTExt.layoutParams = params2
        mLayout.addView(changePasswordWithEmailEditTExt)

        return mLayout
    }

    private fun changeWithOldPasswordFun(){
        val comingLinearLayout = createLayoutForChangePassword()

        val alert = AlertDialog.Builder(mCtx,R.style.CustomAlertDialog)
        alert.setTitle("Change Password")
        alert.setCancelable(false)
        alert.setView(comingLinearLayout)
        alert.setPositiveButton("Save") { _: DialogInterface, _: Int ->

            val netControl = firebaseManage.internetControl(mActivity)

            if (netControl) {
                val oldPasswordString = changePasswordWithOldPasswordEditText.text.toString()
                val newPasswordString = newPasswordEditText.text.toString()
                firebaseManage.changePasswordNoEmail(newPasswordString, oldPasswordString)
            }
            else if (!netControl){
                snackbarCreater.customToast(
                    mActivity, mCtx, null, Toast.LENGTH_SHORT,
                    "You must be connected to the Internet.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackbarCreater.createFailSnack("You must be connected to the Internet.", viewReal)
            }
        }
        alert.setNegativeButton("Cancel") { dialog : DialogInterface, _: Int ->
            dialog.cancel()
        }
        alert.setNeutralButton("CHANGE W.\n EMAIL") { _: DialogInterface, _: Int ->
            changeWithEmailFun()
        }
        alert.show()
    }

    private fun createLayoutForChangePassword() : LinearLayout{
        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params2.setMargins(10, 20, 10, 0)

        val mLayout = LinearLayout(mCtx)
        mLayout.setPadding(30,5,30,5)
        mLayout.orientation = LinearLayout.VERTICAL


        changePasswordWithOldPasswordEditText = EditText(mCtx)
        changePasswordWithOldPasswordEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        changePasswordWithOldPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0)
        changePasswordWithOldPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        changePasswordWithOldPasswordEditText.maxLines = 1
        changePasswordWithOldPasswordEditText.width = 900
        changePasswordWithOldPasswordEditText.hint = "Old Password"
        changePasswordWithOldPasswordEditText.setHintTextColor(Color.parseColor("#FFFFFF"))
        changePasswordWithOldPasswordEditText.setTextColor(Color.parseColor("#FFFFFF"))
        changePasswordWithOldPasswordEditText.compoundDrawablePadding = 5
        changePasswordWithOldPasswordEditText.setPadding(5, 25, 0, 25)
        changePasswordWithOldPasswordEditText.layoutParams = params2
        mLayout.addView(changePasswordWithOldPasswordEditText)


        newPasswordEditText = EditText(mCtx)
        newPasswordEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        newPasswordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0)
        newPasswordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        newPasswordEditText.maxLines = 1
        newPasswordEditText.width = 900
        newPasswordEditText.hint = "New Password"
        newPasswordEditText.setHintTextColor(Color.parseColor("#FFFFFF"))
        newPasswordEditText.setTextColor(Color.parseColor("#FFFFFF"))
        newPasswordEditText.compoundDrawablePadding = 5
        newPasswordEditText.setPadding(5, 25, 0, 25)
        newPasswordEditText.layoutParams = params2
        mLayout.addView(newPasswordEditText)

        return mLayout
    }

    fun changeUsernameCardView (cardView: CardView){
        forLateInits()
        firebaseManage = FirebaseManage(mCtx,viewReal,mActivity)
        val currentUserId = firebaseManage.getUserId()
        cardView.setOnClickListener {
            val mLinearLayout = LinearLayout(mCtx)
            mLinearLayout.orientation = LinearLayout.HORIZONTAL

            val newUsernameEditText = createEditTextForAlertDialog("New Username",R.drawable.ic_username)
            mLinearLayout.addView(newUsernameEditText)

            val alert = AlertDialog.Builder(mCtx,R.style.CustomAlertDialog)
            alert.setTitle("Update Username")
            alert.setCancelable(false)
            alert.setView(mLinearLayout)
            alert.setPositiveButton("Save") { _: DialogInterface, _: Int ->
                if (newUsernameEditText.text.isBlank()){
                    snackbarCreater.customToast(
                        mActivity, mCtx, null, Toast.LENGTH_SHORT,
                        "New username cannot be empty.",
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    //snackbarCreater.showToastCenter(mCtx, "New username cannot be empty.")
                }
                else
                {
                    val netControl = firebaseManage.internetControl(mActivity)
                    if (netControl) {
                        val newUsernameString = newUsernameEditText.text.toString()
                        firebaseManage.updateUsername(
                            currentUserId!!,
                            newUsernameString,
                            usernameText.text.toString()
                        )
                    }
                    else if (!netControl){
                        snackbarCreater.customToast(
                            mActivity, mCtx, null, Toast.LENGTH_SHORT,
                            "You must be connected to the Internet.",
                            R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                        //snackbarCreater.createFailSnack("You must be connected to the Internet.", viewReal)
                    }
                }
            }
            alert.setNegativeButton("Cancel") { dialog : DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.show()
        }
    }

    fun changeEmailCardView (cardView: CardView){
        forLateInits()
        firebaseManage = FirebaseManage(mCtx, viewReal, mActivity)
        cardView.setOnClickListener {
            val mLinearLayout = LinearLayout(mCtx)
            mLinearLayout.orientation = LinearLayout.HORIZONTAL

            val newEmailEditText = createEditTextForAlertDialog("New E-Mail",R.drawable.ic_email)
            mLinearLayout.addView(newEmailEditText)

            val alert = AlertDialog.Builder(mCtx,R.style.CustomAlertDialog)
            alert.setTitle("Change E-Mail")
            alert.setCancelable(false)
            alert.setView(mLinearLayout)
            alert.setPositiveButton("Save") { _: DialogInterface, _: Int ->

                if (newEmailEditText.text.isBlank())
                {
                    snackbarCreater.customToast(
                        mActivity, mCtx, null, Toast.LENGTH_SHORT,
                        "New Email cannot be blank.",
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    //snackbarCreater.createFailSnack("New Email cannot be blank.",viewReal)
                }
                else {
                    val netControl = firebaseManage.internetControl(mActivity)

                    if (netControl) {
                        val newEmailString = newEmailEditText.text.toString()
                        firebaseManage.updateEmail(newEmailString)
                    }
                    else if (!netControl){
                        snackbarCreater.customToast(
                            mActivity, mCtx, null, Toast.LENGTH_SHORT,
                            "You must be connected to the Internet.",
                            R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                        //snackbarCreater.createFailSnack("You must be connected to the Internet.", viewReal)
                    }
                }
            }
            alert.setNegativeButton("Cancel") { dialog : DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.show()
        }
    }

    fun deleteAccountCardView (cardView: CardView){
        forLateInits()
        firebaseManage = FirebaseManage(mCtx,viewReal,mActivity)
        cardView.setOnClickListener {
            deleteCreater()
        }
    }

    private fun deleteCreater(){
        val layoutSey = layoutForDeleteAccount()
        //listeners2()
        val alert = AlertDialog.Builder(mCtx,R.style.CustomAlertDialog)
        alert.setTitle("Delete Account")
        alert.setMessage("If you delete your account, you cannot get it back.\n\nType your current password to confirm.\n")
        alert.setCancelable(false)
        alert.setView(layoutSey)
        alert.setPositiveButton("Delete") { _: DialogInterface, _: Int ->
            val oldPassword = oldPasswordTextView.text.toString()

            val editTextPassword = deleteAccountPasswordControl.text.toString()

            if (editTextPassword == oldPassword){
                val netControl = firebaseManage.internetControl(mActivity)

                if (netControl) {
                    val usernameString = usernameText.text.toString()
                    firebaseManage.deleteAccount(usernameString)
                }
                else if (!netControl){
                    snackbarCreater.customToast(
                        mActivity, mCtx, null, Toast.LENGTH_SHORT,
                        "You must be connected to the Internet.",
                        R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                    //snackbarCreater.createFailSnack("You must be connected to the Internet.", viewReal)
                }
            }
            else{
                snackbarCreater.customToast(
                    mActivity, mCtx, null, Toast.LENGTH_SHORT,
                    "Password could not be matched",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                //snackbarCreater.createFailSnack("Password could not be matched", viewReal)
                deleteCreater()
            }
        }
        alert.setNegativeButton("Cancel") { dialog : DialogInterface, _: Int ->
            dialog.cancel()
        }
        alert.show()
    }

    private fun forLateInits (){
        snackbarCreater = PopupMessageCreator()
        sqlHistories = SqlHistories(mActivity,mCtx,viewReal)
    }
    
    private fun createEditTextForAlertDialog (editTextHint : String, leftIconForEditText : Int) : EditText{
        val editText = EditText(mCtx)
        editText.setBackgroundResource(R.drawable.custom_input_edittext)
        editText.setPadding(5, 25, 0, 25)
        editText.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        editText.setTextColor(Color.parseColor("#FFFFFF"))
        val params3 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params3.setMargins(30, 20, 30, 5)
        editText.layoutParams = params3
        editText.width = 900
        editText.hint = editTextHint
        editText.setHintTextColor(Color.rgb(255,255,255))
        editText.compoundDrawablePadding = 5
        editText.setCompoundDrawablesWithIntrinsicBounds(leftIconForEditText, 0, 0, 0)

        return editText
    }


    private fun underlinedText(text: String, textView: TextView){

        var spannableString = SpannableString(text)
        spannableString = SpannableString(text)
        spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
        textView.text = spannableString
    }

    private fun layoutForDeleteAccount() : LinearLayout{
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(5, 5, 5, 10)

        val myLayout = LinearLayout(mCtx)
        myLayout.setPadding(30,5,30,0)
        myLayout.orientation = LinearLayout.VERTICAL

        deleteAccountPasswordControl = EditText(mCtx)
        deleteAccountPasswordControl.setBackgroundResource(R.drawable.custom_input_edittext)
        deleteAccountPasswordControl.setPadding(7,25,7,25)
        deleteAccountPasswordControl.setTextColor(Color.rgb(255,255,255))
        deleteAccountPasswordControl.hint = "Current Password"
        deleteAccountPasswordControl.width = 900
        deleteAccountPasswordControl.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0)
        deleteAccountPasswordControl.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        deleteAccountPasswordControl.setHintTextColor(Color.rgb(255,255,255))
        deleteAccountPasswordControl.layoutParams = params
        myLayout.addView(deleteAccountPasswordControl)


        return myLayout
    }
}