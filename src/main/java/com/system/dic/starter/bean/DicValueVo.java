package com.system.dic.starter.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HouKunLin
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DicValueVo<V extends Serializable> implements Serializable {
    /**
     * 字典类型代码
     */
    @JsonIgnore
    private String dicType;
    /**
     * 字段值
     */
    private V value;
    /**
     * 字段名称
     */
    private String title;
    /**
     * 备注信息
     */
    private String remark;
    /**
     * 数据字典值列表排序值
     */
    private Integer sorted;
}
