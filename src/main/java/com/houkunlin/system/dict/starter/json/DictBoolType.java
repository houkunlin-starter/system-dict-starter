package com.houkunlin.system.dict.starter.json;

import java.util.function.BooleanSupplier;

/**
 * 数据字典配置类型
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
     * 强制为 YES
     */
    YES,
    /**
     * 强制为 NO
     */
    NO;

    /**
     * 获取所需的值
     *
     * @param global 全局配置值
     * @return true or false
     */
    public boolean getValue(final boolean global) {
        if (this == GLOBAL) {
            return global;
        }
        return this == YES;
    }

    /**
     * 获取所需的值
     *
     * @param booleanSupplier 全局配置值
     * @return true or false
     */
    public boolean getValue(final BooleanSupplier booleanSupplier) {
        if (this == GLOBAL) {
            return booleanSupplier.getAsBoolean();
        }
        return this == YES;
    }
}
