package com.burak.humanbenchmarks

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity


class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        supportActionBar?.hide()

        val timerThread: Thread = object : Thread() {
            override fun run() {
                try {
                    sleep(500)
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                    go()
                } finally {
                    go()
                }
            }
        }
        timerThread.start()
    }

    private fun go(){
        val intent = Intent(this@SplashScreen, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}