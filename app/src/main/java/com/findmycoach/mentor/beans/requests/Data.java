package com.findmycoach.mentor.beans.requests;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("owner")
    @Expose
    private Integer ownerId;

    @SerializedName("invitee")
    @Expose
    private Integer inviteeId;

    @SerializedName("created_on")
    @Expose
    private String createdOn;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("connection_id")
    @Expose
    private String connectionId;

    @SerializedName("connection_message")
    @Expose
    private String connectionMessage;

    public Integer getId() {
        return id;
    }

    public Integer getOwnerId() {
        return ownerId;
    }

    public Integer getInviteeId() {
        return inviteeId;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public String getStatus() {
        return status;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public String getConnectionMessage() {
        return connectionMessage;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setOwnerId(Integer ownerId) {
        this.ownerId = ownerId;
    }

    public void setInviteeId(Integer inviteeId) {
        this.inviteeId = inviteeId;
    }

    public void setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public void setConnectionMessage(String connectionMessage) {
        this.connectionMessage = connectionMessage;
    }
}
