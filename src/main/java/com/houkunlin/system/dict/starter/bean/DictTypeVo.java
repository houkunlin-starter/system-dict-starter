package com.houkunlin.system.dict.starter.bean;

import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典（类别、分类）
 *
 * @author HouKunLin
 */
@ApiModel("字典类型信息")
@Schema(name = "字典类型信息")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DictTypeVo implements Serializable {
    /**
     * 数据字典名称
     *
     * @return 数据字典名称
     */
    @ApiModelProperty("数据字典名称")
    @Schema(title = "数据字典名称")
    private String title;
    /**
     * 数据字典类型（唯一值）
     *
     * @return 数据字典类型（唯一值）
     */
    @ApiModelProperty("数据字典类型（唯一值）")
    @Schema(title = "数据字典类型（唯一值）")
    private String type;
    /**
     * 备注信息
     *
     * @return 备注信息
     */
    @ApiModelProperty("备注信息")
    @Schema(title = "备注信息")
    private String remark;
    /**
     * 字典值列表
     * <p>在使用 {@link RefreshDictTypeEvent} 事件时，此值为 null 意为删除这个字典类型对象信息</p>
     *
     * @return 字典值列表
     */
    @ApiModelProperty("字典值列表")
    @Schema(title = "字典值列表")
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
     * 字典类型构建
     * @since 1.4.3
     */
    public static class DictTypeBuilder {
        /**
         * 字典类型
         */
        private final String type;
        /**
         * 字典标题名称
         */
        private final String title;
        /**
         * 备注信息
         */
        private final String remark;
        /**
         * 字典值列表
         */
        private final List<DictValueVo> children = new ArrayList<>();

        /**
         * 构造方法
         *
         * @param type   字典类型
         * @param title  字典名称标题
         * @param remark 备注
         */
        public DictTypeBuilder(final String type, final String title, final String remark) {
            this.type = type;
            this.title = title;
            this.remark = remark;
        }

        /**
         * 构造方法
         *
         * @param type  字典类型
         * @param title 字典名称标题
         */
        public DictTypeBuilder(final String type, final String title) {
            this.type = type;
            this.title = title;
            this.remark = null;
        }

        /**
         * 增加一个字典值信息
         *
         * @param dictValueVo 字典值对象
         * @return Builder
         */
        public DictTypeVo.DictTypeBuilder add(final DictValueVo dictValueVo) {
            dictValueVo.setDictType(type);
            this.children.add(dictValueVo);
            return this;
        }

        /**
         * 增加一个字典值信息
         *
         * @param value 字典值
         * @param title 字典值文本
         * @return Builder
         */
        public DictTypeVo.DictTypeBuilder add(final Object value, final String title) {
            this.children.add(new DictValueVo(type, value, title, 0));
            return this;
        }

        /**
         * 增加一个字典值信息
         * @param value 字典值
         * @param title 字典值文本
         * @param sorted 字典值排序
         * @return Builder
         */
        public DictTypeVo.DictTypeBuilder add(final Object value, final String title, final int sorted) {
            this.children.add(new DictValueVo(type, value, title, sorted));
            return this;
        }

        /**
         * 树形结构数据
         *
         * @param parentValue 字典父级值
         * @param value       字典值
         * @param title       字典文本
         * @return this
         * @since 1.4.6
         */
        public DictTypeVo.DictTypeBuilder add(final Object parentValue, final Object value, final String title) {
            this.children.add(new DictValueVo(type, parentValue, value, title, 0));
            return this;
        }

        /**
         * 树形结构数据
         *
         * @param parentValue 字典父级值
         * @param value       字典值
         * @param title       字典文本
         * @param sorted      排序值
         * @return this
         * @since 1.4.6
         */
        public DictTypeVo.DictTypeBuilder add(final Object parentValue, final Object value, final String title, final int sorted) {
            this.children.add(new DictValueVo(type, parentValue, value, title, sorted));
            return this;
        }

        /**
         * 构建一个字典类型对象
         * @return 字典类型对象
         */
        public DictTypeVo build() {
            return new DictTypeVo(title, type, remark, children);
        }

        /**
         * 字典值列表
         * @return 字典值列表
         */
        public List<DictValueVo> dictValues() {
            return children;
        }
    }
}
