# 更改日志

## 1.6.0 版本

基于 Java 17 和 Spring Boot 3.4.6 进行开发构建，删除了 Java 8 的动态生成类加载方式，其他的与 1.5.7.2 版本一致

- refactor: 删除了 Java 8 的动态生成类加载方式代码

## 1.5.7.2 版本

这有可能将是最后一个基于 Java 8 字节码发布的版本（基于 Spring Boot 2.7）

- refactor: 重构了一些动态生成类的代码，修复了一些东西生成字节码的错误

## 1.5.7 版本（废弃/撤销）

旧的发布仓库不能用了，切换到新的仓库发布时弄错了一些东西，因此这个版本是作废的版本。

## 1.5.6 版本

- refactor: 优化启动配置，把扫描包改为自动配置

## 1.5.5 版本

- feat(cache): 增加缓存构建结果自定义处理

## 1.5.4 版本

- fix: 修复使用端点重启系统时出现 java.lang.LinkageError: loader 'app' attempted duplicate class definition for 问题

## 1.5.3 版本

- feat(store): 增加 DictStore#storeBatch 批量存储数据字典信息方法，Redis 通过使用 Pipeline 方式批量写入字典值文本数据

## 1.5.2 版本

- feat: DictTypeKeyHandler 增加处理字典值的方法

## 1.5.1 版本

- feat: DictEnum 增加 getParentValue/getSorted/isDisabled 字典值参数方法

## 1.5.0 版本

产生了一些破坏性变更，请谨慎升级。移除了 DictText.Type 改为使用 DictBoolType；修改了 Redis 存储字典文本的方式，改为 Redis Hash 存储字典文本内容。

- refactor: 重构 Jackson 序列化器，抽出一个基类序列化器，把 DictText.Type 提取为 DictBoolType 对象
- perf: 优化 Jackson 序列化器对于集合字典的处理
- feat: 防止系统字典值重复加入缓存
- feat: 系统字典类型完整信息单独存储一份，系统字典不允许通过事件进行更新字典数据
- feat: 缓存到 Redis 改为存储到 Redis Hash

## 1.4.11 版本

- feat(swagger): 增加 SpringDoc 的注解配置，把 Swagger/SpringDoc 的注解依赖范围从 传递依赖 改为仅在编译时有效
- fix: 适配 SpringBoot 3.0.0 ，修复在 3.0.0 下启动失败和路径访问效果跟 2.7.x 不一致的问题
- feat: 支持在已引入 Redis 的场景下通过配置文件指定使用本地 Map 来存储字典数据信息，不强制必须用 Redis 来存储字典数据
- feat: 字典MQ不指定Redis时，不再自动创建 RedisMessageListenerContainer 对象
- feat: 增加 DictUtil#setDictStore 方法在运行期间动态设置字典存储对象

## 1.4.10 版本

- fix(bytecode/javassist): 修复在SpringBoot热加载时重新生成转换器报错问题

## 1.4.9 版本

- fix: 修复使用 ASM 构建转换器实现类时 ClassWriter 参数错误问题
- feat(controller): 获取字典类型信息接口增加一个参数进行树形结构数据转换
- feat(util): 增加一个静态方法处理对象中的字典值文本，返回处理后的对象

## 1.4.8 版本

已在 `JDK8` `JDK11` `JDK17` 环境下跑通所有单元测试样例

- fix: 修复 JDK17 下运行失败问题
- feat: 重构使用字节码生成 Converter 转换器，增加 ASM 字节码支持
- feat: 增加配置支持切换字节码工具：ASM/JAVASSIST

## 1.4.7 版本

- feat: 增加 DictText#dictTypeHandler 字典类型代码处理器支持，可以动态设置字段的字典类型代码
- feat: 支持自定义缓存键前缀

## 1.4.6.3 版本

