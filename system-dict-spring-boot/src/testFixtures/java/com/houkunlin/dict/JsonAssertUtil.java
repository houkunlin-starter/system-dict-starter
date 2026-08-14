package com.houkunlin.dict;

import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

/**
 * JSON 深度比对工具类。
 * <p>
 * 基于 JSONAssert 实现对两个 JSON 的深度比对，只关心各个字段的取值是否一致，
 * 与 JSON 字符串的格式、字段顺序无关，用于防止因序列化字段顺序不同而导致断言失败。
 * </p>
 *
 * @author HouKunLin
 */
public final class JsonAssertUtil {
    private JsonAssertUtil() {
    }

    /**
     * 深度比对两个 JSON 是否完全一致（忽略字段顺序，不允许存在多余或缺失字段）。
     *
     * @param expectedJson 期望的 JSON 字符串
     * @param actualJson   实际的 JSON 字符串
     */
    public static void assertEquals(final String expectedJson, final String actualJson) {
        assertJsonEquals(expectedJson, actualJson, JSONCompareMode.STRICT);
    }

    /**
     * 深度比对实际的 JSON 是否包含期望的 JSON 中声明的所有字段（忽略字段顺序，允许存在多余字段）。
     *
     * @param actualJson         实际的 JSON 字符串
     * @param expectedFieldsJson 期望包含的字段组成的 JSON 字符串
     */
    public static void assertContains(final String actualJson, final String expectedFieldsJson) {
        assertJsonEquals(expectedFieldsJson, actualJson, JSONCompareMode.LENIENT);
    }

    /**
     * 执行 JSON 深度比对
     *
     * @param expectedJson 期望的 JSON 字符串
     * @param actualJson   实际的 JSON 字符串
     * @param mode         比对模式
     */
    private static void assertJsonEquals(final String expectedJson, final String actualJson, final JSONCompareMode mode) {
        try {
            JSONAssert.assertEquals(expectedJson, actualJson, mode);
        } catch (Exception e) {
            throw new AssertionError("JSON 解析失败：expected=" + expectedJson + ", actual=" + actualJson, e);
        }
    }
}
