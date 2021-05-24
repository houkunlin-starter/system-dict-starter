package test.application.common.valid;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

/**
 * @author HouKunLin
 */
@RestController
@RequestMapping
public class DicValidController {
    @GetMapping("/testValidBean")
    public Object testValidate(@Validated DicValidBean bean, Errors errors) {
        if (errors.hasErrors()) {
            return errors.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        }
        return bean;
    }
}
