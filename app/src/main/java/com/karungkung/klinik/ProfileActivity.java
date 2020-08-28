package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.karungkung.klinik.adapaters.InfoAppAdapter;
import com.karungkung.klinik.domains.InfoApp;
import com.karungkung.klinik.domains.Profil;
import com.karungkung.klinik.listeners.InfoAppListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileActivity extends AppCompatActivity {
    @BindView(R.id.list_menu_app)
    RecyclerView rvApp;

    @BindView(R.id.txt_nama)
    TextView txtName;

    @BindView(R.id.txt_email)
    TextView txtEmail;

    @BindView(R.id.img_profile)
    CircleImageView fotoProfile;

    private SessionManager sm = new SessionManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        prepareInfoApp();
        prepareData();
    }

    private void prepareData(){
        final ProgressDialog progress = new ProgressDialog(ProfileActivity.this);
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

                    String imgUrl = ApiClient.BASE_URL + getString(R.string.path_assets) + profil.get(0).getFileImage();
                    Glide.with(getApplicationContext()).load(imgUrl).apply(requestOptions).into(fotoProfile);
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), resBody.getMessage(), Toast.LENGTH_LONG).show();
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

    @Override
    protected void onResume() {
        super.onResume();

        prepareData();
    }

    private void prepareInfoApp() {
        List<InfoApp> infoappList = new ArrayList<>();
        InfoApp pf;


        pf = new InfoApp("Data diri", "Detail data diri pengguna", R.drawable.ic_person);
        infoappList.add(pf);

        pf = new InfoApp("Ganti kata kunci", "Mengganti password dengan yang baru", R.drawable.ic_lock);
        infoappList.add(pf);

        pf = new InfoApp("Tentang aplikasi", "Versi "+ BuildConfig.VERSION_NAME, R.drawable.ic_phone);
        infoappList.add(pf);

        pf = new InfoApp("Keluar aplikasi", "Untuk keluar dari aplikasi", R.drawable.ic_sign_out);
        infoappList.add(pf);

        InfoAppAdapter infoAppAdapter = new InfoAppAdapter(getApplicationContext(), infoappList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rvApp.setLayoutManager(mLayoutManager);
        rvApp.setItemAnimator(new DefaultItemAnimator());
        rvApp.setAdapter(infoAppAdapter);

        DividerItemDecoration itemDecor = new DividerItemDecoration(rvApp.getContext(), DividerItemDecoration.VERTICAL);
        rvApp.addItemDecoration(itemDecor);

        rvApp.addOnItemTouchListener(new InfoAppListener(getApplicationContext(), rvApp, new InfoAppListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(position == 0){
                    if(BuildConfig.FLAVOR.equals("bidan")){
                        dialogDataDiriBidan();
                    }else{
                        dialogDataDiri();
                    }
                } else if(position == 1){
                    Intent intent = new Intent(getApplicationContext(), ChangePasswordActivity.class);
                    startActivity(intent);
                } else if(position == 3){
                    sm.setPref("token", "");
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onLongClick(View view, final int position) {

            }
        }));
    }

    private void dialogDataDiri(){
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottom_sheet_profile, null);

        prepareDataView(view);

        view.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), ProfileformActivity.class);
                startActivity(intent);
            }
        });

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

    private void prepareDataView(View view){
        final ProgressDialog progress = new ProgressDialog(ProfileActivity.this);
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

                    CircleImageView viewFotoProfile = (CircleImageView) view.findViewById(R.id.img_profile);

                    TextView viewTxtName = (TextView) view.findViewById(R.id.field_nama);
                    viewTxtName.setText(profil.get(0).getName());

                    TextView viewTxtNameKeluarga = (TextView) view.findViewById(R.id.field_nama_keluarga);
                    viewTxtNameKeluarga.setText(profil.get(0).getNamaSuami());

                    TextView viewTxtTglLahir = (TextView) view.findViewById(R.id.field_tgl_lahir);
                    viewTxtTglLahir.setText(profil.get(0).getTgllahir());

                    TextView viewTxtAddress = (TextView) view.findViewById(R.id.field_address);
                    viewTxtAddress.setText(profil.get(0).getAddress());

                    TextView viewTxtPendidikan = (TextView) view.findViewById(R.id.field_pendidikan);
                    viewTxtPendidikan.setText(profil.get(0).getPendidikan());

                    TextView viewTxtJobibu = (TextView) view.findViewById(R.id.field_jobs_ibu);
                    viewTxtJobibu.setText(profil.get(0).getJobIstri());

                    TextView viewTxtJobsuami = (TextView) view.findViewById(R.id.field_jobs_suami);
                    viewTxtJobsuami.setText(profil.get(0).getJobSuami());

                    TextView viewTxtAgama = (TextView) view.findViewById(R.id.field_agama);
                    viewTxtAgama.setText(profil.get(0).getAgama());

                    TextView viewTxtGoldar = (TextView) view.findViewById(R.id.field_goldar);
                    viewTxtGoldar.setText(profil.get(0).getGoldar());

                    TextView viewTxtEmail = (TextView) view.findViewById(R.id.field_email);
                    viewTxtEmail.setText(profil.get(0).getEmail());

                    TextView viewTxtPhone = (TextView) view.findViewById(R.id.field_phone);
                    viewTxtPhone.setText(profil.get(0).getPhone());

                    RequestOptions requestOptions = new RequestOptions();
                    requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                    requestOptions.override(800, 400);
                    requestOptions.skipMemoryCache(true);
                    requestOptions.placeholder(R.drawable.no_profile);

                    String imgUrl = ApiClient.BASE_URL + getString(R.string.path_assets) + profil.get(0).getFileImage();
                    Glide.with(getApplicationContext()).load(imgUrl).apply(requestOptions).into(viewFotoProfile);
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            Toast.makeText(getApplicationContext(), resBody.getMessage(), Toast.LENGTH_LONG).show();
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

    private void dialogDataDiriBidan(){
        Intent intent = new Intent(getApplicationContext(), ProfileformbidanActivity.class);
        startActivity(intent);
    }
}