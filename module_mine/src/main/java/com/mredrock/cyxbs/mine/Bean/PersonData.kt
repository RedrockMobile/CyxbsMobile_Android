package com.mredrock.cyxbs.mine.Bean

data class PersonData(
    val `data`: Data,
    val info: String,
    val status: Int
) {
    data class Data(
        val background_url: String,
        val birthday: String,
        val college: String,
        val constellation: String,
        val gender: String,
        val grade: String,
        val identityies: Any,
        val introduction: String,
        val isBefocused: Boolean,
        val isFocus: Boolean,
        val is_self: Boolean,
        val nickname: String,
        val phone: String,
        val photo_src: String,
        val photo_thumbnail_src: String,
        val qq: String,
        val redid: String,
        val stunum: String,
        val uid: Int,
        val username: String
    )
}