package com.mredrock.cyxbs.noclass.util

import android.util.Log
import java.util.*

/**
 *
 * @ProjectName:    CyxbsMobile_Android
 * @Package:        com.mredrock.cyxbs.noclass.util
 * @ClassName:      LogUtil
 * @Author:         Yan
 * @CreateDate:     2022年09月06日 23:07:00
 * @UpdateRemark:   更新说明：
 * @Version:        1.0
 * @Description:    测试用Log类 为了解决日志过长无法打印完的问题
 */

object LogUtil{
  /**
   * 打印日志到控制台（解决Android控制台丢失长日志记录）
   *
   * @param priority
   * @param tag
   * @param content
   */
  fun print(priority: Int, tag: String?, content: String, maxLine : Int? = null) {
    // 1. 测试控制台最多打印4062个字节，不同情况稍有出入（注意：这里是字节，不是字符！！）
    // 2. 字符串默认字符集编码是utf-8，它是变长编码一个字符用1~4个字节表示
    // 3. 这里字符长度小于1000，即字节长度小于4000，则直接打印，避免执行后续流程，提高性能哈
    if (content.length < 1000) {
      Log.println(priority, tag, content)
      return
    }
    
    // 一次打印的最大字节数
    val maxByteNum = 4000
    
    // 字符串转字节数组
    var bytes = content.toByteArray()
    
    // 超出范围直接打印
    if (maxByteNum >= bytes.size) {
      Log.println(priority, tag, content)
      return
    }
    
    // 分段打印计数
    var count = 1
    
    // 在数组范围内，则循环分段
    while (maxByteNum < bytes.size) {
      // 按字节长度截取字符串
      val subStr = cutStr(bytes, maxByteNum)
      
      // 打印日志
      val desc = String.format("分段打印(%s):%s", count++, subStr)
      Log.println(priority, tag, desc)
      
      // 截取出尚未打印字节数组
      bytes = bytes.copyOfRange(subStr!!.toByteArray().size, bytes.size)
      
      // 可根据需求添加一个次数限制，避免有超长日志一直打印
      if (count == maxLine) { break }
    }
    
    // 打印剩余部分
    Log.println(priority, tag, String.format("分段打印(%s):%s", count, String(bytes)))
  }
  
  
  /**
   * 按字节长度截取字节数组为字符串
   *
   * @param bytes
   * @param subLength
   * @return
   */
  fun cutStr(bytes: ByteArray?, subLength: Int): String? {
    // 边界判断
    if (bytes == null || subLength < 1) {
      return null
    }
    
    // 超出范围直接返回
    if (subLength >= bytes.size) {
      return String(bytes)
    }
    
    // 复制出定长字节数组，转为字符串
    val subStr = String(Arrays.copyOf(bytes, subLength))
    
    // 避免末尾字符是被拆分的，这里减1使字符串保持完整
    return subStr.substring(0, subStr.length - 1)
  }
}
