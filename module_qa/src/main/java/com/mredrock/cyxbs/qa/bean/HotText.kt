package com.mredrock.cyxbs.qa.bean

import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by yyfbe, Date on 2020/8/16.
 */
class HotText(@SerializedName("hot_words")
              val scrollerHotWord: List<String>) : Serializable