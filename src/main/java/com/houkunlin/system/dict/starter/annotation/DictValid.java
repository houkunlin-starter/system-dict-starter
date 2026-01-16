package com.houkunlin.system.dict.starter.annotation;

import com.houkunlin.system.dict.starter.json.DictValidConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典校验，如果找不到字典值文本，则校验失败。
 * SpringBoot 3.x 的校验用法。主要是 javax.validation 包名问题，SpringBoot 3.x 改包名了
 *
 * @author HouKunLin
 */
@Constraint(validatedBy = {DictValidConstraintValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DictValid {
    String message() default "字典参数错误";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 数据字典的代码。
     * 当此注解在系统字典枚举上时，该字段表示字典类型代码。
     *
     * @return 数据字典代码
     * @see DictText#value()
     */
    String value();
}
