package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 校验字典信息是否在字典列表中。校验字典信息的填写是否符合需求
 * <p>
 * 该类实现了 ConstraintValidator 接口，用于验证字段值是否在指定的字典列表中存在。
 * 通过 @DictValid 注解可以在字段上标注需要进行字典校验的字段。
 * </p>
 * <pre>
 *  class Bean {
 *      @DictValid("dictUserStatus")
 *      private Integer status;
 *  }
 * </pre>
 *
 * @author HouKunLin
 */
public class DictValidConstraintValidator implements ConstraintValidator<DictValid, Object> {
    /**
     * 字典类型代码，用于指定要校验的字典类型
     */
    private String dictType;

    /**
     * 验证字段值是否在字典列表中存在
     * <p>
     * 如果字典类型为 null 或者字段值为 null，则返回 false
     * 否则，通过 DictUtil.getDictText 方法检查字段值是否在字典列表中存在
     * </p>
     *
     * @param value   要验证的字段值
     * @param context 验证上下文
     * @return 如果字段值在字典列表中存在则返回 true，否则返回 false
     */
    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (dictType == null || value == null) {
            return false;
        }
        return DictUtil.getDictText(dictType, value.toString()) != null;
    }

    /**
     * 初始化校验器
     * <p>
     * 从 @DictValid 注解中获取字典类型代码并保存到 dictType 字段
     * </p>
     *
     * @param constraintAnnotation @DictValid 注解对象
     */
    @Override
    public void initialize(final DictValid constraintAnnotation) {
        this.dictType = constraintAnnotation.value();
    }
}
