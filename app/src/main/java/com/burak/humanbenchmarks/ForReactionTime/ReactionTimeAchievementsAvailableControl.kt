package com.burak.humanbenchmarks.ForReactionTime

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.burak.humanbenchmarks.PopupMessageCreator

class ReactionTimeAchievementsAvailableControl (val ctx : Context, val activity : Activity, val viewReal : View){

    private val achievementsControl : AchievementsControl = AchievementsControl(ctx,activity, viewReal)
    private val snackbarCreater : PopupMessageCreator = PopupMessageCreator()

    fun controlAvailableAchievements(
        row20String : String,
        tooSlowString : String,
        tooLuckString : String,
        turtleString : String,
        robotOrString : String,
        besteKac : Int,
        besteKacTopla : Long,
        roundsCounter : Int,
        timeInMilliseconds : Long,
        row20TextView : TextView,
        tooSlowTextView : TextView,
        tooLuckTextView : TextView,
        s1TextView : TextView,
        turtleTextView : TextView,
        robotOrTextView : TextView,
    )
    {

        // 20 Round arka arkaya.
        if (row20String == "false") {
            if (roundsCounter == 20) {
                achievementsControl.updateAchievements(
                    "20roundsRow",
                    true,
                    "Play 20 Rounds In a Row",
                    "Consecutive Player Achievement"
                )
                AchievementsControl.getAchievementsForPutTextViews(
                    achievementsControl,
                    row20TextView, tooSlowTextView, tooLuckTextView, s1TextView, turtleTextView, robotOrTextView
                )
            }
        }
        /*else{
            snackbarCreater.showToastCenter(ctx,"20rounds var")
        }*/

        // 10 saniyeden fazla
        if (tooSlowString == "false") {
            if (timeInMilliseconds > 10000) {
                achievementsControl.updateAchievements(
                    "tooSlow",
                    true,
                    "Congratulations, you achieved the 'Too Slow' achievement by waiting 10 seconds",
                    "Too Slow Achievement"
                )
                AchievementsControl.getAchievementsForPutTextViews(
                    achievementsControl,
                    row20TextView, tooSlowTextView, tooLuckTextView, s1TextView, turtleTextView, robotOrTextView)
            }
        }
        /*else{
            snackbarCreater.showToastCenter(ctx,"tooSlow (10s) var")
        }*/

        // 80ms'den az -SKOR-
        if (tooLuckString == "false") {
            if (timeInMilliseconds <= 80) {
                achievementsControl.updateAchievements(
                    "tooLucky",
                    true,
                    "80 ms or less score",
                    "Too Lucky Achievement"
                )
                AchievementsControl.getAchievementsForPutTextViews(
                    achievementsControl,
                    row20TextView, tooSlowTextView, tooLuckTextView, s1TextView, turtleTextView, robotOrTextView)
            }
        }
        /*else{
            snackbarCreater.showToastCenter(ctx,"tooLuck(80ms) var")
        }*/

        /*if (s1String == "false"){
            val olmasiGerekTextView = TextView(this)
            firebaseManage.loadLeaderScores(leaderBoardLayout,false, olmasiGerekTextView, deleteMeOnLeaderBoardImage, 1, true)
        }*/
        /** Birinci sırada olmasını kontrol eden achievementsControl burada değil getLeaders fonksiyonunda yazılacak **/

        // 50 Saniyeden fazla.
        if (turtleString == "false") {
            if (timeInMilliseconds > 50000) {
                achievementsControl.updateAchievements(
                    "turtle",
                    true,
                    "Wait 50 seconds or more ",
                    "Turtle Achievement"
                )
                AchievementsControl.getAchievementsForPutTextViews(
                    achievementsControl,
                    row20TextView, tooSlowTextView, tooLuckTextView, s1TextView, turtleTextView, robotOrTextView)
            }
        }
        /*else{
            snackbarCreater.showToastCenter(ctx,"turtle(50s) var")
        }*/

        // 100ms'den az -ORTALAMA-
        if (besteKac == 5) /** Ortalama cinsinden hesap yaptığımız için 5. roundda kontrolü yapmamız gerek **/ {
            if (robotOrString == "false") {
                if (besteKacTopla <= 100) {
                    achievementsControl.updateAchievements("areYouRobot", true, "100 ms or less average", "Are You Robot Achievement")
                    AchievementsControl.getAchievementsForPutTextViews(
                        achievementsControl,
                        row20TextView, tooSlowTextView, tooLuckTextView, s1TextView, turtleTextView, robotOrTextView)
                }
            }
            /*else {
                snackbarCreater.showToastCenter(ctx, "robot var")
            }*/
        }

    }

}