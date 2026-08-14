package com.houkunlin.dict.common.provider;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 测试用用户对象
 * <p>
 * 用于测试字典提供者返回的用户数据对象。
 * </p>
 *
 * @author HouKunLin
 */
@Data
@AllArgsConstructor
public class DictUser {
    /**
     * 用户ID
     */
    private int id;
    /**
     * 用户名称
     */
    private String name;
}
