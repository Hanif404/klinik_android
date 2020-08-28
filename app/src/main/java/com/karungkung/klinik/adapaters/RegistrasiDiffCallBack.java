package com.karungkung.klinik.adapaters;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import com.karungkung.klinik.domains.Registrasi;

import java.util.List;


public class RegistrasiDiffCallBack extends DiffUtil.Callback {
    private final List<Registrasi.RegistrasiList> mOldTaskList;
    private final List<Registrasi.RegistrasiList> mNewTaskList;

    public RegistrasiDiffCallBack(List<Registrasi.RegistrasiList> mOldTaskList, List<Registrasi.RegistrasiList> mNewTaskList) {
        this.mOldTaskList = mOldTaskList;
        this.mNewTaskList = mNewTaskList;
    }

    @Override
    public int getOldListSize() {
        return mOldTaskList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewTaskList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldTaskList.get(oldItemPosition).getId() == mNewTaskList.get(newItemPosition).getId();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldTaskList.get(oldItemPosition).equals(mNewTaskList.get(newItemPosition));
    }

    @Nullable
    @Override
    public Object getChangePayload(int oldItemPosition, int newItemPosition) {
        // Implement method if you're going to use ItemAnimator
        return super.getChangePayload(oldItemPosition, newItemPosition);
    }
}
