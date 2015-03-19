package com.findmycoach.app.beans.requests;

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

    @SerializedName("connection_message")
    @Expose
    private String connectionMessage;

    @SerializedName("owner_name")
    @Expose
    private String ownerName;

    @SerializedName("invitee_name")
    @Expose
    private String inviteeName;

    @SerializedName("owner_image")
    @Expose
    private String ownerImage;

    @SerializedName("invitee_image")
    @Expose
    private String inviteeImage;

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

    public void setConnectionMessage(String connectionMessage) {
        this.connectionMessage = connectionMessage;
    }

    public void setOwnerImage(String ownerImage) {
        this.ownerImage = ownerImage;
    }

    public void setInviteeImage(String imviteeImage) {
        this.inviteeImage = imviteeImage;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public void setInviteeName(String inviteeName) {
        this.inviteeName = inviteeName;
    }

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

    public String getConnectionMessage() {
        return connectionMessage;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public String getInviteeName() {
        return inviteeName;
    }

    public String getOwnerImage() {
        return ownerImage;
    }

    public String getInviteeImage() {
        return inviteeImage;
    }
}
