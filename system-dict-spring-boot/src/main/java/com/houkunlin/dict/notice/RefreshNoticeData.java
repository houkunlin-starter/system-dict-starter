package com.houkunlin.dict.notice;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 刷新字典事件的通知信息对象
 * <p>
 * 该类用于封装字典刷新事件的通知信息，包含事件消息、来源应用名称、是否通知兄弟系统等信息。
 * 实现了 Serializable 接口，支持在不同系统间序列化传输。
 * 使用 @Builder 注解提供了构建者模式的创建方式，便于对象的创建和配置。
 * </p>
 * <pre>
 * // 创建刷新通知数据示例
 * RefreshNoticeData data = RefreshNoticeData.builder()
 *     .message("刷新用户状态字典")
 *     .applicationName("user-service")
 *     .notifyBrother(true)
 *     .dictProviderClasses(Set.of("com.example.dict.UserStatusDictProvider"))
 *     .build();
 * </pre>
 *
 * @author HouKunLin
 * @since 1.4.4
 */
@Data
@Builder
public class RefreshNoticeData implements Serializable {
    /**
     * 事件消息文本内容，描述本次刷新的具体内容
     */
    private String message;
    /**
     * 消息来源应用名称，标识消息是由哪个应用发送的
     */
    private String applicationName;
    /**
     * 是否通知兄弟系统（相同 applicationName 的实例），true 表示通知，false 表示不通知
     */
    private boolean notifyBrother;
    /**
     * 通知需要刷新的字典提供者，指定哪些字典提供者需要刷新
     */
    private Set<String> dictProviderClasses;
}
