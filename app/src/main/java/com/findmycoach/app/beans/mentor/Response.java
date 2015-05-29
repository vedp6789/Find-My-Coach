package com.findmycoach.app.beans.mentor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

    @Expose
    private String message;
    @Expose
    private MentorBean mentorBean;
    @SerializedName("auth_token")
    @Expose
    private String authToken;

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
    public MentorBean getMentorBean() {
        return mentorBean;
    }

    /**
     * @param mentorBean The data
     */
    public void setMentorBean(MentorBean mentorBean) {
        this.mentorBean = mentorBean;
    }

    /**
     * @return The authToken
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * @param authToken The auth_token
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
