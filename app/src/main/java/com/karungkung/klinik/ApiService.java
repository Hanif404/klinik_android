package com.karungkung.klinik;

import com.karungkung.klinik.domains.Antrian;
import com.karungkung.klinik.domains.Klinik;
import com.karungkung.klinik.domains.Konsultasi;
import com.karungkung.klinik.domains.Profil;
import com.karungkung.klinik.domains.Registrasi;
import com.karungkung.klinik.domains.RekamMedis;

import java.util.HashMap;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by hanif on 23/09/18.
 */

public interface ApiService {

    @POST("api/v1/register")
    @FormUrlEncoded
    Call<ResponseBody> registration(@FieldMap HashMap<String, String> params);

    @POST("api/v1/user/upload/{id}")
    @Multipart
    Call<ResponseBody> uploadFoto(@Part MultipartBody.Part file, @Path("id") String user_id);

    @POST("api/v1/user/login")
    @FormUrlEncoded
    Call<ResponseBody> login(@FieldMap HashMap<String, String> params);

    @POST("api/v1/user/change")
    @FormUrlEncoded
    Call<ResponseBody> changePassword(@FieldMap HashMap<String, String> params);

    @POST("api/v1/user/reset")
    @FormUrlEncoded
    Call<ResponseBody> resetPassword(@FieldMap HashMap<String, String> params);

    @GET("api/v1/user/get")
    Call<Profil> getProfile();

    @POST("api/v1/user/edit")
    @FormUrlEncoded
    Call<ResponseBody> editProfile(@FieldMap HashMap<String, String> params);

    @GET("api/v1/antrian/add")
    Call<ResponseBody> getNoAntrian();

    @GET("api/v1/antrian")
    Call<Antrian> getDataAntrian();

    @GET("api/v1/klinik/last")
    Call<Klinik> getDataKlinik();

    @POST("api/v1/klinik/edit")
    @FormUrlEncoded
    Call<ResponseBody> closeKlinik(@FieldMap HashMap<String, String> params);

    @POST("api/v1/klinik/add")
    @FormUrlEncoded
    Call<ResponseBody> openKlinik(@FieldMap HashMap<String, String> params);

    @GET("api/v1/registrasi/all")
    Call<Registrasi> getDataRegistrasi();

    @GET("api/v1/registrasi/detail/{id}")
    Call<Registrasi> getDetailRegistrasi(@Path("id") String id);

    @POST("api/v1/registrasi/add")
    @FormUrlEncoded
    Call<ResponseBody> addRegistrasi(@FieldMap HashMap<String, String> params);

    @POST("api/v1/registrasi/edit")
    @FormUrlEncoded
    Call<ResponseBody> editRegistrasi(@FieldMap HashMap<String, String> params);

    @POST("api/v1/registrasi/delete")
    @FormUrlEncoded
    Call<ResponseBody> delRegistrasi(@FieldMap HashMap<String, String> params);

    @GET("api/v1/rekam_medis/all/{id}")
    Call<RekamMedis> getDataRekammedis(@Path("id") Integer id);

    @GET("api/v1/rekam_medis/detail/{id}")
    Call<RekamMedis> getDetailRekammedis(@Path("id") Integer id);

    @GET("api/v1/rekam_medis/last")
    Call<RekamMedis> getLastRekammedis();

    @POST("api/v1/rekam_medis/add")
    @FormUrlEncoded
    Call<ResponseBody> addRekammedis(@FieldMap HashMap<String, String> params);

    @POST("api/v1/rekam_medis/edit")
    @FormUrlEncoded
    Call<ResponseBody> editRekammedis(@FieldMap HashMap<String, String> params);

    @POST("api/v1/rekam_medis/delete")
    @FormUrlEncoded
    Call<ResponseBody> delRekammedis(@FieldMap HashMap<String, String> params);

    @GET("api/v1/konsultasi/all/{id}")
    Call<Konsultasi> getKonsultasi(@Path("id") int id);

    @GET("api/v1/konsultasi/user/{id}")
    Call<Konsultasi> getKonsultasiUser(@Path("id") String id);

    @POST("api/v1/konsultasi/add")
    @FormUrlEncoded
    Call<ResponseBody> addKonsultasi(@FieldMap HashMap<String, String> params);

    @POST("api/v1/konsultasi/edit_read")
    @FormUrlEncoded
    Call<ResponseBody> readKonsultasi(@FieldMap HashMap<String, String> params);

    //    for bidan
    @GET("api/v1/registrasi/all/{id}")
    Call<Registrasi> getRegBidan(@Path("id") String id);

    @GET("api/v1/registrasi/detail/{user_id}/{id}")
    Call<Registrasi> getDetailRegBidan(@Path("user_id") String user_id, @Path("id") String id);
}
