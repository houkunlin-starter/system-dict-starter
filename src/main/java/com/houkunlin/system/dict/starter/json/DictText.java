package com.houkunlin.system.dict.starter.json;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.houkunlin.system.dict.starter.DictEnum;
import tools.jackson.databind.annotation.JsonSerialize;

import java.lang.annotation.*;

/**
 * 把数据字典值转换成数据字典文本。
 * 使用自定义注解好像发现一个无解的问题，自定义注解会使 @JsonIgnore 注解失效。
 * 本身 @JsonIgnore 会忽略字段，再使用自定义注解会出现：字段没有被忽略，但是自定义注解的功能被忽略了，也就是字段值照样输出，但是数据字典值无法生成。
 * 用在系统枚举对象上：标记该枚举的内容。
 * 用在实体类字段上：自动转换该字段的字典文本信息
 *
 * @author HouKunLin
 */
@JacksonAnnotationsInside
// @JsonSerialize(using = DictTextJsonSerializer.class)
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DictText {
    /**
     * 数据字典的代码。
     *
     * @return 数据字典代码
     */
    String value() default "";

    /**
     * 数据字典的代码处理器。
     * 在配置了 {@link #enums()} 时此配置无法生效，此配置优先于 {@link #value()} 配置
     *
     * @return 数据字典代码处理器
     * @since 1.4.7 beta 不稳定，此选项后期有可能会被删除
     */
    Class<? extends DictTypeKeyHandler> dictTypeHandler() default VoidDictTypeKeyHandler.class;

    /**
     * 显示字典文本的字段名称，假如为空字符串则默认为 字典字段名称 + "Text" 作为显示字典文本的字段名称
     *
     * @return 字典文本字段
     */
    String fieldName() default "";

    /**
     * 直接从系统字典枚举解析，不走Redis缓存
     *
     * @return 与当前字典有关的系统字典枚举列表
     */
    Class<? extends DictEnum>[] enums() default {};

    /**
     * 设置当没有获取到数据时是否为 null。
     *
     * @return Type
     */
    DictBoolType nullable() default DictBoolType.GLOBAL;

    /**
     * 此配置将会使 #fieldName 配置失效；用来标记是否使用 Map 对象返回字典值信息
     * <pre>
     * {
     *   "peopleType" : {
     *     "value" : 0,
     *     "text" : "系统管理"
     *   }
     * }
     * </pre>
     *
     * @return mapValue
     */
    DictBoolType mapValue() default DictBoolType.GLOBAL;

    /**
     * 标记是否替换原始值，不使用 {@link #fieldName} 字段输出，直接用字典文本替换原来的字典值输出。
     * 默认使用 全局配置 配置参数
     *
     * @return 是否替换原始值
     */
    DictBoolType replace() default DictBoolType.GLOBAL;

    /**
     * 数据字典分割成数组配置。
     * 用在字段是字符串时，并且字段使用了特定的分隔符来存储多个字典值。
     * 例如：
     * <p>userType = "1,2,3,4" 可配置 {@link Array#split()} = "," 进行分割</p>
     * <p>userType301 = Arrays.asList("0", "1", "3", "0", "0", "2") 可配置 {@link Array#toText()} = false 字典文本输出成数组</p>
     *
     * @return 分隔配置（默认不分割）
     * @since 1.4.3
     */
    Array array() default @Array(split = "");

    /**
     * 是否是树形结构数据；
     *
     * @return boolean <ul>
     * <li>true 是树形结构数据，加载父级信息（采用递归加载，可能会多次加载父级信息）；</li>
     * <li>false 不是树形结构数据，不加载父级信息；</li>
     * </ul>
     * @since 1.4.6
     */
    boolean tree() default false;

    /**
     * 为防止陷入死循环，请设置树形结构数据的向访问的最大访问深度，超过最大访问深度则直接返回。
     *
     * @return int &lt;= 0 视为不限制深度
     * @since 1.4.6.1
     */
    int treeDepth() default -1;

}
