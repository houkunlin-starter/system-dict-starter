package com.houkunlin.dict.notice;

import com.houkunlin.dict.bean.DictType;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;

/**
 * 刷新单个字典类型信息事件
 * <p>
 * 该事件用于刷新单个字典类型的完整信息，包含字典类型本身和其对应的字典值列表信息。
 * 当需要更新整个字典类型的信息时，应使用此事件。
 * </p>
 *
 * @author HouKunLin
 * @since 1.4.5
 */
public class RefreshDictTypeEvent extends ApplicationEvent {
    /**
     * 刷新单个字典类型信息
     * <p>
     * 创建一个刷新单个字典类型信息的事件，包含完整的字典值列表信息。
     * </p>
     *
     * @param dictType 字典类型（包含完整字典值列表信息）
     */
    public RefreshDictTypeEvent(final DictType dictType) {
        super(Collections.singletonList(dictType));
    }

    /**
     * 刷新多个字典类型信息
     * <p>
     * 创建一个刷新多个字典类型信息的事件，包含完整的字典值列表信息。
     * </p>
     *
     * @param source 字典类型集合（每个字典类型都包含完整字典值列表信息）
     */
    public RefreshDictTypeEvent(final Iterable<DictType> source) {
        super(source);
    }

    /**
     * 获取事件源，即字典类型对象集合
     * <p>
     * 重写父类方法，返回类型为 Iterable&lt;DictType&gt;，方便直接使用。
     * </p>
     *
     * @return 字典类型对象集合
     */
    @SuppressWarnings("all")
    @Override
    public Iterable<DictType> getSource() {
        return (Iterable<DictType>) super.getSource();
    }
}
