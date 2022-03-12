package ext

operator fun Any?.get(key: String): Any? {
    check(this is Map<*, *>) {
        "你干啥呢？没有key值为$key"
    }
    return this.getOrDefault(key, null)
}

