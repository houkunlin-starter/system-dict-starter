package com.houkunlin.system.dict.starter.notice;

import com.houkunlin.system.dict.starter.DictProperties;
import com.houkunlin.system.dict.starter.DictRegistrar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Queue;
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

import java.util.*;

/**
 * 数据字典消息队列配置
 *
 * @author HouKunLin
 */
@ConditionalOnClass(AmqpTemplate.class)
@Configuration
public class DictMqConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DictMqConfiguration.class);
    private static final String DIC_PROVIDER_CLASSES_KEY = "DIC.dictProviderClasses";
    private final DictRegistrar dictRegistrar;
    private final AmqpTemplate amqpTemplate;
    private final String applicationName;
    private final String exchangeName;
    private final String headerSourceKey;

    public DictMqConfiguration(@Lazy final DictRegistrar dictRegistrar,
                               final AmqpTemplate amqpTemplate,
                               @Value("${spring.application.name:'system-dict'}") final String applicationName,
                               final DictProperties dictProperties) {
        this.dictRegistrar = dictRegistrar;
        this.amqpTemplate = amqpTemplate;
        this.applicationName = applicationName;
        this.exchangeName = dictProperties.getMqExchangeName();
        this.headerSourceKey = dictProperties.getMqHeaderSourceKey();
    }

    /**
     * 配置匿名队列，应用停止时会自动删除队列，并且能够保证队列唯一性
     *
     * @return 队列
     */
    @Bean
    public Queue dictQueue() {
        return new AnonymousQueue();
    }

    /**
     * 配置交换器
     *
     * @return 日志交换器
     */
    @Bean
    Exchange dictExchange() {
        return new FanoutExchange(exchangeName);
    }

    /**
     * 配置交换器和队列的绑定，
     *
     * @param dictQueue    队列
     * @param dictExchange 交换器
     * @return 绑定关系
     */
    @Bean
    public Binding dictBindingExchangeMessage(Queue dictQueue, Exchange dictExchange) {
        return BindingBuilder.bind(dictQueue).to(dictExchange).with("").noargs();
    }

    /**
     * 监听刷新数据字典消息队列广播通知
     */
    @RabbitListener(queues = "#{dictQueue.name}")
    public void refreshDict(@Payload String content, @Headers Map<Object, Object> map) {
        if (applicationName.equals(map.get(headerSourceKey))) {
            logger.debug("收到来自当前系统发起的MQ消息，可以忽略不处理");
            return;
        }
        logger.info("[start] MQ 通知刷新字典：{}", content);
        final Object dictProviderClasses = map.get(DIC_PROVIDER_CLASSES_KEY);
        if (dictProviderClasses instanceof List) {
            dictRegistrar.refreshDict(new HashSet<>((Collection<String>) dictProviderClasses));
        } else if (dictProviderClasses instanceof Set) {
            dictRegistrar.refreshDict((Set<String>) dictProviderClasses);
        } else if (dictProviderClasses instanceof Collection) {
            dictRegistrar.refreshDict(new HashSet<>((Collection<String>) dictProviderClasses));
        } else {
            dictRegistrar.refreshDict(null);
        }
        logger.info("[finish] MQ 通知刷新字典");
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     */
    @EventListener
    public void refreshDict(RefreshDictEvent event) {
        final Object source = event.getSource();
        if (event.isNotifyOtherSystem()) {
            logger.debug("接收到刷新数据字典事件，通知 MQ 与其他协同系统刷新 Redis 数据字典内容。事件内容：{}", source);
            amqpTemplate.convertAndSend(exchangeName, "", "刷新事件：" + source, message -> {
                final MessageProperties properties = message.getMessageProperties();
                properties.setHeader(DIC_PROVIDER_CLASSES_KEY, event.getDictProviderClasses());

                if (!event.isNotifyOtherSystemAndBrother()) {
                    // event.isNotifyOtherSystemAndBrother = true 表示通知所有系统，不忽略当前系统的副本系统
                    // event.isNotifyOtherSystemAndBrother = false 表示通知所有系统，忽略当前系统的副本系统
                    // 仅通知其他系统，不通知兄弟系统，需要带上来源应用名称来进行过滤
                    properties.setHeader(headerSourceKey, applicationName);
                }
                return message;
            });
        }
    }
}
