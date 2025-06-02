package com.example.healthi.Adapter

import android.R
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.healthchatbot.models.MoodEntry


class MoodAdapter : RecyclerView.Adapter<MoodAdapter.MoodViewHolder>() {
    private var moodEntries: List<MoodEntry> = ArrayList<MoodEntry>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_mood_item, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val entry: MoodEntry = moodEntries[position]
        val moodText = getMoodText(entry.getMoodValue())
        holder.moodValue.text = moodText
        holder.moodDate.setText(entry.getTimestamp())
    }

    override fun getItemCount(): Int {
        return moodEntries.size
    }

    fun setMoodEntries(entries: List<MoodEntry>) {
        moodEntries = entries
        notifyDataSetChanged()
    }

    fun addMood(entry: MoodEntry) {
        moodEntries.add(0, entry)
        notifyItemInserted(0)
    }

    private fun getMoodText(moodValue: Int): String {
        return when (moodValue) {
            1 -> "Very Good"
            2 -> "Good"
            3 -> "Okay"
            4 -> "Not Good"
            else -> "Unknown"
        }
    }

    internal class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var moodValue: TextView
        var moodDate: TextView

        init {
            moodValue = itemView.findViewById<TextView>(R.id.moodValue)
            moodDate = itemView.findViewById<TextView>(R.id.moodDate)
        }
    }
}