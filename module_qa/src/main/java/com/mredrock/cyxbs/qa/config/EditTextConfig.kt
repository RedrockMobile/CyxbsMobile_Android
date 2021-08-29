package com.mredrock.cyxbs.qa.config

/**
 * Author by OkAndGreat，Date on 2021/8/29.
 *
 */
object EditTextConfig {
    /**
     * This view does not want keystrokes.
     * Use with {@link #setFocusable(int)} and <a href="#attr_android:focusable">{@code
     * android:focusable}.
     */
    const val NOT_FOCUSABLE = 0x00000000

    /**
     * This view wants keystrokes.
     * Use with {@link #setFocusable(int)} and <a href="#attr_android:focusable">{@code
     * android:focusable}.
     */
    const val FOCUSABLE = 0x00000001

    /**
     * This view determines focusability automatically. This is the default.
     * Use with {@link #setFocusable(int)} and <a href="#attr_android:focusable">{@code
     * android:focusable}.
     */
    const val FOCUSABLE_AUTO = 0x00000010
}