package com.system.dic.starter.notice;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 刷新字典事件，通知系统刷新Redis数据字典
 *
 * @author HouKunLin
 */
@Getter
public class RefreshDicEvent extends ApplicationEvent {
    /**
     * 是否通知所有的系统。使用 MQ 进行广播通知所有系统更新数据字典
     */
    private final boolean notifyAllSystem;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public RefreshDicEvent(final Object source) {
        super(source);
        this.notifyAllSystem = false;
    }

    /**
     * 刷新字典事件，通知系统刷新Redis数据字典
     *
     * @param source          事件来源等相关信息
     * @param notifyAllSystem 是否通知所有的系统。使用 MQ 进行广播通知所有系统更新数据字典
     */
    public RefreshDicEvent(final Object source, final boolean notifyAllSystem) {
        super(source);
        this.notifyAllSystem = notifyAllSystem;
    }
}
