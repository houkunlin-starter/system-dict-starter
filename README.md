[![](https://jitci.com/gh/houkunlin/system-dict-starter/svg)](https://jitci.com/gh/houkunlin/system-dict-starter)
[![](https://jitpack.io/v/houkunlin/system-dict-starter.svg)](https://jitpack.io/#houkunlin/system-dict-starter)
[![Maven Central](https://img.shields.io/maven-central/v/com.houkunlin/system-dict-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.houkunlin%22%20AND%20a:%22system-dict-starter%22)

# 系统字典 Starter

> 注意：自 `com.houkunlin:system-dic-starter:1.3.0` 版本发布后将会进行以下变更：
>
> - 更改包名：由 `com.houkunlin.system.dic.starter` 改为 `com.houkunlin.system.dict.starter`
> - 更改坐标：由 `com.houkunlin:system-dic-starter` 改为 `com.houkunlin:system-dict-starter`
> - 新坐标 `com.houkunlin:system-dict-starter` 起始版本号为 `1.3.0` ，除了与旧坐标的包名不同，其他代码完全相同
> - 旧坐标将不再提供后续更新发布，后续更新发布将会使用新坐标
> - 旧坐标代码迁移到 `old-version-dic` 分支，主分支 `main` 将只保留新坐标代码，GIT 标签保持不变：不删除、不更名


> - 1.4.0
>   - 1.4.0 版本与 1.3.0 版本不兼容，1.4.0 版本代码改动量很多，很多的引用需要重新调整， 1.3.0 不再有修复版本
>   - 主要为包名、文件名称、对象名称、字段名称、方法名称改动，改动特征：`dic` 改为 `dict`
>   - 其他一些破坏性改动，主要涉及：`DictProvider` `DictTypeVo` `DictValueVo`
>   - 配置文件参数改动，主要涉及配置前缀和配置字段名称
>   - 增加 `DictProvider#storeDictType` 来决定 `DictStore` 是否存储完整的字典类型信息对象
>   - 端点 `DictEndpoint` 增加返回系统中缓存的字典类型代码信息


## 依赖引入

**Maven**

```xml
<dependency>
    <groupId>com.houkunlin</groupId>
    <artifactId>system-dict-starter</artifactId>
    <version>${latest.version}</version>
</dependency>
```

**Gradle**

```groovy
implementation "com.houkunlin:system-dict-starter:${latest.release}"
```



## 如何启用？

- 在应用启动类上添加 `SystemDictScan` 注解
- 后续步骤请看本文后面内容

```java
// 启动类上加注解
@SystemDictScan
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
```



## 配置文件配置

- 请参考 `DictProperties` 文件
- 系统启动时会自动刷新字典，假如字典数据数量比较多时会影响系统启动速度，此时可以通过 `system.dict.on-boot-refresh-dict=false` 禁用启动时刷新字典，在系统启动完成后再通过 `RefreshDictEvent` 事件通知刷新字典



配置前缀：`system.dict` 下方参数将忽略前缀信息

| 配置键                  | 参数类型     | 默认值                    | 配置说明                                                     |
| ----------------------- | ------------ | ------------------------- | ------------------------------------------------------------ |
| raw-value               | boolean      | false                     | 是否显示原生数据字典值。true 实际类型转换，false 转换成字符串值 |
| text-value-default-null | boolean      | false                     | 字典文本的值是否默认为null，true 默认为null，false 默认为空字符串 |
| on-boot-refresh-dict     | boolean      | true                      | 是否在启动的时候刷新字典                                     |
| map-value               | DictText.Type | Type.GLOBAL、Type.NO      | 是否把字典值转换成 Map 形式，包含字典值和文本。NO 时在 json 中插入字段显示字典文本；YES 时把原字段的值变成 Map 数据 |
| mq-exchange-name        | String       | app.dict.fanout.refreshDict | 消息队列 FanoutExchange 交换器名称. 在多系统协同的时候刷新字典的时候会用到 |
| mq-header-source-key    | String       | SourceApplicationName     | 刷新日志消息的Header配置，存储标记消息来源应用名称的 Header KEY |
| refresh-dict-interval    | long         | 60 * 1000L                | 两次刷新字典事件的时间间隔；两次刷新事件时间间隔小于配置参数将不会刷新。单位：毫秒 |



缓存配置前缀：`system.dict.cache`
| 配置键          | 参数类型 | 默认值 | 配置说明                                                     |
| --------------- | -------- | ------ | ------------------------------------------------------------ |
| enabled         | boolean  | true   | 是否启用缓存                                                 |
| maximumSize     | int      | 500    | 缓存最大容量                                                 |
| initialCapacity | int      | 50     | 缓存初始化容量                                               |
| duration        | Duration | 30s    | 有效期时长                                                   |
| missNum         | int      | 50     | 在有效期内同一个字典值未命中指定次数将快速返回，不再重复请求获取数据字典信息 |



默认 Controller 配置前缀：`system.dict.controller`

| 配置键  | 参数类型 | 默认值 | 配置说明              |
| ------- | -------- | ------ | --------------------- |
| enabled | boolean  | true   | 是否启用 WEB 请求接口 |
| prefix  | String   | /dict  | WEB 请求接口前缀      |

启用默认 Controller 后将自动提供 4 个接口信息：

- `${prefix}/{dict}` 通过字典类型代码获取字典类型信息
- `${prefix}/{dict}/{value}` 通过字典类型代码和字典值获取字典文本信息
- `${prefix}/?dict={dict}` 通过字典类型代码获取字典类型信息
- `${prefix}/?dict={dict}&value={value}` 通过字典类型代码和字典值获取字典文本信息




## 使用枚举对象做系统字典

- 需要实现 `DictEnum` 接口的枚举对象才能被扫描到
- 使用 `DictType` 注解应用到枚举上自定义字典类型名称和说明

> `@DictConverter` 一般与 `@DictType` 配合使用。
>
> `@DictType` 用来标记枚举对象的字典类型代码
>
> `@DictConverter` 用来标记是否对这个枚举对象生成 `org.springframework.core.convert.converter.Converter` 转换对象，提供使用枚举接收参数时自动转换字典值到相应枚举对象类型的功能

```java
@DictConverter
@DictType(value = "PeopleType", comment = "用户类型")
@Getter
@AllArgsConstructor
public enum PeopleType implements DictEnum<Integer> {
    /** 系统管理员 */
    ADMIN(0, "系统管理"),
    /** 普通用户 */
    USER(1, "普通用户"),
    ;
    private final Integer value;
    private final String title;

    /**
     * Jackson 枚举处理，把枚举值转换成枚举对象
     *
     * @param code 代码
     * @return 枚举对象
     */
    @JsonCreator
    public static PeopleType getItem(Integer code) {
        return DictEnum.valueOf(values(), code);
    }
}
```



## 字典文本自动转换

- 在实体字段中使用 `DictText` 注解

```java
@Data
@AllArgsConstructor
class Bean {
    @DictText("PeopleType")
    private String userType1 = "1";

    @DictText(value = "PeopleType", array = @Array)
    private String userType2 = "1,2,3";

    @DictText(value = "PeopleType", array = @Array(toText = false))
    private List<String> userType3 = Arrays.asList("1", "2", "3");
}
```



## 配合 @Valid 或 Validated 进行字典校验

- 需要引入 `org.springframework.boot:spring-boot-starter-validation` 的 SpringBoot 依赖
- 在需要校验对象的相关字段添加 `DictValid` 注解，使用方式： `@DictValid(value = "数据字典类型 dictType")`



## 提供一些其他字典信息到系统字典存储对象中

- 实现 `DictProvider` 接口并扫描到SpringBoot中

```java
@Component
public class MyProvider implements DictProvider {
    @Override
    public boolean isStoreDictType() {
        return true;
    }

    @Override
    public Iterator<DictTypeVo> dictTypeIterator() {
        // 从其他地方（其他服务、数据库、本地文件）加载完整的数据字典信息（字典类型+字典值列表）
        // 从这里返回的数据字典信息将会被存入缓存中，以便下次直接调用，当有数据变动时可以发起 RefreshDictEvent 事件通知更新字典信息
        final DictTypeVo typeVo = DictTypeVo.newBuilder("name", "测试字典")
            .add("1", "测试1")
            .add("2", "测试2")
            .build();
        return Collections.singletonList(typeVo).iterator();
    }
}
```



## 自定义本地字段缓存存储

- 默认了 `LocalDictStore` 本地存储对象
- 当存在 Redis 环境时，默认使用 `RedisDictStore` 存储对象
- 自行实现 `DictStore` 接口并扫描到SpringBoot中



## 当在系统字典中获取不到数据时，请求第三方服务获取字典信息

- 实现 `RemoteDict` 接口并扫描到SpringBoot中，当自行定义 `LocalDictStore` 对象时，此时的默认`RemoteDict`无法生效，需要手动处理此类情况。
- 例如无法从 `DictStore` 获取到字典信息时，可以使用 `RemoteDict` 从特定的系统服务中获取字典信息

```java
@Configuration
public class DictConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DictConfiguration.class);

    @Bean
    public RemoteDict remoteDic() {
        return new RemoteDict() {
            @Override
            public DictTypeVo getDictType(final String type) {
                // 从其他地方（其他服务、数据库、本地文件）加载一个完整的数据字典信息（字典类型+字典值列表）
                return null;
            }

            @Override
            public String getDictText(final String type, final String value) {
                // 从其他地方（其他服务、数据库、本地文件）加载一个字典文本信息
                return null;
            }
        };
    }
}
```



## 全局工具类直接获取字典信息

- 调用 `DictUtil` 对象

```java
@Component
@AllArgsConstructor
public class CommandRunnerTests implements CommandLineRunner {
    private final ApplicationEventPublisher publisher;

    @Override
    public void run(final String... args) throws Exception {
        System.out.println(DictUtil.getDictText("PeopleType", "1"))
    }
}
```



## 字典刷新

- 字典刷新不会清空旧的字典数据

- 接收 `RefreshDictEvent` 事件处理字典刷新提交最新数据字典数据
- 当存在 rabbitmq 环境时会监听来自其他系统的刷新通知
- 当存在 rabbitmq 环境时，可以通过 `RefreshDictEvent` 通知其他系统进行字典刷新提交最新数据字典数据

```java
@Component
@AllArgsConstructor
public class CommandRunnerTests implements CommandLineRunner {
    private final ApplicationEventPublisher publisher;

    @Override
    public void run(final String... args) throws Exception {
        // 发起 RefreshDictEvent 事件通知刷新字典信息
        publisher.publishEvent(new RefreshDictEvent("test", true, true));
    }
}
```



## 微服务环境下与其他系统的字典协调工作

- 引入依赖后确保 rabbitmq 服务连接正常无需其他配置即可正常使用
- 需要 rabbitmq 环境；微服务环境建议使用 Redis 存储字典；
- 在系统管理模块可以发起 `RefreshDictEvent` 事件通知其他系统刷新提交最新数据字典数据
- 系统收到 `RefreshDictEvent` 事件或者 MQ 事件会从 `DictProvider` 中获取最新数据字典信息，然后写入到 `DictStore` 存储对象中



## Actuator 端点支持

启用端点配置：

```yaml
# yaml
management:
  endpoints:
    web:
      cors:
        allowed-headers: '*'
        allowed-methods: '*'
        allowed-origins: '*'
      exposure:
        include: dict,dict-system
```



在有 actuator 依赖环境会暴露出两个端点：

### `dict` 默认端点，暴露两个接口

- 默认接口：返回一些类名称信息

  - ```json
    // GET http://localhost:8081/api/system/actuator/dict/
    
    {
      "dict-types": [
        "name",
        "PeopleType"
      ],
      "dict-classes": {
        "remoteDict": "test.application.server.local.DictConfiguration$1",
        "stores": "com.houkunlin.system.dict.starter.store.LocalDictStore",
        "providers": [
          "test.application.common.MyProvider",
          "com.houkunlin.system.dict.starter.provider.SystemDictProvider"
        ]
      }
    }
    ```

- 接口1：获取字典类型信息

  - ```json
    // GET http://localhost:8081/api/system/actuator/dict/name
    
    {
      "title": "测试字典",
      "type": "name",
      "remark": null,
      "children": [
        {
          "value": "1",
          "title": "测试1",
          "sorted": 0
        },
        {
          "value": "2",
          "title": "测试2",
          "sorted": 0
        }
      ]
    }
    ```

- 接口2：获取字典值文本信息

  - ```text
    // GET http://localhost:8081/api/system/actuator/dict/PeopleType/1
    
    普通用户
    ```




### `dict-system` 系统字典 Provider 端点

- 默认接口：返回所有系统字典类型代码列表

  - ```json
    // GET http://localhost:8081/api/system/actuator/dict-system
    
    {
      "types": [
        "PeopleType"
      ]
    }
    ```

  - 

- 接口1：获取系统字典类型信息

  - ```json
    // GET http://localhost:8081/api/system/actuator/dict-system/PeopleType
    
    {
      "title": "用户类型",
      "type": "PeopleType",
      "remark": "From Application: system",
      "children": [
        {
          "value": 0,
          "title": "系统管理",
          "sorted": 0
        },
        {
          "value": 1,
          "title": "普通用户",
          "sorted": 0
        },
        {
          "value": 2,
          "title": "其他用户",
          "sorted": 0
        }
      ]
    }
    ```
