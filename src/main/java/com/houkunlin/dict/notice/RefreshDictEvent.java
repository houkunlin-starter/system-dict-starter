package com.houkunlin.dict.notice;

import com.houkunlin.dict.provider.DictProvider;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Set;

/**
 * 刷新字典事件
 * <p>
 * 该事件用于通知系统刷新Redis数据字典，支持多种刷新模式：
 * 1. 仅刷新本地系统的字典数据
 * 2. 同时通知其他系统刷新字典数据
 * 3. 通知其他系统的同时，也通知本系统的兄弟实例
 * 4. 限定只刷新指定的字典提供者
 * </p>
 *
 * @author HouKunLin
 */
@Getter
public class RefreshDictEvent extends ApplicationEvent {
    /**
     * 是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     */
    private final boolean notifyOtherSystem;
    /**
     * 使用MQ通知其他系统的同时，也通知本系统的兄弟系统（同一个系统部署多个实例）
     */
    private final boolean notifyOtherSystemAndBrother;
    /**
     * 限定只刷新指定的 DictProvider 对象。当为 null 或者 空列表 时会刷新所有的 DictProvider 数据。
     * 通常传入 DictProvider 的完整 class 名称（默认），或者传入 DictType 需要自定实现 {@link DictProvider#supportRefresh(java.util.Set)} 方法
     */
    private final Set<String> dictProviderClasses;

    /**
     * 创建刷新字典事件
     * <p>
     * 默认不通知其他系统，也不通知兄弟系统，刷新所有字典提供者的数据。
     * </p>
     *
     * @param source 事件来源等相关信息
     */
    public RefreshDictEvent(final Object source) {
        super(source);
        this.notifyOtherSystem = false;
        this.notifyOtherSystemAndBrother = false;
        this.dictProviderClasses = null;
    }

    /**
     * 创建刷新字典事件
     * <p>
     * 默认不通知其他系统，也不通知兄弟系统，只刷新指定的字典提供者的数据。
     * </p>
     *
     * @param source             事件来源等相关信息
     * @param dictProviderClasses 限定只刷新指定的 DictProvider 对象。当为 null 或者 空列表 时会刷新所有的 DictProvider 数据
     */
    public RefreshDictEvent(final Object source, final Set<String> dictProviderClasses) {
        super(source);
        this.notifyOtherSystem = false;
        this.notifyOtherSystemAndBrother = false;
        this.dictProviderClasses = dictProviderClasses;
    }

    /**
     * 创建刷新字典事件
     * <p>
     * 可选择是否通知其他系统，默认不通知兄弟系统，刷新所有字典提供者的数据。
     * </p>
     *
     * @param source            事件来源等相关信息
     * @param notifyOtherSystem 是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     */
    public RefreshDictEvent(final Object source, final boolean notifyOtherSystem) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = false;
        this.dictProviderClasses = null;
    }

    /**
     * 创建刷新字典事件
     * <p>
     * 可选择是否通知其他系统，默认不通知兄弟系统，只刷新指定的字典提供者的数据。
     * </p>
     *
     * @param source             事件来源等相关信息
     * @param notifyOtherSystem  是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     * @param dictProviderClasses 限定只刷新指定的 DictProvider 对象。当为 null 或者 空列表 时会刷新所有的 DictProvider 数据
     */
    public RefreshDictEvent(final Object source, final boolean notifyOtherSystem, final Set<String> dictProviderClasses) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = false;
        this.dictProviderClasses = dictProviderClasses;
    }

    /**
     * 创建刷新字典事件
     * <p>
     * 可选择是否通知其他系统和兄弟系统，刷新所有字典提供者的数据。
     * </p>
     *
     * @param source                      事件来源等相关信息
     * @param notifyOtherSystem           是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     * @param notifyOtherSystemAndBrother 使用MQ通知其他系统的同时，也通知本系统的兄弟系统（同一个系统部署多个实例）
     */
    public RefreshDictEvent(final Object source, final boolean notifyOtherSystem, final boolean notifyOtherSystemAndBrother) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = notifyOtherSystemAndBrother;
        this.dictProviderClasses = null;
    }

    /**
     * 创建刷新字典事件
     * <p>
     * 可选择是否通知其他系统和兄弟系统，只刷新指定的字典提供者的数据。
     * </p>
     *
     * @param source                      事件来源等相关信息
     * @param notifyOtherSystem           是否通知其他的系统。使用 MQ 进行广播通知其他系统更新数据字典
     * @param notifyOtherSystemAndBrother 使用MQ通知其他系统的同时，也通知本系统的兄弟系统（同一个系统部署多个实例）
     * @param dictProviderClasses         限定只刷新指定的 DictProvider 对象。当为 null 或者 空列表 时会刷新所有的 DictProvider 数据
     */
    public RefreshDictEvent(final Object source, final boolean notifyOtherSystem, final boolean notifyOtherSystemAndBrother, final Set<String> dictProviderClasses) {
        super(source);
        this.notifyOtherSystem = notifyOtherSystem;
        this.notifyOtherSystemAndBrother = notifyOtherSystemAndBrother;
        this.dictProviderClasses = dictProviderClasses;
    }
}
