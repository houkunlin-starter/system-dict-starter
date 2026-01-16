package com.houkunlin.dict.notice;

import com.houkunlin.dict.bean.DictType;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;

/**
 * 刷新单个字典类型信息（包含完整字典值信息）。
 *
 * @author HouKunLin
 * @since 1.4.5
 */
public class RefreshDictTypeEvent extends ApplicationEvent {
    /**
     * 刷新单个字典类型信息
     *
     * @param dictType 字典类型（包含完整字典值列表信息）
     */
    public RefreshDictTypeEvent(final DictType dictType) {
        super(Collections.singletonList(dictType));
    }

    public RefreshDictTypeEvent(final Iterable<DictType> source) {
        super(source);
    }

    @SuppressWarnings("all")
    @Override
    public Iterable<DictType> getSource() {
        return (Iterable<DictType>) super.getSource();
    }
}
