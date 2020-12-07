package com.burak.humanbenchmarks.ForNumbersMemory

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.SpannableString
import android.text.style.StrikethroughSpan
import android.util.Log
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_number_memory.*
import java.util.concurrent.ThreadLocalRandom

open class NumberMemory : AppCompatActivity() {

    private var progressBarValue = 0
    private var progressRunnable: Runnable = Runnable {}
    private var progressHandler: Handler = Handler()
    private var levelCounter: Long = 0
    private var getRandomNumber: Long = 0
    private var getInput: Long = 0
    private lateinit var progressBar: ProgressBar
    private var snackCreator = SnackbarCreater()
    private lateinit var viewReal: View
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private lateinit var firebase: FirebaseFirestore
    private lateinit var firebaseManage: FirebaseManage
    private lateinit var numbersMemoryAchievementsUpdater: NumbersMemoryAchievementsUpdater
    private var mSQL : SQLiteDatabase? = null

    companion object{
        var skipFastBool = false
    }

    /** Max score(18) ulaştığı zaman achievements gibi bi ekranda göstersin
     * daha fazla yapmaya çalışınca uyarı falan versin
     **/

    /** DİKKAT!! 0. BÖLÜM 1. BÖLÜMDÜR
     * YANİ LEVEL SAYISI levelCounter + 1'dir
     **/

    /** NUMBER MEMORY ACTIVITY REWARD AD ID
     * ca-app-pub-8014812102703860/2764893438
     *
     * NUMBER MEMORY ACTIVITY REWARD AD TEST ID
     * ca-app-pub-3940256099942544/5224354917
     **/

    private lateinit var rewardedAd: RewardedAd
    private var rewardEarned = false

    private fun createAndLoadRewardedAd(showInfo : Boolean) : RewardedAd {
        val rewardedAd = RewardedAd(this, "ca-app-pub-3940256099942544/5224354917")
        val adLoadCallback = object: RewardedAdLoadCallback() {
            override fun onRewardedAdLoaded() {
                if (showInfo) {
                    snackCreator.showToastCenter(this@NumberMemory, "Ad Loaded")
                }
            }
            override fun onRewardedAdFailedToLoad(adError: LoadAdError) {
                //snackCreator.showToastCenter(this@NumberMemory, "Ad Yüklenemedi")
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
                    //snackCreator.showToastCenter(this@NumberMemory, "opened")
                }
                override fun onRewardedAdClosed() {
                    //snackCreator.showToastCenter(this@NumberMemory, "closed")
                    if (!rewardEarned) // Eğer reward alınmamışsa (false) yeniden yüklesin.
                    {
                        rewardedAd = createAndLoadRewardedAd(false)
                    }
                    protectedExit = false
                }
                override fun onUserEarnedReward(@NonNull reward: RewardItem) {
                    //snackCreator.showToastCenter(this@NumberMemory, "earned")
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
                    snackCreator.showToastCenter(this@NumberMemory, "$adError")
                    rewardedAd = createAndLoadRewardedAd(false)
                    protectedExit = false
                }
            }
            rewardedAd.show(activityContext, adCallback)
        }
        else {
            snackCreator.showToastLong(this@NumberMemory, "Ad loading... Please wait a few seconds without doing anything.")
            rewardedAd = createAndLoadRewardedAd(true)
            Log.d("TAG", "The rewarded ad wasn't loaded yet.")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_number_memory)

        rewardedAd = createAndLoadRewardedAd(false)

        continueWithAdButton.setOnClickListener {
            showAd()
        }
        /***************************************************************************************************/

        viewReal = window.decorView.rootView
        numbersMemoryAchievementsUpdater = NumbersMemoryAchievementsUpdater(this,this,viewReal)

        val olmasıGerekenAmaBuradaIsleviOlmayanTextView1 = TextView(this)
        val olmasıGerekenAmaBuradaIsleviOlmayanTextView2 = TextView(this)
        val olmasıGerekenAmaBuradaIsleviOlmayanTextView3 = TextView(this)
        val olmasıGerekenAmaBuradaIsleviOlmayanTextView4 = TextView(this)
        val olmasıGerekenAmaBuradaIsleviOlmayanTextView5 = TextView(this)
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
                val gelen = inputNumberEditText.text.toString()
                getInput = gelen.toLong()

