package com.houkunlin.system.dict.starter.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 字典值信息对象
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@ApiModel("字典值信息")
@Data
@Builder
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

    public DictValueVo(final String dictType, final Object value, final String title, final int sorted) {
        this.dictType = dictType;
        this.value = value;
        this.title = title;
        this.sorted = sorted;
    }
}
