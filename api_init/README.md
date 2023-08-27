# api_init
该模块主要用于应用初始化加载的 SPI 注入

## 使用教程
### 1. 添加依赖
在 `build.gradle` 中添加如下依赖
```kotlin
dependApiInit() // 已包含 AutoService
```

### 2. 设置启动类
- 设置一个启动类，比如：XXXInitialService（建议以 InitialService 结尾）
- 添加 `@AutoService(InitialService::class)`
- 接入 `InitialService` 接口
```kotlin
@AutoService(InitialService::class)
class CrashInitialService : InitialService {
  // Ctrl + o 实现里面的方法
}
```

之后该类会在 Application 的 onCreate() 中初始化
