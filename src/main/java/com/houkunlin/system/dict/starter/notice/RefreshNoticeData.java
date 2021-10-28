package com.houkunlin.system.dict.starter.notice;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * 刷新字典事件的通知信息对象
 *
 * @author HouKunLin
 * @since 1.4.4
 */
@Data
@Builder
public class RefreshNoticeData implements Serializable {
    /**
     * 事件消息文本内容
     */
    private String message;
    /**
     * 消息来源应用名称
     */
    private String applicationName;
    /**
     * 是否通知兄弟系统（相同 applicationName 的实例）
     */
    private boolean notifyBrother;
    /**
     * 通知需要刷新的字典提供者
     */
    private Set<String> dictProviderClasses;
}
