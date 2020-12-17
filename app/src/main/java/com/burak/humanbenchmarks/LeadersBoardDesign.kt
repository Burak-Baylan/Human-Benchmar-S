package com.burak.humanbenchmarks

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView

class LeadersBoardDesign(val context: Context) {

    fun createLinearLayout() : LinearLayout{
        val mLinearLayout = LinearLayout(context)
        mLinearLayout.orientation = LinearLayout.HORIZONTAL
        val linearParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        mLinearLayout.layoutParams = linearParams
        mLinearLayout.setPadding(20, 5, 0, 5)
        mLinearLayout.setBackgroundResource(R.drawable.numbers_memory_linear_layout_for_persons)
        return mLinearLayout
    }

    fun getRealGridLayout(
        linearLayout: LinearLayout,
        usernameTextView: TextView,
        scoreTextView: TextView,
        achievementsCountTextView: TextView,
        usernameText: SpannableString,
        scoreText: SpannableString,
        achievementsCountText: SpannableString,
        onlineOrOffline : TextView
    ) : LinearLayout{
        /******************************************************************************************/
        val spannableString3 = SpannableString("WAITING..")
        spannableString3.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            spannableString3.length,
            0
        )
        onlineOrOffline.textSize = 20f
        onlineOrOffline.text = spannableString3
        onlineOrOffline.setTextColor(Color.parseColor("#2196F3"))
        //onlineOrOffline.setBackgroundColor(Color.parseColor("#F4683D"))
        onlineOrOffline.gravity = Gravity.CENTER
        /******************************************************************************************/
        val linear2 = LinearLayout(context)
        linear2.orientation = LinearLayout.VERTICAL
        /******************************************************************************************/
        val linear3 = LinearLayout(context)
        linear3.orientation = LinearLayout.VERTICAL
        val params6 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        linear3.layoutParams = params6
        linear3.addView(onlineOrOffline)
        /******************************************************************************************/
        val mGridLayout = GridLayout(context)
        mGridLayout.columnCount = 2
        mGridLayout.setPadding(20, 5, 20, 5)
        /******************************************************************************************/
        val mGridLayout3 = GridLayout(context)
        mGridLayout3.columnCount = 2
        mGridLayout3.setPadding(20, 5, 20, 5)
        /******************************************************************************************/
        usernameTextView.text = usernameText
        scoreTextView.text = scoreText
        achievementsCountTextView.text = achievementsCountText
        /******************************************************************************************/
        mGridLayout.addView(usernameTextView, GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(0)))
        mGridLayout.addView(scoreTextView, GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(1)))
        mGridLayout3.addView(achievementsCountTextView)
        /******************************************************************************************/
        linear2.addView(mGridLayout)
        linear2.addView(mGridLayout3)
        linearLayout.addView(linear2)
        linearLayout.addView(linear3)
        /******************************************************************************************/
        return linearLayout
    }

    fun createUsernameTextView() : TextView {
        val usernameTextView = TextView(context)
        usernameTextView.setTextColor(Color.parseColor("#142A4E"))
        usernameTextView.textSize = 18f
        return usernameTextView
    }

    fun createScoreTextView() : TextView {
        val scoreTextView = TextView(context)
        scoreTextView.setTextColor(Color.parseColor("#DDA35B"))
        scoreTextView.textSize = 18f
        scoreTextView.gravity = Gravity.CENTER_HORIZONTAL
        return scoreTextView
    }

    fun createAchievementsCountTextView() : TextView{
        val achievementsCountTextView = TextView(context)
        achievementsCountTextView.setTextColor(Color.parseColor("#DC3B50"))
        achievementsCountTextView.textSize = 18f
        return achievementsCountTextView
    }

    fun createOnlineOrOfflineTextView() : TextView{
        val onlineOrOfflineTextView = TextView(context)
        //onlineOrOfflineTextView.setTextColor(Color.parseColor("#2196F3"))
        onlineOrOfflineTextView.textSize = 20f
        return onlineOrOfflineTextView
    }

}