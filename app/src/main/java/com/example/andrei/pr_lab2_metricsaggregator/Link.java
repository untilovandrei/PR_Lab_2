package com.example.andrei.pr_lab2_metricsaggregator;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by andrei on 3/5/18.
 */

public class Link {
    @SerializedName("method")
    @Expose
    private String method;
    @SerializedName("path")
    @Expose
    private String path;


    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
