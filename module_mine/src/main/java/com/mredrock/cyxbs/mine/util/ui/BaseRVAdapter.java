package com.mredrock.cyxbs.mine.util.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.mredrock.cyxbs.mine.util.widget.RvFooter;

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

    private RvFooter.State state = RvFooter.State.LOADING;

    abstract @LayoutRes
    protected int getNormalLayout();

    public void setState(RvFooter.State state) {
        if (this.state != state) {
            this.state = state;
            notifyItemChanged(datas.size());
        }
    }


    //设置新数据集，通过旧数据集和DiffUtil来添加动画
    public void setNewData(List<D> newData) {
        List<D> oldData = new ArrayList<>(datas);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldData.size() + 1;
            }

            @Override
            public int getNewListSize() {
                return newData.size() + 1;
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                if (oldItemPosition == oldData.size() || newItemPosition == newData.size()) {
                    return false;
                } else {
                    return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
                }
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                if (oldItemPosition == oldData.size() || newItemPosition == newData.size()) {
                    return false;
                } else {
                    return oldData.get(oldItemPosition).equals(newData.get(newItemPosition));
                }
            }
        });
        diffResult.dispatchUpdatesTo(this);
        datas = newData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == FOOTER) {
            RvFooter rvFooter = new RvFooter(parent.getContext());
            return new FooterHolder(rvFooter);
        } else {
            return new DataHolder(LayoutInflater.from(parent.getContext()).inflate(getNormalLayout(), parent, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == FOOTER) {
            ((RvFooter)holder.itemView).setState(state);
            bindFooterHolder(holder, position);
        } else {
            bindDataHolder(holder, position, datas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return FOOTER;
        } else {
            return NORMAL;
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
