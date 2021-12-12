# 一种优雅的数据字典文本转换方式

```
0. 项目地址
0.1 依赖坐标
1. 开始使用
1.1 数据准备
1.2 字典缓存存储
1.3 DictProvider 中的字典信息变动如何刷新字典？
2. 用法示例
2.1 基础用法示例
2.2 静态工具直接获取字典信息
3. 其他
3.1 SpringBoot Actuator 端点支持
3.2 默认 Controller 接口
3.3 面对大量数据需要转换的场景，是否会频繁去调用接口获取实际字典文本？
3.4 配置说明
```



在日常项目开发中，不免都会用到一些数据字典的信息，以及前端展示的时候通常也需要把这些数据字典值转换成具体字典文本信息。遇到这种场景通常都是后端把字典的文本转换好一起返回给前端，前端只需要直接展示即可。

一般情况下后端可能需要单独给返回对象创建一个字段来存储对应的字典文本值，然后进行手动的处理，这种方式通常比较繁琐，在字段多的时候会增加更多的工作量。

本项目基于 Jackson 的自定义注解功能实现了这一自动转换过程，不需要在对象中定义存放字典文本的字段，只需要在字段上使用特定的注解配置，Jackson序列化的时候即可自动把字典值转换成字典文本。



## 0. 项目地址

- https://gitee.com/houkunlin/system-dict-starter

- https://github.com/houkunlin-starter/system-dict-starter

### 0.1 依赖坐标

