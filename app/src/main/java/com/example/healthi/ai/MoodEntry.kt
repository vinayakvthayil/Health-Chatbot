package com.example.healthi.ai

public class MoodEntry {
    private long id;
    private int moodValue;    // 1: Very Good, 2: Good, 3: Okay, 4: Not Good
    private String moodNote;
    private String timestamp;

    public MoodEntry() {
    }

    public MoodEntry(int moodValue, String moodNote) {
        this.moodValue = moodValue;
        this.moodNote = moodNote;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getMoodValue() {
        return moodValue;
    }

    public void setMoodValue(int moodValue) {
        this.moodValue = moodValue;
    }

    public String getMoodNote() {
        return moodNote;
    }

    public void setMoodNote(String moodNote) {
        this.moodNote = moodNote;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
