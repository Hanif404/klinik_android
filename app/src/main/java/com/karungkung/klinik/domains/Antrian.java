package com.karungkung.klinik.domains;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Antrian {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<AntrianList> data  = null;

    public List<AntrianList> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public class AntrianList{
        @SerializedName("id")
        private Integer id;

        @SerializedName("no_urut")
        private Integer noUrut;

        @SerializedName("is_periksa")
        private Boolean isPeriksa;

        public Integer getId() {
            return id;
        }

        public Integer getNoUrut() {
            return noUrut;
        }

        public Boolean getPeriksa() {
            return isPeriksa;
        }
    }
}
