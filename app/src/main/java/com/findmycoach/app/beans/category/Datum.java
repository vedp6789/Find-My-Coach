package com.findmycoach.app.beans.category;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Datum {

    @SerializedName("category_id")
    @Expose
    private String id;
    @SerializedName("category_name")
    @Expose
    private String name;

    @SerializedName("sub_categories")
    @Expose
    private List<DatumSub> dataSub = new ArrayList<DatumSub>();

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

    public List<DatumSub> getDataSub() {
        return dataSub;
    }

    public void setDataSub(List<DatumSub> dataSub) {
        this.dataSub = dataSub;
    }
}

