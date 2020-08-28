package com.karungkung.klinik;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ResponseDataStr {
    @SerializedName("success")
    private boolean success;

    @SerializedName("data")
    private String data;

    @SerializedName("message")
    private String message;

    public boolean getSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
