package com.example.healthi.Activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val startChatButton = findViewById<MaterialButton>(R.id.startChatButton)
        val moodTrackerButton = findViewById<MaterialButton>(R.id.moodTrackerButton)
        val settingsButton = findViewById<MaterialButton>(R.id.settingsButton)
        startChatButton.setOnClickListener { v: View? ->
            val intent = Intent(
                this,
                ChatActivity::class.java
            )
            startActivity(intent)
        }
        moodTrackerButton.setOnClickListener { v: View? ->
            val intent = Intent(
                this,
                MoodTrackerActivity::class.java
            )
            startActivity(intent)
        }
        settingsButton.setOnClickListener { v: View? ->
            val intent = Intent(
                this,
                SettingsActivity::class.java
            )
            startActivity(intent)
        }
    }
}