package com.houkunlin.system.dict.starter.notice;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
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
     * @param dictTypeVo 字典类型（包含完整字典值列表信息）
     */
    public RefreshDictTypeEvent(final DictTypeVo dictTypeVo) {
        super(Collections.singletonList(dictTypeVo));
    }

    public RefreshDictTypeEvent(final Iterable<DictTypeVo> source) {
        super(source);
    }

    @SuppressWarnings("all")
    @Override
    public Iterable<DictTypeVo> getSource() {
        return (Iterable<DictTypeVo>) super.getSource();
    }
}
