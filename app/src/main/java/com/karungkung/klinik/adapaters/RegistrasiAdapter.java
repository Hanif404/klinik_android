package com.karungkung.klinik.adapaters;

import android.app.Activity;

import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.karungkung.klinik.BuildConfig;
import com.karungkung.klinik.R;
import com.karungkung.klinik.domains.Registrasi;

import java.util.List;

/**
 * Created by hanif on 16/04/20.
 */

public class RegistrasiAdapter extends RecyclerView.Adapter<RegistrasiAdapter.MyViewHolder> {
    private List<Registrasi.RegistrasiList> dataList;
    private Activity mContext;
    private RegistrasiAdapter.ClickListener onClickListener;
    private int typeMenu;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        public Button btnKonsultasi, btnRekamMedis, btnView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_title);
            subtitle = (TextView) view.findViewById(R.id.txt_subtitle);
            btnKonsultasi = (Button) view.findViewById(R.id.btn_konsultasi);
            btnRekamMedis = (Button) view.findViewById(R.id.btn_rekam_medis);
            btnView = (Button) view.findViewById(R.id.btn_view);

            view.findViewById(R.id.btn_rekam_medis).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickRekammedis(v, getAdapterPosition());
                }
            });
            view.findViewById(R.id.btn_view).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickDetail(v, getAdapterPosition());
                }
            });
            view.findViewById(R.id.btn_konsultasi).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickKonsultasi(v, getAdapterPosition());
                }
            });
        }
    }

    public RegistrasiAdapter(Activity mContext, List<Registrasi.RegistrasiList> dataList, int typeMenu, RegistrasiAdapter.ClickListener listener) {
        this.mContext = mContext;
        this.dataList = dataList;
        onClickListener = listener;
        this.typeMenu = typeMenu;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_registrasi, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Registrasi.RegistrasiList dt = dataList.get(position);
        if(typeMenu == 2){
            //Konsultasi
            holder.title.setText(dt.getNamaPengguna());
            holder.btnView.setVisibility(View.GONE);
            holder.btnRekamMedis.setVisibility(View.GONE);
            if(dt.getIsKonsultasi().equals("1")){
                holder.btnKonsultasi.setVisibility(View.VISIBLE);
                holder.btnKonsultasi.setText("Chatting");
                holder.subtitle.setText("Konsultasi aktif");
            }else{
                holder.btnKonsultasi.setVisibility(View.GONE);
                holder.subtitle.setText("Konsultasi tidak aktif");
            }
        }else{
            //Registrasi
            holder.title.setText("Data Reg #"+ dt.getId());
            String message = "tidak aktif";
            if(dt.getIsKonsultasi().equals("1")){
                message = "aktif";
            }
            holder.subtitle.setText(dt.getCreateDate()+", Konsultasi : "+message);

            if(BuildConfig.FLAVOR.equals("user")) {
                holder.btnKonsultasi.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ClickListener{
        void onClickRekammedis(View v, int position);
        void onClickDetail(View v, int position);
        void onClickKonsultasi(View v, int position);
    }

    public void updateListItems(List<Registrasi.RegistrasiList> ol) {
        final RegistrasiDiffCallBack diffCallback = new RegistrasiDiffCallBack(this.dataList, ol);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        diffResult.dispatchUpdatesTo(this);

        this.dataList.clear();
        this.dataList.addAll(ol);
    }
}
