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
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import java.io.Serializable
import java.util.*


class FirebaseManage {

    private var firebase : FirebaseFirestore
    private var auth : FirebaseAuth
    private var snackCreator : PopupMessageCreator
    var currentUser : FirebaseUser? = null
    private var loadingDialog: LoadingDialog
    private var userNameControl : TextView
    private var oldPasswordNow : String = ""
    private var leadersBoardDesign: LeadersBoardAndAchievementsScreenDesign


    var mCtx : Context? = null
    var mView : View? = null
    var activity : Activity? = null

    var currentRealUsername = ""
    constructor(context: Context, view: View, activity: Activity){
        this.mCtx = context
        this.mView = view
        this.activity = activity

        snackCreator = PopupMessageCreator()
        userNameControl = TextView(context)
        loadingDialog = LoadingDialog(activity)
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val currentEmail = currentUser?.email
        leadersBoardDesign = LeadersBoardAndAchievementsScreenDesign(context)

        if (currentUser != null) {
            val gidecekText = TextView(mCtx)
            getOldPassword(gidecekText, currentEmail)
        }
        val oylesineLinearLayout = GridLayout(context)
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
        val snackCreater = PopupMessageCreator()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()
        try {
            val getUsername = currentUser!!.displayName
            if (!welcomeControl) {
                snackCreater.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT, "Welcome $getUsername",
                    R.drawable.custom_toast_success, null
                )
            }
            this.currentRealUsername = getUsername!!
            putText.text = getUsername
        }
        catch (e: Exception){}
    }

    fun loadLeaderScores(
        leaderLayout: GridLayout,
        nameColorControl: Boolean,
        myNameControlTextView: TextView,
        deleteMeOnLeaderBoardButton: TextView,
        enFazlaKacKisi: Long
    ){
        val snackbarCreater = PopupMessageCreator()
        var deleteMeOnLeaderBoardButtonVisibilityControl = false
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()
        /******************************************************************************************/
        var getLinearLayout = leadersBoardDesign.createLinearLayout()
        var getScoreTextView = leadersBoardDesign.createScoreTextView()
        var getAchievementsTextView = leadersBoardDesign.createAchievementsCountTextView()
        var getUsernameTextView = leadersBoardDesign.createUsernameTextView()
        var getOnlineOrOfflineTextView = leadersBoardDesign.createOnlineOrOfflineTextView()
        /******************************************************************************************/
        firebase.collection("Scores").orderBy("ScoreAverage", Query.Direction.ASCENDING).limit(
            enFazlaKacKisi
        ).addSnapshotListener{ snapshot, exception ->
            if (exception != null)
            {
                snackbarCreater.createFailSnack("Leader Board couldn't be installed.", mView!!)
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
                        if (!deleteMeOnLeaderBoardButtonVisibilityControl)
                        {
                            deleteMeOnLeaderBoardButton.visibility = View.INVISIBLE
                        }

                        val leaderScoresText = TextView(mCtx)
                        leaderScoresText.textSize = 16f
                        leaderScoresText.setTextColor(Color.rgb(255, 255, 255))
                        var usernameCurrent : String = document.get("Username") as String
                        val averageScore: Number = document.get("ScoreAverage") as Number
                        val uid : String = document.get("Uid") as String
                        var isimDegisBool = false
                        if (averageScore.toInt() > 0) {

                            firebase.collection("Users").document(uid).collection("Achievements").document(
                                "allAchievements"
                            ).addSnapshotListener { snapshotHere, excepitonHere ->
                                if (excepitonHere != null) {
                                    snackbarCreater.createFailSnack("Leader Board couldn't be installed.", mView!!)
                                }
                                else{
                                    countAllAchievement = 0
                                    countAchievement = 0
                                    if (snapshotHere != null && snapshotHere.exists()) {
                                        val st1 = snapshotHere.get("1st") as Boolean
                                        val round20Row = snapshotHere.get("20roundsRow") as Boolean
                                        val areYouRobot = snapshotHere.get("areYouRobot") as Boolean
                                        val tooLucky = snapshotHere.get("tooLucky") as Boolean
                                        val tooSlow = snapshotHere.get("tooSlow") as Boolean
                                        val turtle = snapshotHere.get("turtle") as Boolean

                                        fun kontrolEt(bool : Boolean){
                                            if (bool){
                                                countAchievement++
                                            }
                                            countAllAchievement++
                                        }
                                        kontrolEt(st1)
                                        kontrolEt(round20Row)
                                        kontrolEt(areYouRobot)
                                        kontrolEt(tooLucky)
                                        kontrolEt(tooSlow)
                                        kontrolEt(turtle)
                                        /******************************************************************************************/
                                        getLinearLayout = leadersBoardDesign.createLinearLayout()
                                        getScoreTextView = leadersBoardDesign.createScoreTextView()
                                        getAchievementsTextView = leadersBoardDesign.createAchievementsCountTextView()
                                        getUsernameTextView = leadersBoardDesign.createUsernameTextView()
                                        getOnlineOrOfflineTextView = leadersBoardDesign.createOnlineOrOfflineTextView()
                                        /******************************************************************************************/
                                        if (!nameColorControl)
                                        /** Eğer ismi kontrol ederek kullanıcıya kullanıcının adıyla aynı birisi varsa yeşil renkte sunmak istiyorsa **/
                                        {
                                            if (usernameCurrent == currentRealUsername)
                                            /** DB'den çekilen isim currenUsername ile aynıysa yeşil yap ve deleteMeOnLeaderBoard button visibility **/
                                            {
                                                isimDegisBool = true
                                                leaderScoresText.setTextColor(Color.parseColor("#66E66B"))
                                                getUsernameTextView.setTextColor(Color.parseColor("#66E66B"))
                                                myNameControlTextView.text = "true"
                                                deleteMeOnLeaderBoardButton.visibility = View.VISIBLE
                                                deleteMeOnLeaderBoardButtonVisibilityControl = true
                                            }
                                        } else if (nameColorControl)
                                        /** Eğer ismi sadece beyaz istiyorsa **/
                                        {
                                            leaderScoresText.setTextColor(Color.parseColor("#142A4E"))
                                            getUsernameTextView.setTextColor(Color.parseColor("#142A4E"))
                                        }
                                        /******************************************************************************************/
                                        if (usernameCurrent.length > 8)
                                        /** LeaderBoard'da aşşağıya taşmaması için alınmış isim kısaltma önlemi. **/
                                        {
                                            usernameCurrent = "${usernameCurrent.substring(0, 6)}.."
                                        }
                                        /******************************************************************************************/
                                        if (kisi >= 1)
                                        /** Her isimden sonra çizgi eklemek.
                                         * Buradaki kontrol ilk resmin üzerine çizgiyi eklememek için. Nedeni kötü gözükmesi. **/
                                        {
                                            val imageForCizgi = TextView(mCtx)
                                            imageForCizgi.setBackgroundColor(Color.parseColor("#00adb5"))
                                            imageForCizgi.width = 900
                                            imageForCizgi.height = 15
                                            imageForCizgi.setPadding(0, 2, 0, 2)
                                            leaderLayout.addView(imageForCizgi)
                                        }
                                        /******************************************************************************************/
                                        addListener(getLinearLayout, usernameCurrent, uid)
                                        val userStatusClass = UserStatus()
                                        userStatusClass.addOnlineOrOfflineChangeListener(uid, getOnlineOrOfflineTextView, null)
                                        kisi++
                                        if (isimDegisBool){usernameCurrent = "You"}
                                        var scoreString = "$kisi- $usernameCurrent: $averageScore"
                                        val averageInt = averageScore.toInt()
                                        if (averageInt > 9999)
                                        /** Alt satıra taşmaması için yapılmış skor kısaltma önlemi. **/
                                        {
                                            leaderScoresText.textSize = 14f
                                            scoreString = "$kisi- $usernameCurrent: 10k+"
                                        }
                                        /******************************************************************************************/
                                        /******************************************************************************************/
                                        val spannableStringScore = SpannableString("$averageScore ms")
                                        spannableStringScore.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableStringScore.length,
                                            0
                                        )
                                        /******************************************************************************************/
                                        val spannableStringUser = SpannableString("#$kisi $usernameCurrent: ")
                                        spannableStringUser.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableStringUser.length,
                                            0
                                        )
                                        /******************************************************************************************/
                                        val spannableStringAchievement = SpannableString("Achievements: $countAchievement/$countAllAchievement")
                                        spannableStringAchievement.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableStringAchievement.length,
                                            0
                                        )
                                        /******************************************************************************************/
                                        /******************************************************************************************/
                                        leadersBoardDesign.getRealGridLayout(
                                            getLinearLayout,
                                            getUsernameTextView, getScoreTextView, getAchievementsTextView,
                                            spannableStringUser, spannableStringScore, spannableStringAchievement,
                                            getOnlineOrOfflineTextView
                                        )
                                        /******************************************************************************************/
                                        leaderScoresText.text = spannableStringScore
                                        leaderLayout.addView(getLinearLayout)
                                        /******************************************************************************************/
                                        if (!animControl) {
                                            getLinearLayout.animation = AnimationUtils.loadAnimation(mCtx, R.anim.anim_for_message_row)
                                            animControl = true
                                        }
                                        else{
                                            getLinearLayout.animation = AnimationUtils.loadAnimation(mCtx, R.anim.right_to_left_slide_anim)
                                            animControl = false
                                        }
                                        /******************************************************************************************/
                                    }
                                }
                            }
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
        val snackbarCreator = PopupMessageCreator()
        var deleteMeOnLeaderBoardButtonVisibilityControl = false
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()
        /*********************************************************************************************/
        var getLinearLayout = leadersBoardDesign.createLinearLayout()
        var getUsernameTextView = leadersBoardDesign.createUsernameTextView()
        var getScoreTextView = leadersBoardDesign.createScoreTextView()
        var getAchievementsTextView = leadersBoardDesign.createAchievementsCountTextView()
        var getOnlineOrOfflineTextView = leadersBoardDesign.createOnlineOrOfflineTextView()
        /*********************************************************************************************/
        firebase.collection("Scores").orderBy("NumbersScore", Query.Direction.DESCENDING).limit(
            enFazlaKacKisi
        ).addSnapshotListener{ snapshot, exception ->
            if (exception != null)
            {
                snackbarCreator.createFailSnack("Leader Board couldn't be installed.", mView!!)
            }
            else
            {
                if (snapshot != null)
                {
                    var kisi = 0
                    leaderLayout.removeAllViews()
                    val documents = snapshot.documents

                    var countAchievement : Int
                    var countAllAchievement: Int
                    var docSizeCounter = 0
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
                        val after18Count : Number = document.get("after18Count") as Number
                        println(uid)
                        var isimDegisBool = false
                        if (lastInit.toInt() > 0) {
                            firebase.collection("Users").document(uid).collection("Achievements").document(
                                "numbersMemoryAchievements"
                            ).addSnapshotListener { snapshotHere, excepitonHere ->
                                if (excepitonHere != null){
                                    snackbarCreator.createFailSnack(
                                        "Leader Board couldn't be installed.",
                                        mView!!
                                    )
                                }
                                else{ // Hata yoksa
                                    if (snapshotHere != null && snapshotHere.exists()){
                                        countAllAchievement = 0
                                        countAchievement = 0
                                        val brainStorm = snapshotHere.get("brainStorm") as Boolean
                                        val impatient = snapshotHere.get("impatient") as Boolean
                                        val rookie = snapshotHere.get("rookie") as Boolean
                                        val smart = snapshotHere.get("smart") as Boolean

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
                                        getLinearLayout = leadersBoardDesign.createLinearLayout()
                                        getUsernameTextView = leadersBoardDesign.createUsernameTextView()
                                        getScoreTextView = leadersBoardDesign.createScoreTextView()
                                        getAchievementsTextView = leadersBoardDesign.createAchievementsCountTextView()
                                        getOnlineOrOfflineTextView = leadersBoardDesign.createOnlineOrOfflineTextView()
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
                                            leaderScoresText.setTextColor(Color.parseColor("#142A4E"))
                                            getUsernameTextView.setTextColor(Color.parseColor("#142A4E"))
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
                                        addListener(getLinearLayout, usernameCurrent, uid)
                                        val userStatusClass = UserStatus()
                                        userStatusClass.addOnlineOrOfflineChangeListener(uid, getOnlineOrOfflineTextView, null)
                                        kisi++
                                        if (isimDegisBool){usernameCurrent = "You"}
                                        /******************************************************************************************/
                                        /******************************************************************************************/
                                        val spannableStringScore =
                                            if (after18Count.toInt() > 0 && lastInit.toInt() == 18) {
                                                SpannableString("$lastInit+$after18Count Digit")
                                            }
                                            else {
                                                SpannableString("$lastInit Digit")
                                            }.apply {
                                            this.setSpan(
                                                StyleSpan(Typeface.BOLD),
                                                0,
                                                this.length,
                                                0
                                            )
                                        }
                                        /******************************************************************************************/
                                        val spannableString2 = SpannableString("#$kisi $usernameCurrent: ")
                                        spannableString2.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableString2.length,
                                            0
                                        )
                                        /******************************************************************************************/
                                        val spannableString3 = SpannableString("Achievements: $countAchievement/$countAllAchievement")
                                        spannableString3.setSpan(
                                            StyleSpan(Typeface.BOLD),
                                            0,
                                            spannableString3.length,
                                            0
                                        )
                                        /******************************************************************************************/
                                        /******************************************************************************************/
                                        leadersBoardDesign.getRealGridLayout(
                                            getLinearLayout,
                                            getUsernameTextView, getScoreTextView, getAchievementsTextView,
                                            spannableString2, spannableStringScore, spannableString3
                                            ,getOnlineOrOfflineTextView)
                                        /******************************************************************************************/
                                        leaderScoresText.text = spannableStringScore
                                        leaderLayout.addView(getLinearLayout)
                                        /******************************************************************************************/
                                        if (!animControl) {
                                            getLinearLayout.animation = AnimationUtils.loadAnimation(mCtx, R.anim.anim_for_message_row)
                                            animControl = true
                                        }
                                        else{
                                            getLinearLayout.animation = AnimationUtils.loadAnimation(mCtx, R.anim.right_to_left_slide_anim)
                                            animControl = false
                                        }
                                        /******************************************************************************************/
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    var animControl = false

    private fun addListener (layout : LinearLayout, username : String, userId : String){
        layout.setOnClickListener {
            println(userId)
        }
    }

    fun addUserFirestore(email: String, password: String, username: String){
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        val userId = currentUser?.uid
        val user = hashMapOf(
            "UserName" to username,
            "Email" to email,
            "Password" to password,
            "userStatus" to "offline",
            "uid" to userId,
            "ppurl" to "nulla",
        )

        firebase.collection("Users").document(userId!!).set(user).addOnSuccessListener {

            val achievementsMap = hashMapOf(
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
                    "Username" to username,
                    "after18Count" to 0
                )

                val numbersMemoryAchievementsMap = hashMapOf(
                    "brainStorm" to false, // Know 18-digit number
                    "rookie" to false, // Know 7-digit number
                    "smart" to false, // Know 10-digit numbersayı bil
                    "impatient" to false // Know the number before to time is up
                )

                firebase.collection("Scores").document(userId).set(addScoreAverage).addOnSuccessListener {
                    firebase.collection("Users").document(userId).collection("Achievements").document(
                        "numbersMemoryAchievements"
                    ).set(numbersMemoryAchievementsMap).addOnSuccessListener {

                        snackCreator.customToast(
                            activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                            "Sign Up Success.", R.drawable.custom_toast_success, R.drawable.ic_success_image
                        )
                    }.addOnFailureListener {
                        firebase.collection("Users").document(userId).delete().addOnCompleteListener {
                            firebase.collection("Scores").document(userId).delete().addOnCompleteListener {
                                currentUser?.delete()?.addOnCompleteListener {

                                    snackCreator.customToast(
                                        activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                                        "User couldn't be created. Try again.", R.drawable.custom_toast_error, R.drawable.ic_error_image
                                    )
                                }
                            }
                        }
                    }
                }.addOnFailureListener {
                    firebase.collection("Users").document(userId).delete().addOnCompleteListener {
                        firebase.collection("Scores").document(userId).delete().addOnCompleteListener {
                            currentUser?.delete()?.addOnCompleteListener {
                                snackCreator.customToast(
                                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                                    "User couldn't be created. Try again.", R.drawable.custom_toast_error, R.drawable.ic_error_image
                                )
                            }
                        }
                    }
                }
            }.addOnFailureListener{
                firebase.collection("Users").document(userId).delete().addOnCompleteListener {
                    currentUser?.delete()?.addOnCompleteListener {
                        snackCreator.customToast(
                            activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                            "User couldn't be created. Try again.", R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                    }
                }
            }
        }.addOnFailureListener { _ ->
            currentUser?.delete()?.addOnCompleteListener {
                snackCreator.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                    "User couldn't be created. Try again.", R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
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
        snackCreator = PopupMessageCreator()
        loadingDialog = LoadingDialog(activity)
        loadingDialog.loadingAlertDialog()
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val uuId = UUID.randomUUID()
        uuId.toString()
        firebase.collection(collection).document("$userId $uuId").set(hashMap).addOnSuccessListener {
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                successMessage, R.drawable.custom_toast_success, R.drawable.ic_success_image
            )
            loadingDialog.dismissDialog()
        }.addOnFailureListener {
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                failMessage, R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
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
        snackCreator = PopupMessageCreator()
        firebase = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        firebase.collection(collection).document(userId).set(hashMap).addOnSuccessListener {
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                successMessage, R.drawable.custom_toast_success, R.drawable.ic_success_image
            )
        }.addOnFailureListener {
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                failMessage, R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
        }
    }

    fun resetPasswordWithEmail(email: String){
        snackCreator = PopupMessageCreator()
        loadingDialog = LoadingDialog(activity)
        loadingDialog.loadingAlertDialog()
        auth = FirebaseAuth.getInstance()

        try {
            auth.sendPasswordResetEmail(email).addOnSuccessListener {
                snackCreator.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_LONG,
                    "Email sent. Check your mail box.", R.drawable.custom_toast_success, R.drawable.ic_success_image
                )
                loadingDialog.dismissDialog()

            }.addOnFailureListener {
                snackCreator.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                    it.localizedMessage!!, R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                loadingDialog.dismissDialog()
            }
        }
        catch (e: Exception) {
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                e.localizedMessage!!, R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
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
        snackCreator = PopupMessageCreator()

        val loginLinearLayout = LinearLayout(mCtx)
        loginLinearLayout.orientation = LinearLayout.VERTICAL

        loginLinearLayout.setPadding(10, 20, 10, 10)

        val emailEditText = EditText(mCtx)
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

        val passwordEditText = EditText(mCtx!!)
        passwordEditText.setBackgroundResource(R.drawable.custom_input_edittext)
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
        passwordEditText.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_password, 0, 0, 0)
        loginLinearLayout.addView(passwordEditText)

        val loginAlert = AlertDialog.Builder(mCtx!!, R.style.CustomAlertDialog)
        loginAlert.setTitle("Login")
        loginAlert.setView(loginLinearLayout)
        loginAlert.setCancelable(false)
        loginAlert.setPositiveButton("LOGIN") { _ : DialogInterface, _ : Int ->
            loadingDialog.loadingAlertDialog()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            try {
                auth.signInWithEmailAndPassword(email, password).addOnSuccessListener {
                    loadingDialog.dismissDialog()
                    getUser(textView, mView!!, welcomeControl = false)
                }.addOnFailureListener {
                    loadingDialog.dismissDialog()
                    snackCreator.customToast(
                        activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                        it.localizedMessage!!, R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                }
            }
            catch (e: Exception){
                loadingDialog.dismissDialog()
                snackCreator.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                    "Something went wrong. Try again.", R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
            }

        }
        loginAlert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
            dialog.cancel()
        }
        val dialog = loginAlert.create()
        dialog.window!!.attributes!!.windowAnimations = R.style.CustomAlertDialog
        dialog.show()
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
        snackCreator = PopupMessageCreator()
        val currentUser = auth.currentUser

        val currentPpUrl = currentUser?.photoUrl

        val newProfileUpdates = userProfileChangeRequest {
            displayName = newUsername
            photoUri = currentPpUrl
        }

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
                            snackCreator.customToast(
                                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                                "Username changed!", R.drawable.custom_toast_success, R.drawable.ic_success_image
                            )

                            val intent = Intent(mCtx, MainActivity::class.java)
                            activity?.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))

                        }.addOnFailureListener {
                            if (userNameControl.text == "true") {
                                firebase.collection("Users").document(userId)
                                    .update("UserName", oldUsernameTextView.text.toString())
                                    .addOnSuccessListener {
                                        currentUser.updateProfile(oldProfileUpdates).addOnCompleteListener {
                                            loadingScreenDestroyer(false)
                                            snackCreator.customToast(
                                                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                                                "Username could not be changed!", R.drawable.custom_toast_error, R.drawable.ic_error_image
                                            )
                                        }
                                    }.addOnFailureListener {
                                        currentUser.updateProfile(oldProfileUpdates).addOnCompleteListener {
                                            loadingScreenDestroyer(false)
                                            snackCreator.customToast(
                                                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                                                "Username could not be changed!", R.drawable.custom_toast_error, R.drawable.ic_error_image
                                            )
                                        }
                                    }
                            } else {
                                currentUser.updateProfile(oldProfileUpdates).addOnCompleteListener {
                                    loadingScreenDestroyer(false)
                                    snackCreator.customToast(
                                        activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                                        "Username changed!", R.drawable.custom_toast_success, R.drawable.ic_success_image
                                    )
                                }
                            }
                        }
                }.addOnFailureListener {
                    loadingScreenDestroyer(false)
                    snackCreator.customToast(
                        activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                        "Username could not be changed!", R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                }
        }?.addOnFailureListener {
            loadingScreenDestroyer(false)
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                "Username could not be changed!", R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
        }
    }

    fun updateEmail(newEmail: String){
        snackCreator = PopupMessageCreator()
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
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                it.localizedMessage!!, R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
        }
    }

    private fun updateEmailUsers(newEmail: String, oldEmail: String) {
        snackCreator = PopupMessageCreator()
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
                    snackCreator.customToast(
                        activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                        "Email could not be changed!", R.drawable.custom_toast_error, R.drawable.ic_error_image
                    )
                }
            }
        }
    }

    private fun updateEmailScores(newEmail: String, oldEmail: String){
        snackCreator = PopupMessageCreator()
        val currentUserId = getUserId()
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser

        firebase.collection("Scores").document(currentUserId!!).update("Email", newEmail).addOnCompleteListener { task ->
            if (task.isSuccessful){
                loadingScreenDestroyer(false)
                snackCreator.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                    "Email address updated!", R.drawable.custom_toast_success, R.drawable.ic_success_image
                )
            }
            else{
                user!!.updateEmail(oldEmail).addOnCompleteListener{
                    firebase.collection("Users").document(currentUserId).update("Email", oldEmail).addOnCompleteListener{
                        loadingScreenDestroyer(false)
                        snackCreator.customToast(
                            activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                            "Email could not be updated!", R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                    }
                }
            }
        }
    }

    fun changePasswordNoEmail(newPassword: String, oldPasswordComing: String){
        snackCreator = PopupMessageCreator()
        val currentUserId = getUserId()
        auth = FirebaseAuth.getInstance()
        firebase = FirebaseFirestore.getInstance()
        val user = Firebase.auth.currentUser

        loadingScreenStarter(false)

        if (oldPasswordComing == oldPasswordNow) {
            snackCreator.createSuccessSnack("Matched", mView!!)
            user!!.updatePassword(newPassword).addOnSuccessListener {

                firebase.collection("Users").document(currentUserId!!)
                    .update("Password", newPassword).addOnSuccessListener {
                        snackCreator.customToast(
                            activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                            "Password changed!", R.drawable.custom_toast_success, R.drawable.ic_success_image
                        )
                        loadingScreenDestroyer(false)

                    }.addOnFailureListener {
                        user.updatePassword(oldPasswordNow).addOnCompleteListener {
                            snackCreator.customToast(
                                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                                "Password could not be changed!", R.drawable.custom_toast_error, R.drawable.ic_error_image
                            )
                            loadingScreenDestroyer(false)
                        }
                    }
            }.addOnFailureListener {
                snackCreator.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                    it.localizedMessage!!, R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
                loadingScreenDestroyer(false)
            }
        }
        else{
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                "Password could not be matched.", R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
            loadingScreenDestroyer(false)
        }
    }

    private var scoreAverageGet : Number = 0
    private var usernameGet = ""
    private var numbersMemoryScoreGet : Number = 0


    fun deleteAccount(username: String){
        val userId = getUserId()
        firebase = FirebaseFirestore.getInstance()
        loadingScreenStarter(false)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase.collection("Users").document(userId!!).delete().addOnSuccessListener {
            firebase.collection("Scores").document(userId).delete().addOnSuccessListener {
                currentUser!!.delete().addOnSuccessListener {

                    snackCreator.customToast(
                        activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                        "User deleted.", R.drawable.custom_toast_success, R.drawable.ic_success_image
                    )
                    loadingScreenDestroyer(false)
                    val intent = Intent(activity, MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    activity!!.startActivity(intent)

                }.addOnFailureListener{

                    //Toast.makeText(mCtx, it.localizedMessage!!, Toast.LENGTH_LONG).show()
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
                    snackCreator.showToastCenter(mCtx!!, "Olması gereken oldu.")
                }
                setUserAchievementsAgain()
            }

        }.addOnFailureListener{
            loadingScreenDestroyer(false)
            snackCreator.customToast(
                activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                "Account could not be deleted.", R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
        }
    }

    private fun setUserAchievementsAgain(){

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

        val snackbarCreater = PopupMessageCreator()
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
            //snackCreator.createSuccessSnack("", mView!!)
        }.addOnFailureListener {
            //snackCreator.createFailSnack("", mView!!)
        }
    }

    private fun getUserCurrentUser (){

        val snackbarCreater = PopupMessageCreator()
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        val currentEmail = currentUser?.email
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
                snackCreator.customToast(
                    activity!!, mCtx!!, null, Toast.LENGTH_SHORT,
                    "The password could not be obtained.", R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
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