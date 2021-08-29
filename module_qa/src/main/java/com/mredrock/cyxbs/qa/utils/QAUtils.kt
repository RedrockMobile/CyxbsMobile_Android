package com.mredrock.cyxbs.qa.utils


internal fun <T> List<T>?.isNullOrEmpty() = this == null || this.isEmpty()