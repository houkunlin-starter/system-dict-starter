package com.houkunlin.dict;

import com.houkunlin.dict.common.bean.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.ConverterNotFoundException;
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
    void testJavaAtPath() {
        TestStarterAssertions.assertCurrentStarterModule("3");
    }

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
        Assertions.assertEquals(PeopleType2.ADMIN, conversionService.convert("ADMIN", PeopleType2.class));
        Assertions.assertEquals(PeopleType2.USER, conversionService.convert("USER", PeopleType2.class));
        Assertions.assertEquals(PeopleType2.OTHER, conversionService.convert("OTHER", PeopleType2.class));
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
        Assertions.assertEquals(Switch2.ON, conversionService.convert("ON", Switch2.class));
        Assertions.assertEquals(Switch2.OFF, conversionService.convert("OFF", Switch2.class));
    }

    @Test
    void testBooleanEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(BooleanEnum1.ITEM1, conversionService.convert("ITEM1", BooleanEnum1.class));
        Assertions.assertEquals(BooleanEnum1.ITEM2, conversionService.convert("ITEM2", BooleanEnum1.class));
        Assertions.assertEquals(BooleanEnum1.ITEM1, conversionService.convert("true", BooleanEnum1.class));
        Assertions.assertEquals(BooleanEnum1.ITEM2, conversionService.convert("false", BooleanEnum1.class));
        Assertions.assertEquals(BooleanEnum1.ITEM2, conversionService.convert(1, BooleanEnum1.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.BooleanEnum1] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(2, BooleanEnum1.class));
        Assertions.assertEquals(BooleanEnum1.ITEM1, conversionService.convert(true, BooleanEnum1.class));
        Assertions.assertEquals(BooleanEnum1.ITEM2, conversionService.convert(false, BooleanEnum1.class));

        Assertions.assertEquals(BooleanEnum2.ITEM1, conversionService.convert("ITEM1", BooleanEnum2.class));
        Assertions.assertEquals(BooleanEnum2.ITEM2, conversionService.convert("ITEM2", BooleanEnum2.class));
        Assertions.assertEquals(BooleanEnum2.ITEM1, conversionService.convert("true", BooleanEnum2.class));
        Assertions.assertEquals(BooleanEnum2.ITEM2, conversionService.convert("false", BooleanEnum2.class));
        Assertions.assertEquals(BooleanEnum2.ITEM2, conversionService.convert(1, BooleanEnum2.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.BooleanEnum2] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(2, BooleanEnum2.class));
        Assertions.assertEquals(BooleanEnum2.ITEM1, conversionService.convert(true, BooleanEnum2.class));
        Assertions.assertEquals(BooleanEnum2.ITEM2, conversionService.convert(false, BooleanEnum2.class));

        Assertions.assertEquals(BooleanEnum3.ITEM1, conversionService.convert("ITEM1", BooleanEnum3.class));
        Assertions.assertEquals(BooleanEnum3.ITEM2, conversionService.convert("ITEM2", BooleanEnum3.class));
        Assertions.assertEquals(BooleanEnum3.ITEM1, conversionService.convert("true", BooleanEnum3.class));
        Assertions.assertEquals(BooleanEnum3.ITEM2, conversionService.convert("false", BooleanEnum3.class));
        Assertions.assertEquals(BooleanEnum3.ITEM2, conversionService.convert(1, BooleanEnum3.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.BooleanEnum3] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(2, BooleanEnum3.class));
        Assertions.assertEquals(BooleanEnum3.ITEM1, conversionService.convert(true, BooleanEnum3.class));
        Assertions.assertEquals(BooleanEnum3.ITEM2, conversionService.convert(false, BooleanEnum3.class));
    }

    @Test
    void testByteEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(ByteEnum1.ITEM1, conversionService.convert("ITEM1", ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM2, conversionService.convert("ITEM2", ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM3, conversionService.convert("ITEM3", ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM1, conversionService.convert("1", ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM2, conversionService.convert("2", ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM3, conversionService.convert("3", ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM2, conversionService.convert(1, ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM3, conversionService.convert(2, ByteEnum1.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.ByteEnum1] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM1, conversionService.convert((byte) 1, ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM2, conversionService.convert((byte) 2, ByteEnum1.class));
        Assertions.assertEquals(ByteEnum1.ITEM3, conversionService.convert((byte) 3, ByteEnum1.class));

        Assertions.assertEquals(ByteEnum2.ITEM1, conversionService.convert("ITEM1", ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM2, conversionService.convert("ITEM2", ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM3, conversionService.convert("ITEM3", ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM1, conversionService.convert("1", ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM2, conversionService.convert("2", ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM3, conversionService.convert("3", ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM2, conversionService.convert(1, ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM3, conversionService.convert(2, ByteEnum2.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.ByteEnum2] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM1, conversionService.convert((byte) 1, ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM2, conversionService.convert((byte) 2, ByteEnum2.class));
        Assertions.assertEquals(ByteEnum2.ITEM3, conversionService.convert((byte) 3, ByteEnum2.class));

        Assertions.assertEquals(ByteEnum3.ITEM1, conversionService.convert("ITEM1", ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM2, conversionService.convert("ITEM2", ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM3, conversionService.convert("ITEM3", ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM1, conversionService.convert("1", ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM2, conversionService.convert("2", ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM3, conversionService.convert("3", ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM2, conversionService.convert(1, ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM3, conversionService.convert(2, ByteEnum3.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.ByteEnum3] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM1, conversionService.convert((byte) 1, ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM2, conversionService.convert((byte) 2, ByteEnum3.class));
        Assertions.assertEquals(ByteEnum3.ITEM3, conversionService.convert((byte) 3, ByteEnum3.class));
    }

    @Test
    void testDoubleEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(DoubleEnum1.ITEM1, conversionService.convert("ITEM1", DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM2, conversionService.convert("ITEM2", DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM3, conversionService.convert("ITEM3", DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM1, conversionService.convert("1.0", DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM2, conversionService.convert("2.0", DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM3, conversionService.convert("3.0", DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM2, conversionService.convert(1, DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM3, conversionService.convert(2, DoubleEnum1.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.DoubleEnum1] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM1, conversionService.convert(1.0D, DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM2, conversionService.convert(2.0D, DoubleEnum1.class));
        Assertions.assertEquals(DoubleEnum1.ITEM3, conversionService.convert(3.0D, DoubleEnum1.class));

        Assertions.assertEquals(DoubleEnum2.ITEM1, conversionService.convert("ITEM1", DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM2, conversionService.convert("ITEM2", DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM3, conversionService.convert("ITEM3", DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM1, conversionService.convert("1.0", DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM2, conversionService.convert("2.0", DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM3, conversionService.convert("3.0", DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM2, conversionService.convert(1, DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM3, conversionService.convert(2, DoubleEnum2.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.DoubleEnum2] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM1, conversionService.convert(1.0D, DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM2, conversionService.convert(2.0D, DoubleEnum2.class));
        Assertions.assertEquals(DoubleEnum2.ITEM3, conversionService.convert(3.0D, DoubleEnum2.class));

        Assertions.assertEquals(DoubleEnum3.ITEM1, conversionService.convert("ITEM1", DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM2, conversionService.convert("ITEM2", DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM3, conversionService.convert("ITEM3", DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM1, conversionService.convert("1.0", DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM2, conversionService.convert("2.0", DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM3, conversionService.convert("3.0", DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM2, conversionService.convert(1, DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM3, conversionService.convert(2, DoubleEnum3.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.DoubleEnum3] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM1, conversionService.convert(1.0D, DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM2, conversionService.convert(2.0D, DoubleEnum3.class));
        Assertions.assertEquals(DoubleEnum3.ITEM3, conversionService.convert(3.0D, DoubleEnum3.class));
    }

    @Test
    void testFloatEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(FloatEnum1.ITEM1, conversionService.convert("ITEM1", FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM2, conversionService.convert("ITEM2", FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM3, conversionService.convert("ITEM3", FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM1, conversionService.convert("1.0", FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM2, conversionService.convert("2.0", FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM3, conversionService.convert("3.0", FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM2, conversionService.convert(1, FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM3, conversionService.convert(2, FloatEnum1.class));
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM1, conversionService.convert(1.0F, FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM2, conversionService.convert(2.0F, FloatEnum1.class));
        Assertions.assertEquals(FloatEnum1.ITEM3, conversionService.convert(3.0F, FloatEnum1.class));

        Assertions.assertEquals(FloatEnum2.ITEM1, conversionService.convert("ITEM1", FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM2, conversionService.convert("ITEM2", FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM3, conversionService.convert("ITEM3", FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM1, conversionService.convert("1.0", FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM2, conversionService.convert("2.0", FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM3, conversionService.convert("3.0", FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM2, conversionService.convert(1, FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM3, conversionService.convert(2, FloatEnum2.class));
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM1, conversionService.convert(1.0F, FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM2, conversionService.convert(2.0F, FloatEnum2.class));
        Assertions.assertEquals(FloatEnum2.ITEM3, conversionService.convert(3.0F, FloatEnum2.class));

        Assertions.assertEquals(FloatEnum3.ITEM1, conversionService.convert("ITEM1", FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM2, conversionService.convert("ITEM2", FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM3, conversionService.convert("ITEM3", FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM1, conversionService.convert("1.0", FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM2, conversionService.convert("2.0", FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM3, conversionService.convert("3.0", FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM2, conversionService.convert(1, FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM3, conversionService.convert(2, FloatEnum3.class));
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM1, conversionService.convert(1.0F, FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM2, conversionService.convert(2.0F, FloatEnum3.class));
        Assertions.assertEquals(FloatEnum3.ITEM3, conversionService.convert(3.0F, FloatEnum3.class));
    }

    @Test
    void testIntegerEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(IntegerEnum1.ITEM1, conversionService.convert("ITEM1", IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM2, conversionService.convert("ITEM2", IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM3, conversionService.convert("ITEM3", IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM1, conversionService.convert("1", IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM2, conversionService.convert("2", IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM3, conversionService.convert("3", IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM1, conversionService.convert(1, IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM2, conversionService.convert(2, IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM3, conversionService.convert(3, IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM1, conversionService.convert(1, IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM2, conversionService.convert(2, IntegerEnum1.class));
        Assertions.assertEquals(IntegerEnum1.ITEM3, conversionService.convert(3, IntegerEnum1.class));
        Assertions.assertNull(conversionService.convert(4, IntegerEnum1.class));

        Assertions.assertEquals(IntegerEnum2.ITEM1, conversionService.convert("ITEM1", IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM2, conversionService.convert("ITEM2", IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM3, conversionService.convert("ITEM3", IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM1, conversionService.convert("1", IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM2, conversionService.convert("2", IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM3, conversionService.convert("3", IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM1, conversionService.convert(1, IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM2, conversionService.convert(2, IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM3, conversionService.convert(3, IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM1, conversionService.convert(1, IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM2, conversionService.convert(2, IntegerEnum2.class));
        Assertions.assertEquals(IntegerEnum2.ITEM3, conversionService.convert(3, IntegerEnum2.class));
        Assertions.assertNull(conversionService.convert(4, IntegerEnum2.class));

        Assertions.assertEquals(IntegerEnum3.ITEM1, conversionService.convert("ITEM1", IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM2, conversionService.convert("ITEM2", IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM3, conversionService.convert("ITEM3", IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM1, conversionService.convert("1", IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM2, conversionService.convert("2", IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM3, conversionService.convert("3", IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM1, conversionService.convert(1, IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM2, conversionService.convert(2, IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM3, conversionService.convert(3, IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM1, conversionService.convert(1, IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM2, conversionService.convert(2, IntegerEnum3.class));
        Assertions.assertEquals(IntegerEnum3.ITEM3, conversionService.convert(3, IntegerEnum3.class));
        Assertions.assertNull(conversionService.convert(4, IntegerEnum3.class));
    }

    @Test
    void testLongEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(LongEnum1.ITEM1, conversionService.convert("ITEM1", LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM2, conversionService.convert("ITEM2", LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM3, conversionService.convert("ITEM3", LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM1, conversionService.convert("1", LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM2, conversionService.convert("2", LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM3, conversionService.convert("3", LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM2, conversionService.convert(1, LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM3, conversionService.convert(2, LongEnum1.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.LongEnum1] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM1, conversionService.convert(1L, LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM2, conversionService.convert(2L, LongEnum1.class));
        Assertions.assertEquals(LongEnum1.ITEM3, conversionService.convert(3L, LongEnum1.class));

        Assertions.assertEquals(LongEnum2.ITEM1, conversionService.convert("ITEM1", LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM2, conversionService.convert("ITEM2", LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM3, conversionService.convert("ITEM3", LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM1, conversionService.convert("1", LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM2, conversionService.convert("2", LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM3, conversionService.convert("3", LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM2, conversionService.convert(1, LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM3, conversionService.convert(2, LongEnum2.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.LongEnum2] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM1, conversionService.convert(1L, LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM2, conversionService.convert(2L, LongEnum2.class));
        Assertions.assertEquals(LongEnum2.ITEM3, conversionService.convert(3L, LongEnum2.class));

        Assertions.assertEquals(LongEnum3.ITEM1, conversionService.convert("ITEM1", LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM2, conversionService.convert("ITEM2", LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM3, conversionService.convert("ITEM3", LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM1, conversionService.convert("1", LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM2, conversionService.convert("2", LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM3, conversionService.convert("3", LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM2, conversionService.convert(1, LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM3, conversionService.convert(2, LongEnum3.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.LongEnum3] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM1, conversionService.convert(1L, LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM2, conversionService.convert(2L, LongEnum3.class));
        Assertions.assertEquals(LongEnum3.ITEM3, conversionService.convert(3L, LongEnum3.class));
    }

    @Test
    void testShortEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(ShortEnum1.ITEM1, conversionService.convert("ITEM1", ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM2, conversionService.convert("ITEM2", ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM3, conversionService.convert("ITEM3", ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM1, conversionService.convert("1", ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM2, conversionService.convert("2", ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM3, conversionService.convert("3", ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM2, conversionService.convert(1, ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM3, conversionService.convert(2, ShortEnum1.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.ShortEnum1] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM1, conversionService.convert((short) 1, ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM2, conversionService.convert((short) 2, ShortEnum1.class));
        Assertions.assertEquals(ShortEnum1.ITEM3, conversionService.convert((short) 3, ShortEnum1.class));

        Assertions.assertEquals(ShortEnum2.ITEM1, conversionService.convert("ITEM1", ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM2, conversionService.convert("ITEM2", ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM3, conversionService.convert("ITEM3", ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM1, conversionService.convert("1", ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM2, conversionService.convert("2", ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM3, conversionService.convert("3", ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM2, conversionService.convert(1, ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM3, conversionService.convert(2, ShortEnum2.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.ShortEnum2] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM1, conversionService.convert((short) 1, ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM2, conversionService.convert((short) 2, ShortEnum2.class));
        Assertions.assertEquals(ShortEnum2.ITEM3, conversionService.convert((short) 3, ShortEnum2.class));

        Assertions.assertEquals(ShortEnum3.ITEM1, conversionService.convert("ITEM1", ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM2, conversionService.convert("ITEM2", ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM3, conversionService.convert("ITEM3", ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM1, conversionService.convert("1", ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM2, conversionService.convert("2", ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM3, conversionService.convert("3", ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM2, conversionService.convert(1, ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM3, conversionService.convert(2, ShortEnum3.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.ShortEnum3] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM1, conversionService.convert((short) 1, ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM2, conversionService.convert((short) 2, ShortEnum3.class));
        Assertions.assertEquals(ShortEnum3.ITEM3, conversionService.convert((short) 3, ShortEnum3.class));
    }

    @Test
    void testStringEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(StringEnum1.ITEM1, conversionService.convert("ITEM1", StringEnum1.class));
        Assertions.assertEquals(StringEnum1.ITEM2, conversionService.convert("ITEM2", StringEnum1.class));
        Assertions.assertEquals(StringEnum1.ITEM3, conversionService.convert("ITEM3", StringEnum1.class));
        Assertions.assertEquals(StringEnum1.ITEM1, conversionService.convert("1", StringEnum1.class));
        Assertions.assertEquals(StringEnum1.ITEM2, conversionService.convert("2", StringEnum1.class));
        Assertions.assertEquals(StringEnum1.ITEM3, conversionService.convert("3", StringEnum1.class));
        Assertions.assertEquals(StringEnum1.ITEM2, conversionService.convert(1, StringEnum1.class));
        Assertions.assertEquals(StringEnum1.ITEM3, conversionService.convert(2, StringEnum1.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.StringEnum1] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, StringEnum1.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 1, StringEnum1.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 2, StringEnum1.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 3, StringEnum1.class));

        Assertions.assertEquals(StringEnum2.ITEM1, conversionService.convert("ITEM1", StringEnum2.class));
        Assertions.assertEquals(StringEnum2.ITEM2, conversionService.convert("ITEM2", StringEnum2.class));
        Assertions.assertEquals(StringEnum2.ITEM3, conversionService.convert("ITEM3", StringEnum2.class));
        Assertions.assertEquals(StringEnum2.ITEM1, conversionService.convert("1", StringEnum2.class));
        Assertions.assertEquals(StringEnum2.ITEM2, conversionService.convert("2", StringEnum2.class));
        Assertions.assertEquals(StringEnum2.ITEM3, conversionService.convert("3", StringEnum2.class));
        Assertions.assertEquals(StringEnum2.ITEM2, conversionService.convert(1, StringEnum2.class));
        Assertions.assertEquals(StringEnum2.ITEM3, conversionService.convert(2, StringEnum2.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.StringEnum2] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, StringEnum2.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 1, StringEnum2.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 2, StringEnum2.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 3, StringEnum2.class));

        Assertions.assertEquals(StringEnum3.ITEM1, conversionService.convert("ITEM1", StringEnum3.class));
        Assertions.assertEquals(StringEnum3.ITEM2, conversionService.convert("ITEM2", StringEnum3.class));
        Assertions.assertEquals(StringEnum3.ITEM3, conversionService.convert("ITEM3", StringEnum3.class));
        Assertions.assertEquals(StringEnum3.ITEM1, conversionService.convert("1", StringEnum3.class));
        Assertions.assertEquals(StringEnum3.ITEM2, conversionService.convert("2", StringEnum3.class));
        Assertions.assertEquals(StringEnum3.ITEM3, conversionService.convert("3", StringEnum3.class));
        Assertions.assertEquals(StringEnum3.ITEM2, conversionService.convert(1, StringEnum3.class));
        Assertions.assertEquals(StringEnum3.ITEM3, conversionService.convert(2, StringEnum3.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.StringEnum3] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, StringEnum3.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 1, StringEnum3.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 2, StringEnum3.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 3, StringEnum3.class));
    }

    @Test
    void testSimpleEnum() {
        // 默认是通过枚举名称转换的
        Assertions.assertEquals(SimpleEnum1.ITEM1, conversionService.convert("ITEM1", SimpleEnum1.class));
        Assertions.assertEquals(SimpleEnum1.ITEM2, conversionService.convert("ITEM2", SimpleEnum1.class));
        Assertions.assertEquals(SimpleEnum1.ITEM3, conversionService.convert("ITEM3", SimpleEnum1.class));
        // 通过枚举名称转换失败 Failed to convert from type [java.lang.String] to type [com.houkunlin.dict.common.bean.SimpleEnum1] for value [1]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert("1", SimpleEnum1.class));
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert("2", SimpleEnum1.class));
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert("3", SimpleEnum1.class));
        // 默认是通过枚举索引值转换的
        Assertions.assertEquals(SimpleEnum1.ITEM2, conversionService.convert(1, SimpleEnum1.class));
        Assertions.assertEquals(SimpleEnum1.ITEM3, conversionService.convert(2, SimpleEnum1.class));
        // 通过枚举索引值转换，数组越界；Failed to convert from type [java.lang.Integer] to type [com.houkunlin.dict.common.bean.SimpleEnum1] for value [3]
        Assertions.assertThrows(ConversionFailedException.class, () -> conversionService.convert(3, SimpleEnum1.class));
        // 找不到映射转换关系 Unexpected exception type thrown, expected: <java.lang.IllegalArgumentException> but was: <org.springframework.core.convert.ConverterNotFoundException>
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 1, SimpleEnum1.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 2, SimpleEnum1.class));
        Assertions.assertThrows(ConverterNotFoundException.class, () -> conversionService.convert((short) 3, SimpleEnum1.class));
    }
}
