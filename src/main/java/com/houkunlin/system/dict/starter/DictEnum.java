package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.annotation.JsonValue;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Map;

/**
 * 数据字典枚举接口。系统字典枚举接口
 *
 * @author HouKunLin
 */
public interface DictEnum<T extends Serializable> {
    /**
     * 通过枚举值从枚举列表中获取枚举对象
     *
     * @param values 枚举对象列表
     * @param value  枚举值
     * @param <T>    枚举值类型
     * @param <E>    枚举对象类型
     * @return 枚举对象
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
     *
     * @return 父级字典值
     */
    default T getParentValue() {
        return null;
    }

    /**
     * 字典值
     *
     * @return 字典值
     */
    @JsonValue
    T getValue();

    /**
     * 字典文本
     *
     * @return 字典文本
     */
    String getTitle();

    /**
     * 排序值
     *
     * @return 排序值
     */
    default int getSorted() {
        return 0;
    }

    /**
     * 是否禁用
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
     */
    default Map<String, Object> getData() {
        return null;
    }

    /**
     * 判断字典值是否相等
     *
     * @param o 传入的值，可为当前的枚举对象
     * @return 判断是否相等
     */
    default boolean eq(Object o) {
        return this == o || Objects.equal(o, getValue());
    }
}
