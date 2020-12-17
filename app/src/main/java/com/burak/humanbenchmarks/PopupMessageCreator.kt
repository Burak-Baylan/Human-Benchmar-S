package com.burak.humanbenchmarks

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.text.Layout
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.custom_toast.*
import kotlinx.android.synthetic.main.custom_toast.view.*


class PopupMessageCreator {

    private var toast : Toast? = null

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
    fun showToastBottom(context : Context, sentence : String) {
        if (toast != null) {
            toast!!.cancel()
        }
        toast = Toast.makeText(context, sentence, Toast.LENGTH_LONG)
        toast!!.setGravity(Gravity.BOTTOM, 0, 0)
        toast!!.show()
    }

    fun customToast(
        activity : Activity, context : Context, gravity : Int?, toastDuration : Int,
        message : String, background : Int, image : Int?
    ){
        val inflater = activity.layoutInflater.inflate(R.layout.custom_toast,  activity.findViewById(R.id.custom_toast_layout))
        inflater.custom_toast_text.text = message
        inflater.custom_toast_image.visibility = View.GONE

        inflater.setOnClickListener {
            println("Toast Clicked")
        }

        inflater.custom_toast_layout.setBackgroundResource(background)
        inflater.custom_toast_image.visibility = View.VISIBLE

        image?.let {
            inflater.custom_toast_image.setImageResource(image)
        }

        if (toast != null){
            toast!!.cancel()
        }

        toast = Toast(context).apply {
            duration = toastDuration
            if (gravity != null) {
                setGravity(gravity, 0, 0)
            }
            view = inflater
        }
        toast?.show()
    }
}