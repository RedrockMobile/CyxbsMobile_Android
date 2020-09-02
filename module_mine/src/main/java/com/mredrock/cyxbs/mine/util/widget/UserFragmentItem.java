package com.mredrock.cyxbs.mine.util.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.mredrock.cyxbs.common.component.JCardViewPlus;
import com.mredrock.cyxbs.mine.R;


/**
 * Created By jay68 on 2018/5/2.
 */
public class UserFragmentItem extends JCardViewPlus {
    private View remindIcon;
    private TextView title;
    private ImageView icon;

    public UserFragmentItem(Context context) {
        this(context, null);
    }

    public UserFragmentItem(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public UserFragmentItem(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        LayoutInflater.from(context).inflate(R.layout.mine_layout_user_fragment_item, this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.UserFragmentItem);
        icon = findViewById(R.id.mine_user_item_icon);
        icon.setImageResource(typedArray.getResourceId(R.styleable.UserFragmentItem_itemIcon, 0));
        title = findViewById(R.id.mine_user_item_title);
        title.setText(typedArray.getString(R.styleable.UserFragmentItem_itemTitle));
        remindIcon = findViewById(R.id.mine_user_item_remindIcon);
        boolean showRemindIcon = typedArray.getBoolean(R.styleable.UserFragmentItem_showRemindIcon, false);
        setRemindIconShowing(showRemindIcon);
        setShadowColor(Color.parseColor("#0d000000"));
        typedArray.recycle();
    }

    public boolean isRemindIconShowing() {
        return remindIcon.getVisibility() == VISIBLE;
    }

    public void setRemindIconShowing(boolean remindIconShowing) {
        if (remindIconShowing) remindIcon.setVisibility(VISIBLE);
        else remindIcon.setVisibility(GONE);
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setIcon(Drawable icon) {
        this.icon.setImageDrawable(icon);
    }
}
