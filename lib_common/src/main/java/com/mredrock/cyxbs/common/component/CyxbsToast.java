package com.mredrock.cyxbs.common.component;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.mredrock.cyxbs.common.BuildConfig;
import com.mredrock.cyxbs.common.R;

import java.util.ArrayList;
import java.util.Collections;

/**
 * 这个类如果改成了 kt 会报错，跟使用了 Context.toast() 扩展函数有关，所以暂时不要改
 */
public class CyxbsToast {
    public static Toast makeText(Context context, @StringRes int resId, int duration) {
        return CyxbsToast.makeText(context, context.getResources().getText(resId), duration);
    }

    public static Toast makeText(@NonNull Context context,
                                 @NonNull CharSequence text, int duration) {
        Toast result = new Toast(context);
        if (BuildConfig.DEBUG) {
            // 以下逻辑与 lib_utils 模块中的 Toast.kt 保持一致，注释请看那个类
            Throwable throwable = new Throwable();
            StackTraceElement[] array = throwable.getStackTrace();
            ArrayList<StackTraceElement> list = new ArrayList<>();
            Collections.addAll(list, array);
            list.remove(0);
            StringBuilder builder = new StringBuilder();
            boolean isFoundFirst = false;
            // 下面这些是用于定位堆栈信息
            for (StackTraceElement it : list) {
                if (!it.isNativeMethod()
                  && it.getFileName() != null
                  && it.getFileName().endsWith(".kt")
                  && it.getClassName().startsWith("com.")
                ) {
                    if (isFoundFirst) {
                        builder.append("(")
                            .append(it.getFileName())
                              .append(":")
                                .append(it.getLineNumber())
                                  .append(")")
                                    .append(" <- "); // 最后一个也会添加上，懒得删了
                    } else {
                        isFoundFirst = !it.getClassName().contains(".base.")
                          && !it.getFileName().startsWith("Base")
                          && !it.getFileName().matches("[tT]oast");
                    }
                }
            }
            Log.d("toast", "toast: text = " + text + "   path: " + builder);
        }
        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.common_cyxbs_toast, null);
        TextView tv = (TextView) v.findViewById(R.id.tv_cyxbs_toast);
        tv.setText(text);

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        result.setView(v);
        result.setDuration(duration);
        result.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, point.y / 8);

        return result;
    }
}
