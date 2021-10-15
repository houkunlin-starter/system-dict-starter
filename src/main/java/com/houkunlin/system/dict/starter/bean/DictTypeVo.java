package com.houkunlin.system.dict.starter.bean;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
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

    /**
     * 构造字典类型信息
     *
     * @param type  字典类型代码
     * @param title 字典类型名称
     * @return DictTypeVo.DictTypeBuilder
     * @since 1.4.3
     */
    public static DictTypeVo.DictTypeBuilder newBuilder(final String type, final String title) {
        return new DictTypeVo.DictTypeBuilder(type, title);
    }

    /**
     * 构造字典类型信息
     *
     * @param type   字典类型代码
     * @param title  字典类型名称
     * @param remark 备注信息
     * @return DictTypeVo.DictTypeBuilder
     * @since 1.4.3
     */
    public static DictTypeVo.DictTypeBuilder newBuilder(final String type, final String title, final String remark) {
        return new DictTypeVo.DictTypeBuilder(type, title, remark);
    }

    /**
     * @since 1.4.3
     */
    public static class DictTypeBuilder {
        private final String type;
        private final String title;
        private final String remark;
        private final List<DictValueVo> children = new ArrayList<>();

        public DictTypeBuilder(final String type, final String title, final String remark) {
            this.type = type;
            this.title = title;
            this.remark = remark;
        }

        public DictTypeBuilder(final String type, final String title) {
            this.type = type;
            this.title = title;
            this.remark = null;
        }

        public DictTypeVo.DictTypeBuilder add(final Object value, final String title) {
            this.children.add(new DictValueVo(type, value, title, 0));
            return this;
        }

        public DictTypeVo build() {
            return new DictTypeVo(title, type, remark, children);
        }

        public List<DictValueVo> dictValues() {
            return children;
        }
    }
}
