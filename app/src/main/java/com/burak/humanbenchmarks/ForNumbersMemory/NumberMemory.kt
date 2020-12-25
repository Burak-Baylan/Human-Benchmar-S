package com.burak.humanbenchmarks.ForNumbersMemory

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import com.burak.humanbenchmarks.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_number_memory.*
import java.util.concurrent.ThreadLocalRandom

class NumberMemory : AppCompatActivity() {

    private var progressBarValue = 0
    private var progressRunnable: Runnable = Runnable {}
    private var progressHandler: Handler = Handler()
    private var levelCounter: Long = 0
    private var getRandomNumber: Long = 0
    private var getInput: Long = 0
    private lateinit var progressBar: ProgressBar
    private var snackCreator = PopupMessageCreator()
    private lateinit var viewReal: View
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var firebase: FirebaseFirestore
    private lateinit var firebaseManage: FirebaseManage
    private lateinit var numbersMemoryAchievementsUpdater: NumbersMemoryAchievementsUpdater
    private var mSQL : SQLiteDatabase? = null
    private var animationControl : animationControl = animationControl(this)

    companion object{
        lateinit var preferences : SharedPreferences
        var skipFastBool = false
    }

    

    /** DİKKAT!! 0. BÖLÜM 1. BÖLÜMDÜR
     * YANİ LEVEL SAYISI levelCounter + 1'dir
     **/

    private lateinit var rewardedAd: RewardedAd
    private var rewardEarned = false

    private val activity = this
    private val context = this

