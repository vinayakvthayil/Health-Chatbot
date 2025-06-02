package com.example.healthi.Activity

import android.R
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.healthchatbot.adapters.MoodAdapter
import com.example.healthchatbot.database.DatabaseHelper
import com.example.healthchatbot.models.MoodEntry
import com.google.android.material.button.MaterialButton


class MoodTrackerActivity : AppCompatActivity() {
    private var moodHistoryRecyclerView: RecyclerView? = null
    private var moodAdapter: MoodAdapter? = null
    private var databaseHelper: DatabaseHelper? = null
    private var veryGoodButton: MaterialButton? = null
    private var goodButton: MaterialButton? = null
    private var okayButton: MaterialButton? = null
    private var badButton: MaterialButton? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mood_tracker)
        initializeViews()
        setupDatabase()
        setupRecyclerView()
        setupMoodButtons()
        loadMoodHistory()
    }

    private fun initializeViews() {
        moodHistoryRecyclerView = findViewById<RecyclerView>(R.id.moodHistoryRecyclerView)
        veryGoodButton = findViewById<MaterialButton>(R.id.veryGoodButton)
        goodButton = findViewById<MaterialButton>(R.id.goodButton)
        okayButton = findViewById<MaterialButton>(R.id.okayButton)
        badButton = findViewById<MaterialButton>(R.id.badButton)
    }

    private fun setupDatabase() {
        databaseHelper = DatabaseHelper(this)
    }

    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter()
        moodHistoryRecyclerView!!.layoutManager = LinearLayoutManager(this)
        moodHistoryRecyclerView!!.adapter = moodAdapter
    }

    private fun setupMoodButtons() {
        veryGoodButton!!.setOnClickListener { v: View? ->
            logMood(
                1,
                "Very Good"
            )
        }
        goodButton!!.setOnClickListener { v: View? ->
            logMood(
                2,
                "Good"
            )
        }
        okayButton!!.setOnClickListener { v: View? ->
            logMood(
                3,
                "Okay"
            )
        }
        badButton!!.setOnClickListener { v: View? ->
            logMood(
                4,
                "Not Good"
            )
        }
    }

    private fun logMood(moodValue: Int, moodNote: String) {
        val moodEntry = MoodEntry(moodValue, moodNote)
        val id: Long = databaseHelper.insertMoodEntry(moodEntry)
        if (id != -1L) {
            moodEntry.setId(id)
            moodAdapter.addMood(moodEntry)
            moodHistoryRecyclerView!!.smoothScrollToPosition(0)
            Toast.makeText(this, "Mood logged successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to log mood", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadMoodHistory() {
        val moodEntries: List<MoodEntry> = databaseHelper.getAllMoodEntries()
        moodAdapter.setMoodEntries(moodEntries)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (databaseHelper != null) {
            databaseHelper.close()
        }
    }
}