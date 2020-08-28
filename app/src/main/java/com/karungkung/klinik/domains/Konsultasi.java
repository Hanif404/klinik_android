package com.karungkung.klinik.domains;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Konsultasi {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<KonsultasiList> data  = null;

    public List<KonsultasiList> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public class KonsultasiList{
        @SerializedName("id")
        private Integer id;

        @SerializedName("comment")
        private String txtComment;

        @SerializedName("name")
        private String txtName;

        @SerializedName("user_id")
        private String userId;

        @SerializedName("create_date")
        private long createDate;

        @SerializedName("is_read")
        private int isRead;

        @SerializedName("reg_id")
        private Integer idReg;

        public Integer getId() {
            return id;
        }

        public String getTxtComment() {
            return txtComment;
        }

        public String getTxtName() {
            return txtName;
        }

        public String getUserId() {
            return userId;
        }

        public long getCreateDate() {
            return createDate;
        }

        public int getIsRead() {
            return isRead;
        }

        public Integer getIdReg() {
            return idReg;
        }
    }
}
