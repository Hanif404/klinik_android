package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements Validator.ValidationListener{
    private Validator validator;
    private SessionManager sm;

    @BindView(R.id.txt_username)
    @NotEmpty
    TextInputLayout username;

    @BindView(R.id.txt_password)
    @NotEmpty
    TextInputLayout password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());

        sm = new SessionManager(this);
        if(sm.getPref("token") != ""){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @OnClick(R.id.btn_akun_baru)
    public void handleCreateAccount(){
        Intent intent = new Intent(getApplicationContext(), RegistrationActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_forgot)
    public void handleForgotPassword(){
        Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btn_login)
    public void handleLogin(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        final ProgressDialog progress = new ProgressDialog(LoginActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("username", username.getEditText().getText().toString());
        params.put("password", password.getEditText().getText().toString());

        ApiService service = ApiClient.getClient(getApplicationContext(), false).create(ApiService.class);
        Call<ResponseBody> callAdd = service.login(params);
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Gson gson = new Gson();
                TypeAdapter<ResponseWithData> adapterObj = gson.getAdapter(ResponseWithData.class);
                ResponseWithData resBodyObj;
                if(response.code() == 200){
                    try {
                        if (response.body() != null) {
                            progress.dismiss();

                            resBodyObj = adapterObj.fromJson(response.body().string());
                            JsonObject json = resBodyObj.getData();

                            sm.setPref("token", json.get("token").toString().replaceAll("\"", ""));
                            sm.setPref("user_id", json.get("id").toString().replaceAll("\"", ""));
                            String isBidan = json.get("is_bidan").toString().replaceAll("\"", "");

                            if(BuildConfig.FLAVOR.equals("user") && isBidan.equals("0")){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else if(BuildConfig.FLAVOR.equals("bidan") && isBidan.equals("1")){
                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                Toast.makeText(getApplicationContext(), getString(R.string.message_nouser), Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(response.code() == 409){
                    try {
                        if (response.errorBody() != null){
                            resBodyObj = adapterObj.fromJson(response.errorBody().string());
                            progress.dismiss();

                            JsonObject obj = resBodyObj.getData();
                            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                                String message = entry.getValue().toString().replaceAll("\"", "");

                                if(entry.getKey().equals("username")){
                                    username.setError(message);
                                }else if(entry.getKey().equals("password")){
                                    password.setError(message);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else if(response.code() == 404){
                    progress.dismiss();
                    Toast.makeText(getApplicationContext(), getString(R.string.message_nouser), Toast.LENGTH_SHORT).show();
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
}