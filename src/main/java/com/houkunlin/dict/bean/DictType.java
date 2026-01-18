package com.houkunlin.dict.bean;

import com.houkunlin.dict.notice.RefreshDictTypeEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据字典类型信息对象，用于表示数据字典的类别、分类信息。
 *
 * @author HouKunLin
 */
@Schema(description = "字典类型信息")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictType implements Serializable {
    /**
     * 数据字典名称
     */
    @Schema(description = "数据字典名称")
    private String title;
    /**
     * 数据字典类型（唯一值）
     */
    @Schema(description = "数据字典类型（唯一值）")
    private String type;
    /**
     * 备注信息
     */
    @Schema(description = "备注信息")
    private String remark;
    /**
     * 字典值列表
     * <p>在使用 {@link RefreshDictTypeEvent} 事件时，此值为 null 意为删除这个字典类型对象信息</p>
     */
    @Schema(description = "字典值列表")
    private List<DictValue> children;

    /**
     * 构造字典类型信息构建器。
     *
     * @param type  字典类型代码
     * @param title 字典类型名称
     * @return DictType.DictTypeBuilder 构建器对象
     * @since 1.4.3
     */
    public static DictType.DictTypeBuilder newBuilder(final String type, final String title) {
        return new DictType.DictTypeBuilder(type, title);
    }

    /**
     * 构造字典类型信息构建器。
     *
     * @param type   字典类型代码
     * @param title  字典类型名称
     * @param remark 备注信息
     * @return DictType.DictTypeBuilder 构建器对象
     * @since 1.4.3
     */
    public static DictType.DictTypeBuilder newBuilder(final String type, final String title, final String remark) {
        return new DictType.DictTypeBuilder(type, title, remark);
    }

    /**
     * 字典类型构建器，用于链式构建字典类型对象。
     *
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
        private final List<DictValue> children = new ArrayList<>();

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
         * @param dictValue 字典值对象
         * @return DictTypeBuilder 构建器对象
         */
        public DictType.DictTypeBuilder add(final DictValue dictValue) {
            dictValue.setDictType(type);
            this.children.add(dictValue);
            return this;
        }

        /**
         * 增加一个字典值信息
         *
         * @param value 字典值
         * @param title 字典值文本
         * @return DictTypeBuilder 构建器对象
         */
        public DictType.DictTypeBuilder add(final Object value, final String title) {
            this.children.add(new DictValue(type, value, title, 0));
            return this;
        }

        /**
         * 增加一个字典值信息
         *
         * @param value  字典值
         * @param title  字典值文本
         * @param sorted 字典值排序
         * @return DictTypeBuilder 构建器对象
         */
        public DictType.DictTypeBuilder add(final Object value, final String title, final int sorted) {
            this.children.add(new DictValue(type, value, title, sorted));
            return this;
        }

        /**
         * 增加一个树形结构字典值信息
         *
         * @param parentValue 字典父级值
         * @param value       字典值
         * @param title       字典文本
         * @return DictTypeBuilder 构建器对象
         * @since 1.4.6
         */
        public DictType.DictTypeBuilder add(final Object parentValue, final Object value, final String title) {
            this.children.add(new DictValue(type, parentValue, value, title, 0));
            return this;
        }

        /**
         * 增加一个树形结构字典值信息
         *
         * @param parentValue 字典父级值
         * @param value       字典值
         * @param title       字典文本
         * @param sorted      排序值
         * @return DictTypeBuilder 构建器对象
         * @since 1.4.6
         */
        public DictType.DictTypeBuilder add(final Object parentValue, final Object value, final String title, final int sorted) {
            this.children.add(new DictValue(type, parentValue, value, title, sorted));
            return this;
        }

        /**
         * 构建一个字典类型对象
         *
         * @return 字典类型对象
         */
        public DictType build() {
            return new DictType(title, type, remark, children);
        }

        /**
         * 获取字典值列表
         *
         * @return 字典值列表
         */
        public List<DictValue> dictValues() {
            return children;
        }
    }
}
