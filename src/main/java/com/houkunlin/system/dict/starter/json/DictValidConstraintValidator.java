package com.houkunlin.system.dict.starter.json;

import com.houkunlin.system.dict.starter.DictUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * 校验字典信息是否在字典列表中。校验字典信息的填写是否符合需求
 *
 * @author HouKunLin
 */
public class DictValidConstraintValidator implements ConstraintValidator<DictValid, Object> {
    private String dictType;

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (dictType == null || value == null) {
            return false;
        }
        return DictUtil.getDictText(dictType, value.toString()) != null;
    }

    @Override
    public void initialize(final DictValid constraintAnnotation) {
        this.dictType = constraintAnnotation.value();
    }
}
