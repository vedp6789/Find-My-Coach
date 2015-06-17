package com.findmycoach.app.beans.category;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DatumSub {

    @Expose
    private String id;
    @SerializedName("sub_category_name")
    @Expose
    private String name;
    @Expose
    private String price;
    @Expose
    private String level;

    private boolean isSelected;

    public boolean isSelected() {
        return isSelected;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

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
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

}

