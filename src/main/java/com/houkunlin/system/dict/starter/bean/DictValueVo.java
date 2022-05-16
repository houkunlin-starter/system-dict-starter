package com.houkunlin.system.dict.starter.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * 字典值信息对象
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@ApiModel("字典值信息")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DictValueVo implements Serializable {
    /**
     * 字典类型代码
     */
    @ApiModelProperty(value = "字典类型代码", hidden = true)
    @JsonIgnore
    private String dictType;
    /**
     * 父级字典值，由父级字典值可以组成一个类似树形结构数据的字典信息。
     * 构建树形结构字典数据所需要的一个父级值；
     *
     * @since 1.4.6
     */
    @ApiModelProperty("父级字典值")
    private Object parentValue;
    /**
     * 字典值
     */
    @ApiModelProperty("字典值")
    private Object value;
    /**
     * 字典名称。
     * <p>在使用 {@link RefreshDictValueEvent} 事件时，此值为 null 意为删除这个字典值文本信息</p>
     */
    @ApiModelProperty("字典名称")
    private String title;
    /**
     * 数据字典值列表排序值
     */
    @ApiModelProperty("排序值（系统不会执行排序后再返回给前端）")
    private int sorted;
    /**
     * 是否禁用，禁用的字典文本不会从缓存中删除，因为禁用的字典文本可能在之前的数据中被使用。
     * 并且禁用的字典文本也不会从缓存的字典类型对象中删除，需要在调用 {@link RefreshDictTypeEvent} 时明确的把字典文本移除才能从字典类型缓存中删除。
     *
     * @since 1.4.9
     */
    @ApiModelProperty("是否禁用")
    private boolean disabled;
    /**
     * 子字典值列表
     *
     * @since 1.4.9
     */
    @ApiModelProperty("子字典值列表")
    private List<DictValueVo> children;

    public DictValueVo(final String dictType, final Object value, final String title, final int sorted) {
        this.dictType = dictType;
        this.value = value;
        this.title = title;
        this.sorted = sorted;
    }

    public DictValueVo(final String dictType, final Object parentValue, final Object value, final String title, final int sorted) {
        this.dictType = dictType;
        this.parentValue = parentValue;
        this.value = value;
        this.title = title;
        this.sorted = sorted;
    }
}
