package com.houkunlin.dict;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Map;

/**
 * 数据字典枚举接口
 * <p>
 * 该接口定义了数据字典枚举的基本方法，用于统一管理系统中的字典枚举类型。
 * 实现该接口的枚举类可以作为系统字典的数据源，提供字典值、字典文本等信息。
 * 主要功能包括：
 * <ul>
 * <li>获取字典值</li>
 * <li>获取字典文本</li>
 * <li>获取父级字典值（可选）</li>
 * <li>获取排序值（可选）</li>
 * <li>判断是否禁用（可选）</li>
 * <li>判断字典值是否相等</li>
 * <li>通过枚举值获取枚举对象</li>
 * </ul>
 * 该接口使用了泛型 T 来表示字典值的类型，要求实现类的字典值类型必须实现 Serializable 接口。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 * @param <T> 字典值类型，必须实现 Serializable 接口
 */
public interface DictEnum<T extends Serializable> {
    /**
     * 通过枚举值从枚举列表中获取枚举对象
     * <p>
     * 该静态方法用于根据枚举值从给定的枚举对象列表中查找对应的枚举对象。
     * 实现逻辑是遍历枚举对象列表，比较每个枚举对象的字典值是否与传入的枚举值相等，
     * 如果找到相等的，则返回对应的枚举对象；否则返回 null。
     * </p>
     *
     * @param values 枚举对象列表
     * @param value  枚举值
     * @param <T>    枚举值类型，必须实现 Serializable 接口
     * @param <E>    枚举对象类型，必须同时实现 Enum 接口和 DictEnum 接口
     * @return 找到的枚举对象，如果没有找到则返回 null
     */
    static <T extends Serializable, E extends Enum<E> & DictEnum<T>> E valueOf(E[] values, T value) {
        for (final E enums : values) {
            if (enums.getValue().equals(value)) {
                return enums;
            }
        }
        return null;
    }

    /**
     * 父级字典值
     * <p>
     * 该方法用于获取当前字典值的父级字典值，用于构建字典的树形结构。
     * 默认实现返回 null，表示当前字典值没有父级。
     * </p>
     *
     * @return 父级字典值
     */
    default T getParentValue() {
        return null;
    }

    /**
     * 字典值
     * <p>
     * 该方法用于获取当前字典枚举的字典值，是字典枚举的核心属性。
     * 使用了 @JsonValue 注解，使得在 JSON 序列化时，枚举对象会被序列化为其字典值。
     * </p>
     *
     * @return 字典值
     */
    @JsonValue
    T getValue();

    /**
     * 字典文本
     * <p>
     * 该方法用于获取当前字典枚举的字典文本，用于在界面上显示。
     * </p>
     *
     * @return 字典文本
     */
    String getTitle();

    /**
     * 排序值
     * <p>
     * 该方法用于获取当前字典枚举的排序值，用于在字典列表中排序。
     * 默认实现返回 0，表示使用默认排序。
     * </p>
     *
     * @return 排序值
     */
    default int getSorted() {
        return 0;
    }

    /**
     * 是否禁用
     * <p>
     * 该方法用于判断当前字典枚举是否禁用，禁用的字典值通常不会在界面上显示。
     * 默认实现返回 false，表示当前字典值未禁用。
     * </p>
     *
     * @return 是否禁用
     */
    default boolean isDisabled() {
        return false;
    }

