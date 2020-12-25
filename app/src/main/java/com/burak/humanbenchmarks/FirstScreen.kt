package com.burak.humanbenchmarks

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_first_screen.*

class FirstScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_screen)
        supportActionBar?.hide()
        skipButtonFirstScreen.setOnClickListener {
            sharedUpdater()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        signUpClick.setOnClickListener {
            sharedUpdater()
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("openSignUpAlert", true)
            startActivity(intent)
            finish()
        }
    }

    private fun sharedUpdater(){
        val preferences = getSharedPreferences("com.burak.humanbenchmarks", Context.MODE_PRIVATE)
        val edit: SharedPreferences.Editor = preferences.edit()
        val firstEnterIsTrue = false
        edit.putBoolean("firstenter", firstEnterIsTrue)
        edit.apply()
    }
}