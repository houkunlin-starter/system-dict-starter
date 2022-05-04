[![Maven Central](https://img.shields.io/maven-central/v/com.houkunlin/system-dict-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.houkunlin%22%20AND%20a:%22system-dict-starter%22)
[![Java CI with Gradle](https://github.com/houkunlin-starter/system-dict-starter/actions/workflows/gradle.yml/badge.svg)](https://github.com/houkunlin-starter/system-dict-starter/actions/workflows/gradle.yml)

# 系统字典 Starter

在日常项目开发中，不免都会用到一些数据字典的信息，以及前端展示的时候通常也需要把这些数据字典值转换成具体字典文本信息。遇到这种场景通常都是后端把字典的文本转换好一起返回给前端，前端只需要直接展示即可。

一般情况下后端可能需要单独给返回对象创建一个字段来存储对应的字典文本值，然后进行手动的处理，这种方式通常比较繁琐，在字段多的时候会增加更多的工作量。

本项目基于 Jackson 的自定义注解功能实现了这一自动转换过程，不需要在对象中定义存放字典文本的字段，只需要在字段上使用特定的注解配置，Jackson序列化的时候即可自动把字典值转换成字典文本。

**本项目只适用使用 Jackson 做 JSON 序列化，在 fastjson 下失效**

**`v1.4.8` 版本已在 `JDK8` `JDK11` `JDK17` 环境下跑通所有单元测试样例**

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



#### 详细使用文档请点击查看 [基础用法文档](./usage.md) 

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

- 在字段中使用 `DictText` 注解

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



## 当在系统字典中获取不到数据时，请求第三方服务获取字典信息

- 实现 `RemoteDict` 接口并扫描到SpringBoot中，当自行定义 `LocalDictStore` 对象时，此时的默认`RemoteDict`无法生效，需要手动处理此类情况。
- 例如无法从 `DictStore` 获取到字典信息时，可以使用 `RemoteDict` 从特定的系统服务中获取字典信息

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



## 全局工具类直接获取字典信息

- 调用 `DictUtil` 对象

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
