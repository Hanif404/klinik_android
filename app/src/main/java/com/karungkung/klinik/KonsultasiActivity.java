package com.karungkung.klinik;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.karungkung.klinik.domains.Konsultasi;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.intentservice.chatui.ChatView;
import co.intentservice.chatui.models.ChatMessage;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static co.intentservice.chatui.models.ChatMessage.Type.RECEIVED;
import static co.intentservice.chatui.models.ChatMessage.Type.SENT;

public class KonsultasiActivity extends AppCompatActivity {

    @BindView(R.id.chat_view)
    ChatView chatView;

    private int idRegistrasi;
    private SessionManager sm = new SessionManager(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_konsultasi);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            idRegistrasi = bundle.getInt("id");
        }

        getSupportActionBar().setTitle("Chatting");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        chatView.setOnSentMessageListener(new ChatView.OnSentMessageListener(){
            @Override
            public boolean sendMessage(ChatMessage chatMessage){
                saveChat(chatMessage);
                return true;
            }
        });
        prepareList();
    }

    private void saveChat(ChatMessage message){
        ProgressDialog progress = new ProgressDialog(KonsultasiActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        // TODO user id ngambil dari scan barcode
        params.put("user_id", sm.getPref("user_id"));
        params.put("comment", message.getMessage());
        params.put("create_date", String.valueOf(message.getTimestamp()));
        params.put("reg_id", String.valueOf(idRegistrasi));

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> call = service.addKonsultasi(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
                if(response.code() != 200){
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
                            new Utilities().dialogModal(KonsultasiActivity.this, null, "Pesan", resBody.getMessage(), false);
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

    private void editReadChat(){
        ProgressDialog progress = new ProgressDialog(KonsultasiActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        HashMap<String, String> params = new HashMap<>();
        params.put("user_id", sm.getPref("user_id"));
        params.put("reg_id", String.valueOf(idRegistrasi));

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<ResponseBody> call = service.readKonsultasi(params);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progress.dismiss();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    private void prepareList() {
        ProgressDialog progress = new ProgressDialog(KonsultasiActivity.this);
        progress.setTitle("Loading");
        progress.setMessage(getString(R.string.message_loading));
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();

        ApiService service = ApiClient.getClient(getApplicationContext(), true).create(ApiService.class);
        Call<Konsultasi> call = service.getKonsultasi(idRegistrasi);
        call.enqueue(new Callback<Konsultasi>() {
            @Override
            public void onResponse(Call<Konsultasi> call, Response<Konsultasi> response) {
                progress.dismiss();
                if (response.code() == 200) {
                    List<Konsultasi.KonsultasiList> regData = response.body().getData();
                    for (Konsultasi.KonsultasiList kl: regData) {
                        if(kl.getUserId().equals(sm.getPref("user_id"))){
                            ChatMessage message = new ChatMessage(kl.getTxtComment(), kl.getCreateDate(), SENT);
                            chatView.addMessage(message);
                        }else{
                            ChatMessage message = new ChatMessage(kl.getTxtName()+"\n"+kl.getTxtComment(), kl.getCreateDate(), RECEIVED);
                            chatView.addMessage(message);
                        }
                    }
                    editReadChat();
                } else {
                    Gson gson = new Gson();
                    TypeAdapter<ResponseDataObj> adapter = gson.getAdapter(ResponseDataObj.class);
                    try {
                        if (response.errorBody() != null){
                            ResponseDataObj resBody = adapter.fromJson(response.errorBody().string());
//                            new Utilities().dialogModal(KonsultasiActivity.this, null, "Pesan", resBody.getMessage(), false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Konsultasi> call, Throwable t) {
                progress.dismiss();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //Handle the back button
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        prepareList();
    }
}