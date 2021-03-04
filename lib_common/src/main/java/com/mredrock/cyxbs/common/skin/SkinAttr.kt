package com.mredrock.cyxbs.common.skin

/**皮肤的一些属性名字 */
class SkinAttr(
    /**
     * API
     * @return
     */
    var attrName
    /**属性名（例如：background、textColor）*/
    : String,
    /**属性类型（例如：drawable、color）*/
    var attrType: String,
    /**资源名称（例如：ic_bg）*/
    var resName: String,
    /**资源id（例如：123）*/
    var resId: Int
)