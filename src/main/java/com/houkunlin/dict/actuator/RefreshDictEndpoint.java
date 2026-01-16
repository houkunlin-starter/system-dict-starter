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
 * 字典刷新端点
 *
 * @author HouKunLin
 * @since 1.4.4
 */
@Endpoint(id = "dictRefresh")
@RequiredArgsConstructor
public class RefreshDictEndpoint implements InitializingBean {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final Map<String, Object> map = new HashMap<>();
    private final Map<String, Object> mapErr = new HashMap<>();

    /**
     * 默认端点接口信息
     *
     * @return 返回系统一些class对象名称
     */
    @WriteOperation
    public Object index() {
        applicationEventPublisher.publishEvent(new RefreshDictEvent("Endpoint/dictRefresh"));
        return map;
    }

    @WriteOperation
    public Object indexMsg(@Selector String msg) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg));
        return map;
    }

    @WriteOperation
    public Object indexMsgNotify(@Selector String msg, @Selector boolean notify) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg, notify));
        return map;
    }

    @WriteOperation
    public Object indexMsgNotifyBrother(@Selector String msg, @Selector boolean notify, @Selector boolean notifyBrother) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg, notify, notifyBrother));
        return map;
    }

    @WriteOperation
    public Object indexMsgNotifyBrotherClasses(@Selector String msg, @Selector boolean notify, @Selector boolean notifyBrother, @Selector Set<String> classes) {
        if (!StringUtils.hasText(msg)) {
            return mapErr;
        }
        applicationEventPublisher.publishEvent(new RefreshDictEvent(msg, notify, notifyBrother, classes));
        return map;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        map.put("result", "ok");
        mapErr.put("result", "fail");
        mapErr.put("msg", "msg must not blank");
    }
}
