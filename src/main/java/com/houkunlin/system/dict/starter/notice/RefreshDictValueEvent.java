package com.houkunlin.system.dict.starter.notice;

import com.houkunlin.system.dict.starter.bean.DictValueVo;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;

/**
 * 刷新单个字典文本信息。仅会刷新字典文本值，不会刷新和更新整个字典信息。
 * 使用此事件时 {@link DictValueVo#dictType} 字段值为必填项，否则会导致数据更新失败。
 *
 * @author HouKunLin
 * @since 1.4.3.4
 */
public class RefreshDictValueEvent extends ApplicationEvent {
    public RefreshDictValueEvent(final DictValueVo source) {
        super(Collections.singletonList(source));
    }

    public RefreshDictValueEvent(final Iterable<DictValueVo> source) {
        super(source);
    }

    @SuppressWarnings("all")
    @Override
    public Iterable<DictValueVo> getSource() {
        return (Iterable<DictValueVo>) super.getSource();
    }
}
