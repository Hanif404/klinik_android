package com.karungkung.klinik.adapaters;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.karungkung.klinik.BuildConfig;
import com.karungkung.klinik.R;
import com.karungkung.klinik.domains.Registrasi;
import com.karungkung.klinik.domains.RekamMedis;

import java.util.List;

/**
 * Created by hanif on 16/04/20.
 */

public class RekamMedisAdapter extends RecyclerView.Adapter<RekamMedisAdapter.MyViewHolder> {
    private List<RekamMedis.RekamMedisList> dataList;
    private Activity mContext;
    private RekamMedisAdapter.ClickListener onClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        public Button btnEdit, btnDelete;
        public TextView kakiBengkak, keluhan,tekananDarah, umurKehamilan,beratBadan, imunisasi,letakJanin, denyutJanin,tinggiFundus, nasihat;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.txt_title);
            subtitle = (TextView) view.findViewById(R.id.txt_subtitle);

            kakiBengkak = (TextView) view.findViewById(R.id.field_is_kaki_bengkak);
            keluhan = (TextView) view.findViewById(R.id.field_keluhan);
            tekananDarah = (TextView) view.findViewById(R.id.field_tekanan_darah);
            umurKehamilan = (TextView) view.findViewById(R.id.field_umur_kehamilan);
            beratBadan = (TextView) view.findViewById(R.id.field_berat_badan);
            imunisasi = (TextView) view.findViewById(R.id.field_imunisasi);
            letakJanin = (TextView) view.findViewById(R.id.field_letak_janin);
            denyutJanin = (TextView) view.findViewById(R.id.field_denyut_janin);
            tinggiFundus = (TextView) view.findViewById(R.id.field_tinggi_fundus);
            nasihat = (TextView) view.findViewById(R.id.field_nasihat);
            btnEdit = (Button) view.findViewById(R.id.btn_edit);
            btnDelete = (Button) view.findViewById(R.id.btn_delete);

            view.findViewById(R.id.btn_edit).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickEdit(v, getAdapterPosition());
                }
            });
            view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.onClickDelete(v, getAdapterPosition());
                }
            });
        }
    }

    public RekamMedisAdapter(Activity mContext, List<RekamMedis.RekamMedisList> dataList, RekamMedisAdapter.ClickListener listener) {
        this.mContext = mContext;
        this.dataList = dataList;

        onClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_rekammedis, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        RekamMedis.RekamMedisList dt = dataList.get(position);
        int i = position + 1;
        holder.title.setText("Rekam No. "+ 1);
        holder.subtitle.setText("Bidan "+dt.getNameBidan()+"\nTgl. "+dt.getCreateDate());

        String strKaki = "tidak";
        if(dt.getIsKakiBengkak().equals("1")){
            strKaki="ya";
        }
        holder.kakiBengkak.setText(strKaki);
        holder.keluhan.setText(dt.getKeluhan());
        holder.tekananDarah.setText(dt.getTekananDarah());
        holder.umurKehamilan.setText(dt.getUmurKehamilan() +" minggu");
        holder.beratBadan.setText(dt.getBeratBadan() +" kg");
        holder.imunisasi.setText(dt.getImunisasi() +" / "+ dt.getTablet());
        holder.letakJanin.setText(dt.getLetakJanin());
        holder.denyutJanin.setText(dt.getDenyutJanin());
        holder.tinggiFundus.setText(dt.getTinggiFundus());
        holder.nasihat.setText(dt.getNasihat());

        if(BuildConfig.FLAVOR.equals("user")){
            holder.btnEdit.setVisibility(View.GONE);
            holder.btnDelete.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public interface ClickListener{
        void onClickEdit(View v, int position);
        void onClickDelete(View v, int position);
    }

    public void updateListItems(List<RekamMedis.RekamMedisList> ol) {
        final RekamMedisDiffCallBack diffCallback = new RekamMedisDiffCallBack(this.dataList, ol);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        diffResult.dispatchUpdatesTo(this);
        this.dataList.clear();
        this.dataList.addAll(ol);
    }
}
