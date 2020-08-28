package com.karungkung.klinik;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

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

public class RegistrationActivity extends AppCompatActivity implements Validator.ValidationListener{

    @BindView(R.id.img_profile)
    CircleImageView imgProfile;

    @BindView(R.id.txt_nama_lengkap)
    @NotEmpty
    TextInputLayout name;

    @BindView(R.id.txt_email)
    @NotEmpty
    TextInputLayout email;

    @BindView(R.id.txt_address)
    @NotEmpty
    TextInputLayout address;

    @BindView(R.id.txt_phone)
    @NotEmpty
    TextInputLayout phone;

    @BindView(R.id.txt_password)
    @NotEmpty
    @Password(min = 6, scheme = Password.Scheme.NUMERIC)
    TextInputLayout pass;

    private Validator validator;
    private SessionManager sm;
    private ResponseWithData resBody;
    public static final int REQUEST_IMAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());

        sm = new SessionManager(this);
        sm.setPref("foto_profile", "");
    }

    @OnClick(R.id.img_profile)
    public void handleToPickimage() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        } else {
                            // TODO - handle permission denied case
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getApplicationContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getApplicationContext(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getParcelableExtra("path");
                File fileCrop = new File(uri.getPath());
                sm.setPref("foto_profile", uri.getPath());

                RequestOptions requestOptions = new RequestOptions();
                requestOptions.diskCacheStrategy(DiskCacheStrategy.NONE);
                requestOptions.override(800, 400);
                requestOptions.skipMemoryCache(true);
                requestOptions.placeholder(R.drawable.no_profile);

                Glide.with(getApplicationContext()).load(fileCrop).apply(requestOptions).into(imgProfile);
            }
        }
    }

    @OnClick(R.id.btn_registrasi)
    public void handleToRegistration(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        if(sm.getPref("foto_profile").length() > 0){
            final ProgressDialog progress = new ProgressDialog(RegistrationActivity.this);
            progress.setTitle("Loading");
            progress.setMessage(getString(R.string.message_loading));
            progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
            progress.show();

            HashMap<String, String> params = new HashMap<>();
            params.put("email", email.getEditText().getText().toString());
            params.put("address", address.getEditText().getText().toString());
            params.put("name", name.getEditText().getText().toString());
            params.put("phone", phone.getEditText().getText().toString());
            params.put("password", pass.getEditText().getText().toString());
            if(BuildConfig.FLAVOR.equals("bidan")){
                params.put("is_bidan", "1");
            }else{
                params.put("is_bidan", "0");
            }

            ApiService service = ApiClient.getClient(getApplicationContext(), false).create(ApiService.class);
            Call<ResponseBody> callAdd = service.registration(params);
            callAdd.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    Gson gson = new Gson();
                    if(response.code() == 201){
                        TypeAdapter<ResponseWithData> adapter = gson.getAdapter(ResponseWithData.class);
                        try {
                            if (response.body() != null){
                                resBody = adapter.fromJson(response.body().string());
                                String id = "";

                                JsonObject obj = resBody.getData();
                                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                                    id = entry.getValue().toString().replaceAll("\"", "");
                                }

                                File fileUpload = new File(sm.getPref("foto_profile"));
                                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileUpload);
                                MultipartBody.Part body = MultipartBody.Part.createFormData("foto_profile", fileUpload.getName(), requestFile);

                                Call<ResponseBody> callUpload = service.uploadFoto(body, id);
                                callUpload.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        progress.dismiss();
                                        if(response.code() == 201){
                                            dialogSuccessUpload();
                                        }else if(response.code() == 409){
                                            TypeAdapter<ResponseDataStr> adapter = gson.getAdapter(ResponseDataStr.class);
                                            try {
                                                if (response.errorBody() != null){
                                                    ResponseDataStr resBodyUpload = adapter.fromJson(response.errorBody().string());
                                                    String message = resBodyUpload.getData().replaceAll("\"", "");
                                                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }else if(response.code() == 409){
                        TypeAdapter<ResponseWithData> adapter = gson.getAdapter(ResponseWithData.class);
                        try {
                            if (response.errorBody() != null){
                                resBody = adapter.fromJson(response.errorBody().string());
                                progress.dismiss();

                                JsonObject obj = resBody.getData();
                                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                                    String message = entry.getValue().toString().replaceAll("\"", "");

                                    if(entry.getKey().equals("name")){
                                        name.setError(message);
                                    }else if(entry.getKey().equals("email")){
                                        email.setError(message);
                                    }else if(entry.getKey().equals("password")){
                                        pass.setError(message);
                                    }else if(entry.getKey().equals("phone")){
                                        phone.setError(message);
                                    }else if(entry.getKey().equals("address")){
                                        address.setError(message);
                                    }
                                }
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
        }else{
            Toast.makeText(getApplicationContext(), getString(R.string.message_noupload), Toast.LENGTH_SHORT).show();
        }
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

    private void dialogSuccessUpload(){
        Dialog dialog = new Dialog(RegistrationActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtTitle.setText("Registrasi");

        TextView txtClaimer = (TextView) dialog.findViewById(R.id.txt_caption);
        txtClaimer.setText("Akun Anda berhasil terdaftar");

        Button btnClose = (Button) dialog.findViewById(R.id.btn_dialog);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        dialog.show();
    }
}