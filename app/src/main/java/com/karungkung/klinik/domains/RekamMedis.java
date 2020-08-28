package com.karungkung.klinik.domains;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RekamMedis {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<RekamMedisList> data  = null;

    public List<RekamMedisList> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public class RekamMedisList{
        @SerializedName("id")
        private Integer id;

        @SerializedName("is_kaki_bengkak")
        private String isKakiBengkak;

        @SerializedName("keluhan")
        private String keluhan;

        @SerializedName("tekanan_darah")
        private String tekananDarah;

        @SerializedName("berat_badan")
        private String beratBadan;

        @SerializedName("umur_kehamilan")
        private String umurKehamilan;

        @SerializedName("tinggi_fundus")
        private String tinggiFundus;

        @SerializedName("letak_janin")
        private String letakJanin;

        @SerializedName("denyut_janin")
        private String denyutJanin;

        @SerializedName("imunisasi")
        private String imunisasi;

        @SerializedName("tablet")
        private String tablet;

        @SerializedName("tata_laksana")
        private String tataLaksana;

        @SerializedName("hasil_lab")
        private String hasilLab;

        @SerializedName("tindakan")
        private String tindakan;

        @SerializedName("nasihat")
        private String nasihat;

        @SerializedName("create_date")
        private String createDate;

        @SerializedName("name")
        private String nameBidan;

        @SerializedName("jadwal_periksa")
        private String jadwalPeriksa;

        public Integer getId() {
            return id;
        }

        public String getIsKakiBengkak() {
            return isKakiBengkak;
        }

        public String getKeluhan() {
            return keluhan;
        }

        public String getTekananDarah() {
            return tekananDarah;
        }

        public String getBeratBadan() {
            return beratBadan;
        }

        public String getUmurKehamilan() {
            return umurKehamilan;
        }

        public String getTinggiFundus() {
            return tinggiFundus;
        }

        public String getLetakJanin() {
            return letakJanin;
        }

        public String getDenyutJanin() {
            return denyutJanin;
        }

        public String getImunisasi() {
            return imunisasi;
        }

        public String getTablet() {
            return tablet;
        }

        public String getTataLaksana() {
            return tataLaksana;
        }

        public String getHasilLab() {
            return hasilLab;
        }

        public String getTindakan() {
            return tindakan;
        }

        public String getNasihat() {
            return nasihat;
        }

        public String getCreateDate() {
            return createDate;
        }

        public String getNameBidan() {
            return nameBidan;
        }

        public String getJadwalPeriksa() {
            return jadwalPeriksa;
        }
    }
}