    /**
     * 扩展数据
     * <p>
     * 用于存储字典项的额外信息，可根据业务需求自由扩展
     * </p>
     * <p>
     * 使用场景示例：
     * <ul>
     * <li>1. 存储字典项的颜色值，如状态字典中不同状态对应不同颜色</li>
     * <li>2. 存储字典项的图标信息，如菜单类型字典中不同类型对应不同图标</li>
     * <li>3. 存储字典项的业务属性，如用户类型字典中不同类型对应的权限标识</li>
     * <li>4. 存储字典项的国际化信息，如多语言环境下的不同语言文本</li>
     * <li>5. 存储字典项的关联信息，如部门字典中关联的上级部门详情</li>
     * </ul>
     * </p>
     * <p>
     * 注意事项：
     * <ul>
     * <li>1. 扩展数据的键值对可以根据业务需求自定义，不强制要求使用固定的键名</li>
     * <li>2. 扩展数据的键值对可以为空，即返回 null 或空 Map</li>
     * </ul>
     * </p>
     * <p>
     * 示例：
     * <pre>{@code
     *
     * // 示例1：状态字典中存储颜色值
     * public enum StatusDictEnum implements DictEnum<Integer> {
     *     SUCCESS(1, "成功"),
     *     FAILURE(2, "失败");
     *
     *     private final Integer value;
     *     private final String title;
     *
     *     StatusDictEnum(Integer value, String title) {
     *         this.value = value;
     *         this.title = title;
     *     }
     *
     *     @Override
     *     public Integer getValue() {
     *         return value;
     *     }
     *
     *     @Override
     *     public String getTitle() {
     *         return title;
     *     }
     *
     *     @Override
     *     public Map<String, Object> getData() {
     *         // 根据不同状态返回不同的颜色值
     *         if (this == SUCCESS) {
     *             return Map.of("color", "green", "icon", "check-circle");
     *         } else if (this == FAILURE) {
     *             return Map.of("color", "red", "icon", "times-circle");
     *         }
     *         return null;
     *     }
     * }
     *
     * // 示例2：用户类型字典中存储权限标识
     * public enum UserTypeDictEnum implements DictEnum<String> {
     *     ADMIN("admin", "管理员"),
     *     USER("user", "普通用户");
     *
     *     private final String value;
     *     private final String title;
     *
     *     UserTypeDictEnum(String value, String title) {
     *         this.value = value;
     *         this.title = title;
     *     }
     *
     *     @Override
     *     public String getValue() {
     *         return value;
     *     }
     *
     *     @Override
     *     public String getTitle() {
     *         return title;
     *     }
     *
     *     @Override
     *     public Map<String, Object> getData() {
     *         // 根据不同用户类型返回不同的权限标识
     *         if (this == ADMIN) {
     *             return Map.of("permissions", Arrays.asList("read", "write", "delete"), "role", "admin");
     *         } else if (this == USER) {
     *             return Map.of("permissions", Collections.singletonList("read"), "role", "user");
     *         }
     *         return null;
     *     }
     * }
     *
     * // 示例3：使用多个字段扩展数据项，字典项初始化时就有了图标信息、颜色值、权限标识等字段
     * public enum MenuTypeDictEnum implements DictEnum<String> {
     *     DIR("dir", "目录", "folder", "blue"),
     *     MENU("menu", "菜单", "file", "green"),
     *     BUTTON("button", "按钮", "play", "orange");
     *
     *     private final String value;
     *     private final String title;
     *     private final String icon;
     *     private final String color;
     *
     *     MenuTypeDictEnum(String value, String title, String icon, String color) {
     *         this.value = value;
     *         this.title = title;
     *         this.icon = icon;
     *         this.color = color;
     *     }
     *
     *     @Override
     *     public String getValue() {
     *         return value;
     *     }
     *
     *     @Override
     *     public String getTitle() {
     *         return title;
     *     }
     *
     *     @Override
     *     public Map<String, Object> getData() {
     *         return Map.of("icon", icon, "color", color);
     *     }
     * }
     * }</pre>
     * </p>
     *
     * @return 扩展数据，返回字典项的额外信息，默认返回 null
     * @since 仅 1.6.3 和 1.7.1 和 2.0.1 版本开始有的特性
     */
    default Map<String, Object> getData() {
        return null;
    }

    /**
     * 判断字典值是否相等
     * <p>
     * 该方法用于判断传入的值是否与当前字典枚举的字典值相等。
     * 实现逻辑是：
     * 1. 首先判断传入的对象是否就是当前枚举对象本身
     * 2. 如果不是，则使用 Objects.equal 方法比较传入的对象是否与当前枚举的字典值相等
     * </p>
     *
     * @param o 传入的值，可为当前的枚举对象或其他类型的对象
     * @return 如果相等则返回 true，否则返回 false
     */
    default boolean eq(Object o) {
        return this == o || Objects.equal(o, getValue());
    }
}
