package com.findmycoach.app.beans.category;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Datum {

    @Expose
    private String id;
    @Expose
    private String name;
    @SerializedName("parent_id")
    @Expose
    private int parentId;
    @SerializedName("sub_categories")
    @Expose
    private List<DatumSub> subCategories = new ArrayList<DatumSub>();
    @SerializedName("categories")
    @Expose
    private List<Datum> categories = new ArrayList<Datum>();
    @Expose
    private int selectedItems;

    public int getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(int selectedItems) {
        this.selectedItems = selectedItems;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public List<DatumSub> getSubCategories() {
        return subCategories;
    }

    public void setSubCategories(List<DatumSub> subCategories) {
        this.subCategories = subCategories;
    }

    public List<Datum> getCategories() {
        return categories;
    }

    public void setCategories(List<Datum> categories) {
        this.categories = categories;
    }

    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<DatumSub> getDataSub() {
        return subCategories;
    }

    public void setDataSub(List<DatumSub> subCategories) {
        this.subCategories = subCategories;
    }
}

