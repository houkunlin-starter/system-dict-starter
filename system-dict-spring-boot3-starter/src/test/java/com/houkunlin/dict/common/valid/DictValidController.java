package com.houkunlin.dict.common.valid;

import lombok.AllArgsConstructor;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.convert.converter.Converter;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author HouKunLin
 */
@RestController
@RequestMapping
@AllArgsConstructor
public class DictValidController {
    private final List<Converter> converters;

    @GetMapping("/testValidBean")
    public Object testValidate(@Validated DictValidBean bean, Errors errors) {
        if (errors.hasErrors()) {
            return errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        }
        return bean;
    }
}
