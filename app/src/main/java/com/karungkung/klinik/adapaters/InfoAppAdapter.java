package com.karungkung.klinik.adapaters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.karungkung.klinik.R;
import com.karungkung.klinik.domains.InfoApp;

import java.util.List;

/**
 * Created by hanif on 23/09/18.
 */

public class InfoAppAdapter extends RecyclerView.Adapter<InfoAppAdapter.MyViewHolder> {
    private List<InfoApp> dataList;
    private Context mContext;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, subtitle;
        private ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.label_title);
            subtitle = (TextView) view.findViewById(R.id.label_subtitle);
            icon = (ImageView) view.findViewById(R.id.label_icon);
        }
    }

    public InfoAppAdapter(Context mContext, List<InfoApp> dataList) {
        this.mContext = mContext;
        this.dataList = dataList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_info_app, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        InfoApp pf = dataList.get(position);
        holder.title.setText(pf.getTitle());
        holder.subtitle.setText(pf.getSubtitle());
        holder.icon.setImageResource(pf.getIcon());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
