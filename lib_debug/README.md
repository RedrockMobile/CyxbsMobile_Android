# lib_debug
该模块主要用于 debugImplementation 第三方库，不是用于单模块调试的

## 为什么不直接 debugImplementation，反而需要一个模块？
因为有些东西需要单独设置，比如：Pandora，但单独设置的话，又需要依赖它，最开始在 module_app 中设置，但因为是 debugImplementation，
导致打不了 release 包，所以只能单独放在一个模块里，然后对这个模块进行 debugImplementation