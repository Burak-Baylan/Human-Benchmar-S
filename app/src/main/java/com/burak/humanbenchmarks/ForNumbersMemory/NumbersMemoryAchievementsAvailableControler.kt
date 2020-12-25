package com.burak.humanbenchmarks.ForNumbersMemory

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.view.View
import android.widget.Toast
import com.burak.humanbenchmarks.FirebaseManage
import com.burak.humanbenchmarks.LoadingDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class NumbersMemoryAchievementsAvailableControler (context : Context, activity : Activity, view : View){

    private val mCtx = context
    private val mActivity = activity
    private val viewReal = view
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseManage = FirebaseManage(mCtx, viewReal, mActivity)
    private var numbersMemoryAchievementsUpdater: NumbersMemoryAchievementsUpdater = NumbersMemoryAchievementsUpdater(mCtx, mActivity, viewReal)

    fun controlAvailableAchievements(levelCounter : Long){
        firebaseManage.loadingScreenStarter(false)
        val brainStormBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["brainStorm"]
        val impatientBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["impatient"]
        val rookieBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["rookie"]
        val smartBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["smart"]
        if (NumberMemory.skipFastBool){
            if (firebaseManage.internetControl(mActivity)) {
                if (!impatientBool!!) {
                    numbersMemoryAchievementsUpdater.updateTrueAnAchievements("impatient", "Impatient", "Congrats. You earned 'Brain Storm' achievement.")
                }
                NumberMemory.skipFastBool = false
            }
        }
        if (levelCounter.toInt() + 1 == 7) {
            /** ROOKIE **/
            if (firebaseManage.internetControl(mActivity)) {
                if (!rookieBool!!) {
                    numbersMemoryAchievementsUpdater.updateTrueAnAchievements("rookie", "Rookie", "Congrats. You earned 'Impatient' achievement.")
                }
            }
        }
        if (levelCounter.toInt() + 1 == 10) {
            /** SMART **/
            if (firebaseManage.internetControl(mActivity)) {
                if (!smartBool!!) {
                    numbersMemoryAchievementsUpdater.updateTrueAnAchievements("smart", "Smart", "Congrats. You earned 'Rookie' achievement.")
                }
            }
        }
        if (levelCounter.toInt() + 1 == 18) {
            /** BRAIN STORM **/
            if (firebaseManage.internetControl(mActivity)) {
                if (!brainStormBool!!) {
                    numbersMemoryAchievementsUpdater.updateTrueAnAchievements("brainStorm", "Brain Storm", "Congrats. You earned 'Smart' achievement.")
                }
            }
        }
        firebaseManage.loadingScreenDestroyer(false)
    }
}