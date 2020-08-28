package com.karungkung.klinik.domains;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Profil {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<ProfileList> data  = null;

    public List<ProfileList> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public class ProfileList{
        @SerializedName("id")
        private Integer id;

        @SerializedName("name")
        private String name;

        @SerializedName("address")
        private String address;

        @SerializedName("email")
        private String email;

        @SerializedName("phone")
        private String phone;

        @SerializedName("file_image")
        private String fileImage;

        @SerializedName("nama_suami")
        private String namaSuami;

        @SerializedName("tgl_lahir")
        private String tgllahir;

        @SerializedName("pendidikan")
        private String pendidikan;

        @SerializedName("agama")
        private String agama;

        @SerializedName("goldar")
        private String goldar;

        @SerializedName("pekerjaan_suami")
        private String jobSuami;

        @SerializedName("pekerjaan_istri")
        private String jobIstri;

        public Integer getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getAddress() {
            return address;
        }

        public String getEmail() {
            return email;
        }

        public String getPhone() {
            return phone;
        }

        public String getFileImage() {
            return fileImage;
        }

        public String getNamaSuami() {
            return namaSuami;
        }

        public String getTgllahir() {
            return tgllahir;
        }

        public String getPendidikan() {
            return pendidikan;
        }

        public String getAgama() {
            return agama;
        }

        public String getGoldar() {
            return goldar;
        }

        public String getJobSuami() {
            return jobSuami;
        }

        public String getJobIstri() {
            return jobIstri;
        }
    }
}
