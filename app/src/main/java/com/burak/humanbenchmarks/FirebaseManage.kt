package com.burak.humanbenchmarks

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.net.ConnectivityManager
import android.text.InputType
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.burak.humanbenchmarks.ForNumbersMemory.LeadersBoardDesign
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.util.*


class FirebaseManage {

    private var firebase : FirebaseFirestore
    private var auth : FirebaseAuth
    private var snackCreater : SnackbarCreater
    var currentUser : FirebaseUser? = null
    private var loadingDialog: LoadingDialog
    private var userNameControl : TextView
    private var oldPasswordNow : String = ""
    lateinit private var leadersBoardDesign: LeadersBoardDesign


    var mCtx : Context? = null
    var mView : View? = null
    var activity : Activity? = null

    var currentRealUsername = ""
    constructor(context: Context, view: View, activity: Activity){
        this.mCtx = context
        this.mView = view
        this.activity = activity

        snackCreater = SnackbarCreater()
        userNameControl = TextView(context)
        loadingDialog = LoadingDialog(activity)
        //fullScreenLoadingDialog = FullScreenAlertDialogForStartUpApp(activity)
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val currentEmail = currentUser?.email
        leadersBoardDesign = LeadersBoardDesign(context)

        if (currentUser != null) {
            val gidecekText = TextView(mCtx)
            getOldPassword(gidecekText, currentEmail)
        }
        val oylesineLinearLayout = LinearLayout(context)
        val oylesineTextView = TextView(context)
        loadLeaderScores(oylesineLinearLayout, false, userNameControl, oylesineTextView, 15)

        getScoresCurrentUser()
    }

    fun loadingScreenStarter(controlForFullScreen: Boolean){
        loadingController(start = true, stop = false, controlForFullScreen)
    }
    fun loadingScreenDestroyer(controlForFullScreen: Boolean){
        loadingController(start = false, stop = true, controlForFullScreen)
    }

    private fun loadingController(start: Boolean, stop: Boolean, controlFullScreen: Boolean){
        if (!controlFullScreen){
            if (start) {
                loadingDialog.loadingAlertDialog()
            }
            else if (stop){
                loadingDialog.dismissDialog()
            }
        }
    }
    fun getUser(putText: TextView, viewReal: View, welcomeControl: Boolean){
        //loadingScreenStarter(false)
        val snackCreater = SnackbarCreater()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val emailCurrent = currentUser?.email
        firebase = FirebaseFirestore.getInstance()
        //Toast.makeText(mCtx,emailCurrent,Toast.LENGTH_SHORT).show()

        try {
            val getUsername = currentUser!!.displayName

            if (!welcomeControl) {
                snackCreater.createSuccessSnack("Welcome '$getUsername'", viewReal)
            }

            this.currentRealUsername = getUsername!!
            putText.text = getUsername
        }
        catch (e: Exception){

        }
    }

    /*fun getAchievements(){
        val getAchievements = AchievementsControl(mCtx!!,activity!!,mView!!)
        //getAchievements.getAchievementsForShowNumber()
    }*/

