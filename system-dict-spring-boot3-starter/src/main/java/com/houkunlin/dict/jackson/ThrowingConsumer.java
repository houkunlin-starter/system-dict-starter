package com.houkunlin.dict.jackson;

import java.io.IOException;

/**
 * 允许抛出受检异常 IO 异常的消费者，用于向 JsonGenerator 写入 JSON 数据。
 *
 * @param <T> 参数类型
 * @author HouKunLin
 * @since 2.0.0
 */
@FunctionalInterface
public interface ThrowingConsumer<T> {
    /**
     * 消费参数，可能抛出 IO 异常
     *
     * @param t 参数
     * @throws IOException IO 异常
     */
    void accept(T t) throws IOException;
}
