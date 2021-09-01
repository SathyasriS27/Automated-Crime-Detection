package com.openlab.homodex;

public class TrackerLog {

    private String time, date, description;

    public TrackerLog(String time, String date, String description) {
        this.time = time;
        this.date = date;
        this.description = description;
    }

    public String getTime() {
        return time;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }
}
