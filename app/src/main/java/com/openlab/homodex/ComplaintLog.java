package com.openlab.homodex;

public class ComplaintLog {

    String date, time, offence, complaintNo;

    public ComplaintLog(String date, String time, String offence, String complaintNo) {
        this.date = date;
        this.time = time;
        this.offence = offence;
        this.complaintNo = complaintNo;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getOffence() {
        return offence;
    }

    public String getComplaintNo() {
        return complaintNo;
    }
}
