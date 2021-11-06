# 更改日志


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
