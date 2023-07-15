package com.mredrock.cyxbs.api.dialog

/**
 * com.mredrock.cyxbs.api.dialog.DialogWebEvent.kt
 * CyxbsMobile_Android
 *
 * @author 寒雨
 * @since 2022/11/5 下午4:29
 */
/**
 * 暂时去掉密封类的使用，待R8完全支持后请改回来，原因：
 * 密封类在 D8 和 R8 编译器（产生错误的编译器）中不完全受支持。
 * 在 https://issuetracker.google.com/227160052 中跟踪完全支持的密封类。
 * D8支持将出现在Android Studio Electric Eel中，目前处于预览状态，而R8支持在更高版本之前不会出现
 * stackoverflow上的回答：
 * https://stackoverflow.com/questions/73453524/what-is-causing-this-error-com-android-tools-r8-internal-nc-sealed-classes-are#:~:text=This%20is%20due%20to%20building%20using%20JDK%2017,that%20the%20dexer%20won%27t%20be%20able%20to%20handle.
 */
interface DialogWebEvent {
    data class Custom(val command: String) : DialogWebEvent
    data class Download(val url: String, val fileName: String) : DialogWebEvent
    data class SavePic(val url: String) : DialogWebEvent
}