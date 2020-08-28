package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.karungkung.klinik.adapaters.RegistrasiAdapter;
import com.karungkung.klinik.adapaters.RekamMedisAdapter;
import com.karungkung.klinik.domains.Registrasi;
import com.karungkung.klinik.domains.RekamMedis;

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

public class RekamMedisActivity extends AppCompatActivity {
    @BindView(R.id.rv_rekam_medis)
    RecyclerView rvRekammedis;

    @BindView(R.id.fab_rekam_medis)
    FloatingActionButton fabRekammedis;

    private RekamMedisAdapter rekamMedisAdapter;
    private int idRegistrasi = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rekam_medis);
        ButterKnife.bind(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(BuildConfig.FLAVOR.equals("user")){
            fabRekammedis.setVisibility(View.GONE);
        }
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idRegistrasi = bundle.getInt("id");
        }
        prepareList();
    }

    private void prepareList() {
        ProgressDialog progress = new ProgressDialog(RekamMedisActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<RekamMedis> call = service.getDataRekammedis(idRegistrasi);
        call.enqueue(new Callback<RekamMedis>() {
            @Override
            public void onResponse(Call<RekamMedis> call, Response<RekamMedis> response) {
                progress.dismiss();
                if (response.code() == 200) {
                    rekamMedisAdapter = new RekamMedisAdapter(RekamMedisActivity.this, response.body().getData(), new RekamMedisAdapter.ClickListener() {
                        @Override
                        public void onClickEdit(View v, int position) {
                            RekamMedis.RekamMedisList rmList = response.body().getData().get(position);
                            Intent intent = new Intent(RekamMedisActivity.this, RekamMedisFormActivity.class);
                            intent.putExtra("id", rmList.getId());
                            intent.putExtra("reg_id", idRegistrasi);
                            startActivity(intent);
                        }

                        @Override
                        public void onClickDelete(View v, int position) {
                            RekamMedis.RekamMedisList rmList = response.body().getData().get(position);
                            handleDelete(rmList.getId());
                        }
                    });

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                    rvRekammedis.setLayoutManager(mLayoutManager);
                    rvRekammedis.setItemAnimator(new DefaultItemAnimator());
                    rvRekammedis.setAdapter(rekamMedisAdapter);
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(RekamMedisActivity.this, null, "Pesan", resBody.getMessage(), false);
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

    private void handleDelete(Integer id){
        final ProgressDialog progress = new ProgressDialog(RekamMedisActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("id", String.valueOf(id));

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> callAdd = service.delRekammedis(params);
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();// put your code here...

                if(response.code() == 200){
                    //reload registrasi
                    reloadList();
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(RekamMedisActivity.this, null, "Pesan", resBody.getMessage(), false);
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
        ProgressDialog progress = new ProgressDialog(RekamMedisActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<RekamMedis> call = service.getDataRekammedis(idRegistrasi);
        call.enqueue(new Callback<RekamMedis>() {
            @Override
            public void onResponse(Call<RekamMedis> call, Response<RekamMedis> response) {
                progress.dismiss();
                if (response.code() == 200) {
                    rekamMedisAdapter.updateListItems(response.body().getData());
                }else{
                    rekamMedisAdapter.updateListItems(new ArrayList<>());
                }
            }

            @Override
            public void onFailure(Call<RekamMedis> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    @OnClick(R.id.fab_rekam_medis)
    public void handleAddRekamMedis(){
        Intent intent = new Intent(RekamMedisActivity.this, RekamMedisFormActivity.class);
        intent.putExtra("reg_id", idRegistrasi);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}