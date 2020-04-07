package com.mredrock.cyxbs.common.component;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.mredrock.cyxbs.common.R;

public class CyxbsToast {
    public static Toast makeText(Context context, @StringRes int resId, int duration)
            throws Resources.NotFoundException {
        return CyxbsToast.makeText(context, context.getResources().getText(resId), duration);
    }

    public static Toast makeText(@NonNull Context context,
                                 @NonNull CharSequence text, int duration) {
        Toast result = new Toast(context);

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
