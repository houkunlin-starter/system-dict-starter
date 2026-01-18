package com.houkunlin.dict;

import com.houkunlin.dict.common.bean.PeopleType;
import com.houkunlin.dict.common.bean.PeopleType2;
import com.houkunlin.dict.common.bean.Switch;
import com.houkunlin.dict.common.bean.Switch2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.test.annotation.DirtiesContext;

/**
 * 测试转换器
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class TestConverter {
    @Autowired
    private ConversionService conversionService;

    @Test
    void testPeopleType() {
        Assertions.assertEquals(PeopleType.ADMIN, conversionService.convert("0", PeopleType.class));
        Assertions.assertEquals(PeopleType.USER, conversionService.convert("1", PeopleType.class));
        Assertions.assertEquals(PeopleType.OTHER, conversionService.convert("2", PeopleType.class));

        Assertions.assertEquals(PeopleType.ADMIN, conversionService.convert("ADMIN", PeopleType.class));
        Assertions.assertEquals(PeopleType.USER, conversionService.convert("USER", PeopleType.class));
        Assertions.assertEquals(PeopleType.OTHER, conversionService.convert("OTHER", PeopleType.class));
    }

    @Test
    void testPeopleType2() {
        Assertions.assertEquals(PeopleType2.ADMIN, conversionService.convert("0", PeopleType2.class));
        Assertions.assertEquals(PeopleType2.USER, conversionService.convert("1", PeopleType2.class));
        Assertions.assertEquals(PeopleType2.OTHER, conversionService.convert("2", PeopleType2.class));

        // 字典值不是字符串类型，使用此方式转换时，会抛出异常，因为会尝试把 "ADMIN" 转换成 Integer 类型导致失败
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert("ADMIN", PeopleType2.class));
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert("USER", PeopleType2.class));
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert("OTHER", PeopleType2.class));
    }

    @Test
    void testSwitch() {
        Assertions.assertEquals(Switch.ON, conversionService.convert("on", Switch.class));
        Assertions.assertEquals(Switch.OFF, conversionService.convert("off", Switch.class));

        Assertions.assertEquals(Switch.ON, conversionService.convert("ON", Switch.class));
        Assertions.assertEquals(Switch.OFF, conversionService.convert("OFF", Switch.class));
    }

    @Test
    void testSwitch2() {
        Assertions.assertEquals(Switch2.ON, conversionService.convert("on", Switch2.class));
        Assertions.assertEquals(Switch2.OFF, conversionService.convert("off", Switch2.class));

        // 字典值是字符串类型，使用此方式转换时，会返回null值
        Assertions.assertNull(conversionService.convert("ON", Switch2.class));
        Assertions.assertNull(conversionService.convert("OFF", Switch2.class));
    }
}
