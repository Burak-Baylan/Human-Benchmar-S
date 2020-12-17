package com.burak.humanbenchmarks

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.database.sqlite.SQLiteDatabase
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class SqlHistories(activity: Activity, context: Context, view : View) {

    private var mActivity : Activity = activity
    private var mCtx : Context = context
    private var viewReal : View = view

    private lateinit var mSQL : SQLiteDatabase
    private lateinit var snackCreater: PopupMessageCreator
    private lateinit var firebaseManage: FirebaseManage

    @SuppressLint("Recycle")
    fun myHistories (){
        createSQL()
        snackCreater = PopupMessageCreator()
        try {
            var gelenString = ""

            val cursor = mSQL.rawQuery("SELECT * FROM reactionhistory", null)

            val historyIx = cursor?.getColumnIndex("history")

            while (cursor!!.moveToNext()) {
                gelenString = cursor.getString(historyIx!!)
            }

            gelenString = gelenString.substring(3, gelenString.length)
            //Toast.makeText(this,"${gelenString.length}",Toast.LENGTH_SHORT).show()

            val alert = AlertDialog.Builder(mCtx, R.style.CustomAlertDialog)
            alert.setTitle("Reaction Time History")
            alert.setMessage(gelenString)
            alert.setPositiveButton("Done") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.setNegativeButton("Back") { dialog: DialogInterface, _: Int ->
                dialog.cancel()
            }
            alert.setNeutralButton("Delete") { dialog: DialogInterface, _: Int ->

                val alert2 = AlertDialog.Builder(mCtx, R.style.CustomAlertDialog)
                alert2.setTitle("Delete")
                alert2.setCancelable(false)
                alert2.setMessage("If you delete this history you can't get it back.")
                alert2.setPositiveButton("Delete"){ _: DialogInterface, _: Int ->
                    try {
                        mSQL.execSQL("DELETE FROM reactionhistory")
                        snackCreater.customToast(
                            mActivity, mCtx, null, Toast.LENGTH_SHORT,
                            "Deleted",
                            R.drawable.custom_toast_success, R.drawable.ic_success_image
                        )
                        //snackCreater.showToastBottom(mCtx, "Deleted")
                    }
                    catch (e: Exception){
                        snackCreater.customToast(
                            mActivity, mCtx, null, Toast.LENGTH_SHORT,
                            "Delete Fail!",
                            R.drawable.custom_toast_error, R.drawable.ic_error_image
                        )
                        //snackCreater.showToastBottom(mCtx, "Delete Fail!")
                    }
                }
                alert2.setNegativeButton("Cancel"){ _: DialogInterface, _: Int ->
                    dialog.cancel()
                }
                alert2.setNeutralButton("Back"){ _: DialogInterface, _: Int ->
                    dialog.cancel()
                    alert.show()
                }
                alert2.show()
            }
            alert.show()

        } catch (e: Exception) {
            snackCreater.customToast(
                mActivity, mCtx, null, Toast.LENGTH_SHORT,
                "History could not be shown.",
                R.drawable.custom_toast_error, R.drawable.ic_error_image
            )
            //snackCreater.createFailSnack("History could not be shown.", viewReal)
        }
    }

    @SuppressLint("Recycle")
    fun saveReactionHistory (previouslyScore : String){
        createSQL()
        snackCreater = PopupMessageCreator()
        try {
            var gelenString = ""
            try {
                val cursor = mSQL.rawQuery("SELECT * FROM reactionhistory", null)

                val historyIx = cursor?.getColumnIndex("history")

                while (cursor!!.moveToNext()) {
                    gelenString = cursor.getString(historyIx!!)
                }
            } catch (e: Exception) {
                //snackbarCreater.createFailSnack("", viewReal)
            }

            gelenString += "\n\n\n$previouslyScore"

            mSQL.execSQL("DELETE FROM reactionhistory")
            mSQL.execSQL("INSERT INTO reactionhistory (history) VALUES (?)", arrayOf(gelenString))

            //snackCreater.showToastCenter(mCtx,"kaydedildi")

            //snackbarCreater.showToastTop(this,gelenString)
        }
        catch (e : Exception){
            //snackCreater.showToastCenter(mCtx,"kaydedilemedi: $e")
        }
    }

    private fun createSQL (){
        snackCreater = PopupMessageCreator()
        firebaseManage = FirebaseManage(mCtx,viewReal,mActivity)
        val currentId = firebaseManage.getUserId()
        try {
            mSQL = mCtx.openOrCreateDatabase(currentId, AppCompatActivity.MODE_PRIVATE, null)
            mSQL.execSQL("CREATE TABLE IF NOT EXISTS reactionhistory (id INTEGER PRIMARY KEY, history VARCHAR)")
            mSQL.execSQL("CREATE TABLE IF NOT EXISTS reactionhistoryaverage (id INTEGER PRIMARY KEY, history VARCHAR)")
        }
        catch (e: Exception){
            //snackCreater.showToastCenter(mCtx,"oluşturulamadı")
        }
    }
}