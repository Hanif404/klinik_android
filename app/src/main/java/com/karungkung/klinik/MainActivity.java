package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.karungkung.klinik.domains.Antrian;
import com.karungkung.klinik.domains.Klinik;
import com.karungkung.klinik.domains.Profil;
import com.karungkung.klinik.domains.RekamMedis;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.topAppBar)
    MaterialToolbar topAppBar;

    @BindView(R.id.txt_nama)
    TextView txtName;

    @BindView(R.id.txt_email)
    TextView txtEmail;

    @BindView(R.id.txt_antrian)
    TextView txtAntrian;

    @BindView(R.id.txt_info_content)
    TextView txtInfoContent;

    @BindView(R.id.img_profile)
    CircleImageView fotoProfile;

    @BindView(R.id.btn_antrian)
    Button btnAntrian;

    @BindView(R.id.btn_open_klinik)
    Button btnOpenKlinik;

    @BindView(R.id.btn_close_klinik)
    Button btnCloseKlinik;

    @BindView(R.id.lay_info)
    RelativeLayout layInfoPemeriksaan;

    @BindView(R.id.lay_konsultasi_bidan)
    RelativeLayout layKonsultasiBidan;

    @BindView(R.id.lay_user)
    LinearLayout layUser;

    SessionManager sm = new SessionManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Intent notif = new Intent(this, NotificationService.class);
        startService(notif);

        if(BuildConfig.FLAVOR.equals("bidan")){
            txtAntrian.setVisibility(View.GONE);
            btnAntrian.setVisibility(View.GONE);
            btnOpenKlinik.setVisibility(View.VISIBLE);
            layKonsultasiBidan.setVisibility(View.VISIBLE);
            layInfoPemeriksaan.setVisibility(View.GONE);
            layUser.setVisibility(View.GONE);
            getDataKlinik();
        }else{
            btnAntrian.setVisibility(View.VISIBLE);
            txtAntrian.setVisibility(View.VISIBLE);
            getDataAntrian();
        }

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.action_profile) {
                    Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(intent);
                    return true;
                }else if(id == R.id.action_barcode){
                    if(BuildConfig.FLAVOR.equals("bidan")){
                        new IntentIntegrator(MainActivity.this).initiateScan();
                    }else{
                        dialogGetBarcode();
                    }
                    return true;
                }
                return false;
            }
        });

        prepareData();
    }

    private void prepareData(){
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<Profil> callAdd = service.getProfile();
        callAdd.enqueue(new Callback<Profil>() {
            @Override
            public void onResponse(Call<Profil> call, Response<Profil> response) {
                progress.dismiss();
                if(response.code() == 200){
                    List<Profil.ProfileList> profil = response.body().getData();

                    txtName.setText(profil.get(0).getName());
                    txtEmail.setText(profil.get(0).getEmail());

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                    requestOptions.override(800, 400);
                    requestOptions.skipMemoryCache(true);
                    requestOptions.placeholder(R.drawable.no_profile);

                    String imgUrl = sm.getPref("base_url") + getString(R.string.path_assets) + profil.get(0).getFileImage();
                    Glide.with(getApplicationContext()).load(imgUrl).apply(requestOptions).into(fotoProfile);

                    getDataJadwalPemeriksaan();
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(MainActivity.this, null, "Pesan", resBody.getMessage(), false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Profil> call, Throwable t) {
                progress.dismiss();
                Log.d("DEBUG KLINIK",  t.getMessage());
            }
        });
    }

    @OnClick(R.id.btn_antrian)
    public void handleAntrian(){
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> callAdd = service.getNoAntrian();
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                Gson gson = new Gson();
                if(response.code() == 200){
                    getDataAntrian();
                }else if(response.code() == 404){
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(MainActivity.this, null, "Pesan", resBody.getMessage(), false);
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

    private void getDataAntrian(){
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<Antrian> callAdd = service.getDataAntrian();
        callAdd.enqueue(new Callback<Antrian>() {
            @Override
            public void onResponse(Call<Antrian> call, Response<Antrian> response) {
                progress.dismiss();
                if(response.code() == 200){
                    btnAntrian.setVisibility(View.GONE);

                    List<Antrian.AntrianList> antrian = response.body().getData();
                    txtAntrian.setText("No. Antrian : "+ antrian.get(0).getNoUrut());
                }else{
                    btnAntrian.setVisibility(View.VISIBLE);
                    txtAntrian.setText("No. Antrian : -");
                }
            }

            @Override
            public void onFailure(Call<Antrian> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    private void getDataJadwalPemeriksaan(){
        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<RekamMedis> callAdd = service.getLastRekammedis();
        callAdd.enqueue(new Callback<RekamMedis>() {
            @Override
            public void onResponse(Call<RekamMedis> call, Response<RekamMedis> response) {
                if(response.code() == 200){
                    List<RekamMedis.RekamMedisList> rmlist = response.body().getData();
                    txtInfoContent.setText(rmlist.get(0).getJadwalPeriksa());
                }else{
                    txtInfoContent.setText("Belum ada Jadwal");
                }
            }

            @Override
            public void onFailure(Call<RekamMedis> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        getDataJadwalPemeriksaan();
    }

    private void getDataKlinik(){
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<Klinik> callAdd = service.getDataKlinik();
        callAdd.enqueue(new Callback<Klinik>() {
            @Override
            public void onResponse(Call<Klinik> call, Response<Klinik> response) {
                progress.dismiss();
                if(response.code() == 200){
                    List<Klinik.KlinikList> klinik = response.body().getData();
                    if(klinik.get(0).getIsClose().equals("1")){
                        sm.setPref("id_klinik","");

                        btnOpenKlinik.setVisibility(View.VISIBLE);
                        btnCloseKlinik.setVisibility(View.GONE);
                    }else{
                        sm.setPref("id_klinik", String.valueOf(klinik.get(0).getId()));

                        btnOpenKlinik.setVisibility(View.GONE);
                        btnCloseKlinik.setVisibility(View.VISIBLE);
                    }
                }else{
                    btnOpenKlinik.setVisibility(View.VISIBLE);
                    btnCloseKlinik.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Klinik> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    @OnClick(R.id.btn_close_klinik)
    public void handleCloseKlinik(){
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("is_close", "1");
        params.put("id", sm.getPref("id_klinik"));

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> callAdd = service.closeKlinik(params);
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    btnOpenKlinik.setVisibility(View.VISIBLE);
                    btnCloseKlinik.setVisibility(View.GONE);
                }else{
                    btnOpenKlinik.setVisibility(View.GONE);
                    btnCloseKlinik.setVisibility(View.VISIBLE);
                }
                getDataKlinik();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    @OnClick(R.id.btn_open_klinik)
    public void handleOpenKlinik(){
        final ProgressDialog progress = new ProgressDialog(MainActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("waktu_buka", "07:00:00");
        params.put("waktu_tutup", "16:00:00");
        params.put("is_close", "0");

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> callAdd = service.openKlinik(params);
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    btnOpenKlinik.setVisibility(View.GONE);
                    btnCloseKlinik.setVisibility(View.VISIBLE);
                }else{
                    btnOpenKlinik.setVisibility(View.VISIBLE);
                    btnCloseKlinik.setVisibility(View.GONE);
                }
                getDataKlinik();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    @OnClick(R.id.lay_periksa)
    public void handlePeriksa(){
        Intent intent = new Intent(MainActivity.this, PeriksaActivity.class);
        intent.putExtra("type_menu", 1);
        startActivity(intent);
    }

    @OnClick(R.id.lay_konsultasi)
    public void handleKonsultasi(){
        Intent intent = new Intent(MainActivity.this, PeriksaActivity.class);
        intent.putExtra("type_menu", 2);
        startActivity(intent);
    }

    @OnClick(R.id.lay_konsultasi_bidan)
    public void handleKonsultasiBidan(){
        Intent intent = new Intent(MainActivity.this, PeriksaActivity.class);
        intent.putExtra("type_menu", 2);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
            } else {
                Intent intent = new Intent(MainActivity.this, PeriksaActivity.class);
                intent.putExtra("user_id", Integer.valueOf(result.getContents()));
                intent.putExtra("type_menu", 1);
                startActivity(intent);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void dialogGetBarcode(){
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_barcode, null);

        try {
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.encodeBitmap(sm.getPref("user_id"), BarcodeFormat.QR_CODE, 400, 400);
            ImageView imageViewQrCode = (ImageView) view.findViewById(R.id.img_barcode);
            imageViewQrCode.setImageBitmap(bitmap);
        } catch(Exception e) {

        }

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);
        dialog.show();
    }
}