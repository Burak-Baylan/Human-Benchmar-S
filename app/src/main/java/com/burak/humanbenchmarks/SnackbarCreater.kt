package com.burak.humanbenchmarks

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar


class SnackbarCreater {

    var toast : Toast? = null

    fun createFailSnack(message : String, view : View){
        createSnack(message,view,"#570F0F", "#FFFFFF")
    }
    fun createSuccessSnack(message : String, view : View){
        createSnack(message,view,"#26572C","#FFFFFF")
    }

    private fun createSnack(message : String, view : View, hexBackgroundColor: String, hexTextColor: String){

        val snackBar = Snackbar.make(
            view, message,
            Snackbar.LENGTH_LONG
        ).setAction("Action", null)
        snackBar.setActionTextColor(Color.parseColor(hexTextColor))
        val snackBarView = snackBar.view
        snackBarView.setBackgroundColor(Color.parseColor(hexBackgroundColor))
        val textView = snackBarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
        textView.setTextColor(Color.parseColor(hexTextColor))
        snackBar.show()
    }

    fun showToastShort(context : Context, sentence : String){
        if (toast != null){
            toast!!.cancel()
        }
        toast = Toast.makeText(context, sentence,Toast.LENGTH_SHORT)
        toast!!.show()
    }
    fun showToastLong(context : Context, sentence : String){
        if (toast != null){
            toast!!.cancel()
        }
        toast = Toast.makeText(context, sentence,Toast.LENGTH_LONG)
        toast!!.show()
    }

    fun showToastCenter(context : Context, sentence : String){
        if (toast != null){
            toast!!.cancel()
        }
        toast = Toast.makeText(context, sentence,Toast.LENGTH_LONG)
        toast!!.setGravity(Gravity.CENTER ,0,0)
        toast!!.show()
    }
    fun showToastTop(context : Context, sentence : String){
        if (toast != null){
            toast!!.cancel()
        }
        toast = Toast.makeText(context, sentence,Toast.LENGTH_LONG)
        toast!!.setGravity(Gravity.TOP ,0,0)
        toast!!.show()
    }
    fun showToastBottom(context : Context, sentence : String){
        if (toast != null){
            toast!!.cancel()
        }
        toast = Toast.makeText(context, sentence,Toast.LENGTH_LONG)
        toast!!.setGravity(Gravity.BOTTOM ,0,0)
        toast!!.show()
    }
}