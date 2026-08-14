package com.houkunlin.dict.notice;

import com.houkunlin.dict.DictRegistrar;
import com.houkunlin.dict.properties.DictProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.handler.annotation.Payload;

import java.util.Objects;

/**
 * 数据字典 AMQP 消息队列配置
 * <p>
 * 该配置类用于在启用 AMQP 消息队列模式时，处理字典刷新事件的发布和订阅。
 * 当系统内部发起刷新字典事件时，会通过 AMQP 发布消息通知其他系统；
 * 同时会订阅 AMQP 消息，接收其他系统发来的字典刷新通知。
 * </p>
 *
 * @author HouKunLin
 */
@ConditionalOnProperty(prefix = "system.dict", name = "mq-type", havingValue = "AMQP")
@ConditionalOnClass(AmqpTemplate.class)
@Configuration(proxyBeanMethods = false)
public class DictAmqpConfiguration {
    /**
     * 日志
     */
    private static final Logger logger = LoggerFactory.getLogger(DictAmqpConfiguration.class);
    /**
     * 数据字典注册器，用于刷新字典数据
     */
    private final DictRegistrar dictRegistrar;
    /**
     * AMQP 模板，用于发布 AMQP 消息
     */
    private final AmqpTemplate amqpTemplate;
    /**
     * 当前应用名称，用于标识消息的来源和过滤
     */
    private final String applicationName;
    /**
     * 交换器名称，用于指定 AMQP 消息的交换器
     */
    private final String exchangeName;

    /**
     * 构造方法
     *
     * @param dictRegistrar   数据字典注册器
     * @param amqpTemplate    AMQP 模板
     * @param applicationName 当前应用名称
     * @param dictProperties  数据字典配置参数信息
     */
    public DictAmqpConfiguration(final DictRegistrar dictRegistrar,
                                 final AmqpTemplate amqpTemplate,
                                 @Value("${spring.application.name:'system-dict'}") final String applicationName,
                                 final DictProperties dictProperties) {
        this.dictRegistrar = dictRegistrar;
        this.amqpTemplate = amqpTemplate;
        this.applicationName = applicationName;
        this.exchangeName = dictProperties.getMqExchangeName();
    }

    /**
     * 配置匿名队列
     * <p>
     * 应用停止时会自动删除队列，并且能够保证队列唯一性。
     * </p>
     *
     * @return 队列
     */
    @Bean
    public Queue dictQueue() {
        return new AnonymousQueue();
    }

    /**
     * 配置交换器
     * <p>
     * 使用 FanoutExchange 类型的交换器，实现广播消息的功能。
     * </p>
     *
     * @return 交换器
     */
    @Bean
    Exchange dictExchange() {
        return new FanoutExchange(exchangeName);
    }

    /**
     * 配置交换器和队列的绑定
     * <p>
     * 将队列绑定到交换器，实现消息的接收。
     * </p>
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
     * <p>
     * 当收到 AMQP 消息时，会检查消息是否来自当前系统且不需要通知兄弟系统，
     * 如果是则忽略处理，否则刷新字典数据。
     * </p>
     *
     * @param noticeData 刷新通知数据
     */
    @RabbitListener(queues = "#{dictQueue.name}")
    public void refreshDict(@Payload final RefreshNoticeData noticeData) {
        if (!noticeData.isNotifyBrother() && Objects.equals(applicationName, noticeData.getApplicationName())) {
            logger.debug("收到来自当前系统发起的MQ消息，并且被标记忽略处理");
            return;
        }
        logger.debug("[start] AMQP 通知刷新字典：{}", noticeData.getMessage());
        dictRegistrar.refreshDict(noticeData.getDictProviderClasses());
        logger.debug("[finish] AMQP 通知刷新字典");
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     * <p>
     * 当收到刷新字典事件且需要通知其他系统时，会将事件封装为 RefreshNoticeData 对象，
     * 通过 AMQP 发布消息。
     * </p>
     *
     * @param event 刷新字典通知事件
     */
    @EventListener
    public void refreshDict(RefreshDictEvent event) {
        final Object source = event.getSource();
        if (event.isNotifyOtherSystem()) {
            logger.debug("接收到刷新数据字典事件，使用 AMQP 通知其他协同系统刷新数据字典内容。事件内容：{}", source);
            final RefreshNoticeData noticeData = RefreshNoticeData.builder()
                .message("刷新事件：" + source)
                .applicationName(applicationName)
                .notifyBrother(event.isNotifyOtherSystemAndBrother())
                .dictProviderClasses(event.getDictProviderClasses()).build();
            amqpTemplate.convertAndSend(exchangeName, "", noticeData);
        }
    }
}
