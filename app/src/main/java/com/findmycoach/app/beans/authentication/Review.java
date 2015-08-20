package com.findmycoach.app.beans.authentication;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by ShekharKG on 20/8/15.
 */
public class Review {

    @SerializedName("commented_by")
    @Expose
    String commentedBy;
    @Expose
    String comment;

    public String getCommentedBy() {
        return commentedBy;
    }

    public void setCommentedBy(String commentedBy) {
        this.commentedBy = commentedBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
