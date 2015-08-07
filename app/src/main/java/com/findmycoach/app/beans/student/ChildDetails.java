package com.findmycoach.app.beans.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by abhi7 on 27/07/15.
 */
public class ChildDetails {

    private String name;
    private String gender;
    private String dob;

    @Expose
    private String grade_id;
    private String grade;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getGradeId() {
        return grade_id;
    }

    public void setGradeId(String grade_id) {
        this.grade_id = grade_id;
    }
}
