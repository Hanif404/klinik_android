package com.karungkung.klinik;

import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;

public class ResponseWithData {
    @SerializedName("data")
    private JsonObject data;

    @SerializedName("message")
    private String message;

    public JsonObject getData() {
        return data;
    }

    public void setData(JsonObject data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
