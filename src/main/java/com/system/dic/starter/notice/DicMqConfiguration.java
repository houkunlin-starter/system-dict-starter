package com.system.dic.starter.notice;

import com.system.dic.starter.DicRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Map;

/**
 * 数据字典消息队列配置
 *
 * @author HouKunLin
 */
@ConditionalOnClass(AmqpTemplate.class)
@Configuration
public class DicMqConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DicMqConfiguration.class);
    public static final String EXCHANGE_NAME = "app.fanout.refreshDic";
    private final DicRegistrar dicRegistrar;
    private final AmqpTemplate amqpTemplate;
    private final String applicationName;
    private static final String MQ_SOURCE = "SourceApplicationName";

    public DicMqConfiguration(@Lazy final DicRegistrar dicRegistrar,
                              final AmqpTemplate amqpTemplate,
                              @Value("${spring.application.name:'system-dic'}") final String applicationName) {
        this.dicRegistrar = dicRegistrar;
        this.amqpTemplate = amqpTemplate;
        this.applicationName = applicationName;
    }

    /**
     * 配置匿名队列，应用停止时会自动删除队列，并且能够保证队列唯一性
     *
     * @return 队列
     */
    @Bean
    public Queue dicQueue() {
        return new AnonymousQueue();
    }

    /**
     * 配置交换器
     *
     * @return 日志交换器
     */
    @Bean
    Exchange dicExchange() {
        return new FanoutExchange(EXCHANGE_NAME);
    }

    /**
     * 配置交换器和队列的绑定，
     *
     * @param dicQueue    队列
     * @param dicExchange 交换器
     * @return 绑定关系
     */
    @Bean
    public Binding dicBindingExchangeMessage(Queue dicQueue, Exchange dicExchange) {
        return BindingBuilder.bind(dicQueue).to(dicExchange).with("").noargs();
    }

    /**
     * 监听刷新数据字典消息队列广播通知
     */
    @RabbitListener(queues = "#{dicQueue.name}")
    public void refreshDic(@Payload String content, @Headers Map<Object, Object> map) throws Exception {
        if (applicationName.equals(map.get(MQ_SOURCE))) {
            logger.debug("收到来自当前系统发起的MQ消息，可以忽略不处理");
            return;
        }
        logger.info("[start] MQ 通知刷新字典：{}", content);
        dicRegistrar.afterPropertiesSet();
        logger.info("[finish] MQ 通知刷新字典");
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     */
    @EventListener
    public void refreshDic(RefreshDicEvent event) {
        final Object source = event.getSource();
        if (event.isNotifyAllSystem()) {
            logger.debug("接收到刷新数据字典事件，通知 MQ 与其他协同系统刷新 Redis 数据字典内容。事件内容：{}", source);
            amqpTemplate.convertAndSend(DicMqConfiguration.EXCHANGE_NAME, "", "刷新事件：" + source, message -> {
                final MessageProperties properties = message.getMessageProperties();
                properties.setHeader(MQ_SOURCE, applicationName);
                return message;
            });
        }
    }
}
