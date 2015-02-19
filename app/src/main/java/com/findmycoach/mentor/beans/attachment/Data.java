package com.findmycoach.mentor.beans.attachment;

import com.google.gson.annotations.Expose;

public class Data {

    @Expose
    private String file_name;
    @Expose
    private String file_type;
    @Expose
    private String raw_name;
    @Expose
    private String orig_name;
    @Expose
    private String client_name;
    @Expose
    private String file_ext;
    @Expose
    private String file_size;
    @Expose
    private String is_image;
    @Expose
    private String image_width;
    @Expose
    private String image_height;
    @Expose
    private String image_type;
    @Expose
    private String image_size_str;
    @Expose
    private String path;

    public String getFile_name() {
        return file_name;
    }

    public String getFile_type() {
        return file_type;
    }

    public String getRaw_name() {
        return raw_name;
    }

    public String getOrig_name() {
        return orig_name;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getFile_ext() {
        return file_ext;
    }

    public String getFile_size() {
        return file_size;
    }

    public String getIs_image() {
        return is_image;
    }

    public String getImage_width() {
        return image_width;
    }

    public String getImage_height() {
        return image_height;
    }

    public String getImage_type() {
        return image_type;
    }

    public String getImage_size_str() {
        return image_size_str;
    }

    public String getPath() {
        return path;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
    }

    public void setRaw_name(String raw_name) {
        this.raw_name = raw_name;
    }

    public void setOrig_name(String orig_name) {
        this.orig_name = orig_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public void setFile_ext(String file_ext) {
        this.file_ext = file_ext;
    }

    public void setFile_size(String file_size) {
        this.file_size = file_size;
    }

    public void setIs_image(String is_image) {
        this.is_image = is_image;
    }

    public void setImage_width(String image_width) {
        this.image_width = image_width;
    }

    public void setImage_height(String image_height) {
        this.image_height = image_height;
    }

    public void setImage_type(String image_type) {
        this.image_type = image_type;
    }

    public void setImage_size_str(String image_size_str) {
        this.image_size_str = image_size_str;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
