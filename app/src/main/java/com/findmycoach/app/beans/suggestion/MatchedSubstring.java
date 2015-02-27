package com.findmycoach.app.beans.suggestion;

import com.google.gson.annotations.Expose;

public class MatchedSubstring {

    @Expose
    private Integer length;
    @Expose
    private Integer offset;

    /**
     * @return The length
     */
    public Integer getLength() {
        return length;
    }

    /**
     * @param length The length
     */
    public void setLength(Integer length) {
        this.length = length;
    }

    /**
     * @return The offset
     */
    public Integer getOffset() {
        return offset;
    }

    /**
     * @param offset The offset
     */
    public void setOffset(Integer offset) {
        this.offset = offset;
    }

}
