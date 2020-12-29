package com.burak.humanbenchmarks

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.SpannableString
import android.text.style.StyleSpan
import android.view.Gravity
import android.widget.*
import com.burak.humanbenchmarks.ForNumbersMemory.NumbersMemoryAchievementsUpdater

class LeadersBoardAndAchievementsScreenDesign(val context: Context) {

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
    ){
        /******************************************************************************************/
        val spannableString3 = SpannableString("WAITING..")
        spannableString3.setSpan(StyleSpan(Typeface.BOLD), 0, spannableString3.length, 0)
        onlineOrOffline.textSize = 20f
        onlineOrOffline.text = spannableString3
        onlineOrOffline.setTextColor(Color.parseColor("#2196F3"))
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
        //return linearLayout
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


    fun setLayout(title : String, message : String) : LinearLayout{
        /******************************************************************************************/
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.setMargins(0, 0, 0, 0)
        /******************************************************************************************/
        val linearLayout = LinearLayout(context)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.setPadding(50,10,50,20)
        /******************************************************************************************/
        val titleText = TextView(context)
        titleText.text = title
        titleText.setTextColor(Color.parseColor("#FFFFFF"))
        titleText.textSize = 30f
        titleText.gravity = Gravity.CENTER
        titleText.typeface = Typeface.DEFAULT_BOLD
        linearLayout.addView(titleText)
        /******************************************************************************************/
        val cupImage = ImageView(context)
        cupImage.setImageResource(R.drawable.cup_for_achievement)
        cupImage.layoutParams = setGravityOnLeaderBoard(Gravity.CENTER, false, null)
        linearLayout.addView(cupImage)
        /******************************************************************************************/
        val textForMessage = TextView(context)
        textForMessage.text = message
        textForMessage.textSize = 20f
        textForMessage.typeface = Typeface.DEFAULT_BOLD
        textForMessage.gravity = Gravity.CENTER
        textForMessage.setTextColor(Color.rgb(255,255,255))
        /******************************************************************************************/
        cupImage.layoutParams.height = 450
        cupImage.layoutParams.width = 450
        cupImage.requestLayout()
        linearLayout.addView(textForMessage)
        /******************************************************************************************/
        /******************************************************************************************/
        /******************************************************************************************/
        val buttonsLayout = LinearLayout(context)
        buttonsLayout.orientation = LinearLayout.HORIZONTAL
        buttonsLayout.gravity = Gravity.END
        buttonsLayout.setBackgroundColor(Color.parseColor("#000000"))
        /******************************************************************************************/
        val skipButton = Button(context)
        skipButton.setBackgroundResource(R.drawable.cust_start_button_no_stroke)
        skipButton.setTextColor(Color.parseColor("#FFFFFF"))
        skipButton.text = "SKIP"
        skipButton.setPadding(13, 0, 13, 0)
        val prm2 = setGravityOnLeaderBoard(Gravity.CENTER,  true, 60)
        skipButton.layoutParams = prm2
        //buttonsLayout.addView(skipButton)
        linearLayout.addView(skipButton)
        skipButton.layoutParams.height = 70
        skipButton.requestLayout()
        skipButton.setOnClickListener {
            NumbersMemoryAchievementsUpdater.builder.dismiss()
        }
        /******************************************************************************************/
        /******************************************************************************************/
        /******************************************************************************************/

        return linearLayout
    }

    private fun setGravityOnLeaderBoard(whichGravity : Int?, topMarginBool : Boolean, topMarginValue : Int?) : LinearLayout.LayoutParams {
        return LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            if (whichGravity != null) {
                gravity = whichGravity
            }
            if (topMarginBool && topMarginValue != null){
                topMargin = topMarginValue
            }
        }
    }
}