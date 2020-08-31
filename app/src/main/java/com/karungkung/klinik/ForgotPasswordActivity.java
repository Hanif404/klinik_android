package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity implements Validator.ValidationListener{
    @BindView(R.id.txt_username)
    @NotEmpty
    TextInputLayout username;

    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_forgot);
        ButterKnife.bind(this);

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());
    }

    @OnClick(R.id.btn_forgot)
    public void handleResetPass(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        final ProgressDialog progress = new ProgressDialog(ForgotPasswordActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("email", username.getEditText().getText().toString());

        ApiService service = ApiClient.getClient(getApplicationContext(), false).create(ApiService.class);
        Call<ResponseBody> callAdd = service.resetPassword(params);
        callAdd.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() == 200){
                    Toast.makeText(getApplicationContext(), "Password Berhasil direset", Toast.LENGTH_LONG).show();
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