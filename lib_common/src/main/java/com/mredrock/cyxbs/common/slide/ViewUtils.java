package com.mredrock.cyxbs.common.slide;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.View;

public final class ViewUtils {

    /**
     * try get Activity by bubble up through base context.
     *
     */
    public static Activity getActivity(View v) {
        Context c = v != null ? v.getContext() : null;
        while (c != null) {
            if (c instanceof Activity) {
                return (Activity) c;
            } else if (c instanceof ContextWrapper) {
                c = ((ContextWrapper) c).getBaseContext();
            } else {
                return null;
            }
        }
        return null;
    }

}
