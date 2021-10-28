package com.houkunlin.system.dict.starter.notice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.DictRegistrar;
import com.houkunlin.system.dict.starter.properties.DictProperties;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.lang.NonNull;

import java.io.IOException;
import java.util.Objects;

/**
 * 使用 Redis 的发布/订阅 功能来处理字典刷新事件通知
 *
 * @author HouKunLin
 * @since 1.4.4
 */
@ConditionalOnClass(StringRedisTemplate.class)
@Configuration
public class DictRedisSubscribeConfiguration implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DictRedisSubscribeConfiguration.class);
    private final RedisMessageListenerContainer redisMessageListenerContainer;
    private final DictRegistrar dictRegistrar;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final String applicationName;
    private final String exchangeName;

    public DictRedisSubscribeConfiguration(final RedisMessageListenerContainer redisMessageListenerContainer,
                                           final DictRegistrar dictRegistrar,
                                           final StringRedisTemplate stringRedisTemplate,
                                           final ObjectMapper objectMapper,
                                           @Value("${spring.application.name:'system-dict'}") final String applicationName,
                                           final DictProperties dictProperties) {
        this.redisMessageListenerContainer = redisMessageListenerContainer;
        this.dictRegistrar = dictRegistrar;
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.applicationName = applicationName;
        this.exchangeName = dictProperties.getMqExchangeName();
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     */
    @EventListener
    public void refreshDict(RefreshDictEvent event) throws JsonProcessingException {
        final Object source = event.getSource();
        if (event.isNotifyOtherSystem()) {
            logger.debug("接收到刷新数据字典事件，使用 Redis 通知其他协同系统刷新数据字典内容。事件内容：{}", source);
            final RefreshNoticeData noticeData = RefreshNoticeData.builder()
                .message("刷新事件：" + source)
                .applicationName(applicationName)
                .notifyBrother(event.isNotifyOtherSystemAndBrother())
                .dictProviderClasses(event.getDictProviderClasses()).build();
            final String json = objectMapper.writeValueAsString(noticeData);
            stringRedisTemplate.convertAndSend(exchangeName, json);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        final MessageListener messageListener = new DictRedisMessageListener(dictRegistrar, objectMapper, applicationName, exchangeName);
        redisMessageListenerContainer.addMessageListener(messageListener, new ChannelTopic(exchangeName));
    }

    @RequiredArgsConstructor
    public static class DictRedisMessageListener implements MessageListener {
        private final DictRegistrar dictRegistrar;
        private final ObjectMapper objectMapper;
        private final String applicationName;
        private final String exchangeName;

        @Override
        public void onMessage(@NonNull final Message message, final byte[] pattern) {
            final String channel = pattern != null ? new String(pattern) : "";
            if (!Objects.equals(channel, exchangeName)) {
                logger.debug("所需要的通道类型不匹配：current {} != {}", exchangeName, channel);
                return;
            }
            final RefreshNoticeData noticeData;
            try {
                noticeData = objectMapper.readValue(message.getBody(), RefreshNoticeData.class);
            } catch (IOException e) {
                logger.error("订阅来自 Redis 的字典刷新事件在解析Json时出现错误", e);
                return;
            }
            if (!noticeData.isNotifyBrother() && Objects.equals(applicationName, noticeData.getApplicationName())) {
                logger.debug("收到来自当前系统发起的MQ消息，并且被标记忽略处理");
                return;
            }
            logger.debug("[start] Redis 通知刷新字典：{}", noticeData.getMessage());
            dictRegistrar.refreshDict(noticeData.getDictProviderClasses());
            logger.debug("[finish] Redis 通知刷新字典");
        }
    }
}