    fun loadLeaderScores(
        leaderLayout: LinearLayout,
        nameColorControl: Boolean,
        myNameControlTextView: TextView,
        deleteMeOnLeaderBoardButton: TextView,
        enFazlaKacKisi: Long
    ){
        val snackbarCreater = SnackbarCreater()
        var deleteMeOnLeaderBoardButtonVisibilityControl = false
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()
        firebase.collection("Scores").orderBy("ScoreAverage", Query.Direction.ASCENDING).limit(
            enFazlaKacKisi
        ).addSnapshotListener{ snapshot, exception ->
            if (exception != null)
            {
                snackbarCreater.createFailSnack("Leader Board cannot be installed.", mView!!)
            }
            else
            {
                if (snapshot != null)
                {
                    var kisi : Int = 0

                    leaderLayout.removeAllViews()
                    val documents = snapshot.documents
                    for (document in documents)
                    {
                        /** Burası bir for döngüsü olduğu için ve biz başta görünürlüğü sıfırladığımız için aşşağıda isim eşleşse de for dögüsü döndüğü için
                         * aşşğıda deleteMeOnLeaderBordButton'u visible yapmamıza rağmen yukarıya tekrar gelip invisible yapıyor ve bu isim currentUsername ile
                         * eşleşmediği için visible yapmıyor ve invisible kalıyor.
                         */
                        if (!deleteMeOnLeaderBoardButtonVisibilityControl)
                        {
                            deleteMeOnLeaderBoardButton.visibility = View.INVISIBLE
                        }

                        val leaderScoresText = TextView(mCtx)
                        leaderScoresText.textSize = 16f
                        leaderScoresText.setTextColor(Color.rgb(255, 255, 255))
                        var usernameCurrent : String = document.get("Username") as String
                        val averageScore: Number = document.get("ScoreAverage") as Number
                        var isimDegisBool = false
                        if (averageScore.toInt() > 0) {
                            if (!nameColorControl)
                            /** Eğer ismi kontrol ederek kullanıcıya kullanıcının adıyla aynı birisi varsa yeşil renkte sunmak istiyorsa **/
                            {
                                if (usernameCurrent == currentRealUsername)
                                /** DB'den çekilen isim currenUsername ile aynıysa yeşil yap ve deleteMeOnLeaderBoard button visibility **/
                                {
                                    isimDegisBool = true
                                    leaderScoresText.setTextColor(Color.parseColor("#66E66B"))
                                    myNameControlTextView.text = "true"
                                    deleteMeOnLeaderBoardButton.visibility = View.VISIBLE
                                    deleteMeOnLeaderBoardButtonVisibilityControl = true
                                }
                            } else if (nameColorControl)
                            /** Eğer ismi sadece beyaz istiyorsa **/
                            {
                                leaderScoresText.setTextColor(Color.parseColor("#FFFFFF"))
                            }

                            if (usernameCurrent.length > 8)
                            /** LeaderBoard'da aşşağıya taşmaması için alınmış isim kısaltma önlemi. **/
                            {
                                usernameCurrent = "${usernameCurrent.substring(0, 6)}.."
                            }

                            if (kisi >= 1)
                            /** Her isimden sonra çizgi eklemek.
                             * Buradaki kontrol ilk resmin üzerine çizgiyi eklememek için. Nedeni kötü gözükmesi. **/
                            {
                                val imageForCizgi = TextView(mCtx)
                                imageForCizgi.setBackgroundColor(Color.parseColor("#EDC755"))
                                imageForCizgi.width = 900
                                imageForCizgi.height = 3
                                imageForCizgi.setPadding(0, 2, 0, 2)
                                leaderLayout.addView(imageForCizgi)
                            }

                            kisi++
                            if (isimDegisBool){usernameCurrent = "You"}
                            var scoreString = "$kisi- $usernameCurrent: $averageScore"

                            if (kisi == 1){
                                if (usernameCurrent == currentRealUsername){

                                }
                            }
                            val averageInt = averageScore.toInt()
                            if (averageInt > 9999)
                            /** Alt satıra taşmaması için yapılmış skor kısaltma önlemi. **/
                            {
                                leaderScoresText.textSize = 14f
                                scoreString = "$kisi- $usernameCurrent: 10k+"
                            }

                            val spannableString = SpannableString(scoreString)
                            spannableString.setSpan(
                                StyleSpan(Typeface.BOLD),
                                0,
                                spannableString.length,
                                0
                            )

                            leaderScoresText.text = spannableString
                            leaderLayout.addView(leaderScoresText)
                        }
                    }
                }
            }
        }
    }

