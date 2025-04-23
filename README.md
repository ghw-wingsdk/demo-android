# WingSDK Demo介绍

## 1.构建变体说明

不同变体对应不同的包，对应包的相关配置会放在该变体的目录下，比如`官网包`配置放在`src/official/`目录下。

比如`official`变体对应官网包，如果需要查看`官网包`相关清单配置，可以查看`official/AndroidManifest.xml`里面的配置。
另外`wa-sdk-ghgl`和`wa-sdk-ghgp`这两个依赖库是`官网包`特有的，所以只有在构建`official`官网包时才需要添加。

| 变体名      | 对应包类型   |
|----------|---------|
| main     | Google包 |
| official | 官网包     |
| nowgg    | Nowgg包  |
| leidian  | 雷电包     |

