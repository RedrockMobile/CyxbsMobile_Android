package com.cyxbsmobile_single.module_todo.adapter;

import com.super_rabbit.wheel_picker.WheelAdapter;

import org.jetbrains.annotations.NotNull;

/**
 * Author: RayleighZ
 * Time: 2021-08-30 20:58
 * kt抽风，用kotlin写了报错，迫不得已用java写
 */
public abstract class BaseWheelAdapter implements WheelAdapter {

    private final int size;

    public BaseWheelAdapter(int size){
        this.size = size;
    }

    @Override
    public int getMaxIndex() {
        return size - 1;
    }

    @Override
    public int getMinIndex() {
        return 0;
    }

    @Override
    public int getPosition(@NotNull String s) {
        return 0;
    }

    @NotNull
    @Override
    public String getValue(int i) {
        if (i < 0) {
            return getPositivePosition((size + i) % size);
        }
        return getPositivePosition(i % size);
    }

    public abstract String getPositivePosition(int position);
}
