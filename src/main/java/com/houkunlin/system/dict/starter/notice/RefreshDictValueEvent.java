package com.houkunlin.system.dict.starter.notice;

import com.houkunlin.system.dict.starter.DictController;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;

/**
 * 刷新单个字典文本信息。仅会刷新字典文本值，不会刷新和更新整个字典信息。
 * 使用此事件时 {@link DictValueVo#dictType} 字段值为必填项，否则会导致数据更新失败。
 *
 * @author HouKunLin
 * @since 1.4.4
 */
@Getter
public class RefreshDictValueEvent extends ApplicationEvent {
    /**
     * <p>是否需要更新维护字典类型对象里面的字典值列表信息。</p>
     * <p>此值无论为 true 还是 false 都会更新单个字典值文本信息。</p>
     * <p>但是此值为 true 时会去维护对应的 字典类型里面的字典值列表数据，字典类型的字典值列表数据量大时可能会有一点耗时问题。</p>
     * <p>此值为 false 时不会去维护字典类型里面的值列表数据（使用 {@link DictUtil#getDictType(java.lang.String)}、{@link DictController#dictType(java.lang.String)}、{@link DictController#dictTypeQuery(java.lang.String)}
     * 得到的数据与最新的字典文本不一致）</p>
     */
    private final boolean updateDictType;

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValueVo 字典值对象（必须要有 {@link DictValueVo#dictType} 字典类型值）
     */
    public RefreshDictValueEvent(final DictValueVo dictValueVo) {
        super(Collections.singletonList(dictValueVo));
        this.updateDictType = true;
    }

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValueVos 多个字典值对象（必须要有 {@link DictValueVo#dictType} 字典类型值）
     */
    public RefreshDictValueEvent(final Iterable<DictValueVo> dictValueVos) {
        super(dictValueVos);
        this.updateDictType = true;
    }

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValueVo    字典值对象（必须要有 {@link DictValueVo#dictType} 字典类型值）
     * @param updateDictType 是否更新维护字典类型对象里面的字典值列表信息
     */
    public RefreshDictValueEvent(final DictValueVo dictValueVo, final boolean updateDictType) {
        super(Collections.singletonList(dictValueVo));
        this.updateDictType = updateDictType;
    }

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValueVos   多个字典值对象（必须要有 {@link DictValueVo#dictType} 字典类型值）
     * @param updateDictType 是否更新维护字典类型对象里面的字典值列表信息
     */
    public RefreshDictValueEvent(final Iterable<DictValueVo> dictValueVos, final boolean updateDictType) {
        super(dictValueVos);
        this.updateDictType = updateDictType;
    }

    @SuppressWarnings("all")
    @Override
    public Iterable<DictValueVo> getSource() {
        return (Iterable<DictValueVo>) super.getSource();
    }
}
