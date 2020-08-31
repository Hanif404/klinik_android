package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Password;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import okhttp3.internal.Util;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity implements Validator.ValidationListener {
    @BindView(R.id.txt_old_password)
    @NotEmpty
    TextInputLayout oldpass;

    @BindView(R.id.txt_new_password)
    @NotEmpty
    @Password(min = 6, scheme = Password.Scheme.NUMERIC)
    TextInputLayout pass;

    @BindView(R.id.txt_renew_password)
    @NotEmpty
    @ConfirmPassword
    TextInputLayout retypePass;

    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());
    }

    @OnClick(R.id.btn_change)
    public void handleChangePass(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        final ProgressDialog progress = new ProgressDialog(ChangePasswordActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("old_password", oldpass.getEditText().getText().toString());
        params.put("password", pass.getEditText().getText().toString());

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> callAdd = service.changePassword(params);
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    new Utilities().dialogModal(ChangePasswordActivity.this, null,"Pesan", getString(R.string.message_success), true);
                } else if(response.code() == 409){
                    try {
                        Gson gson = new Gson();
                        TypeAdapter<ResponseWithData> adapterObj = gson.getAdapter(ResponseWithData.class);
                        ResponseWithData resBodyObj;
                        if (response.errorBody() != null){
                            resBodyObj = adapterObj.fromJson(response.errorBody().string());
                            progress.dismiss();

                            JsonObject obj = resBodyObj.getData();
                            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                                String message = entry.getValue().toString().replaceAll("\"", "");

                                if(entry.getKey().equals("old_password")){
                                    oldpass.setError(message);
                                }else if(entry.getKey().equals("password")){
                                    pass.setError(message);
                                }
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if(response.code() == 404){
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