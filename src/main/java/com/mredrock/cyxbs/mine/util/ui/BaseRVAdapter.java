package com.mredrock.cyxbs.mine.util.ui;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zia on 2018/8/17.
 * 通用adapter
 */
public abstract class BaseRVAdapter<D> extends RecyclerView.Adapter {

    private static final int NORMAL = 0;
    private static final int FOOTER = 20000;
    private List<D> datas = new ArrayList<>();

    abstract @LayoutRes
    protected int getNormalLayout();

    private View footerView = null;

    public void loadData(List<D> dataList) {
        datas.addAll(dataList);
        notifyItemRangeInserted(datas.size(), dataList.size());
    }

    public void clear(){
        datas.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER) {
            return new FooterHolder(footerView);
        } else {
            return new DataHolder(LayoutInflater.from(parent.getContext()).inflate(getNormalLayout(), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == FOOTER) {
            bindFooterHolder(holder, position);
        } else {
            bindDataHolder(holder, position, datas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        int count = datas.size();
        if (footerView != null)
            count++;
        return count;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1 && footerView != null) {
            return FOOTER;
        } else {
            return NORMAL;
        }
    }

    protected void setFooterView(View footerView) {
        if (footerView != null) {
            this.footerView = footerView;
        }
    }

    protected abstract void bindFooterHolder(@NonNull RecyclerView.ViewHolder holder, int position);

    protected abstract void bindDataHolder(@NonNull RecyclerView.ViewHolder holder, int position, D data);

    class DataHolder extends RecyclerView.ViewHolder {

        public DataHolder(View itemView) {
            super(itemView);
        }
    }

    class FooterHolder extends RecyclerView.ViewHolder {

        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    public List<D> getDataList() {
        return datas;
    }
}
