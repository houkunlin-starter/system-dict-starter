package com.houkunlin.system.dic.starter.json;

import com.houkunlin.system.dic.starter.DicUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 校验字典信息是否在字典列表中。校验字典信息的填写是否符合需求
 *
 * @author HouKunLin
 */
public class DicValidConstraintValidator implements ConstraintValidator<DicValid, Object> {
    private String dicType;

    @Override
    public boolean isValid(final Object value, final ConstraintValidatorContext context) {
        if (dicType == null || value == null) {
            return false;
        }
        return DicUtil.getDicValueTitle(dicType, String.valueOf(value)) != null;
    }

    @Override
    public void initialize(final DicValid constraintAnnotation) {
        this.dicType = constraintAnnotation.value();
    }
}
