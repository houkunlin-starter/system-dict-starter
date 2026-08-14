package com.houkunlin.dict.actuator;

import com.houkunlin.dict.notice.RefreshDictEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.annotation.WriteOperation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 字典刷新端点，用于触发字典刷新事件。
 *
 * @author HouKunLin
 * @since 1.4.4
 */
@Endpoint(id = "dictRefresh")
@RequiredArgsConstructor
public class RefreshDictEndpoint implements InitializingBean {
    /**
     * 应用事件发布器
     */
    private final ApplicationEventPublisher applicationEventPublisher;
    /**
     * 成功返回结果
     */
    private final Map<String, Object> map = new HashMap<>();
    /**
     * 失败返回结果
     */
    private final Map<String, Object> mapErr = new HashMap<>();

    /**
     * 默认端点接口，触发字典刷新事件。
     *
     * @return 操作结果
     */
    @WriteOperation
    public Object index() {
        applicationEventPublisher.publishEvent(new RefreshDictEvent("Endpoint/dictRefresh"));
        return map;
    }

    /**
     * 触发字典刷新事件（带消息）。
     *
     * @param msg 刷新消息
     * @return 操作结果
     */
    @WriteOperation
    public Object indexMsg(@Selector String msg) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg));
        return map;
    }

    /**
     * 触发字典刷新事件（带消息和通知标记）。
     *
     * @param msg    刷新消息
     * @param notify 是否通知
     * @return 操作结果
     */
    @WriteOperation
    public Object indexMsgNotify(@Selector String msg, @Selector boolean notify) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg, notify));
        return map;
    }

    /**
     * 触发字典刷新事件（带消息、通知标记和兄弟节点通知标记）。
     *
     * @param msg           刷新消息
     * @param notify        是否通知
     * @param notifyBrother 是否通知兄弟节点
     * @return 操作结果
     */
    @WriteOperation
    public Object indexMsgNotifyBrother(@Selector String msg, @Selector boolean notify, @Selector boolean notifyBrother) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg, notify, notifyBrother));
        return map;
    }

    /**
     * 触发字典刷新事件（带消息、通知标记、兄弟节点通知标记和指定类）。
     *
     * @param msg          刷新消息
     * @param notify       是否通知
     * @param notifyBrother 是否通知兄弟节点
     * @param classes      指定的类集合
     * @return 操作结果
     */
    @WriteOperation
    public Object indexMsgNotifyBrotherClasses(@Selector String msg, @Selector boolean notify, @Selector boolean notifyBrother, @Selector Set<String> classes) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg, notify, notifyBrother, classes));
        return map;
    }

    /**
     * 初始化方法，设置返回结果。
     *
     * @throws Exception 异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        map.put("result", "ok");
        mapErr.put("result", "fail");
        mapErr.put("msg", "msg must not blank");
    }
}
