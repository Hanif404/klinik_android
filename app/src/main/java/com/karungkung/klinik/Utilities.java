package com.karungkung.klinik;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class Utilities {

    public void dialogModal(Activity activity, Class tujuan, String title, String content, boolean backpage){
        Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_view);

        TextView txtTitle = (TextView) dialog.findViewById(R.id.txt_title);
        txtTitle.setText(title);

        TextView txtClaimer = (TextView) dialog.findViewById(R.id.txt_caption);
        txtClaimer.setText(content);

        Button btnClose = (Button) dialog.findViewById(R.id.btn_dialog);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if(backpage){
                    Intent intent = new Intent(activity, tujuan);
                    activity.startActivity(intent);
                    activity.finish();
                }
            }
        });

        dialog.show();
    }
}
