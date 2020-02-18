package com.mredrock.cyxbs.mine.util.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
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


    //设置新数据集，通过旧数据集和DiffUtil来添加动画
    public void setNewData(List<D> newData) {
        List<D> oldData = new ArrayList<>(datas);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldData.size();
            }

            @Override
            public int getNewListSize() {
                return newData.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldData.get(oldItemPosition) == newData.get(newItemPosition);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
            }
        });
        diffResult.dispatchUpdatesTo(this);
        //刷新FooterView,否则由于DiffUtil，不会刷新FooterView所在的位置
        notifyItemChanged(newData.size());
        datas = newData;
        notifyDataSetChanged();
    }

    public void delete(D deleteItem) {
        List<D> oldDatas = new ArrayList<>(datas);
        Iterator<D> iterator = datas.iterator();
        while (iterator.hasNext()) {
            D data = iterator.next();
            if (data.equals(deleteItem)) {
                iterator.remove();
            }
        }
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldDatas.size();
            }

            @Override
            public int getNewListSize() {
                return datas.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldDatas.get(oldItemPosition) == datas.get(newItemPosition);
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldDatas.get(oldItemPosition).equals(datas.get(newItemPosition));
            }
        });
        diffResult.dispatchUpdatesTo(this);
    }

    public void clear() {
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
