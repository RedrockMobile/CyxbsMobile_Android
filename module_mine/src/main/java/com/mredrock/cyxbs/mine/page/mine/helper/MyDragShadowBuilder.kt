package com.mredrock.cyxbs.mine.page.mine.helper

import android.graphics.Canvas
import android.graphics.Point
import android.util.Log
import android.view.View


class MyDragShadowBuilder( view:View): View.DragShadowBuilder(view) {
    private var mScaleFactor: Point? = null

//
//    override fun onProvideShadowMetrics(outShadowSize: Point?, outShadowTouchPoint: Point?) {
//        var width: Int
//        var heigth: Int
//// 设置拖动阴影的宽度/高度为原宽/高度的1.8倍
//        width =  (getView().getWidth() * 1.8).toInt()
//
//        heigth =  (getView().getHeight() * 1.8).toInt()
//// 设置拖动阴影图像的宽度和高度
//        outShadowSize!!.set(width, heigth);
//// 设置手指在拖动图像的位置 设置为中点
//        outShadowTouchPoint!!.set(width / 2, heigth / 2);
//        mScaleFactor = outShadowSize;
//
//        Log.e("wxtagss","(MyDragShadowBuilder.kt:32)->> ")
//
//
//    }

    override fun onDrawShadow(canvas: Canvas?) {
       // canvas!!.scale(mScaleFactor!!.x/view.width.toFloat(),mScaleFactor!!.y/view.height.toFloat())
       Log.e("wxtagss","(MyDragShadowBuilder.kt:32)->> ")
        view.draw(canvas)

    }





}