package com.uruklabs.qrcodeexpress.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import com.uruklabs.qrcodeexpress.MainActivity
import com.uruklabs.qrcodeexpress.R

class SplashScreen : AppCompatActivity() {
    private var SPLASH_SCREEN_TIME : Long = 1500
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)
            setContentView(R.layout.activity_splash_screen)
            Handler(Looper.myLooper()!!).postDelayed({
                intent = Intent(applicationContext, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()

            }, SPLASH_SCREEN_TIME)

        }


    }

