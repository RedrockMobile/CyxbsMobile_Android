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

public class CyxbsToast {
    public static Toast makeText(Context context, @StringRes int resId, int duration) {
        return CyxbsToast.makeText(context, context.getResources().getText(resId), duration);
    }

    public static Toast makeText(@NonNull Context context,
                                 @NonNull CharSequence text, int duration) {
        Toast result = new Toast(context);
        if (BuildConfig.DEBUG) {
            Throwable throwable = new Throwable();
            StackTraceElement[] array = throwable.getStackTrace();
            ArrayList<StackTraceElement> list = new ArrayList<>();
            Collections.addAll(list, array);
            list.remove(0);
            ArrayList<String> list1 = new ArrayList<>();
            for (StackTraceElement element : list) {
                if (element.getClassName().startsWith("com.")
                  && element.getFileName() != null
                  && element.getFileName().endsWith(".kt"))
                {
                    list1.add("(" + element.getFileName() + ":" + element.getLineNumber() + ")");
                }
            }
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < list1.size() - 1; i++) {
                builder.append(list1.get(i))
                  .append(" <- ");
            }
            builder.append(list1.get(list1.size() - 1));
            Log.d("toast", "toast: text = " + text + "   path: " + builder.toString());
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
