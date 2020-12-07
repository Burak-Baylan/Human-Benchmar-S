package com.burak.humanbenchmarks.ForNumbersMemory

import android.content.Context
import android.graphics.Color
import android.text.SpannableString
import android.view.Gravity
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import com.burak.humanbenchmarks.R

class LeadersBoardDesign(val context: Context) {

    fun createLinearLayout() : LinearLayout{
        val mLinearLayout = LinearLayout(context)
        mLinearLayout.orientation = LinearLayout.VERTICAL
        val linearParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mLinearLayout.layoutParams = linearParams
        mLinearLayout.setPadding(20, 5, 20, 5)
        mLinearLayout.setBackgroundResource(R.drawable.numbers_memory_linear_layout_for_persons)

        return mLinearLayout

        /*val params = LinearLayout.LayoutParams(/*width*/LinearLayout.LayoutParams.MATCH_PARENT, /*height*/ LinearLayout.LayoutParams.WRAP_CONTENT)
        params.setMargins(30, 20, 30, 0)
        feedbackEditText.layoutParams = params*/
    }

    fun getRealGridLayout(
        linearLayout: LinearLayout,
        usernameTextView: TextView,
        scoreTextView: TextView,
        achievementsCountTextView: TextView,
        usernameText: SpannableString,
        scoreText: SpannableString,
        achievementsCountText: SpannableString
    ) : LinearLayout{
        /********************************************************/
        val mGridLayout = LinearLayout(context)
        /*mGridLayout.columnCount = 2
        mGridLayout.rowCount = 1*/
        //mGridLayout.setBackgroundResource(R.drawable.numbers_memory_linear_layout_for_persons)
        mGridLayout.setPadding(20, 5, 20, 5)

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mGridLayout.layoutParams = params
        /********************************************************/
        /*val mGridLayout2 = GridLayout(context)
        mGridLayout2.columnCount = 2
        mGridLayout2.rowCount = 1
        val params2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mGridLayout2.layoutParams = params2
        //mGridLayout2.setBackgroundResource(R.drawable.numbers_memory_linear_layout_for_persons)
        mGridLayout2.setPadding(20, 5, 20, 5)*/
        /********************************************************/
        val mGridLayout3 = GridLayout(context)
        mGridLayout3.columnCount = 1
        mGridLayout3.rowCount = 1
        //mGridLayout2.setBackgroundResource(R.drawable.numbers_memory_linear_layout_for_persons)
        mGridLayout3.setPadding(20, 5, 20, 5)
        /********************************************************/
        usernameTextView.text = usernameText
        scoreTextView.text = scoreText
        achievementsCountTextView.text = achievementsCountText

        val params6 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.END
        }
        usernameTextView.layoutParams = params6

        /********************************************************/
        mGridLayout.addView(usernameTextView)
        //usernameTextView.gravity = Gravity.END
        mGridLayout.addView(scoreTextView)

        //scoreTextView.gravity = Gravity.END

        val params4 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.END
        }
        scoreTextView.layoutParams = params4
        scoreTextView.gravity = Gravity.END

        mGridLayout3.addView(achievementsCountTextView)
        achievementsCountTextView.gravity = Gravity.CENTER

        linearLayout.addView(mGridLayout)
        //linearLayout.addView(mGridLayout2)
        linearLayout.addView(mGridLayout3)

        return linearLayout
    }

    fun createUsernameTextView() : TextView{
        val usernameTextView = TextView(context)
        usernameTextView.setTextColor(Color.parseColor("#142A4E"))
        usernameTextView.textSize = 18f
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(30, 20, 30, 0)
        usernameTextView.layoutParams = params

        /*val spannableString = SpannableString("$lastInit-Digit")
        spannableString.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            spannableString.length,
            0
        )*/
        return usernameTextView
    }

    fun createScoreTextView() : TextView {
        val scoreTextView = TextView(context)
        scoreTextView.setTextColor(Color.parseColor("#DDA35B"))
        scoreTextView.textSize = 18f
        scoreTextView.gravity = Gravity.CENTER_HORIZONTAL
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(30, 20, 30, 0)
        scoreTextView.layoutParams = params

        return scoreTextView
    }

    fun createAchievementsCountTextView() : TextView{
        val achievementsCountTextView = TextView(context)
        achievementsCountTextView.setTextColor(Color.parseColor("#DC3B50"))
        achievementsCountTextView.textSize = 18f
        /*val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.CENTER
        }
        achievementsCountTextView.layoutParams = params*/
        achievementsCountTextView.gravity = Gravity.CENTER
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(30, 20, 30, 0)
        achievementsCountTextView.layoutParams = params

        return achievementsCountTextView

    }

}

/*{
    val params = LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT
    )
    params.setMargins(30, 20, 30, 0)
    feedbackEditText.layoutParams = params
}*/