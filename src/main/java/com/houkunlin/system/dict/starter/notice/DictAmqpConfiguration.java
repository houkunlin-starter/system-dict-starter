package com.houkunlin.system.dict.starter.notice;

import com.houkunlin.system.dict.starter.DictRegistrar;
import com.houkunlin.system.dict.starter.properties.DictProperties;
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
 * 数据字典消息队列配置
 *
 * @author HouKunLin
 */
@ConditionalOnProperty(prefix = "system.dict", name = "mq-type", havingValue = "AMQP")
@ConditionalOnClass(AmqpTemplate.class)
@Configuration
public class DictAmqpConfiguration {
    private static final Logger logger = LoggerFactory.getLogger(DictAmqpConfiguration.class);
    private final DictRegistrar dictRegistrar;
    private final AmqpTemplate amqpTemplate;
    private final String applicationName;
    private final String exchangeName;

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
