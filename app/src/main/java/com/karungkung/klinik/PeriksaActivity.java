package com.karungkung.klinik;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.karungkung.klinik.adapaters.RegistrasiAdapter;
import com.karungkung.klinik.domains.Registrasi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PeriksaActivity extends AppCompatActivity {

    @BindView(R.id.topAppBar)
    MaterialToolbar topAppBar;

    @BindView(R.id.rv_registrasi)
    RecyclerView rvRegistrasi;

    @BindView(R.id.fab_registrasi)
    FloatingActionButton fabRegistrasi;

    private RegistrasiAdapter registrasiAdapter;
    private int idUser, typeMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periksa);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idUser = bundle.getInt("user_id");
            typeMenu = bundle.getInt("type_menu");
        }

        if(typeMenu == 1){
            topAppBar.setTitle("Registrasi");
            if(BuildConfig.FLAVOR.equals("user")){
                fabRegistrasi.setVisibility(View.GONE);
            }
        }else if(typeMenu == 2){
            topAppBar.setTitle("Konsultasi");
            fabRegistrasi.setVisibility(View.GONE);
        }

        topAppBar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int id = item.getItemId();
                if (id == R.id.action_close) {
                    if(BuildConfig.FLAVOR.equals("bidan") && typeMenu == 1){
                        new MaterialAlertDialogBuilder(PeriksaActivity.this)
                                .setTitle("Keluar")
                                .setMessage("Apakah anda telah menyelesaikan pemeriksaan?")
                                .setNegativeButton("Tidak", null)
                                .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        finish();
                                    }
                                }).show();
                    }else{
                        finish();
                    }
                    return true;
                }
                return false;
            }
        });
        prepareList();
    }

    private void prepareList() {
        ProgressDialog progress = new ProgressDialog(PeriksaActivity.this);
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
                    registrasiAdapter = new RegistrasiAdapter(PeriksaActivity.this, response.body().getData(), typeMenu, new RegistrasiAdapter.ClickListener() {
                        @Override
                        public void onClickRekammedis(View v, int position) {
                            Registrasi.RegistrasiList regList = response.body().getData().get(position);
                            Intent intent = new Intent(PeriksaActivity.this, RekamMedisActivity.class);
                            intent.putExtra("id", regList.getId());
                            startActivity(intent);
                        }

                        @Override
                        public void onClickDetail(View v, int position) {
                            Registrasi.RegistrasiList regList = response.body().getData().get(position);
                            detailDialog(String.valueOf(regList.getId()));
                        }

                        @Override
                        public void onClickKonsultasi(View v, int position) {
                            Registrasi.RegistrasiList regList = response.body().getData().get(position);
                            if(typeMenu == 1){
                                //Registrasi
                                Integer isAktif = 1;
                                if(regList.getIsKonsultasi().equals("1")){
                                    isAktif = 0;
                                }
                                isKonsultasi(String.valueOf(regList.getId()), String.valueOf(isAktif));
                            }else if(typeMenu == 2){
                                //Konsultasi
                                Intent intent = new Intent(PeriksaActivity.this, KonsultasiActivity.class);
                                intent.putExtra("id", regList.getId());
                                startActivity(intent);
                            }
                        }
                    });

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvRegistrasi.setLayoutManager(mLayoutManager);
                    rvRegistrasi.setItemAnimator(new DefaultItemAnimator());
                    rvRegistrasi.setAdapter(registrasiAdapter);
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(PeriksaActivity.this, null, "Pesan", resBody.getMessage(), false);
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

    private void detailDialog(String id){
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_registrasi, null);

        loadDataDetail(view, dialog, id);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        if(BuildConfig.FLAVOR.equals("user")){
            view.findViewById(R.id.btn_edit).setVisibility(View.GONE);
            view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        dialog.setContentView(view);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {

                // In a previous life I used this method to get handles to the positive and negative buttons
                // of a dialog in order to change their Typeface. Good ol' days.

                BottomSheetDialog d = (BottomSheetDialog) dialog;

                // This is gotten directly from the source of BottomSheetDialog
                // in the wrapInBottomSheet() method
                FrameLayout bottomSheet = (FrameLayout) d.findViewById(com.google.android.material.R.id.design_bottom_sheet);

                // Right here!
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void loadDataDetail(View view, BottomSheetDialog dialog, String id){
        final ProgressDialog progress = new ProgressDialog(PeriksaActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<Registrasi> call;
        if(BuildConfig.FLAVOR.equals("bidan")){
            // TODO user id ngambil dari scan barcode
            call = service.getDetailRegBidan(String.valueOf(idUser), id);
        }else{
            call = service.getDetailRegistrasi(id);
        }
        call.enqueue(new Callback<Registrasi>() {
            @Override
            public void onResponse(Call<Registrasi> call, Response<Registrasi> response) {
                progress.dismiss();
                if(response.code() == 200){
                    List<Registrasi.RegistrasiList> registrasi = response.body().getData();
                    TextView viewTxt1 = (TextView) view.findViewById(R.id.field_hamil_ke);
                    viewTxt1.setText(registrasi.get(0).getHamilKe());

                    TextView viewTxt2 = (TextView) view.findViewById(R.id.field_jml_persalinan);
                    viewTxt2.setText(registrasi.get(0).getJmlPersalinan());

                    TextView viewTxt3 = (TextView) view.findViewById(R.id.field_jml_keguguran);
                    viewTxt3.setText(registrasi.get(0).getJmlKeguguran());

                    TextView viewTxt4 = (TextView) view.findViewById(R.id.field_jml_ank_hidup);
                    viewTxt4.setText(registrasi.get(0).getJmlAnkHidpu());

                    TextView viewTxt5 = (TextView) view.findViewById(R.id.field_jml_ank_mati);
                    viewTxt5.setText(registrasi.get(0).getJmlAnakMati());

                    TextView viewTxt6 = (TextView) view.findViewById(R.id.field_jml_ank_lr_kr_bln);
                    viewTxt6.setText(registrasi.get(0).getJmlAnkKurangBulan());

                    TextView viewTxt7 = (TextView) view.findViewById(R.id.field_jrk_hamil_dr_akhir);
                    viewTxt7.setText(registrasi.get(0).getJrkHamil());

                    TextView viewTxt8 = (TextView) view.findViewById(R.id.field_imunisasi_tt);
                    viewTxt8.setText(registrasi.get(0).getImunisasiTT());

                    TextView viewTxt9 = (TextView) view.findViewById(R.id.field_cara_salin_akhir);
                    viewTxt9.setText(registrasi.get(0).getCaraSalinAkhir());

                    TextView viewTxt10 = (TextView) view.findViewById(R.id.field_penolong_salin_akhir);
                    viewTxt10.setText(registrasi.get(0).getPenolongSalinAkhir());

                    TextView viewTxt11 = (TextView) view.findViewById(R.id.field_hpht);
                    viewTxt11.setText(registrasi.get(0).getHpht());

                    TextView viewTxt12 = (TextView) view.findViewById(R.id.field_htp);
                    viewTxt12.setText(registrasi.get(0).getHtp());

                    TextView viewTxt13 = (TextView) view.findViewById(R.id.field_is_kek);
                    viewTxt13.setText(registrasi.get(0).getIsKek());

                    TextView viewTxt14 = (TextView) view.findViewById(R.id.field_lingkar_lengan_atas);
                    viewTxt14.setText(registrasi.get(0).getLingkarLenganAtas());

                    TextView viewTxt15 = (TextView) view.findViewById(R.id.field_tinggi_bd);
                    viewTxt15.setText(registrasi.get(0).getTinggiBadan());

                    TextView viewTxt16 = (TextView) view.findViewById(R.id.field_kontrasepsi_blm_hamil);
                    viewTxt16.setText(registrasi.get(0).getKontrasepsi());

                    TextView viewTxt17 = (TextView) view.findViewById(R.id.field_rw_penyakit);
                    viewTxt17.setText(registrasi.get(0).getRwPenyakit());

                    TextView viewTxt18 = (TextView) view.findViewById(R.id.field_rw_alergi);
                    viewTxt18.setText(registrasi.get(0).getRwAlergi());

                    view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            Intent intent = new Intent(PeriksaActivity.this, RegistrasiFormActivity.class);
                            intent.putExtra("id", registrasi.get(0).getId());
                            intent.putExtra("user_id", idUser);
                            startActivity(intent);
                        }
                    });

                    view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                            handleDelete(registrasi.get(0).getId());
                        }
                    });
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(PeriksaActivity.this, null, "Pesan", resBody.getMessage(), false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Registrasi> call, Throwable t) {
                progress.dismiss();
                Log.d("DEBUG KLINIK",  t.getMessage());
            }
        });
    }

    private void handleDelete(Integer id){
        final ProgressDialog progress = new ProgressDialog(PeriksaActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(id));

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> callAdd = service.delRegistrasi(params);
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    //reload registrasi
                    reloadList();
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(PeriksaActivity.this, null, "Pesan", resBody.getMessage(), false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
                Log.d("DEBUG KLINIK",  t.getMessage());
            }
        });
    }

    private void reloadList(){
        ProgressDialog progress = new ProgressDialog(PeriksaActivity.this);
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
                    registrasiAdapter.updateListItems(response.body().getData());
                }else{
                    registrasiAdapter.updateListItems(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<Registrasi> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    private void isKonsultasi(String id, String isAktif){
        final ProgressDialog progress = new ProgressDialog(PeriksaActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("is_konsultasi", isAktif);
        params.put("id", id);

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> call = service.editRegistrasi(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    reloadList();
                    String message = "Konsultasi tidak aktif";
                    if(isAktif.equals("1")){
                        message = "Konsultasi aktif";
                    }
                    new Utilities().dialogModal(PeriksaActivity.this, null, "Pesan", message, false);
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(PeriksaActivity.this, null, "Pesan", resBody.getMessage(), false);
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

    @OnClick(R.id.fab_registrasi)
    public void handleAddReg(){
        Intent intent = new Intent(PeriksaActivity.this, RegistrasiFormActivity.class);
        intent.putExtra("user_id", idUser);
        startActivity(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            //Ask the user if they want to quit
            if(typeMenu == 1){
                new MaterialAlertDialogBuilder(this)
                        .setTitle("Keluar")
                        .setMessage("Apakah anda telah menyelesaikan pemeriksaan?")
                        .setNegativeButton("Tidak", null)
                        .setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        }).show();
            }else{
                finish();
            }
            return true;
        }
        else {
            return super.onKeyDown(keyCode, event);
        }

    }
}