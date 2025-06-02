package com.example.healthi.ai

public class UserSettings {
    private long id;
    private String prefKey;
    private String prefValue;

    public UserSettings() {
    }

    public UserSettings(String prefKey, String prefValue) {
        this.prefKey = prefKey;
        this.prefValue = prefValue;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getPrefKey() {
        return prefKey;
    }

    public void setPrefKey(String prefKey) {
        this.prefKey = prefKey;
    }

    public String getPrefValue() {
        return prefValue;
    }

    public void setPrefValue(String prefValue) {
        this.prefValue = prefValue;
    }
}