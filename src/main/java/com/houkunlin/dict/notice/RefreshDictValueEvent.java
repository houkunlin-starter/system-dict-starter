package com.houkunlin.dict.notice;

import com.houkunlin.dict.DictController;
import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.bean.DictValue;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Collections;

/**
 * 刷新单个字典文本信息。仅会刷新字典文本值，不会刷新和更新整个字典信息。
 * 使用此事件时 {@link DictValue#dictType} 字段值为必填项，否则会导致数据更新失败。
 *
 * @author HouKunLin
 * @since 1.4.4
 */
@Getter
public class RefreshDictValueEvent extends ApplicationEvent {
    /**
     * <p>是否需要更新维护字典类型对象里面的字典值列表信息。</p>
     * <p>此值无论为 true 还是 false 都会更新单个字典值文本信息。</p>
     * <p>但是此值为 true 时会去维护对应的 字典类型里面 的字典值列表数据，字典类型的字典值列表数据量大时可能会有一些耗时问题。</p>
     * <p>此值为 false 时不会去维护字典类型里面的值列表数据（使用 {@link DictUtil#getDictType(java.lang.String)}、{@link DictController#dictType(java.lang.String, java.lang.Integer)}、{@link DictController#dictTypeQuery(java.lang.String, java.lang.Integer)}
     * 得到的数据与最新的字典文本不一致）</p>
     *
     * @since 1.4.5
     */
    private final boolean updateDictType;
    /**
     * 此值生效的前提是把 {@link #updateDictType} 设置为 true，意为：当字典类型的字典值列表为空集合时是否删除整个字典类型对象。
     * <p>在使用此事件时可以删除字典值文本信息，设置 {@link #updateDictType} = true 在删除字典值文本信息时维护字典类型对象信息（维护字典类型对象的字典值列表，保证数据一致），
     * 因此会出现某个字典类型对象下没有任何字典值信息，此参数用来决定这种情况下对字典类型的处理方式，当字典类型下的所有字典值被删除后是否把字典类型也同时删除。</p>
     *
     * @since 1.4.5.1
     */
    private final boolean removeDictType;

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValue 字典值对象（必须要有 {@link DictValue#dictType} 字典类型值）
     */
    public RefreshDictValueEvent(final DictValue dictValue) {
        super(Collections.singletonList(dictValue));
        this.updateDictType = true;
        this.removeDictType = true;
    }

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValueVos 多个字典值对象（必须要有 {@link DictValue#dictType} 字典类型值）
     */
    public RefreshDictValueEvent(final Iterable<DictValue> dictValueVos) {
        super(dictValueVos);
        this.updateDictType = true;
        this.removeDictType = true;
    }

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValue    字典值对象（必须要有 {@link DictValue#dictType} 字典类型值）
     * @param updateDictType 是否更新维护字典类型对象里面的字典值列表信息
     */
    public RefreshDictValueEvent(final DictValue dictValue, final boolean updateDictType) {
        super(Collections.singletonList(dictValue));
        this.updateDictType = updateDictType;
        this.removeDictType = false;
    }

    /**
     * 刷新单个字典值文本信息
     *
     * @param dictValueVos   多个字典值对象（必须要有 {@link DictValue#dictType} 字典类型值）
     * @param updateDictType 是否更新维护字典类型对象里面的字典值列表信息
     */
    public RefreshDictValueEvent(final Iterable<DictValue> dictValueVos, final boolean updateDictType) {
        super(dictValueVos);
        this.updateDictType = updateDictType;
        this.removeDictType = false;
    }

    @SuppressWarnings("all")
    @Override
    public Iterable<DictValue> getSource() {
        return (Iterable<DictValue>) super.getSource();
    }
}
