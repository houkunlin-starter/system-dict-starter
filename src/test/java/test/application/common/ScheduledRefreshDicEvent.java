package test.application.common;

import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author HouKunLin
 */
@Configuration
public class ScheduledRefreshDicEvent {
    private static final Logger logger = LoggerFactory.getLogger(ScheduledRefreshDicEvent.class);
    private final ApplicationEventPublisher publisher;

    public ScheduledRefreshDicEvent(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Scheduled(fixedRate = 30000)
    public void refreshDicEvent() {
        logger.info("开始定时刷新事件");
        publisher.publishEvent(new RefreshDictEvent("定时刷新字典：" + System.currentTimeMillis(), true, true));
        logger.info("结束定时刷新事件");
    }
}
