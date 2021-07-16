[![](https://jitci.com/gh/houkunlin/system-dic-starter/svg)](https://jitci.com/gh/houkunlin/system-dic-starter)
[![](https://jitpack.io/v/houkunlin/system-dic-starter.svg)](https://jitpack.io/#houkunlin/system-dic-starter)
[![Maven Central](https://img.shields.io/maven-central/v/com.houkunlin/system-dic-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.houkunlin%22%20AND%20a:%22system-dic-starter%22)

# 系统字典 Starter

## 依赖引入

**Maven**

```xml

<dependency>
    <groupId>com.houkunlin</groupId>
    <artifactId>system-dic-starter</artifactId>
    <version>${latest.version}</version>
</dependency>
```

**Gradle**

```groovy
implementation "com.houkunlin:system-dic-starter:${latest.release}"
```

## 如何使用

- 在应用启动类上添加 `com.houkunlin.system.dic.starter.SystemDicScan` 注解

## 配置文件配置

- 请参考 `com.houkunlin.system.dic.starter.DicProperties` 文件
- 系统启动时会自动刷新字典，假如字典数据数量比较多时会影响系统启动速度，此时可以通过 `system.dic.on-boot-refresh-dic=false` 禁用启动时刷新字典，在系统启动完成后再通过 `com.houkunlin.system.dic.starter.notice.RefreshDicEvent` 事件通知刷新字典



配置前缀：`system.dic` 下方参数将忽略前缀信息

| 配置键                  | 参数类型     | 默认值                    | 配置说明                                                     |
| ----------------------- | ------------ | ------------------------- | ------------------------------------------------------------ |
| raw-value               | boolean      | false                     | 是否显示原生数据字典值。true 实际类型转换，false 转换成字符串值 |
| text-value-default-null | boolean      | false                     | 字典文本的值是否默认为null，true 默认为null，false 默认为空字符串 |
| on-boot-refresh-dic     | boolean      | true                      | 是否在启动的时候刷新字典                                     |
| map-value               | DicText.Type | Type.GLOBAL、Type.NO      | 是否把字典值转换成 Map 形式，包含字典值和文本。NO 时在 json 中插入字段显示字典文本；YES 时把原字段的值变成 Map 数据 |
| mq-exchange-name        | String       | app.dic.fanout.refreshDic | 消息队列 FanoutExchange 交换器名称. 在多系统协同的时候刷新字典的时候会用到 |
| mq-header-source-key    | String       | SourceApplicationName     | 刷新日志消息的Header配置，存储标记消息来源应用名称的 Header KEY |
| refresh-dic-interval    | long         | 60 * 1000L                | 两次刷新字典事件的时间间隔；两次刷新事件时间间隔小于配置参数将不会刷新。单位：毫秒 |


## 使用枚举对象做系统字典

- 需要实现 `com.houkunlin.system.dic.starter.DicEnum` 接口的枚举对象才能被扫描到
- 使用 `com.houkunlin.system.dic.starter.json.DicType` 注解应用到枚举上自定义字典类型名称和说明

## 字典文本自动转换

- 在实体字段中使用 `com.houkunlin.system.dic.starter.json.DicText` 注解

## 配合 @Valid 或 Validated 进行字典校验

- 需要引入 `org.springframework.boot:spring-boot-starter-validation` 的 SpringBoot 依赖
- 在需要校验对象的相关字段添加 `com.houkunlin.system.dic.starter.json.DicValid` 注解，使用方式： `@DicValid(value = "数据字典类型 dicType")`

## 提供一些其他字典信息到系统字典存储对象中

- 实现 `com.houkunlin.system.dic.starter.provider.DicProvider` 接口并扫描到SpringBoot中

## 自定义本地字段缓存存储

- 默认了 `com.houkunlin.system.dic.starter.store.LocalDicStore` 本地存储对象
- 当存在 Redis 环境时，默认使用 `com.houkunlin.system.dic.starter.store.RedisDicStore` 存储对象
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
