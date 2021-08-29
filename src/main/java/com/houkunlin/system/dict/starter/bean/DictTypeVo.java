package com.houkunlin.system.dict.starter.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 数据字典（类别、分类）
 *
 * @author HouKunLin
 */
@ApiModel("字典类型信息")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictTypeVo implements Serializable {
    /**
     * 数据字典名称
     */
    @ApiModelProperty("数据字典名称")
    private String title;
    /**
     * 数据字典类型（唯一值）
     */
    @ApiModelProperty("数据字典类型（唯一值）")
    private String type;
    /**
     * 备注信息
     */
    @ApiModelProperty("备注信息")
    private String remark;
    /**
     * 字典值列表
     */
    @ApiModelProperty("字典值列表")
    private List<DictValueVo> children;
}
