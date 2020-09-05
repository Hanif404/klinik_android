package com.karungkung.klinik.domains;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Registrasi {
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<RegistrasiList> data;

    public List<RegistrasiList> getData() {
        return data;
    }

    public String getMessage() {
        return message;
    }

    public class RegistrasiList{
        @SerializedName("id")
        private Integer id;

        @SerializedName("name")
        private String namaPengguna;

        @SerializedName("hamil_ke")
        private String hamilKe;

        @SerializedName("jml_persalinan")
        private String jmlPersalinan;

        @SerializedName("jml_keguguran")
        private String jmlKeguguran;

        @SerializedName("jml_ank_hidup")
        private String jmlAnkHidpu;

        @SerializedName("jml_ank_mati")
        private String jmlAnakMati;

        @SerializedName("jml_ank_lr_kr_bln")
        private String jmlAnkKurangBulan;

        @SerializedName("jrk_hamil_dr_akhir")
        private String jrkHamil;

        @SerializedName("imunisasi_tt")
        private String imunisasiTT;

        @SerializedName("cara_salin_akhir")
        private String caraSalinAkhir;

        @SerializedName("penolong_salin_akhir")
        private String penolongSalinAkhir;

        @SerializedName("hpht")
        private String hpht;

        @SerializedName("htp")
        private String htp;

        @SerializedName("is_kek")
        private String isKek;

        @SerializedName("lingkar_lengan_atas")
        private String lingkarLenganAtas;

        @SerializedName("tinggi_bd")
        private String tinggiBadan;

        @SerializedName("kontrasepsi_blm_hamil")
        private String kontrasepsi;

        @SerializedName("rw_penyakit")
        private String rwPenyakit;

        @SerializedName("rw_alergi")
        private String rwAlergi;

        @SerializedName("create_date")
        private String createDate;

        @SerializedName("is_konsultasi")
        private String isKonsultasi;

        public Integer getId() {
            return id;
        }

        public String getHamilKe() {
            return hamilKe;
        }

        public String getJmlPersalinan() {
            return jmlPersalinan;
        }

        public String getJmlKeguguran() {
            return jmlKeguguran;
        }

        public String getJmlAnkHidpu() {
            return jmlAnkHidpu;
        }

        public String getJmlAnakMati() {
            return jmlAnakMati;
        }

        public String getJmlAnkKurangBulan() {
            return jmlAnkKurangBulan;
        }

        public String getJrkHamil() {
            return jrkHamil;
        }

        public String getImunisasiTT() {
            return imunisasiTT;
        }

        public String getCaraSalinAkhir() {
            return caraSalinAkhir;
        }

        public String getPenolongSalinAkhir() {
            return penolongSalinAkhir;
        }

        public String getHpht() {
            return hpht;
        }

        public String getHtp() {
            return htp;
        }

        public String getIsKek() {
            return isKek;
        }

        public String getLingkarLenganAtas() {
            return lingkarLenganAtas;
        }

        public String getTinggiBadan() {
            return tinggiBadan;
        }

        public String getKontrasepsi() {
            return kontrasepsi;
        }

        public String getRwPenyakit() {
            return rwPenyakit;
        }

        public String getRwAlergi() {
            return rwAlergi;
        }

        public String getCreateDate() {
            return createDate;
        }

        public String getIsKonsultasi() {
            return isKonsultasi;
        }

        public String getNamaPengguna() {
            return namaPengguna;
        }
    }
}
