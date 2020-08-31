package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.annotation.NotEmpty;
import com.mobsandgeeks.saripaar.annotation.Pattern;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SetWebserviceActivity extends AppCompatActivity implements Validator.ValidationListener{
    @BindView(R.id.txt_host)
    @NotEmpty
    TextInputLayout txtHost;

    SessionManager sm = new SessionManager(this);
    private Validator validator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_webservice);
        ButterKnife.bind(this);

        if(!sm.getPref("base_url").equals("")){
            Intent intent = new Intent(SetWebserviceActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        validator = new Validator(this);
        validator.setValidationListener(this);
        validator.registerAdapter(TextInputLayout.class, new TextInputLayoutAdapter());
    }

    @OnClick(R.id.btn_host)
    public void handleHost(){
        validator.validate();
    }

    @Override
    public void onValidationSucceeded() {
        sm.setPrefWebservice("base_url", txtHost.getEditText().getText().toString());
        Intent intent = new Intent(SetWebserviceActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
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