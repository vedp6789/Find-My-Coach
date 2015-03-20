package com.findmycoach.app.beans.student;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Data {

    @Expose
    private String id;
    @Expose
    private String email;
    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @Expose
    private String phonenumber;
    @SerializedName("middle_name")
    @Expose
    private Object middleName;
    @Expose
    private Object address;
    @Expose
    private Object city;
    @Expose
    private Object state;
    @Expose
    private Object zip;
    @Expose
    private Object dob;
    @Expose
    private Object country;
    @Expose
    private Object photograph;
    @SerializedName("training_location")
    @Expose
    private Object trainingLocation;
    @SerializedName("coaching_type")
    @Expose
    private Object coachingType;
    @SerializedName("mentor_for")
    @Expose
    private String mentorFor;
    @Expose
    private String availabilityYn;
    @SerializedName("google_link")
    @Expose
    private String googleLink;
    @SerializedName("facebook_link")
    @Expose
    private String facebookLink;
    @Expose
    private String gender;

    @SerializedName("connection_id")
    @Expose
    private String connectionId;

    @SerializedName("connection_status")
    @Expose
    private String connectionStatus;

    public String getAvailabilityYn() {
        return availabilityYn;
    }

    public void setAvailabilityYn(String availabilityYn) {
        this.availabilityYn = availabilityYn;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public List<String> getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(List<String> subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    @SerializedName("sub_category_name")
    @Expose
    private List<String> subCategoryName;

    /**
     * @return The id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The firstName
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName The first_name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return The lastName
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName The last_name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return The phonenumber
     */
    public String getPhonenumber() {
        return phonenumber;
    }

    /**
     * @param phonenumber The phonenumber
     */
    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    /**
     * @return The middleName
     */
    public Object getMiddleName() {
        return middleName;
    }

    /**
     * @param middleName The middle_name
     */
    public void setMiddleName(Object middleName) {
        this.middleName = middleName;
    }

    /**
     * @return The address
     */
    public Object getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(Object address) {
        this.address = address;
    }

    /**
     * @return The city
     */
    public Object getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(Object city) {
        this.city = city;
    }

    /**
     * @return The state
     */
    public Object getState() {
        return state;
    }

    /**
     * @param state The state
     */
    public void setState(Object state) {
        this.state = state;
    }

    /**
     * @return The zip
     */
    public Object getZip() {
        return zip;
    }

    /**
     * @param zip The zip
     */
    public void setZip(Object zip) {
        this.zip = zip;
    }

    /**
     * @return The dob
     */
    public Object getDob() {
        return dob;
    }

    /**
     * @param dob The dob
     */
    public void setDob(Object dob) {
        this.dob = dob;
    }

    /**
     * @return The country
     */
    public Object getCountry() {
        return country;
    }

    /**
     * @param country The country
     */
    public void setCountry(Object country) {
        this.country = country;
    }

    /**
     * @return The photograph
     */
    public Object getPhotograph() {
        return photograph;
    }

    /**
     * @param photograph The photograph
     */
    public void setPhotograph(Object photograph) {
        this.photograph = photograph;
    }

    /**
     * @return The trainingLocation
     */
    public Object getTrainingLocation() {
        return trainingLocation;
    }

    /**
     * @param trainingLocation The training_location
     */
    public void setTrainingLocation(Object trainingLocation) {
        this.trainingLocation = trainingLocation;
    }

    /**
     * @return The coachingType
     */
    public Object getCoachingType() {
        return coachingType;
    }

    /**
     * @param coachingType The coaching_type
     */
    public void setCoachingType(Object coachingType) {
        this.coachingType = coachingType;
    }

    /**
     * @return The mentorFor
     */
    public String getMentorFor() {
        return mentorFor;
    }

    /**
     * @param mentorFor The mentor_for
     */
    public void setMentorFor(String mentorFor) {
        this.mentorFor = mentorFor;
    }

    /**
     * @return The googleLink
     */
    public String getGoogleLink() {
        return googleLink;
    }

    /**
     * @param googleLink The google_link
     */
    public void setGoogleLink(String googleLink) {
        this.googleLink = googleLink;
    }

    /**
     * @return The facebookLink
     */
    public String getFacebookLink() {
        return facebookLink;
    }

    /**
     * @param facebookLink The facebook_link
     */
    public void setFacebookLink(String facebookLink) {
        this.facebookLink = facebookLink;
    }


}
