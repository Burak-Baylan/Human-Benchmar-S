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
        mLinearLayout.orientation = LinearLayout.HORIZONTAL
        val linearParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mLinearLayout.layoutParams = linearParams
        mLinearLayout.setPadding(20, 5, 20, 5)
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
    ) : LinearLayout{

        val txtView = TextView(context)
        txtView.textSize = 20f
        txtView.text = "OFFLINE"
        txtView.setTextColor(Color.parseColor("#000000"))
        /******************************************************************************************/
        val linear2 = LinearLayout(context)
        linear2.orientation = LinearLayout.VERTICAL
        val paramsForLinear2 = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        linear2.layoutParams = paramsForLinear2
        /******************************************************************************************/
        val linear3 = LinearLayout(context)
        linear3.orientation = LinearLayout.VERTICAL
        linear3.addView(txtView)
        /******************************************************************************************/

        val mGridLayout = GridLayout(context)
        mGridLayout.columnCount = 2
        mGridLayout.setPadding(20, 5, 20, 5)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        mGridLayout.layoutParams = params
        /******************************************************************************************/
        val mGridLayout3 = GridLayout(context)
        mGridLayout3.columnCount = 2
        mGridLayout3.setPadding(20, 5, 20, 5)
        /******************************************************************************************/
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
        /******************************************************************************************/
        mGridLayout.addView(usernameTextView, GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(0)))
        mGridLayout.addView(scoreTextView, GridLayout.LayoutParams(GridLayout.spec(0), GridLayout.spec(1)))
        //scoreTextView.gravity = Gravity.END
        mGridLayout3.addView(achievementsCountTextView)



        linear2.addView(mGridLayout)
        linear2.addView(mGridLayout3)

        linearLayout.addView(linear2)
        linearLayout.addView(linear3)

        return linearLayout
    }

    fun createUsernameTextView() : TextView{
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
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(30, 20, 30, 0)
        achievementsCountTextView.layoutParams = params
        return achievementsCountTextView
    }

}