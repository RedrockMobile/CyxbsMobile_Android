package com.mredrock.cyxbs.lib.base.utils

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import androidx.core.os.bundleOf
import java.io.Serializable
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 这里面所有的类型参考了官方的设计：[bundleOf]
 */
class IntentHelper<T: Any>(
  private val clazz: Class<T>,
  private val intent: () -> Intent
) : ReadWriteProperty<Any, T> {
    
    private var mValue: T? = null
    
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
      if (mValue != null) return mValue!!
      val name = property.name
      mValue = intent.invoke().run {
        // 因为 intent 的 getExtra() 方法被废弃，所以只能一个一个判断
        when (clazz) {
          String::class -> getStringExtra(name)
  
          Int::class -> getIntExtra(name, Int.MIN_VALUE)
          Boolean::class -> getBooleanExtra(name, false)
          Byte::class -> getByteExtra(name, Byte.MIN_VALUE)
          Char::class -> getCharExtra(name, ' ')
          Double::class -> getDoubleExtra(name, Double.MIN_VALUE)
          Float::class -> getFloatExtra(name, Float.MIN_VALUE)
          Long::class -> getLongExtra(name, Long.MIN_VALUE)
          Short::class -> getShortExtra(name, Short.MIN_VALUE)
          
          Bundle::class -> getBundleExtra(name)
          BooleanArray::class -> getBooleanArrayExtra(name)
          ByteArray::class -> getByteArrayExtra(name)
          CharArray::class -> getCharArrayExtra(name)
          DoubleArray::class -> getDoubleArrayExtra(name)
          FloatArray::class -> getFloatArrayExtra(name)
          IntArray::class -> getIntArrayExtra(name)
          LongArray::class -> getLongArrayExtra(name)
          ShortArray::class -> getShortArrayExtra(name)
          
          Array::class -> {
            val componentType = clazz.componentType!!
            when {
              Parcelable::class.java.isAssignableFrom(componentType) -> {
                getParcelableArrayExtra(name)
              }
              String::class.java.isAssignableFrom(componentType) -> {
                getStringArrayExtra(name)
              }
              CharSequence::class.java.isAssignableFrom(componentType) -> {
                getCharSequenceArrayExtra(name)
              }
              Serializable::class.java.isAssignableFrom(componentType) -> {
                getSerializableExtra(name)
              }
              else -> {
                val valueType = componentType.canonicalName
                throw IllegalArgumentException(
                  "非法数组类型 $valueType for key \"$name\""
                )
              }
            }
          }
          
          else -> {
            when {
              CharSequence::class.java.isAssignableFrom(clazz) -> getCharExtra(name, ' ')
              Parcelable::class.java.isAssignableFrom(clazz) -> getParcelableExtra(name)
              Serializable::class.java.isAssignableFrom(clazz) -> getSerializableExtra(name)
              else -> error("未实现该类型 ${clazz.name} for key \"$name\"")
            }
          }
        }
      } as T
      return mValue!!
    }
    
    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
      val name = property.name
      intent.invoke().apply {
        when (value) {
          is String -> putExtra(name, value) // 优先判断 String
          
          // Scalars
          is Int -> putExtra(name, value)
          is Boolean -> putExtra(name, value)
          is Byte -> putExtra(name, value)
          is Char -> putExtra(name, value)
          is Double -> putExtra(name, value)
          is Float -> putExtra(name, value)
          is Long -> putExtra(name, value)
          is Short -> putExtra(name, value)
    
          // References
          is Bundle -> putExtra(name, value)
          is CharSequence -> putExtra(name, value)
          is Parcelable -> putExtra(name, value)
    
          // Scalar arrays
          is BooleanArray -> putExtra(name, value)
          is ByteArray -> putExtra(name, value)
          is CharArray -> putExtra(name, value)
          is DoubleArray -> putExtra(name, value)
          is FloatArray -> putExtra(name, value)
          is IntArray -> putExtra(name, value)
          is LongArray -> putExtra(name, value)
          is ShortArray -> putExtra(name, value)
    
          // Reference arrays
          is Array<*> -> {
            val componentType = value::class.java.componentType!!
            @Suppress("UNCHECKED_CAST") // Checked by reflection.
            when {
              Parcelable::class.java.isAssignableFrom(componentType) -> {
                putExtra(name, value as Array<Parcelable>)
              }
              String::class.java.isAssignableFrom(componentType) -> {
                putExtra(name, value as Array<String>)
              }
              CharSequence::class.java.isAssignableFrom(componentType) -> {
                putExtra(name, value as Array<CharSequence>)
              }
              Serializable::class.java.isAssignableFrom(componentType) -> {
                putExtra(name, value)
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
          is Serializable -> putExtra(name, value)
          else -> error("未实现该类型 ${value::class.java.name} for key \"$name\"")
        }
      }
      mValue = value
    }
  }