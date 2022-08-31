package com.mredrock.cyxbs.lib.base.utils

import android.os.Bundle
import android.os.IBinder
import android.os.Parcelable
import android.util.Size
import android.util.SizeF
import androidx.core.os.bundleOf
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 这里面所有的类型参考了官方的设计：[bundleOf]
 */
class ArgumentHelper<T: Any>(
  private val arguments: () -> Bundle
) : ReadWriteProperty<Any, T> {
    
    private var mValue: T? = null
    
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
      if (mValue != null) return mValue!!
      mValue = arguments.invoke().get(property.name) as T
      return mValue!!
    }
    
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
      val name = property.name
      arguments.invoke().apply {
        when (value) {
          is String -> putString(name, value) // 优先判断 String
          
          // Scalars
          is Int -> putInt(name, value)
          is Boolean -> putBoolean(name, value)
          is Byte -> putByte(name, value)
          is Char -> putChar(name, value)
          is Double -> putDouble(name, value)
          is Float -> putFloat(name, value)
          is Long -> putLong(name, value)
          is Short -> putShort(name, value)
  
          // References
          is Bundle -> putBundle(name, value)
          is CharSequence -> putCharSequence(name, value)
          is Parcelable -> putParcelable(name, value)
  
          // Scalar arrays
          is BooleanArray -> putBooleanArray(name, value)
          is ByteArray -> putByteArray(name, value)
          is CharArray -> putCharArray(name, value)
          is DoubleArray -> putDoubleArray(name, value)
          is FloatArray -> putFloatArray(name, value)
          is IntArray -> putIntArray(name, value)
          is LongArray -> putLongArray(name, value)
          is ShortArray -> putShortArray(name, value)
  
          // Reference arrays
          is Array<*> -> {
            val componentType = value::class.java.componentType!!
            @Suppress("UNCHECKED_CAST") // Checked by reflection.
            when {
              Parcelable::class.java.isAssignableFrom(componentType) -> {
                putParcelableArray(name, value as Array<Parcelable>)
              }
              String::class.java.isAssignableFrom(componentType) -> {
                putStringArray(name, value as Array<String>)
              }
              CharSequence::class.java.isAssignableFrom(componentType) -> {
                putCharSequenceArray(name, value as Array<CharSequence>)
              }
              Serializable::class.java.isAssignableFrom(componentType) -> {
                putSerializable(name, value)
              }
              else -> {
                val valueType = componentType.canonicalName
                throw IllegalArgumentException(
                  "非法数组类型 $valueType for key \"$name\""
                )
              }
            }
          }
          // Last resort. Also we must check this after Array<*> as all arrays are serializable.
          is Serializable -> putSerializable(name, value)
          is IBinder -> putBinder(name, value)
          is Size -> putSize(name, value)
          is SizeF -> putSizeF(name, value)
          else -> error("未实现该类型 ${value::class.java.name} for key \"$name\"")
        }
      }
      mValue = value
    }
  }