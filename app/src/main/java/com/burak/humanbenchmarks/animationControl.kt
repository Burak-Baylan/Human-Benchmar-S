package com.burak.humanbenchmarks

import android.app.Activity
import android.content.Context
import android.os.Bundle

class animationControl (val activity : Activity) {

    private var onStartCount = 0

    fun forOnCreate(savedInstanceState : Bundle?){
        onStartCount = 1
        if (savedInstanceState == null) // 1st time
        {
            activity.overridePendingTransition(R.anim.anim_slide_in_left,
                R.anim.anim_slide_out_left)
        } else // already created so reverse animation
        {
            onStartCount = 2
        }
    }

    fun forOnStart(){
        if (onStartCount > 1) {
            activity.overridePendingTransition(R.anim.anim_slide_in_right,
                R.anim.anim_slide_out_right)

        } else if (onStartCount == 1) {
            onStartCount++
        }
    }

}