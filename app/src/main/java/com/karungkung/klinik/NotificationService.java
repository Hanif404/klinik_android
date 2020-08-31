package com.karungkung.klinik;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.karungkung.klinik.domains.Konsultasi;
import com.karungkung.klinik.domains.RekamMedis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by hanif on 11/06/20.
 */

public class NotificationService extends Service{
    private Context context = this;
    private SessionManager sm;
    private static final String NOTIFICATION_CHANNEL = "com.karungkung.klinik.service";
    public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private List<Integer> notifPeriksaView = new ArrayList<Integer>();
    private List<Integer> notifKonsultasiView = new ArrayList<Integer>();

    @Override
    public void onCreate() {
        super.onCreate();
        sm = new SessionManager(context);
        notifPeriksaView.clear();
        notifKonsultasiView.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(runnable, 10000);
        notifPeriksaView.clear();
        notifKonsultasiView.clear();
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onTaskRemoved(Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        notifPeriksaView.clear();
        notifKonsultasiView.clear();
    }

    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if(BuildConfig.FLAVOR.equals("user")){
                notifPemeriksaan();
            }
            notifKonsultasi();
            handler.postDelayed(this, 10000);
        }
    };

    private void notifPemeriksaan(){
        ApiService service = ApiClient.getClient(context, true).create(ApiService.class);
        Call<RekamMedis> call = service.getLastRekammedis();
        call.enqueue(new Callback<RekamMedis>() {
            @Override
            public void onResponse(Call<RekamMedis> call, Response<RekamMedis> response) {
                if(response.code() == 200){
                    List<RekamMedis.RekamMedisList> snList = response.body().getData();

                    for (RekamMedis.RekamMedisList sn: snList) {
                        if(!notifPeriksaView.contains(sn.getId())){
                            notifPeriksaView.add(sn.getId());
                            notificationPemeriksaanBuild(sn);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<RekamMedis> call, Throwable t){ }
        });
    }

    private void notificationPemeriksaanBuild(RekamMedis.RekamMedisList sn){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle("Pemeriksaan")
                .setContentText(getString(R.string.message_notif_pemeriksaan))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "KLINIK_CHANNEL", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert manager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            manager.createNotificationChannel(notificationChannel);
        }
        mBuilder.setContentText(getString(R.string.message_notif_pemeriksaan));

        Notification notification = mBuilder.build();
        manager.notify(1, notification);
    }

    private void notifKonsultasi(){
        ApiService service = ApiClient.getClient(context, true).create(ApiService.class);
        Call<Konsultasi> call = service.getKonsultasiUser(sm.getPref("user_id"));
        call.enqueue(new Callback<Konsultasi>() {
            @Override
            public void onResponse(Call<Konsultasi> call, Response<Konsultasi> response) {
                if(response.code() == 200){
                    List<Konsultasi.KonsultasiList> snList = response.body().getData();

                    for (Konsultasi.KonsultasiList sn: snList) {
                        if(!notifKonsultasiView.contains(sn.getId())) {
                            if(!sn.getUserId().equals(sm.getPref("user_id"))){
                                notifKonsultasiView.add(sn.getId());
                                notificationKonsultasiBuild(sn);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Konsultasi> call, Throwable t){ }
        });
    }
    private void notificationKonsultasiBuild(Konsultasi.KonsultasiList sn){
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Intent intent = new Intent(getApplicationContext(), KonsultasiActivity.class);
        intent.putExtra("id", sn.getIdReg());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);;

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_notifications_active)
                .setContentTitle("Konsultasi")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mBuilder.setSound(uri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "KLINIK_CHANNEL", importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            assert manager != null;
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID);
            manager.createNotificationChannel(notificationChannel);
        }

        mBuilder.setContentText("Anda mendapatkan pesan dari "+ sn.getTxtName());

        Notification notification = mBuilder.build();
        manager.notify(2, notification);
    }
}
