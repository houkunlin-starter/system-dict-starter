package com.houkunlin.dict.enums;

import java.util.function.BooleanSupplier;

/**
 * 数据字典布尔配置类型枚举，用于指定字典配置的布尔值策略。
 *
 * @author HouKunLin
 * @since 1.5.0
 */
public enum DictBoolType {
    /**
     * 根据全局参数决定配置
     */
    GLOBAL,
    /**
     * 强制为 YES（true）
     */
    YES,
    /**
     * 强制为 NO（false）
     */
    NO;

    /**
     * 获取所需的布尔值。
     *
     * @param global 全局配置值
     * @return true 或 false
     */
    public boolean getValue(final boolean global) {
        if (this == GLOBAL) {
            return global;
        }
        return this == YES;
    }

    /**
     * 获取所需的布尔值。
     *
     * @param booleanSupplier 全局配置值提供者
     * @return true 或 false
     */
    public boolean getValue(final BooleanSupplier booleanSupplier) {
        if (this == GLOBAL) {
            return booleanSupplier.getAsBoolean();
        }
        return this == YES;
    }
}
