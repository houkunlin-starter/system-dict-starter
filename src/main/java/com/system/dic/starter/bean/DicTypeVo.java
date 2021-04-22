package com.system.dic.starter.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 数据字典（类别、分类）
 *
 * @author HouKunLin
 * @date 2020/3/26 0026 23:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DicTypeVo implements Serializable {
    /**
     * 数据字典名称
     */
    private String title;
    /**
     * 数据字典类型（唯一值）
     */
    private String type;
    /**
     * 备注信息
     */
    private String remark;

    private List<DicValueVo<? extends Serializable>> children;
}