    private fun createAndLoadRewardedAd(showInfo : Boolean) : RewardedAd {
        val rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                if (showInfo) {
                    snackCreator.customToast(
                        activity, context, Gravity.CENTER, Toast.LENGTH_SHORT, "Ad Loaded",
                        R.drawable.custom_toast_success, R.drawable.ic_success_image
                    )
                }
            }
            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
            }
        }
        rewardedAd.loadAd(AdRequest.Builder().build(), adLoadCallback)
        return rewardedAd
    }

    /* WindowFocus kaybolduğunda hile olmaması sebebiyle program kendi kendine oyunu kapatıyor. Bu reklam açılınca da geçerli olduğu için ve biz bunu istmemediğimiz
      * için reklam izleme başlayınca protectedExit'i true yapıyoruz böylece program bunun hile olmadığını algılayıp oyunu durdurmuyor.
     */
    var protectedExit = false

    private fun showAd(){
        if (rewardedAd.isLoaded) {
            protectedExit = true
            val activityContext: Activity = this@NumberMemory
            val adCallback = object: RewardedAdCallback() {
                override fun onRewardedAdOpened() {
                }
                override fun onRewardedAdClosed() {
                    if (!rewardEarned) // Eğer reward alınmamışsa (false) yeniden yüklesin.
                    {
                        rewardedAd = createAndLoadRewardedAd(false)
                    }
                    protectedExit = false
                }
                override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                    rewardEarned = true // Bir reward alan bir daha almasın diye kontrol amaçlı konuldu.
                    continueWithAdButton.visibility = View.INVISIBLE
                    val alert = AlertDialog.Builder(this@NumberMemory,
                        R.style.CustomAlertDialogForHistories
                    )
                    alert.setMessage("Waiting for you...")
                    alert.setCancelable(false)
                    alert.setPositiveButton("CONTINUE"){dialog : DialogInterface, _ : Int ->
                        nextFunc()
                        dialog.cancel()
                    }
                    alert.show()
                    protectedExit = false
                }
                override fun onRewardedAdFailedToShow(adError: AdError) {
                    //snackCreator.showToastCenter(this@NumberMemory, "$adError")
                    snackCreator.customToast(
                        activity, context, null, Toast.LENGTH_SHORT, "$adError", R.drawable.custom_toast_error, R.drawable.ic_error_image)
                    rewardedAd = createAndLoadRewardedAd(false)
                    protectedExit = false
                }
            }
            rewardedAd.show(activityContext, adCallback)
        }
        else {

            snackCreator.customToast(
                activity, context, null, Toast.LENGTH_SHORT,
                "Ad loading... Please wait a few seconds without doing anything.",
                R.drawable.custom_toast_error, R.drawable.ic_error_image)

            //snackCreator.showToastLong(this@NumberMemory, "Ad loading... Please wait a few seconds without doing anything.")
            rewardedAd = createAndLoadRewardedAd(true)
            Log.d("TAG", "The rewarded ad wasn't loaded yet.")
        }
    }

    override fun onStart() {
        animationControl.forOnStart()
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_memory)

        preferences = getSharedPreferences("com.burak.humanbenchmarks", Context.MODE_PRIVATE)

        animationControl.forOnCreate(savedInstanceState)
        /*if (savedInstanceState == null) // 1st time
        {
            this.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left);
        }*/

        rewardedAd = createAndLoadRewardedAd(false)

        continueWithAdButton.setOnClickListener {
            if (firebaseManage.internetControl(this)) {
                showAd()
            }
            else {
                snackCreator.customToast(
                    this, this, null, Toast.LENGTH_SHORT,
                    "Internet connection required to watch ad.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
            }
        }
        /***************************************************************************************************/

        viewReal = window.decorView.rootView
        numbersMemoryAchievementsUpdater = NumbersMemoryAchievementsUpdater(this,this,viewReal)

        val olmasıGerekenAmaBuradaIsleviOlmayanTextView1 = TextView(this); val olmasıGerekenAmaBuradaIsleviOlmayanTextView2 = TextView(this); val olmasıGerekenAmaBuradaIsleviOlmayanTextView3 = TextView(this); val olmasıGerekenAmaBuradaIsleviOlmayanTextView4 = TextView(this); val olmasıGerekenAmaBuradaIsleviOlmayanTextView5 = TextView(this)
        numbersMemoryAchievementsUpdater.giveAchievements(olmasıGerekenAmaBuradaIsleviOlmayanTextView1, olmasıGerekenAmaBuradaIsleviOlmayanTextView2, olmasıGerekenAmaBuradaIsleviOlmayanTextView3, olmasıGerekenAmaBuradaIsleviOlmayanTextView4, olmasıGerekenAmaBuradaIsleviOlmayanTextView5, false)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser
        firebase = FirebaseFirestore.getInstance()
        firebaseManage = FirebaseManage(this,viewReal,this)

        /*****************************************************/
        try {
            val currentIdString = currentUser?.uid
            mSQL = this.openOrCreateDatabase(currentIdString, MODE_PRIVATE, null)
            mSQL?.execSQL("CREATE TABLE IF NOT EXISTS numbershistory (id INTEGER PRIMARY KEY, history VARCHAR)")
        }
        catch (e : Exception){println(e)}
        /*****************************************************/

        hideWindowAndSupportActionBar()

        progressBar = findViewById(R.id.showNumberProgress)

        listeners()
        allInvisible()
        infoLayoutNumbersMemory.visibility = View.VISIBLE
    }

    @SuppressLint("SetTextI18n")
    private fun listeners(){
        startGameButtonNumberMemory.setOnClickListener {
            startFunc()
        }

        submitButton.setOnClickListener {
            if (inputNumberEditText.text.isNotBlank()) {
                val getInput = inputNumberEditText.text.toString()

                try {
                    this.getInput = getInput.toLong()
                }
                catch (e : Exception){
                    snackCreator.customToast(
                        this, this, null, Toast.LENGTH_LONG,
                        "You shouldn't write no more than 18-digits.",
                        R.drawable.custom_toast_warning, R.drawable.ic_warning_image
                    )
                }

                if (this.getInput == getRandomNumber) /** Doğru cevap **/
                {
                    currentNumberTextView.text = "$getRandomNumber"
                    yourNumberTextView.text = "${this.getInput}"

                    if (after18Counter <= 0) {
                        showLevelTextView.text = "Level ${levelCounter + 1}"
                    }
                    else {
                        showLevelTextView.text = "Level ${levelCounter + 1}+$after18Counter"
                    }

                    allInvisible()
                    trueLayoutNumbersMemory.visibility = View.VISIBLE
                    hideWindowAndSupportActionBar()

                    val ring: MediaPlayer = MediaPlayer.create(this, R.raw.correct)
                    ring.start()


                    /** ACHIEVEMENTS **/
                    if (currentUser != null)
                    {
                        val numbersMemoryAchievementsAvailableControler = NumbersMemoryAchievementsAvailableControler(this, this, viewReal)
                        numbersMemoryAchievementsAvailableControler.controlAvailableAchievements(levelCounter)
                    }
                }
                else /** Yanlış Cevap **/
                {
                    //yanlışGirişinÜstünuÇizmeFonksiyonu(getRandomNumber, getInput)

                    allInvisible()

                    currentNumberTextViewFalse.text = "$getRandomNumber"
                    yourNumberTextViewFalse.text = "${this.getInput}"

                    if (after18Counter <= 0) {
                        showLevelTextViewNumbersMemoryFalse.text = "Level ${levelCounter + 1}"
                    }
                    else {
                        showLevelTextViewNumbersMemoryFalse.text = "Level ${levelCounter + 1}+$after18Counter"
                    }

                    showLevelTextViewNumbersMemoryFalse.setTextColor(Color.parseColor("#FF5722"))

                    falseLayoutNumbersMemory.visibility = View.VISIBLE
                    hideWindowAndSupportActionBar()

                    val ring: MediaPlayer = MediaPlayer.create(this, R.raw.wrong_answer)
                    ring.start()

                    saveScoreNumbersMemory.visibility = View.VISIBLE

                    if (currentUser == null){
                        saveScoreNumbersMemory.visibility = View.INVISIBLE
                    }
                    if (levelCounter.toInt() == 0){
                        saveScoreNumbersMemory.visibility = View.INVISIBLE
                    }
                }
            }
            else{
                snackCreator.customToast(
                    this, this, null, Toast.LENGTH_SHORT, "You must enter a few numbers.",
                    R.drawable.custom_toast_warning, R.drawable.ic_warning_image
                )
                //snackCreator.createFailSnack("You must enter a few numbers", viewReal)
            }
        }

        nextButton.setOnClickListener {
            nextFunc()
        }

        tryAgainButtonNumbersMemory.setOnClickListener {
            protectedExit = false
            levelCounter = 0
            after18Counter = 0
            rewardEarned = false
            startFunc()
            inputNumberEditText.setText("")
        }

        shareButtonNumbersMemory.setOnClickListener {
            protectedExit = true

            if (firebaseManage.internetControl(this)) {
                val getLinkForShareApp = GetLinkForShareApp(this, viewReal, this)
                getLinkForShareApp.share(
                    "I can remember a $levelCounter digit number. What about you? Download this game if you want to try it!",
                    "Share Your Score"
                )
            }
            else {
                snackCreator.customToast(this ,this, null, Toast.LENGTH_SHORT,
                    "Internet connection required to share your score.",
                    R.drawable.custom_toast_error, R.drawable.ic_error_image
                )
            }
        }

        backButtonNumbersMemory.setOnClickListener{
            finish()
        }
        backButtonNumbersMemory2.setOnClickListener {
            finish()
        }

        saveScoreNumbersMemory.setOnClickListener {

            if (currentUser != null) {
                protectedExit = true
                val alert = AlertDialog.Builder(this, R.style.CustomAlertDialogForHistories)
                alert.setTitle("Overwrite Your Score")
                alert.setMessage("If you overwrite this score, you cannot get it back!")
                alert.setCancelable(false)
                alert.setPositiveButton("Save") { _: DialogInterface, _: Int ->

                    val netControl = firebaseManage.internetControl(this)

                    if (netControl) {
                        val currentId = currentUser?.uid
                        /** Why do we save it as 3 digits? (levelCounter+after18Counter)
                         * Because we want to save with after18. What is the after18?
                         * after18, when we finish the game (when we reach 18 digit number) we want to continue this game but we use the long number type and long number
                         * type is supporting max 18 digit number. So We cannot reach beyond 18 digits. So i thought this way.
                         * I want to show this like 18+2. How can i do this?
                         * First we get this number then we use toString method then subString method. (I shown this below.)
                         * After we get first two digits, we check 3rd number with x>0. Because if user can't reach level 18 this number saving as 0 and if
                         * the number is 0 we shouldn't do anything. If the number greater than 0 we should do this ->
                         * (This way does not work for 2-digit numbers, but the user cannot increment the number 0 before reaching the 18-digit number.
                         * Because the user needs reach 18-digit for increment the number 0 and when user reach 18-digit the number will 3-digit)
                         * /********************************/
                         * val num1 = 123
                         * val num1String = num1.toString()
                         * val num1SubStr1 = num1String.substring(0, 2)
                         * val num1SubStr2 = num1String.substring(1, 2)
                         * println("$num1SubStr1 + $num1SubStr2") = 'Username': 12 + 3 Digit
                         * /********************************/
                         */
                        if (after18Counter > 0){
                            levelCounter = 18
                        }
                        firebase.collection("Scores").document(currentId!!).update("NumbersScore", levelCounter, "after18Count", after18Counter).addOnSuccessListener {
                            snackCreator.customToast(this, this, null, Toast.LENGTH_SHORT, "Score updated!", R.drawable.custom_toast_success, R.drawable.ic_success_image)
                            //snackCreator.createSuccessSnack("Score updated!", viewReal)
                            protectedExit = false
                        }.addOnFailureListener {
                            snackCreator.customToast(this, this, null, Toast.LENGTH_SHORT, "Score update failed!", R.drawable.custom_toast_error, R.drawable.ic_error_image)
                            //snackCreator.createFailSnack("Score update failed!", viewReal)
                            protectedExit = false
                        }
                    } else {
                        snackCreator.customToast(this, this, null, Toast.LENGTH_SHORT, "Internet connection required to save!", R.drawable.custom_toast_error, R.drawable.ic_error_image)
                        //snackCreator.showToastCenter(this, "Internet connection required to save.")
                        protectedExit = false
                    }
                }
                alert.setNegativeButton("Cancel") { dialog: DialogInterface, _: Int ->
                    protectedExit = false
                    dialog.cancel()
                }
                alert.show()
            }
            else{
                snackCreator.customToast(this, this, null, Toast.LENGTH_SHORT,
                "You must log in if you want to save the score.", R.drawable.custom_toast_warning, R.drawable.ic_warning_image
                    )
                //snackCreator.createFailSnack("You must log in if you want to save the score.", viewReal)
                protectedExit = false
            }
        }

        skipFastButton.setOnClickListener {
            allInvisible()
            inputLayoutNumbersMemory.visibility = View.VISIBLE
            progressHandler.removeCallbacks(progressRunnable)
            skipFastBool = true
        }
    }

    private var after18Counter = 0

    private fun nextFunc(){

        skipFastBool = false
        if (levelCounter < 17) {
            levelCounter++
        }
        else if (levelCounter.toInt() == 17){
            levelCounter = 17
            after18Counter++
        }
        startFunc()
        inputNumberEditText.setText("")
        inputNumberEditText.setText("$getRandomNumber")
        //rewardedAd = createAndLoadRewardedAd(false)
        if (!rewardEarned) {// Eğer daha önce izlenmemişse göster!
            continueWithAdButton.visibility = View.VISIBLE
        }
        else if (rewardEarned) {// Eğer daha önce izlenmişse gösterme!
            continueWithAdButton.visibility = View.INVISIBLE // 290 291 946 647 281 704
        }
        if (levelCounter > 8) {
            showNumberRealTextView.textSize = 40f
        }
        if (levelCounter > 9) {
            showNumberRealTextView.textSize = 30f
        }
        if (levelCounter <= 8) {
            showNumberRealTextView.textSize = 50f
        }
        /*}
        else{
            Toast.makeText(this, "Done", Toast.LENGTH_SHORT).show()
            //snackCreator.showToastCenter(this, "bitti")
        }*/
    }

    private fun startFunc(){
        getRandomNumber = randomNumberCreator()
        showNumberRealTextView.text = "$getRandomNumber"
        allInvisible()
        showNumberLayoutNumbersMemory.visibility = View.VISIBLE
        startProgressBar(levelCounter+2)
        hideWindowAndSupportActionBar()
        continueWithAdButton.visibility = View.VISIBLE
    }

    private fun allInvisible(){
        infoLayoutNumbersMemory.visibility = View.GONE
        showNumberLayoutNumbersMemory.visibility = View.GONE
        inputLayoutNumbersMemory.visibility = View.GONE
        trueLayoutNumbersMemory.visibility = View.GONE
        falseLayoutNumbersMemory.visibility = View.GONE
    }

    private fun hideWindowAndSupportActionBar() {
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
    }

    private fun startProgressBar(second : Long){
        progressBar.progress = 0
        progressBarValue = progressBar.progress
        showNumberProgress.max = 100
        val secondReal = second * 1000
        val currentProgress = 100
        ObjectAnimator.ofInt(showNumberProgress, "progress", currentProgress)
            .setDuration(secondReal)
            .start()

        progressRunnable = Runnable {

            progressHandler.postDelayed(progressRunnable, 10)

            skipFastButton.visibility = View.VISIBLE
            if (progressBar.progress > (progressBar.max / 2)) // Sürenin yarısına kadar skipButton görünür fakat yarıdan sonra görünmez olsun istiyoruz o yüzden böyle.
            {
                skipFastButton.visibility = View.INVISIBLE
            }

            if (levelCounter.toInt() < 7){
                skipFastButton.visibility = View.INVISIBLE
            }

            if (progressBar.progress == progressBar.max){
                allInvisible()
                inputLayoutNumbersMemory.visibility = View.VISIBLE
                progressHandler.removeCallbacks(progressRunnable)
            }
        }
        progressHandler.post(progressRunnable)
    }

    private fun randomNumberCreator() : Long{
        val here = listOf (1,10,100,1000,10000,100000,1000000,10000000,100000000,1000000000,10000000000,100000000000,1000000000000,10000000000000,100000000000000,1000000000000000,10000000000000000,100000000000000000)
        val to = listOf (9,99,999,9999,99999,999999,9999999,99999999,999999999,9999999999,99999999999,999999999999,9999999999999,99999999999999,999999999999999,9999999999999999,99999999999999999,999999999999999999)
        return ThreadLocalRandom.current().nextLong(here[levelCounter.toInt()], to[levelCounter.toInt()] + 1)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if ((infoLayoutNumbersMemory.visibility != View.VISIBLE) && (trueLayoutNumbersMemory.visibility != View.VISIBLE) && (falseLayoutNumbersMemory.visibility != View.VISIBLE)) {
            if (!protectedExit) {
                if (!hasFocus) {
                    snackCreator.customToast(this, this, null, Toast.LENGTH_SHORT,
                    "If you quit while playing, the game will be canceled.",
                        R.drawable.custom_toast_info, R.drawable.ic_info_image
                        )
                    //snackCreator.showToastLong(this, "If you quit while playing, the game will be canceled.")
                    finish()
                }
            }
            super.onWindowFocusChanged(hasFocus)
        }
    }

    /*var fullYourInputString : String = ""

    private fun yanlışGirişinÜstünuÇizmeFonksiyonu(currentNumber : Long, yourInput : Long){
        val currentNumberString = currentNumber.toString()
        val yourInputString = yourInput.toString()
        yourNumberTextViewFalse.text = ""
        for (i in 1..currentNumberString.length){
            val a = currentNumberString.substring(i-1,i)
            val b = yourInputString.substring(i-1,i)
            val result = a.toInt()-b.toInt()

            //snackCreator.showToastCenter(this, "a:$a - b:$b result:$result")

            üstünüÇiz(b, result)
        }
    }

    private fun üstünüÇiz(çizText : String, result : Int)/* : SpannableString*/{

        var spannableString = SpannableString(çizText)
        spannableString = SpannableString(çizText)
        if (result != 0) {
            spannableString.setSpan(StrikethroughSpan(), 0, spannableString.length, 0)
            yourNumberTextViewFalse.text = yourNumberTextViewFalse.text.toString() + spannableString
        }
        else {
            yourNumberTextViewFalse.text = yourNumberTextViewFalse.text.toString() + spannableString
        }

        Toast.makeText(this, spannableString.toString(), Toast.LENGTH_SHORT).show()
        //snackCreator.showToastCenter(this, spannableString.toString())
        //return spannableString
    }*/

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