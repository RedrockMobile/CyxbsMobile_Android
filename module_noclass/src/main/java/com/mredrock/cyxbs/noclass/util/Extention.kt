package com.mredrock.cyxbs.noclass.util

fun Char.isChinese(): Boolean =
    Character.UnicodeScript.of(this.code) == Character.UnicodeScript.HAN

fun String.isAllChinese() : Boolean{
    val charArr = toCharArray()
    for (i in charArr.indices) {
       if(!charArr[i].isChinese()){
           return false
       }
    }
    return true
}