[![Maven Central](https://img.shields.io/maven-central/v/com.houkunlin/system-dict-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.houkunlin%22%20AND%20a:%22system-dict-starter%22)

```xml
<dependency>
    <groupId>com.houkunlin</groupId>
    <artifactId>system-dict-starter</artifactId>
    <version>${latest.version}</version>
</dependency>
```
## 1. 开始使用

使用数据字典通常有两种字典，一种是存储在数据库中的动态形式数据字典，一种是用枚举对象硬编码在代码中的系统字典，本工具为了适应第二种枚举对象字典的情况，定义了一个枚举字典扫描注解，需要在启动类上使用注解，并定义要扫描的包信息。
```java
//  启动类上加注解，这一个步骤是必须的
@SystemDictScan(basePackages = "test.application.dict")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class);
    }
}
```



### 1.1 数据准备

直接使用枚举对象来做字典场景，枚举对象需要实现一个 `DictEnum<V>` 接口才能被正常扫描到，枚举对象有两个自定义的注解 `@DictConverter` 和 `@DictType` 可以做一些相关配置

- `@DictType` 用来标记枚举对象的字典类型代码
- `@DictConverter` 用来标记是否对这个枚举对象生成 `org.springframework.core.convert.converter.Converter` 转换对象，提供使用枚举接收参数时自动转换字典值到相应枚举对象类型的功能，未加此注解将不会生成转换器对象。

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

    @JsonCreator
    public static PeopleType getItem(Integer code) {
        return DictEnum.valueOf(values(), code);
    }
}
```



前面在启动类上加了注解功能仅仅只是启用了基础的功能，我们的字典可能还会存储在数据库（提供给用户动态设定、修改的数据字典信息）或本地文件等其他地方，因此需要向系统提供一个 `DictProvider` 对象来把数据库或者本地文件中的字典信息读取出来提供给 `@DictText` 来使用。

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

上面 `DictProvider` 中返回的字典信息会被存储在缓存中（`DictStore`）。

但是可能我们会有一些数据量特别大的场景不适合直接把数据存储在缓存中，有可能需要直接从数据库中读取。也许有些字典不是本系统的（可能是第三方服务提供的一些字典信息，需要去请求远程服务的信息），此时可以提供一个 `RemoteDict` 对象来处理这种情况，当在缓存中（`DictStore`）找不到字典文本值的时候，会调用 `RemoteDict` 对象来尝试进一步读取字典文本信息。

```java
@Component
public class MyRemoteDict implements RemoteDict {
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
}

```



### 1.2 字典存储

在前面说到系统的枚举字典和 `DictProvider` 提供的字典会被缓存，工具中已经默认提供了两个缓存对象

- `LocalDictStore` 本地 Map 缓存存储使用了 `ConcurrentHashMap` 来缓存字典值/字典文本信息
- `RedisDictStore` 使用了 Redis 来存储字典值/字典文本信息，当想启用 Redis 存储字典的时候只需要在项目中引入 `org.springframework.boot:spring-boot-starter-data-redis` 依赖并配置好 Redis 连接信息即可

有时候，上面提供的两个缓存对象可能并不适用自己的业务场景，那么我们还可以手动实现一个缓存存储对象 `DictStore` ，在手动实现缓存对象时前面的 `RemoteDict` 并不会生效，因此需要在 `DictStore` 中自行处理此种情况。

```java
// 可参考 LocalDictStore 自行实现相关功能
@Component
@AllArgsConstructor
public class MyDictStore implements DictStore {
    private final RemoteDict remoteDict;

    @Override
    public void store(final DictTypeVo dictType) {

    }

    @Override
    public void store(final Iterator<DictValueVo> iterator) {

    }

    @Override
    public Set<String> dictTypeKeys() {
        return null;
    }

    @Override
    public DictTypeVo getDictType(final String type) {
        return remoteDict.getDictType(type);
    }

    @Override
    public String getDictText(final String type, final String value) {
        return remoteDict.getDictText(type, value);
    }
}
```



### 1.3 DictProvider 中的字典信息变动如何刷新字典？

`DictProvider` 提供的字典信息是从其他地方读取的，其字典数据有可能会产生变动，当字典变动后可以发起 `RefreshDictEvent` 事件来触发字典刷新。

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



## 2. 用法示例

### 2.1 基础用法示例

为了正常能够转换数据，因此需要使用一个 Jackson 的自定义注解 `@DictText` ，把此注解用在需要转换的字段上即可。

```java
@Data
@AllArgsConstructor
class Bean {
    @DictText("PeopleType")
    private String userType;
    private String userType1;
}
final Bean bean = new Bean("1", null);
final String value = objectMapper.writeValueAsString(bean);
System.out.println(bean); // Bean(userType=1,userType1=null)
System.out.println(value); // {"userType":"1","userTypeText":"普通用户","userType1":null}
```

我们不需要在对象中为字典文本创建一个单独的字段，`@DictText` 会自动生成一个 `字段名 + Text` 的字段输出到前端。但是有时候我们觉得 `字段名 + Text` 这个字段不行，想要用另外一个字段名称，此时可以用下面这种方式：

```java
@Data
@AllArgsConstructor
class Bean {
    @DictText(value = "PeopleType", fieldName = "typeText")
    private String userType;
}
final Bean bean = new Bean("1");
final String value = objectMapper.writeValueAsString(bean);
System.out.println(bean); // Bean(userType=1)
System.out.println(value); // {"userType":"1","typeText":"普通用户"}
```

有时候我们可能用一个字符串字段来存储多个字典文本信息，并通过特定的符号来分隔，例如：

```java
@Data
@AllArgsConstructor
class Bean {
    @DictText(value = "PeopleType", array = @Array(split = ","))
    private String userType;
}
final Bean bean = new Bean("0,1");
final String value = objectMapper.writeValueAsString(bean);
System.out.println(bean); // Bean(userType=0,1)
System.out.println(value); // {"userType":"0,1","userTypeText":"系统管理、普通用户"}
```

当然也有可能使用一个集合来存储多个字典文本信息：

```java
@Data
@AllArgsConstructor
class Bean {
    @DictText("PeopleType")
    private List<String> userType;
}
final Bean bean = new Bean(Arrays.asList("0", "1"));
final String value = objectMapper.writeValueAsString(bean);
System.out.println(bean); // Bean(userType=["0","1"])
System.out.println(value); // {"userType":["0","1"],"userTypeText":"系统管理、普通用户"}
```

也许对于这种字典值列表可能需要输出文本列表信息

```java
@Data
@AllArgsConstructor
class Bean {
    @DictText(value = "PeopleType", array = @Array(toText = false))
    private List<String> userType;
}
final Bean bean = new Bean(Arrays.asList("0", "1"));
final String value = objectMapper.writeValueAsString(bean);
System.out.println(bean); // Bean(userType=[0, 1])
System.out.println(value); // {"userType":["0","1"],"userTypeText":["系统管理","普通用户"]}
```



### 2.2 静态工具直接获取字典信息

有时候不仅仅是用在返回给前端时自动转换，可能在程序中也需要直接用到这些字典文本，此时可以通过静态工具类来直接获取字典文本信息

```java
@Component
@AllArgsConstructor
public class CommandRunnerTests implements CommandLineRunner {
    @Override
    public void run(final String... args) throws Exception {
        System.out.println(DictUtil.getDictText("PeopleType", "1"))
    }
}
```

静态工具类无法处理多个字典的情况，也就是无法对 `"0,1"` 这种数据进行自动分割，这种场景需要自行分割并获取数据



## 3. 其他

### 3.1 SpringBoot Actuator 端点支持

提供了 `dict`  `dictSystem`  `dictRefresh` 三个端点信息

```
// 获取所有的字典名称列表和一些配置的对象名称
GET /actuator/dict/

// 获取某个字典类型的完整信息
GET /actuator/dict/PeopleType

// 获取某个字典值的字典文本信息
GET /actuator/dict/PeopleType/1

// 获取系统字典的名称列表（枚举对象）
GET /actuator/dictSystem

// 获取系统字典的完整信息
GET /actuator/dictSystem/PeopleType
```

### 3.2 默认 Controller 接口

可通过一个配置 `system.dict.controller.enabled` 来配置是否启用默认接口，使用 `system.dict.controller.prefix` 来配置路径前缀信息，启用后将提供以下4个接口

- `${prefix}/{dict}` 通过字典类型代码获取字典类型信息
- `${prefix}/{dict}/{value}` 通过字典类型代码和字典值获取字典文本信息
- `${prefix}/?dict={dict}` 通过字典类型代码获取字典类型信息
- `${prefix}/?dict={dict}&value={value}` 通过字典类型代码和字典值获取字典文本信息

### 3.3 面对大量数据需要转换的场景，是否会频繁去调用接口获取实际字典文本？

在 `DictUtil` 工具中增加了一层缓存，缓存使用了 `Caffeine` 并配置了一定的缓存过期时间 ，当我们获取一个字典文本的时候并不会直接去调用 `DictStore` 读取字典文本，而是先从缓存中查找是否存在，如果存在则直接返回字典文本信息，并且当从 `DictStore` 读取失败次数达到一定量时也不会继续从 `DictStore` 中读取数据。

这在使用 Redis 存储的场景时可以有效的减少网络请求，虽然 Redis 很快，但是也有可能会造成一定的网络延时，这在转换数量大的时候可以有效的缩短因转换带来的延时问题。

### 3.4 配置说明

- `system.dict` 字典配置
  - `raw-value=false` 是否显示原生数据字典值。true 实际类型输出，false 转换成字符串值
  - `text-value-default-null=false` 字典文本的值是否默认为null，true 默认为null，false 默认为空字符串
  - `on-boot-refresh-dict=true` 是否在启动的时候刷新字典
  - `map-value=false` 是否把字典值转换成 Map 形式，包含字典值和文本。false 时在 json 中插入字段显示字典文本；true 时把原字段的值变成 Map 数据
  - `mq-type` 通知其他协同系统刷新字典的MQ类型
    - 可选值：`none` 不启用，`amqp` 使用 RabbitMQ， `redis` 使用 Redis 的发布/订阅功能
  - `mq-exchange-name` 消息队列交换器名称 或 Redis channel 名称
  - `refresh-dict-interval=60s` 两次刷新字典事件的时间间隔；两次刷新事件时间间隔小于配置参数将不会刷新。
- `system.dict.cache` DictUtil 工具字典缓存
  - `enabled=true` 是否启用缓存
  - `maximum-size=500` 缓存最大容量
  - `initial-capacity=50` 缓存初始化容量
  - `duration=30s` 有效期时长
  - `miss-num=50` 在有效期内同一个字典值未命中指定次数将快速返回，不再重复请求获取数据字典信息
- `system.dict.controller` 默认控制器
  - `enabled=true` 是否启用 WEB 请求接口
  - `prefix=/dict` WEB 请求接口前缀
