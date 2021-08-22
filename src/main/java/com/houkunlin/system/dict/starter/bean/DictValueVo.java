package com.houkunlin.system.dict.starter.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DictValueVo implements Serializable {
    /**
     * 字典类型代码
     */
    @JsonIgnore
    private String dictType;
    /**
     * 字段值
     */
    private Object value;
    /**
     * 字段名称
     */
    private String title;
    /**
     * 数据字典值列表排序值
     */
    private int sorted;
}