- feat: 增加树结构数据访问深度限制，防止陷入死循环
- feat: DictType 注解可重复使用，支持把一个枚举做成多个字典，同时支持把多个枚举字典合并到一个字典中
- fix: 修复字段值为 null 时被序列化成 "null" 字符串的问题

## 1.4.6.2 版本

- fix: 修复某些场景下获取不到 RedisTemplate<String, DictTypeVo> 导致启动失败问题
  - 在一些多模块项目中，公共模块配置了 RedisTemplate<String, Object> Bean 先注入到上下文中，系统字典的
    RedisTemplate<String, DictTypeVo> 无法继续注入，因此导致启动失败

## 1.4.6.1 版本

- fix: 修复字典值使用文本分隔转换数组结果时，字典值无分隔符导致无数据问题

## 1.4.6 版本

- feat: 增加树形结构数据的字典文本转换支持

## 1.4.5.1 版本

- feat(store): 刷新字典时当 DictTypeVo#children = null 时视为删除字典类型对象
- feat(store): 删除字典类型对象的同时也删除此字典类型下的所有字典值文本信息
- feat: RefreshDictValueEvent 增加参数设置删除单个字典值文本后字典类型无字典值列表时删除此字典类型信息

## 1.4.5 版本

功能代码变更
- feat: 增加一个 DictText#replace 配置字段，标记是否用字典文本值替换字典值（在原字段上输出字典文本值）[#I4GT0N](https://gitee.com/houkunlin/system-dict-starter/issues/I4GT0N)
- feat: 增加一个 RefreshDictTypeEvent 事件刷新一个完整的字典类型信息 [#2](https://github.com/houkunlin-starter/system-dict-starter/issues/2#issuecomment-960424924)
- fix: 修复刷新单个字典值文本信息时文本信息未同步到字典类型对象里面的问题 [#2](https://github.com/houkunlin-starter/system-dict-starter/issues/2#issuecomment-960423263)
- feat: RefreshDictValueEvent 事件增加 updateDictType 字段决定在更新单个字典文本值时是否维护对应字典类型对象的字典值列表信息

涵盖 1.4.4.X 变更
- feat: 在刷新字典时当 DictValueVo.title == null 被视为删除相应的字典文本信息
- fix: 修复 SpringBoot 2.4.0 以下版本无法启动问题

配置文件变更：
- 增加一个 `system.dict.replace-value` 配置项（在原字典值字段上把字典值替换成字典文本输出）

由于引入了 DictText#replace 配置，会影响使用 DictText#mapValue=YES 和 system.dict.map-value=true 的配置，有使用上诉配置的请增加如下配置：
1. 使用注解 DictText#mapValue=YES 配置的请增加 DictText#replace=YES 配置
2. 使用全局 system.dict.map-value=true 配置的请增加 system.dict.replace-value=true 配置


## 1.4.4 版本

功能代码变更
- feat: 增加一个 `RefreshDictValueEvent` 事件可以刷新单个字典文本信息
- feat: 增加一个刷新字典的端点：`dictRefresh`
- feat: 增加使用 Redis 的发布/订阅 功能来处理字典刷新事件通知配置（需要设定配置文件来启用）
- refactor: 修改系统字典端点ID `dictSystem` 解决系统字典端点控制台日志警告问题
- refactor: MQ通知其他协同系统刷新字典默认未启用

配置文件变更：
- 移除 `system.dict.mq-header-source-key` 配置项
- 增加 `system.dict.mq-type` 配置项选择性启用 RefreshDictEvent 通知其他系统刷新字典
    - 可选值：`none` 不启用（默认），`amqp` 使用 RabbitMQ， `redis` 使用 Redis 的发布/订阅功能
- 更改 `system.dict.refresh-dict-interval` 属性类型为 `Duration` 类型，默认值未改变

涵盖 1.4.3.X 变更
- fix: 修复因 Redis 客户端不同导致项目启动报错问题
- fix: 修复 Java 8 环境下 SpringBoot 打包后使用 java -jar 启动异常问题
