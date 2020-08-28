package com.karungkung.klinik.domains;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Klinik {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<KlinikList> data  = null;

    public List<KlinikList> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public class KlinikList{
        @SerializedName("id")
        private Integer id;

        @SerializedName("is_close")
        private String isClose;

        public Integer getId() {
            return id;
        }
        public String getIsClose() {
            return isClose;
        }
    }
}
