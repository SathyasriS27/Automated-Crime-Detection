package com.openlab.homodex;

import java.net.URL;

public class ClassStudents {

    private String regNo, name, className;
    private URL photosURL;

    public ClassStudents(String regNo, String name, String className, URL photosURL) {
        this.regNo = regNo;
        this.name = name;
        this.className = className;
        this.photosURL = photosURL;
    }

    public String getRegNo() {
        return regNo;
    }

    public String getStudentName() {
        return name;
    }

    public String getClassName() {
        return className;
    }

    public URL getPhotosURL() {
        return photosURL;
    }
}
