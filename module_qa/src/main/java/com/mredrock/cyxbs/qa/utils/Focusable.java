package com.mredrock.cyxbs.qa.utils;

import static com.mredrock.cyxbs.qa.config.EditTextConfig.FOCUSABLE;
import static com.mredrock.cyxbs.qa.config.EditTextConfig.FOCUSABLE_AUTO;
import static com.mredrock.cyxbs.qa.config.EditTextConfig.NOT_FOCUSABLE;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Author by OkAndGreat，Date on 2021/8/29.
 */
@IntDef({NOT_FOCUSABLE, FOCUSABLE, FOCUSABLE_AUTO})
@Retention(RetentionPolicy.SOURCE)
public @interface Focusable {}