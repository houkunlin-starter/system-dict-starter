package com.houkunlin.dict.jackson;

import java.io.IOException;

/**
 * 允许抛出受检异常 IO 异常的 Runnable，用于向 JsonGenerator 写入 JSON 数据。
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@FunctionalInterface
public interface ThrowingRunnable {
    /**
     * 执行任务，可能抛出 IO 异常
     *
     * @throws IOException IO 异常
     */
    void run() throws IOException;
}
