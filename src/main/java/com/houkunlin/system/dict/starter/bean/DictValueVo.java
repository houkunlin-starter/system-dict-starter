package com.houkunlin.system.dict.starter.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
     * 字典值
     */
    @ApiModelProperty("字典值")
    private Object value;
    /**
     * 字典名称
     */
    @ApiModelProperty("字典名称")
    private String title;
    /**
     * 数据字典值列表排序值
     */
    @ApiModelProperty("排序值（系统不会执行排序后再返回给前端）")
    private int sorted;
}
