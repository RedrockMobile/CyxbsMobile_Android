package com.mredrock.cyxbs.lib.utils.extensions

/**
 * .
 *
 * @author 985892345
 * 2023/3/1 14:53
 */

/**
 * 遍历每行并替换
 */
fun CharSequence.replaceLine(action: (lineIndex: Int, old: String) -> String): String {
  val oldList = split("\n")
  val newList = arrayListOf<String>()
  oldList.forEachIndexed { index, old ->
    val new = action.invoke(index, old)
    if (new.isNotEmpty()) {
      newList.add(new)
    }
  }
  return newList.joinToString(separator = "\n")
}