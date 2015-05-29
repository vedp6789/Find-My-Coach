package com.findmycoach.app.beans.student;


import com.google.gson.annotations.Expose;

public class ProfileResponse {

    @Expose
    private String message;
    @Expose
    private StudentBean studentBean;

    /**
     * @return The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return The data
     */
    public StudentBean getStudentBean() {
        return studentBean;
    }

    /**
     * @param studentBean The data
     */
    public void setStudentBean(StudentBean studentBean) {
        this.studentBean = studentBean;
    }

}
