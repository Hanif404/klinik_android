package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.karungkung.klinik.domains.Registrasi;
import com.karungkung.klinik.domains.RekamMedis;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RekamMedisFormActivity extends AppCompatActivity implements Validator.ValidationListener{
    @BindView(R.id.txt_is_kaki_bengkak)
    @NotEmpty
    TextInputLayout isKakiBengkak;

    @BindView(R.id.filled_exposed_dropdown)
    @NotEmpty
    AutoCompleteTextView isKakiBengkakDp;

    @BindView(R.id.txt_keluhan)
    @NotEmpty
    TextInputLayout keluhan;

    @BindView(R.id.txt_tekanan_darah)
    @NotEmpty
    TextInputLayout tekananDarah;

    @BindView(R.id.txt_berat_badan)
    @NotEmpty
    TextInputLayout beratBadan;

    @BindView(R.id.txt_umur_kehamilan)
    @NotEmpty
    TextInputLayout umurKehamilan;

    @BindView(R.id.txt_tinggi_fundus)
    @NotEmpty
    TextInputLayout tinggiFundus;

    @BindView(R.id.txt_letak_janin)
    @NotEmpty
    TextInputLayout letakJanin;

    @BindView(R.id.txt_denyut_janin)
    @NotEmpty
    TextInputLayout denyutJanin;

    @BindView(R.id.txt_imunisasi)
    @NotEmpty
    TextInputLayout imunisasi;

    @BindView(R.id.txt_tablet)
    @NotEmpty
    TextInputLayout tablet;

    @BindView(R.id.txt_tata_laksana)
    @NotEmpty
    TextInputLayout tataLaksana;

    @BindView(R.id.txt_hasil_lab)
    @NotEmpty
    TextInputLayout hasilLab;

    @BindView(R.id.txt_tindakan)
    @NotEmpty
    TextInputLayout tindakan;

    @BindView(R.id.txt_nasihat)
    @NotEmpty
    TextInputLayout nasihat;

    @BindView(R.id.txt_jadwal_periksa)
    @NotEmpty
    TextInputLayout jadwalPeriksa;

    private Validator validator;
    private int idDetail = 0, idRegistrasi= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekam_medis_form);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idDetail = bundle.getInt("id");
            idRegistrasi = bundle.getInt("reg_id");
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
        isKakiBengkakDp.setAdapter(adapter);
    }

    private void prepareData(){
        if(idDetail > 0){
            ProgressDialog progress = new ProgressDialog(RekamMedisFormActivity.this);
            progress.setTitle("Loading");
            progress.setMessage(getString(R.string.message_loading));
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
            Call<RekamMedis> call = service.getDetailRekammedis(idDetail);
            call.enqueue(new Callback<RekamMedis>() {
                @Override
                public void onResponse(Call<RekamMedis> call, Response<RekamMedis> response) {
                    progress.dismiss();
                    if (response.code() == 200) {
                        List<RekamMedis.RekamMedisList> regData = response.body().getData();
                        for (RekamMedis.RekamMedisList rl: regData) {
                            String strKek = "Tidak";
                            if(rl.getIsKakiBengkak().equals("1")){
                                strKek = "Ya";
                            }
                            isKakiBengkak.getEditText().setText(strKek);

                            keluhan.getEditText().setText(rl.getKeluhan());
                            tekananDarah.getEditText().setText(rl.getTekananDarah());
                            beratBadan.getEditText().setText(rl.getBeratBadan());
                            umurKehamilan.getEditText().setText(rl.getUmurKehamilan());
                            tinggiFundus.getEditText().setText(rl.getTinggiFundus());
                            letakJanin.getEditText().setText(rl.getLetakJanin());
                            denyutJanin.getEditText().setText(rl.getDenyutJanin());
                            imunisasi.getEditText().setText(rl.getImunisasi());
                            tablet.getEditText().setText(rl.getTablet());
                            tataLaksana.getEditText().setText(rl.getTataLaksana());
                            hasilLab.getEditText().setText(rl.getHasilLab());
                            tindakan.getEditText().setText(rl.getTindakan());
                            nasihat.getEditText().setText(rl.getNasihat());
                            jadwalPeriksa.getEditText().setText(rl.getJadwalPeriksa());
                            createDropdown();
                        }
                    } else {
                        Gson gson = new Gson();
                        TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                        try {
                            if (response.errorBody() != null){
                                ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                                new Utilities().dialogModal(RekamMedisFormActivity.this, null, "Pesan", resBody.getMessage(), false);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RekamMedis> call, Throwable t) {
                    progress.dismiss();
                }
            });
        }
    }

    @OnClick(R.id.btn_submit)
    public void handleRekamMedis(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        final ProgressDialog progress = new ProgressDialog(RekamMedisFormActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        String strKek = "0";
        if(isKakiBengkak.getEditText().getText().toString().equals("Ya")){
            strKek = "1";
        }
        params.put("is_kaki_bengkak", strKek);
        params.put("keluhan", keluhan.getEditText().getText().toString());
        params.put("tekanan_darah", tekananDarah.getEditText().getText().toString());
        params.put("berat_badan", beratBadan.getEditText().getText().toString());
        params.put("umur_kehamilan", umurKehamilan.getEditText().getText().toString());
        params.put("tinggi_fundus", tinggiFundus.getEditText().getText().toString());
        params.put("letak_janin", letakJanin.getEditText().getText().toString());
        params.put("denyut_janin", denyutJanin.getEditText().getText().toString());
        params.put("imunisasi", imunisasi.getEditText().getText().toString());
        params.put("tablet", tablet.getEditText().getText().toString());
        params.put("tata_laksana", tataLaksana.getEditText().getText().toString());
        params.put("hasil_lab", hasilLab.getEditText().getText().toString());
        params.put("tindakan", tindakan.getEditText().getText().toString());
        params.put("nasihat", nasihat.getEditText().getText().toString());
        params.put("reg_id", String.valueOf(idRegistrasi));
        params.put("jadwal_periksa", jadwalPeriksa.getEditText().getText().toString());

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> call;
        if(idDetail > 0){
            params.put("id", String.valueOf(idDetail));
            call = service.editRekammedis(params);
        }else{
            call = service.addRekammedis(params);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    new Utilities().dialogModal(RekamMedisFormActivity.this, null, "Pesan", getString(R.string.message_success), true);
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(RekamMedisFormActivity.this, null, "Pesan", resBody.getMessage(), false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
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

    public void dialogModal(String title, String content){
        Dialog dialog = new Dialog(RekamMedisFormActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtTitle.setText(title);

        TextView txtClaimer = (TextView) dialog.findViewById(R.id.txt_caption);
        txtClaimer.setText(content);

        Button btnClose = (Button) dialog.findViewById(R.id.btn_dialog);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
//                Intent intent = new Intent(getApplicationContext(), RekamMedisActivity.class);
//                intent.putExtra("id", idRegistrasi);
//                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }

    @OnClick(R.id.btn_jadwal)
    public void handleJadwalPeriksa(){
        DialogFragment newFragment = new DatePickerFragment(jadwalPeriksa);
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
            String bulan = String.valueOf(month);
            if(month < 10){
                bulan = "0"+month;
            }
            String date = year+"-"+bulan+"-"+day;
            txtDate.getEditText().setText(date);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            finish();
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}