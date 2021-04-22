# 系统字典 Starter

## 如何使用

- 在应用启动类上添加 `com.houkunlin.system.dic.starter.SystemDicScan` 注解



## 配置文件配置

- 请参考 `com.houkunlin.system.dic.starter.DicProperties` 文件



## 使用枚举对象做系统字典

- 需要实现 `com.houkunlin.system.dic.starter.IDicEnums` 接口的枚举对象才能被扫描到
- 使用 `com.houkunlin.system.dic.starter.json.DicType` 注解应用到枚举上自定义字典类型名称和说明



## 字典文本自动转换

- 在实体字段中使用 `com.houkunlin.system.dic.starter.json.DicText` 注解



## 提供一些其他字典信息到系统字典存储对象中

- 实现 `com.houkunlin.system.dic.starter.provider.DicProvider` 接口并扫描到SpringBoot中



## 自定义本地字段缓存存储

- 默认了 `com.houkunlin.system.dic.starter.store.LocalStore` 本地存储对象
- 当存在 Redis 环境时，默认使用 `com.houkunlin.system.dic.starter.store.RedisStore` 存储对象
- 自行实现 `com.houkunlin.system.dic.starter.store.DicStore` 接口并扫描到SpringBoot中



## 当在系统字典中获取不到数据时，请求第三方服务获取字典信息

- 实现 `com.houkunlin.system.dic.starter.store.RemoteDic` 接口并扫描到SpringBoot中，当自行定义 `LocalStore` 对象时，此时的默认`RemoteDic`无法生效，需要手动处理此类情况。
- 例如无法从 `DicStore` 获取到字典信息时，可以使用 `RemoteDic` 从特定的系统服务中获取字典信息



## 全局工具类直接获取字典信息

- 调用 `com.houkunlin.system.dic.starter.DicUtil` 对象



## 字典刷新

- 字典刷新不会清空旧的字典数据

- 接收 `com.houkunlin.system.dic.starter.notice.RefreshDicEvent` 事件处理字典刷新提交最新数据字典数据
- 当存在 rabbitmq 环境时会监听来自其他系统的刷新通知
- 当存在 rabbitmq 环境时，可以通过 `RefreshDicEvent` 通知其他系统进行字典刷新提交最新数据字典数据



## 微服务环境下与其他系统的字典协调工作

- 需要 rabbitmq 环境；微服务环境建议使用 Redis 存储字典
- 在系统管理模块可以发起 `RefreshDicEvent` 事件通知其他系统刷新提交最新数据字典数据
- 系统收到 `RefreshDicEvent` 事件或者 MQ 事件会从 `DicProvider` 中获取最新数据字典信息，然后写入到 `DicStore` 存储对象中