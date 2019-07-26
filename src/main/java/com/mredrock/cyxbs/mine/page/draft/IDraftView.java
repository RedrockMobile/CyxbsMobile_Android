package com.mredrock.cyxbs.mine.page.draft;

import androidx.annotation.NonNull;

import com.mredrock.cyxbs.mine.network.model.Draft;

/**
 * Created by zia on 2018/9/10.
 */
public interface IDraftView {
    void initData();

    void loadMore();

    void deleteDraft(@NonNull Draft draft);

    void showEmpty();

    void showRv();

    void onClickItem(@NonNull Draft draft, int position);
}
