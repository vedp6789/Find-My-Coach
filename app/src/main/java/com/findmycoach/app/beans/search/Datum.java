package com.findmycoach.app.beans.search;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Datum {

    private String id;

    @SerializedName("first_name")
    @Expose
    private String firstName;
    @SerializedName("middle_name")
    @Expose
    private Object middleName;
    @SerializedName("last_name")
    @Expose
    private String lastName;
    @Expose
    private Object photograph;
    @SerializedName("group_id")
    @Expose
    private String groupId;
    @Expose
    private String rating;
    @Expose
    private String latitude;
    @Expose
    private String longitude;
    @SerializedName("connection_status")
    @Expose
    private String connectionStatus;
    @SerializedName("connection_id")
    @Expose
    private String connectionId;
    @Expose
    private String distance;

    @SerializedName("charges")
    @Expose
    private int chargesHour;
    @SerializedName("charges_class")
    @Expose
    private int chargesClass;
    @SerializedName("dates")
    @Expose
    private String availableDays;

    public int getChargesHour() {
        return chargesHour;
    }

    public void setChargesHour(int chargesHour) {
        this.chargesHour = chargesHour;
    }

    public int getChargesClass() {
        return chargesClass;
    }

    public void setChargesClass(int chargesClass) {
        this.chargesClass = chargesClass;
    }

    public String getAvailableDays() {
        return availableDays;
    }

    public void setAvailableDays(String availableDays) {
        this.availableDays = availableDays;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setConnectionStatus(String connectionStatus) {
        this.connectionStatus = connectionStatus;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getRating() {
        return rating;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getConnectionStatus() {
        return connectionStatus;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
     * @return The groupId
     */
    public String getGroupId() {
        return groupId;
    }

    /**
     * @param groupId The group_id
     */
    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String toString() {
        return "id:" + id + ", " + "firstName:" + firstName + ", " + "middleName:" + middleName + ", " + "lastName:" + lastName + ", " + "photograph:" + photograph + ", " + "groupId:" + groupId;
    }

}
