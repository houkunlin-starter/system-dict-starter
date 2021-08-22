package com.houkunlin.system.dict.starter.notice;

import com.houkunlin.system.dict.starter.provider.DicProvider;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * 刷新字典事件，通知系统刷新Redis数据字典
 *
 * @author HouKunLin
 */
@Getter
public class RefreshDicEvent extends ApplicationEvent {
    /**
     * 是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     */
    private final boolean notifyOtherSystem;
    /**
     * 使用MQ通知其他系统的同时，也通知本系统的兄弟系统（同一个系统部署多个实例）
     */
    private final boolean notifyOtherSystemAndBrother;
    /**
     * 限定只刷新指定的 DicProvider 对象。当为 null 或者 空列表 时会刷新所有的 DicProvider 数据。
     * 通常传入 DicProvider 的完整 class 名称（默认），或者传入 DicType 需要自定实现 {@link DicProvider#supportRefresh(java.util.Set)} 方法
     */
    private final Set<String> dicProviderClasses;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public RefreshDicEvent(final Object source) {
        super(source);
        this.notifyOtherSystem = false;
        this.notifyOtherSystemAndBrother = false;
        this.dicProviderClasses = null;
    }

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source             the object on which the event initially occurred or with
     *                           which the event is associated (never {@code null})
     * @param dicProviderClasses 限定只刷新指定的 DicProvider 对象。当为 null 或者 空列表 时会刷新所有的 DicProvider 数据
     */
    public RefreshDicEvent(final Object source, final Set<String> dicProviderClasses) {
        super(source);
        this.notifyOtherSystem = false;
        this.notifyOtherSystemAndBrother = false;
        this.dicProviderClasses = dicProviderClasses;
    }

    /**
     * 刷新字典事件，通知系统刷新Redis数据字典
     *
     * @param source            事件来源等相关信息
     * @param notifyOtherSystem 是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     */
    public RefreshDicEvent(final Object source, final boolean notifyOtherSystem) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = false;
        this.dicProviderClasses = null;
    }

    /**
     * 刷新字典事件，通知系统刷新Redis数据字典
     *
     * @param source             事件来源等相关信息
     * @param notifyOtherSystem  是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     * @param dicProviderClasses 限定只刷新指定的 DicProvider 对象。当为 null 或者 空列表 时会刷新所有的 DicProvider 数据
     */
    public RefreshDicEvent(final Object source, final boolean notifyOtherSystem, final Set<String> dicProviderClasses) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = false;
        this.dicProviderClasses = dicProviderClasses;
    }

    /**
     * 刷新字典事件，通知系统刷新Redis数据字典
     *
     * @param source                      事件来源等相关信息
     * @param notifyOtherSystem           是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     * @param notifyOtherSystemAndBrother 使用MQ通知其他系统的同时，也通知本系统的兄弟系统（同一个系统部署多个实例）
     */
    public RefreshDicEvent(final Object source, final boolean notifyOtherSystem, final boolean notifyOtherSystemAndBrother) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = notifyOtherSystemAndBrother;
        this.dicProviderClasses = null;
    }

    public RefreshDicEvent(final Object source, final boolean notifyOtherSystem, final boolean notifyOtherSystemAndBrother, final Set<String> dicProviderClasses) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = notifyOtherSystemAndBrother;
        this.dicProviderClasses = dicProviderClasses;
    }
}
