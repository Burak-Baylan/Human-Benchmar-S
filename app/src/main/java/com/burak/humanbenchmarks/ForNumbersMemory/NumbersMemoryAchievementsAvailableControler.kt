package com.burak.humanbenchmarks.ForNumbersMemory

import android.app.Activity
import android.content.Context
import android.view.View
import com.burak.humanbenchmarks.ForNumbersMemory.NumberMemory
import com.burak.humanbenchmarks.ForNumbersMemory.NumbersMemoryAchievementsUpdater
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class NumbersMemoryAchievementsAvailableControler (context : Context, activity : Activity, view : View){

    val mCtx = context
    val mActivity = activity
    val viewReal = view

    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    val currentUser : FirebaseUser? = auth.currentUser
    val firebase : FirebaseFirestore = FirebaseFirestore.getInstance()

    private var numbersMemoryAchievementsUpdater: NumbersMemoryAchievementsUpdater = NumbersMemoryAchievementsUpdater(mCtx, mActivity, viewReal)


    fun controlAvailableAchievements(levelCounter : Long){
        val brainStormBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["brainStorm"]
        val impatientBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["impatient"]
        val rookieBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["rookie"]
        val smartBool = NumbersMemoryAchievementsUpdater.giveMapOnNumbersMemoryAchievementsUpdaterGiveAchievements["smart"]

        if (NumberMemory.skipFastBool){
            if (!impatientBool!!){
                numbersMemoryAchievementsUpdater.updateTrueAnAchievements("Impatient", "impatient")
            }
            NumberMemory.skipFastBool = false
        }

        if (levelCounter.toInt() + 1 == 7) {
            /** ROOKIE **/
            if (!rookieBool!!) {
                numbersMemoryAchievementsUpdater.updateTrueAnAchievements("Rookie", "rookie")
            }
        }

        if (levelCounter.toInt() + 1 == 10) {
            /** SMART **/
            if (!smartBool!!) {
                numbersMemoryAchievementsUpdater.updateTrueAnAchievements("Smart", "smart")
            }
        }


        if (levelCounter.toInt() + 1 == 18) {
            /** BRAIN STORM **/
            if (!brainStormBool!!) {
                numbersMemoryAchievementsUpdater.updateTrueAnAchievements("Brain Storm", "brainStorm")
            }
        }
    }
}