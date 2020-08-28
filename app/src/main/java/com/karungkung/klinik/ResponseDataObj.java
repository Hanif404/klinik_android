package com.karungkung.klinik;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ResponseDataObj {
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
