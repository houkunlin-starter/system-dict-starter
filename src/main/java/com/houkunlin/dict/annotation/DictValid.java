package com.houkunlin.dict.annotation;

import com.houkunlin.dict.json.DictValidConstraintValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字典校验注解，用于校验字段值是否为有效的字典值。
 * 如果找不到字典值文本，则校验失败。
 * 此注解适用于 SpringBoot 3.x 版本，使用 jakarta.validation 包（SpringBoot 3.x 改包名）。
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@Constraint(validatedBy = {DictValidConstraintValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DictValid {
    /**
     * 校验失败时的错误信息。
     *
     * @return 错误信息
     */
    String message() default "字典参数错误";

    /**
     * 校验分组，用于分组校验。
     *
     * @return 校验分组
     */
    Class<?>[] groups() default {};

    /**
     * 校验负载，用于传递额外的校验信息。
     *
     * @return 校验负载
     */
    Class<? extends Payload>[] payload() default {};

    /**
     * 数据字典的代码，用于指定要校验的字典类型。
     *
     * @return 数据字典代码
     * @see DictText#value()
     */
    String value();
}