                if (getInput == getRandomNumber) /** Doğru cevap **/
                {
                    currentNumberTextView.text = "$getRandomNumber"
                    yourNumberTextView.text = "$getInput"
                    showLevelTextView.text = "Level ${levelCounter + 1}"

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
                    yourNumberTextViewFalse.text = "$getInput"
                    showLevelTextViewNumbersMemoryFalse.text = "Level ${levelCounter + 1}"
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
                snackCreator.createFailSnack("You must enter a few numbers", viewReal)
            }
        }

        nextButton.setOnClickListener {
            nextFunc()
        }

        tryAgainButtonNumbersMemory.setOnClickListener {
            protectedExit = false
            levelCounter = 0
            rewardEarned = false
            startFunc()
            inputNumberEditText.setText("")
        }

        shareButtonNumbersMemory.setOnClickListener {
            protectedExit = true

            val getLinkForShareApp = GetLinkForShareApp(this, viewReal, this)
            getLinkForShareApp.share(
                "I can remember a $levelCounter digit number. What about you? Download this game if you want to try it!",
                "Share Your Score")
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
                        firebase.collection("Scores").document(currentId!!).update("NumbersScore", levelCounter).addOnSuccessListener {
                            snackCreator.createSuccessSnack("Score updated!", viewReal)
                            protectedExit = false
                        }.addOnFailureListener {
                            snackCreator.createFailSnack("Score update failed!", viewReal)
                            protectedExit = false
                        }
                    } else {
                        snackCreator.showToastCenter(this, "Internet connection required to save.")
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
                snackCreator.createFailSnack("You must log in if you want to save the score.", viewReal)
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

    private fun nextFunc(){
        skipFastBool = false
        if (levelCounter < 17) {

            levelCounter++
            startFunc()
            //inputNumberEditText.setText("")
            rewardedAd = createAndLoadRewardedAd(false)

            if (!rewardEarned) // Eğer daha önce izlenmemişse göster!
            {
                continueWithAdButton.visibility = View.VISIBLE
            }
            else if (rewardEarned) // Eğer daha önce izlenmişse gösterme!
            {
                continueWithAdButton.visibility = View.VISIBLE // 290 291 946 647 281 704
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
        }
        else{
            snackCreator.showToastCenter(this, "bitti")
        }
    }

    private fun startFunc(){
        getRandomNumber = randomNumberCreator()
        showNumberRealTextView.text = "$getRandomNumber"
        inputNumberEditText.setText("$getRandomNumber")
        allInvisible()
        showNumberLayoutNumbersMemory.visibility = View.VISIBLE
        startProgressBar(levelCounter+2)
        hideWindowAndSupportActionBar()
        continueWithAdButton.visibility = View.VISIBLE
        rewardedAd = createAndLoadRewardedAd(false)
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
        val here = listOf<Long>(1,10,100,1000,10000,100000,1000000,10000000,100000000,1000000000,10000000000,100000000000,1000000000000,10000000000000,100000000000000,1000000000000000,10000000000000000,100000000000000000)
        val to = listOf<Long>  (9,99,999,9999,99999,999999,9999999,99999999,999999999,9999999999,99999999999,999999999999,9999999999999,99999999999999,999999999999999,9999999999999999,99999999999999999,999999999999999999)
        return ThreadLocalRandom.current().nextLong(here[levelCounter.toInt()], to[levelCounter.toInt()] + 1)
    }


    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if ((infoLayoutNumbersMemory.visibility != View.VISIBLE) && (trueLayoutNumbersMemory.visibility != View.VISIBLE) && (falseLayoutNumbersMemory.visibility != View.VISIBLE)) {
            if (!protectedExit) {
                if (!hasFocus) {
                    snackCreator.showToastLong(this, "If you quit while playing, the game will be canceled.")
                    finish()
                }
            }
            super.onWindowFocusChanged(hasFocus)
        }
    }

    var fullYourInputString : String = ""

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
        snackCreator.showToastCenter(this, spannableString.toString())
        //return spannableString
    }

    private fun createSuccessSnackWithAction(message : String){

        val oylesineTextView = TextView(this)

        val hexBackgroundColor = "#570F0F"
        val hexTextColor = "#FFFFFF"
        val snackBar = Snackbar.make(
            viewReal, message,
            Snackbar.LENGTH_LONG
        ).setAction("Action", null)
        snackBar.setActionTextColor(Color.parseColor(hexTextColor))
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.parseColor(hexBackgroundColor))
        val textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.parseColor(hexTextColor))
        snackBar.setAction("LogIn", View.OnClickListener {
            firebaseManage.loginAlertDialog(oylesineTextView)
        })
        snackBar.show()
    }
}