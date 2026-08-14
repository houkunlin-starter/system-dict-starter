package com.houkunlin.dict;

/**
 * JSON 序列化/反序列化抽象接口。
 * <p>
 * 该接口抽象了 Jackson2（{@code com.fasterxml.jackson.databind.ObjectMapper}）与
 * Jackson3（{@code tools.jackson.databind.ObjectMapper}）的 JSON 序列化操作，
 * 使字典刷新消息的序列化逻辑与具体 Jackson 版本解耦。
 * 各版本 Starter 提供对应的 {@link DictJsonCodec} 实现。
 * </p>
 *
 * @author HouKunLin
 */
public interface DictJsonCodec {
    /**
     * 将对象序列化为 JSON 字符串。
     *
     * @param value 对象
     * @return JSON 字符串
     * @throws Exception 序列化异常
     */
    String writeValueAsString(Object value) throws Exception;

    /**
     * 将字节数组反序列化为指定类型对象。
     *
     * @param src       字节数组
     * @param valueType 目标类型
     * @param <T>       目标类型
     * @return 反序列化对象
     * @throws Exception 反序列化异常
     */
    <T> T readValue(byte[] src, Class<T> valueType) throws Exception;
}
