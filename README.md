# WingSDK Demo介绍

## 1.构建变体说明

不同变体对应不同的软件包，其相关配置存放在对应变体的目录下。例如，`official` 变体对应`官网包`，其配置位于 `src/official/` 目录下。

因此，如需查看`官网包`的清单配置，请查阅 `src/official/AndroidManifest.xml` 文件，该文件包含了`官网包`的所有清单配置。

| 变体名     | 对应包类型       |
|---------|-------------|
| main    | Google包、官网包 |
| nowgg   | Nowgg包      |
| leidian | 雷电包         |

