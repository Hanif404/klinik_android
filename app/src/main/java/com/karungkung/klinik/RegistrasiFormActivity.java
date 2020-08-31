package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.datepicker.MaterialCalendar;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.karungkung.klinik.domains.Registrasi;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrasiFormActivity extends AppCompatActivity implements Validator.ValidationListener{

    @BindView(R.id.txt_hamil_ke)
    @NotEmpty
    TextInputLayout hamilKe;

    @BindView(R.id.txt_jml_persalinan)
    @NotEmpty
    TextInputLayout jmlPersalinan;

    @BindView(R.id.txt_jml_keguguran)
    @NotEmpty
    TextInputLayout jmlKeguguran;
    @BindView(R.id.txt_jml_ank_hidup)
    @NotEmpty
    TextInputLayout jmlAnkHidup;
    @BindView(R.id.txt_jml_ank_mati)
    @NotEmpty
    TextInputLayout jmlAnkMati;

    @BindView(R.id.txt_jml_ank_lr_kr_bln)
    @NotEmpty
    TextInputLayout jmlAnkKurangBln;

    @BindView(R.id.txt_jrk_hamil_dr_akhir)
    @NotEmpty
    TextInputLayout jrkHamil;

    @BindView(R.id.txt_imunisasi_tt)
    @NotEmpty
    TextInputLayout imunisasiTT;

    @BindView(R.id.txt_cara_salin_akhir)
    @NotEmpty
    TextInputLayout caraSalin;
    @BindView(R.id.txt_penolong_salin_akhir)
    @NotEmpty
    TextInputLayout penolongSalin;

    @BindView(R.id.txt_hpht)
    @NotEmpty
    TextInputLayout hpht;

    @BindView(R.id.txt_htp)
    @NotEmpty
    TextInputLayout htp;

    @BindView(R.id.filled_exposed_dropdown)
    @NotEmpty
    AutoCompleteTextView isKek;

    @BindView(R.id.filled_exposed_dropdown_1)
    @NotEmpty
    AutoCompleteTextView imunisasiTTDp;

    @BindView(R.id.txt_is_kek)
    @NotEmpty
    TextInputLayout isKektxt;

    @BindView(R.id.txt_lingkar_lengan_atas)
    @NotEmpty
    TextInputLayout lingkarLengan;
    @BindView(R.id.txt_tinggi_bd)
    @NotEmpty
    TextInputLayout tinggiBadan;
    @BindView(R.id.txt_kontrasepsi_blm_hamil)
    @NotEmpty
    TextInputLayout kontrasepsiBlmHamil;
    @BindView(R.id.txt_rw_penyakit)
    @NotEmpty
    TextInputLayout rwPenyakit;
    @BindView(R.id.txt_rw_alergi)
    @NotEmpty
    TextInputLayout rwAlergi;

    private Validator validator;
    private int idRegistrasi = 0, idUser = 0;
    private int selectInput;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrasi_form);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idRegistrasi = bundle.getInt("id");
            idUser = bundle.getInt("user_id");
        }

        prepareData();
        createDropdown();
    }

    private void createDropdown(){
        String[] isKekArr = new String[] {"Tidak", "Ya"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.dropdown_menu_popup_item,
                        isKekArr);
        isKek.setAdapter(adapter);
        imunisasiTTDp.setAdapter(adapter);
    }

    private void prepareData(){
        if(idRegistrasi > 0){
            ProgressDialog progress = new ProgressDialog(RegistrasiFormActivity.this);
            progress.setTitle("Loading");
            progress.setMessage(getString(R.string.message_loading));
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
            Call<Registrasi> call;
            if(BuildConfig.FLAVOR.equals("bidan")){
                // TODO user id ngambil dari scan barcode
                call = service.getRegBidan(String.valueOf(idUser));
            }else{
                call = service.getDataRegistrasi();
            }
            call.enqueue(new Callback<Registrasi>() {
                @Override
                public void onResponse(Call<Registrasi> call, Response<Registrasi> response) {
                    progress.dismiss();
                    if (response.code() == 200) {
                        List<Registrasi.RegistrasiList> regData = response.body().getData();
                        for (Registrasi.RegistrasiList rl: regData) {
                            hamilKe.getEditText().setText(rl.getHamilKe());
                            jmlPersalinan.getEditText().setText(rl.getJmlPersalinan());
                            jmlKeguguran.getEditText().setText(rl.getJmlKeguguran());
                            jmlAnkHidup.getEditText().setText(rl.getJmlAnkHidpu());
                            jmlAnkMati.getEditText().setText(rl.getJmlAnakMati());
                            jmlAnkKurangBln.getEditText().setText(rl.getJmlAnkKurangBulan());
                            jrkHamil.getEditText().setText(rl.getJrkHamil());

                            String strImunisasi = "Tidak";
                            if(rl.getImunisasiTT() == "1"){
                                strImunisasi = "Ya";
                            }
                            imunisasiTT.getEditText().setText(strImunisasi);
                            caraSalin.getEditText().setText(rl.getCaraSalinAkhir());
                            penolongSalin.getEditText().setText(rl.getPenolongSalinAkhir());
                            hpht.getEditText().setText(rl.getHpht());
                            htp.getEditText().setText(rl.getHtp());

                            String strKek = "Tidak";
                            if(rl.getIsKek() == "1"){
                                strKek = "Ya";
                            }
                            isKektxt.getEditText().setText(strKek);

                            lingkarLengan.getEditText().setText(rl.getLingkarLenganAtas());
                            tinggiBadan.getEditText().setText(rl.getTinggiBadan());
                            kontrasepsiBlmHamil.getEditText().setText(rl.getKontrasepsi());
                            rwPenyakit.getEditText().setText(rl.getRwPenyakit());
                            rwAlergi.getEditText().setText(rl.getRwAlergi());
                        }
                        createDropdown();
                    } else {
                        Gson gson = new Gson();
                        TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                        try {
                            if (response.errorBody() != null){
                                ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                                new Utilities().dialogModal(RegistrasiFormActivity.this, null, "Pesan", resBody.getMessage(), false);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<Registrasi> call, Throwable t) {
                    progress.dismiss();
                }
            });
        }
    }

    @OnClick(R.id.btn_registrasi)
    public void handleRegistrasi(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        final ProgressDialog progress = new ProgressDialog(RegistrasiFormActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        // TODO user id ngambil dari scan barcode
        params.put("user_id", String.valueOf(idUser));
        params.put("hamil_ke", hamilKe.getEditText().getText().toString());
        params.put("jml_persalinan", jmlPersalinan.getEditText().getText().toString());
        params.put("jml_keguguran", jmlKeguguran.getEditText().getText().toString());
        params.put("jml_ank_hidup", jmlAnkHidup.getEditText().getText().toString());
        params.put("jml_ank_mati", jmlAnkMati.getEditText().getText().toString());
        params.put("jml_ank_lr_kr_bln", jmlAnkKurangBln.getEditText().getText().toString());
        params.put("jrk_hamil_dr_akhir", jrkHamil.getEditText().getText().toString());

        String strImunisasi = "0";
        if(imunisasiTT.getEditText().getText().toString().equals("Ya")){
            strImunisasi = "1";
        }
        params.put("imunisasi_tt", strImunisasi);
        params.put("cara_salin_akhir", caraSalin.getEditText().getText().toString());
        params.put("penolong_salin_akhir", penolongSalin.getEditText().getText().toString());
        params.put("hpht", hpht.getEditText().getText().toString());
        params.put("htp", htp.getEditText().getText().toString());

        String strKek = "0";
        if(isKektxt.getEditText().getText().toString().equals("Ya")){
            strKek = "1";
        }
        params.put("is_kek", strKek);

        params.put("lingkar_lengan_atas", lingkarLengan.getEditText().getText().toString());
        params.put("tinggi_bd", tinggiBadan.getEditText().getText().toString());
        params.put("kontrasepsi_blm_hamil", kontrasepsiBlmHamil.getEditText().getText().toString());
        params.put("rw_penyakit", rwPenyakit.getEditText().getText().toString());
        params.put("rw_alergi", rwAlergi.getEditText().getText().toString());

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> call;
        if(idRegistrasi > 0){
            params.put("id", String.valueOf(idRegistrasi));
            call = service.editRegistrasi(params);
        }else{
            call = service.addRegistrasi(params);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    new Utilities().dialogModal(RegistrasiFormActivity.this, PeriksaActivity.class, "Pesan", getString(R.string.message_success), true);
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(RegistrasiFormActivity.this, null, "Pesan", resBody.getMessage(), false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(this);

            // Display error messages ;)
            if (view instanceof TextInputLayout) {
                ((TextInputLayout) view).setError(message);
                ((TextInputLayout) view).requestFocus();
            } else {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    @OnClick(R.id.btn_hpht)
    public void handleHpht(){
        DialogFragment newFragment = new DatePickerFragment(hpht);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @OnClick(R.id.btn_htp)
    public void handleHtp(){
        DialogFragment newFragment = new DatePickerFragment(htp);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {
        private TextInputLayout txtDate;

        public DatePickerFragment(TextInputLayout txtDate){
            this.txtDate = txtDate;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            month = month+1;
            String bulan = String.valueOf(month);
            if(month < 10){
                bulan = "0"+month;
            }
            String date = year+"-"+bulan+"-"+day;
            txtDate.getEditText().setText(date);
        }
    }
}