    fun getNumbersMemoryLeader(
        leaderLayout: GridLayout,
        nameColorControl: Boolean,
        enFazlaKacKisi: Long,
        deleteMeOnLeaderBoardButton: TextView
    ){
        val snackbarCreator = SnackbarCreater()
        var deleteMeOnLeaderBoardButtonVisibilityControl = false
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()
        firebase.collection("Scores").orderBy("NumbersScore", Query.Direction.DESCENDING).limit(
            enFazlaKacKisi
        ).addSnapshotListener{ snapshot, exception ->
            if (exception != null)
            {
                snackbarCreator.createFailSnack("Leader Board cannot be installed.", mView!!)
            }
            else
            {
                if (snapshot != null)
                {
                    var kisi : Int = 0
                    leaderLayout.removeAllViews()
                    val documents = snapshot.documents

                    var countAchievement : Int
                    var countAllAchievement: Int
                    for (document in documents)
                    {
                        /** Burası bir for döngüsü olduğu için ve biz başta görünürlüğü sıfırladığımız için aşşağıda isim eşleşse de for dögüsü döndüğü için
                         * aşşğıda deleteMeOnLeaderBordButton'u visible yapmamıza rağmen yukarıya tekrar gelip invisible yapıyor ve bu isim currentUsername ile
                         * eşleşmediği için visible yapmıyor ve invisible kalıyor.
                         */
                        if (!deleteMeOnLeaderBoardButtonVisibilityControl){
                            deleteMeOnLeaderBoardButton.visibility = View.INVISIBLE
                        }

                        val leaderScoresText = TextView(mCtx)
                        leaderScoresText.textSize = 16f
                        leaderScoresText.setTextColor(Color.rgb(255, 255, 255))
                        var usernameCurrent : String = document.get("Username") as String
                        val lastInit: Number = document.get("NumbersScore") as Number
                        val uid : String = document.get("Uid") as String
                        println("$uid")
                        var isimDegisBool = false
                        if (lastInit.toInt() > 0) {

                            firebase.collection("Users").document(uid).collection("Achievements").document(
                                "numbersMemoryAchievements"
                            ).addSnapshotListener { snapshot, excepiton ->
                                if (exception != null){
                                    snackbarCreator.createFailSnack(
                                        "Leader Board cannot be installed.",
                                        mView!!
                                    )
                                }
                                else{ // Hata yoksa
                                    if (snapshot != null && snapshot.exists()){
                                        countAllAchievement = 0
                                        countAchievement = 0
                                        val brainStorm = snapshot.get("brainStorm") as Boolean
                                        val impatient = snapshot.get("impatient") as Boolean
                                        val rookie = snapshot.get("rookie") as Boolean
                                        val smart = snapshot.get("smart") as Boolean

                                        fun kontrolEt(getBool: Boolean){
                                            if (getBool){
                                                countAchievement++
                                            }
                                            countAllAchievement++
                                        }
                                        kontrolEt(brainStorm)
                                        kontrolEt(impatient)
                                        kontrolEt(rookie)
                                        kontrolEt(smart)

                                        /*********************************************************************************************/
                                        val getLinearLayout = leadersBoardDesign.createLinearLayout()
                                        val getUsernameTextView = leadersBoardDesign.createUsernameTextView()
                                        val getScoreTextView = leadersBoardDesign.createScoreTextView()
                                        val getAchievementsTextView = leadersBoardDesign.createAchievementsCountTextView()
                                        /*********************************************************************************************/

                                        if (!nameColorControl)
                                        /** Eğer ismi kontrol ederek kullanıcıya kullanıcının adıyla aynı birisi varsa yeşil renkte sunmak istiyorsa **/
                                        {
                                            val getUsername = currentUser?.displayName
                                            if (usernameCurrent == getUsername)
                                            /** DB'den çekilen isim currenUsername ile aynıysa yeşil yap ve deleteMeOnLeaderBoard button visibility **/
                                            {
                                                leaderScoresText.setTextColor(Color.parseColor("#66E66B"))
                                                getUsernameTextView.setTextColor(Color.parseColor("#66E66B"))
                                                isimDegisBool = true
                                                deleteMeOnLeaderBoardButton.visibility = View.VISIBLE
                                                deleteMeOnLeaderBoardButtonVisibilityControl = true
                                            }
                                        } else if (nameColorControl)
                                        /** Eğer ismi sadece beyaz istiyorsa **/
                                        {
                                            leaderScoresText.setTextColor(Color.parseColor("#FFFFFF"))
                                            getUsernameTextView.setTextColor(Color.parseColor("#FFFFFF"))
                                        }

                                        if (usernameCurrent.length > 8)
                                        /** LeaderBoard'da aşşağıya taşmaması için alınmış isim kısaltma önlemi. **/
                                        {
                                            usernameCurrent = "${usernameCurrent.substring(0, 6)}.."
                                        }

                                        if (kisi >= 1)
                                        /** Her isimden sonra çizgi eklemek. **/
                                        {
                                            val imageForCizgi = TextView(mCtx)
                                            imageForCizgi.setBackgroundColor(Color.parseColor("#B06558"/*#EDC755*/))
                                            imageForCizgi.width = 900
                                            imageForCizgi.height = 15
                                            imageForCizgi.setPadding(0, 2, 0, 2)
                                            leaderLayout.addView(imageForCizgi)
                                        }

                                        kisi++
                                        if (isimDegisBool){usernameCurrent = "You"}
                                        val scoreString = "$kisi- $usernameCurrent: $lastInit"

                                        val spannableString = SpannableString("$lastInit Digit")
                                        spannableString.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableString.length,
                                            0
                                        )

                                        val spannableString2 = SpannableString("$kisi- $usernameCurrent")
                                        spannableString2.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableString2.length,
                                            0
                                        )

                                        val spannableString3 = SpannableString("Achievements: $countAchievement/$countAllAchievement")
                                        spannableString3.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableString3.length,
                                            0
                                        )

                                        leadersBoardDesign.getRealGridLayout(
                                            getLinearLayout,
                                            getUsernameTextView, getScoreTextView, getAchievementsTextView,
                                            spannableString2, spannableString, spannableString3
                                        )

                                        leaderScoresText.text = spannableString



                                        leaderLayout.addView(getLinearLayout)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    fun addUserFirestore(email: String, password: String, username: String){
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        var user = hashMapOf(
            "UserName" to username,
            "Email" to email,
            "Password" to password
        )
        var userId = currentUser?.uid
        firebase.collection("Users").document(userId!!).set(user).addOnSuccessListener {

            var achievementsMap = hashMapOf(
                "20roundsRow" to false, // 20 round arka arkaya
                "tooSlow" to false, // 10 saniyeden fazla
                "tooLucky" to false, // 80ms den az -SKOR-
                "1st" to false, // 1 sıraya yerleşmiş
                "turtle" to false, // 50 saniyeden fazla
                "areYouRobot" to false // 80ms den daha az -ORTALAMA-
            )

            firebase.collection("Users").document(userId).collection("Achievements").document("allAchievements").set(
                achievementsMap
            ).addOnSuccessListener {

                val currentEmail = currentUser!!.email

                val addScoreAverage: HashMap<String, Serializable?> = hashMapOf(
                    "Email" to currentEmail,
                    "Uid" to userId,
                    "ScoreAverage" to 0,
                    "NumbersScore" to 0,
                    "Username" to username
                )

                val numbersMemoryAchievementsMap = hashMapOf(
                    "brainStorm" to false, // 18 haneli saı bil
                    "rookie" to false, // 7 haneli sayı bil
                    "smart" to false, // 10 haneli sayı bil
                    "impatient" to false // Süre bitmeden geç
                )

                firebase.collection("Scores").document(userId).set(addScoreAverage).addOnSuccessListener {//
                    firebase.collection("Users").document(userId).collection("Achievements").document(
                        "numbersMemoryAchievements"
                    ).set(numbersMemoryAchievementsMap).addOnSuccessListener {
                        snackCreater.createSuccessSnack("Sign Up Success.", mView!!)
                    }.addOnFailureListener {
                        firebase.collection("Users").document(userId).delete().addOnCompleteListener {
                            firebase.collection("Scores").document(userId).delete().addOnCompleteListener {
                                currentUser?.delete()?.addOnCompleteListener {
                                    snackCreater.createFailSnack(
                                        "User cannot be created. Try again.",
                                        mView!!
                                    )
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    firebase.collection("Users").document(userId).delete().addOnCompleteListener {
                        firebase.collection("Scores").document(userId).delete().addOnCompleteListener {
                            currentUser?.delete()?.addOnCompleteListener {
                                snackCreater.createFailSnack(
                                    "User cannot be created. Try again.",
                                    mView!!
                                )
                            }
                        }
                    }
                }
            }.addOnFailureListener{
                firebase.collection("Users").document(userId).delete().addOnCompleteListener {
                    currentUser?.delete()?.addOnCompleteListener {
                        snackCreater.createFailSnack("User cannot be created. Try again.", mView!!)
                    }
                }
            }
        }.addOnFailureListener { e ->
            currentUser?.delete()?.addOnCompleteListener {
                snackCreater.createFailSnack("User cannot be created. Try again.", mView!!)
            }
        }
    }

    /** Eğer hata olursa hashMap : HashMap<String, String?> yap **/
    fun firestoreAdd(
        hashMap: HashMap<String, String>,
        collection: String,
        userId: String,
        successMessage: String,
        failMessage: String
    ){
        snackCreater = SnackbarCreater()
        loadingDialog = LoadingDialog(activity)
        loadingDialog.loadingAlertDialog()
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val uuId = UUID.randomUUID()
        uuId.toString()
        firebase.collection(collection).document("$userId $uuId").set(hashMap).addOnSuccessListener {
            snackCreater.createSuccessSnack(successMessage, mView!!)
            loadingDialog.dismissDialog()
        }.addOnFailureListener {
            snackCreater.createFailSnack(failMessage, mView!!)
            loadingDialog.dismissDialog()
        }
    }

    fun firestoreAddWNoUuid(
        hashMap: HashMap<String, Serializable?>,
        collection: String,
        userId: String,
        successMessage: String,
        failMessage: String
    ){
        snackCreater = SnackbarCreater()
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        firebase.collection(collection).document(userId).set(hashMap).addOnSuccessListener {
            snackCreater.createSuccessSnack(successMessage, mView!!)
        }.addOnFailureListener {
            snackCreater.createFailSnack(failMessage, mView!!)
        }
    }

    /*fun doSnackbar(message : String, backgroundColor : String, textColor : String){
        snackCreater = SnackbarCreater()
        snackCreater.createSnack(message,mView!!,backgroundColor,textColor)
    }*/

    fun resetPasswordWithEmail(email: String){
        snackCreater = SnackbarCreater()

        loadingDialog = LoadingDialog(activity)

        loadingDialog.loadingAlertDialog()

        auth = FirebaseAuth.getInstance()


        try {
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                snackCreater.showToastCenter(mCtx!!, "Email sent. Check your mail box.")
                loadingDialog.dismissDialog()

            }.addOnFailureListener {

                snackCreater.createFailSnack(it.localizedMessage!!, mView!!)
                loadingDialog.dismissDialog()
            }
        }
        catch (e: Exception) {
            snackCreater.createFailSnack(e.localizedMessage!!, mView!!)
            loadingDialog.dismissDialog()
        }
    }

    fun internetControl(activity: Activity):Boolean{
        val connectivityManager=activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo=connectivityManager.activeNetworkInfo
        return networkInfo!=null && networkInfo.isConnected
    }

    fun loginAlertDialog(textView: TextView){
        auth = FirebaseAuth.getInstance()
        loadingDialog = LoadingDialog(activity)
        snackCreater = SnackbarCreater()

        val loginLinearLayout = LinearLayout(mCtx)
        loginLinearLayout.orientation = LinearLayout.VERTICAL

        loginLinearLayout.setPadding(10, 20, 10, 10)

        val emailEditText = EditText(mCtx)
        emailEditText.setBackgroundResource(R.drawable.custom_input_edittext)
        //emailEditText.hint = "E-Mail"
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


        val passwordEditText = EditText(mCtx!!)
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

        var loginAlert = AlertDialog.Builder(mCtx!!, R.style.CustomAlertDialog)
        loginAlert.setTitle("Login")
        loginAlert.setView(loginLinearLayout)
        loginAlert.setCancelable(false)
        loginAlert.setPositiveButton("Login") { dialog: DialogInterface, i: Int ->
            loadingDialog.loadingAlertDialog()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            try {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    loadingDialog.dismissDialog()
                    getUser(textView, mView!!, welcomeControl = false)
                }.addOnFailureListener {
                    loadingDialog.dismissDialog()
                    snackCreater.createFailSnack(it.localizedMessage, mView!!)
                }
            }
            catch (e: Exception){
                loadingDialog.dismissDialog()
                snackCreater.createFailSnack("Somethings went wrong. Try again.", mView!!)
            }

        }
        loginAlert.setNegativeButton("Cancel") { dialog: DialogInterface, i: Int ->
            dialog.cancel()
        }
        loginAlert.show()
    }

    fun getUserId () : String? {
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        return if (currentUser != null) {
            currentUser!!.uid
        } else{
            null
        }
    }

    fun updateUsername(userId: String, newUsername: String, oldUsername: String){
        val oldUsernameTextView = TextView(mCtx)
        oldUsernameTextView.text = oldUsername

        loadingScreenStarter(false)
        firebase = FirebaseFirestore.getInstance()
        snackCreater = SnackbarCreater()
        val currentUser = auth.currentUser
        //snackCreater.showToastCenter(mCtx!!,"oldTextViewText: ${oldUsernameTextView.text.toString()}")

        val currentPpUrl = currentUser?.photoUrl

        val newProfileUpdates = userProfileChangeRequest {
            displayName = newUsername
            photoUri = currentPpUrl
        }

        //Toast.makeText(mCtx,"$newUsername",Toast.LENGTH_LONG).show()

        val oldProfileUpdates = userProfileChangeRequest {
            displayName = oldUsername
            photoUri = currentPpUrl
        }

        currentUser?.updateProfile(newProfileUpdates)?.addOnCompleteListener {
            firebase.collection("Users").document(userId).update("UserName", newUsername)
                .addOnSuccessListener {
                    firebase.collection("Scores").document(userId).update("Username", newUsername)
                        .addOnSuccessListener {
                            loadingScreenDestroyer(false)
                            snackCreater.createSuccessSnack("Username changed!", mView!!)

                            val intent = Intent(mCtx, MainActivity::class.java)
                            activity?.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                        }.addOnFailureListener {
                            if (userNameControl.text == "true") {
                                firebase.collection("Users").document(userId)
                                    .update("UserName", oldUsernameTextView.text.toString())
                                    .addOnSuccessListener {
                                        currentUser.updateProfile(oldProfileUpdates).addOnCompleteListener {
                                            loadingScreenDestroyer(false)
                                            snackCreater.showToastCenter(
                                                mCtx!!, "Username could not be changed!"
                                            )
                                        }
                                    }.addOnFailureListener {
                                        currentUser.updateProfile(oldProfileUpdates).addOnCompleteListener {
                                            loadingScreenDestroyer(false)
                                            snackCreater.createFailSnack(
                                                "Username could not be changed!",
                                                mView!!
                                            )
                                        }
                                    }
                            } else {
                                currentUser.updateProfile(oldProfileUpdates).addOnCompleteListener {
                                    loadingScreenDestroyer(false)
                                    snackCreater.createSuccessSnack("Username changed!", mView!!)
                                }
                            }
                        }
                }.addOnFailureListener {
                    loadingScreenDestroyer(false)
                    snackCreater.createFailSnack("Username could not be changed!", mView!!)
                }
        }?.addOnFailureListener {
            loadingScreenDestroyer(false)
            snackCreater.createFailSnack("Username could not be changed!", mView!!)
        }
    }

    fun updateEmail(newEmail: String){
        snackCreater = SnackbarCreater()
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser
        val oldEmail = user!!.email
        loadingScreenStarter(false)

        user.updateEmail(newEmail).addOnSuccessListener {

            loadingScreenDestroyer(false)
            updateEmailUsers(newEmail, oldEmail!!)

        }.addOnFailureListener{

            loadingScreenDestroyer(false)
            snackCreater.createFailSnack(it.localizedMessage!!, mView!!)

        }
    }

    private fun updateEmailUsers(newEmail: String, oldEmail: String) {
        snackCreater = SnackbarCreater()
        val currentUserId = getUserId()
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser

        firebase.collection("Users").document(currentUserId!!).update("Email", newEmail).addOnCompleteListener { task ->
            if (task.isSuccessful){
                updateEmailScores(newEmail, oldEmail)
            }
            else{
                user!!.updateEmail(oldEmail).addOnCompleteListener{
                    loadingScreenDestroyer(false)
                    snackCreater.createFailSnack("Email could not be updated", mView!!)
                }
            }
        }
    }

    private fun updateEmailScores(newEmail: String, oldEmail: String){
        snackCreater = SnackbarCreater()
        val currentUserId = getUserId()
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser

        firebase.collection("Scores").document(currentUserId!!).update("Email", newEmail).addOnCompleteListener { task ->
            if (task.isSuccessful){
                loadingScreenDestroyer(false)
                snackCreater.showToastCenter(mCtx!!, "Email address updated.")
            }
            else{
                user!!.updateEmail(oldEmail).addOnCompleteListener{
                    firebase.collection("Users").document(currentUserId).update("Email", oldEmail).addOnCompleteListener{
                        loadingScreenDestroyer(false)
                        snackCreater.createFailSnack("Email could not be updated", mView!!)
                    }
                }
            }
        }
    }

    fun changePasswordNoEmail(newPassword: String, oldPasswordComing: String){
        snackCreater = SnackbarCreater()
        val currentUserId = getUserId()
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser

        loadingScreenStarter(false)

        if (oldPasswordComing == oldPasswordNow) {
            snackCreater.createSuccessSnack("Matched", mView!!)
            user!!.updatePassword(newPassword).addOnSuccessListener {

                firebase.collection("Users").document(currentUserId!!)
                    .update("Password", newPassword).addOnSuccessListener {

                        snackCreater.showToastCenter(mCtx!!, "Password changed.")
                        loadingScreenDestroyer(false)

                    }.addOnFailureListener {
                        user.updatePassword(oldPasswordNow).addOnCompleteListener {
                            snackCreater.createFailSnack("Password could not be changed.", mView!!)
                            loadingScreenDestroyer(false)
                        }
                    }
            }.addOnFailureListener {
                snackCreater.createFailSnack(it.localizedMessage!!, mView!!)
                loadingScreenDestroyer(false)
            }
        }
        else{
            snackCreater.createFailSnack("Password could not be matched.", mView!!)
            loadingScreenDestroyer(false)
        }
    }

    var scoreAverageGet : Number = 0
    var usernameGet = ""
    var numbersMemoryScoreGet : Number = 0


    fun deleteAccount(username: String){
        val userId = getUserId()
        firebase = FirebaseFirestore.getInstance()
        loadingScreenStarter(false)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase.collection("Users").document(userId!!).delete().addOnSuccessListener {
            firebase.collection("Scores").document(userId).delete().addOnSuccessListener {
                currentUser!!.delete().addOnSuccessListener {

                    snackCreater.showToastCenter(mCtx!!, "User deleted.")
                    loadingScreenDestroyer(false)
                    val intent = Intent(activity, MainActivity::class.java)
                    activity!!.startActivity(intent)
                    activity!!.finish()

                }.addOnFailureListener{

                    Toast.makeText(mCtx, it.localizedMessage!!, Toast.LENGTH_LONG).show()
                    loadingScreenDestroyer(false)
                    setUserCurrentUser()
                    setScoresCurrentUser()
                    setUserAchievementsAgain()
                    println("burada")
                }
            }.addOnFailureListener{
                if (userNameControl.text == "true"){
                    setUserCurrentUser()
                    loadingScreenDestroyer(false)
                }
                else{
                    loadingScreenDestroyer(false)
                    snackCreater.showToastCenter(mCtx!!, "Olması gereken oldu.")
                }
                setUserAchievementsAgain()
            }

        }.addOnFailureListener{
            loadingScreenDestroyer(false)
            snackCreater.createFailSnack("Account could not be deleted.", mView!!)
        }
    }

    private fun setUserAchievementsAgain(){

        val snackbarCreater = SnackbarCreater()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val uid = currentUser?.uid
        firebase = FirebaseFirestore.getInstance()

        val reactionTimeAchievementsMap = hashMapOf(
            "1st" to st1,
            "20roundsRow" to row20Rounds,
            "areYouRobot" to robotOr,
            "tooLucky" to tooLuck,
            "tooSlow" to tooSlow,
            "turtle" to turtle
        )

        val numberMemoryAchievementsMap = hashMapOf(
            "brainStorm" to brainStorm,
            "impatient" to impatient,
            "rookie" to rookie,
            "smart" to smart
        )
        firebase.collection("Users").document(uid!!).collection("Achievements").document("allAchievements").set(
            reactionTimeAchievementsMap
        ).addOnCompleteListener {
            firebase.collection("Users").document(uid).collection("Achievements").document("numbersMemoryAchievements").set(
                numberMemoryAchievementsMap
            ).addOnCompleteListener {

            }
        }
    }

    /** ReactionTime Achievements **/
    private var row20Rounds = false
    private var tooSlow = false
    private var tooLuck = false
    private var st1 = false
    private var turtle = false
    private var robotOr = false

    /** NumbersMemoryAchievements **/
    private var brainStorm = false
    private var impatient = false
    private var rookie = false
    private var smart = false

    private fun getScoresCurrentUser(){

        val snackbarCreater = SnackbarCreater()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser?.uid
            firebase = FirebaseFirestore.getInstance()
            firebase.collection("Scores").whereEqualTo("Uid", uid)
                .addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        snackbarCreater.showToastCenter(mCtx!!, "$exception")
                    } else {
                        if (snapshot != null) {
                            val documents = snapshot.documents
                            for (document in documents) {
                                scoreAverageGet = document.get("ScoreAverage") as Number
                                numbersMemoryScoreGet = document.get("NumbersScore") as Number
                                usernameGet = document.get("Username") as String
                                //Toast.makeText(mCtx, "$scoreAverageGet", Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }

            firebase.collection("Users").document(uid!!).collection("Achievements")
                .document("allAchievements").addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        snackbarCreater.showToastCenter(mCtx!!, "$exception")
                    } else {
                        if (snapshot != null && snapshot.exists()) {
                            row20Rounds = snapshot.get("20roundsRow") as Boolean // 20 round arka arkaya
                            tooSlow = snapshot.get("tooSlow") as Boolean // 10 saniyeden fazla
                            tooLuck = snapshot.get("tooLucky") as Boolean // 80ms den az -SKOR-
                            st1 = snapshot.get("1st") as Boolean // 1 sıraya yerleşmiş
                            turtle = snapshot.get("turtle") as Boolean // 50 saniyeden fazla
                            robotOr = snapshot.get("areYouRobot") as Boolean // 80ms den az -ORTALAMA-
                        }
                    }
                }

            firebase.collection("Users").document(uid).collection("Achievements")
                .document("numbersMemoryAchievements").addSnapshotListener { snapshot, exception ->
                    if (exception != null) {
                        snackbarCreater.showToastCenter(mCtx!!, "$exception")
                    } else {
                        if (snapshot != null && snapshot.exists()) {
                            brainStorm = snapshot.get("brainStorm") as Boolean // 18 haneli bir sayı bil
                            impatient = snapshot.get("impatient") as Boolean // 10 haneli bir sayı bil
                            rookie = snapshot.get("rookie") as Boolean // 7 haneli bir sayı bil
                            smart = snapshot.get("smart") as Boolean // 1 sıraya yerleşmiş
                        }
                    }
                }
        }
    }

    private fun setUserCurrentUser (){
        val emailCurrent = currentUser?.email
        //addUserFirestore(emailCurrent!!,oldPasswordNow,usernameGet)

        val user = hashMapOf(
            "UserName" to usernameGet,
            "Email" to emailCurrent,
            "Password" to oldPasswordNow
        )
        val userId = currentUser?.uid
        firebase.collection("Users").document(userId!!).set(user).addOnCompleteListener {
            println("kaydedildi")
        }
    }

    private fun setScoresCurrentUser (){
        val emailCurrent = currentUser?.email
        val idCurrent = currentUser?.uid

        val userScores = hashMapOf(
            "Email" to emailCurrent,
            "NumbersScore" to numbersMemoryScoreGet,
            "ScoreAverage" to scoreAverageGet,
            "Uid" to idCurrent,
            "Username" to usernameGet
        )

        firebase.collection("Scores").document(idCurrent!!).set(userScores).addOnSuccessListener {
            snackCreater.createSuccessSnack("", mView!!)
        }.addOnFailureListener {
            snackCreater.createFailSnack("", mView!!)
        }
    }

    private fun getUserCurrentUser (){

        var snackbarCreater = SnackbarCreater()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        var currentEmail = currentUser?.email
        firebase = FirebaseFirestore.getInstance()
        firebase.collection("Users").whereEqualTo("Email", currentEmail).addSnapshotListener{ snapshot, exception ->
            if (exception != null)
            {
                snackbarCreater.createFailSnack("User get failed.", mView!!)
            }
            else
            {
                if (snapshot != null)
                {
                    val documents = snapshot.documents
                    for (document in documents)
                    {
                        usernameGet = document.get("Username") as String
                    }
                }
            }
        }
    }

    fun getOldPassword(textView: TextView, currentEmail: String?){
        firebase.collection("Users").whereEqualTo("Email", currentEmail).addSnapshotListener{ snapshot, exception ->
            if (exception != null){
                //loadingScreenDestroyer(false)
                snackCreater.showToastLong(mCtx!!, "The password could not be obtained.")
            }
            else{
                if (snapshot != null){
                    val documents = snapshot.documents
                    for (document in documents) {
                        oldPasswordNow = document.get("Password") as String
                        textView.text = oldPasswordNow
                    }
                }
            }
        }
    }
}