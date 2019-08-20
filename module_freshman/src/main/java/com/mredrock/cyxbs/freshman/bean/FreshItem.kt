package com.mredrock.cyxbs.freshman.bean

import java.io.Serializable

sealed class FreshItem {
    abstract val id: Int
    data class TextItem(val text: FreshTextItem): FreshItem() {
        override val id = 1
    }
    object HeaderItem: FreshItem() {
        override val id = 0
    }
    object FooterItem: FreshItem() {
        override val id = 2
    }

}
data class FreshTextItem(val title: String, val discript: String): Serializable