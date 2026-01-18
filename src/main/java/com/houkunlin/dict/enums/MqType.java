package com.houkunlin.dict.enums;

/**
 * 消息中间件类型枚举，用于指定字典刷新时使用的消息通知方式。
 *
 * @author HouKunLin
 * @since 1.4.4
 */
public enum MqType {
    /**
     * 不使用消息中间件通知其他系统刷新字典，或者自定义处理此类情况
     */
    NONE,
    /**
     * 使用 AMQP（RabbitMQ） 通知其他系统刷新字典
     */
    AMQP,
    /**
     * 使用 Redis 的发布/订阅来通知其他系统刷新字典
     */
    REDIS
    ;
}
