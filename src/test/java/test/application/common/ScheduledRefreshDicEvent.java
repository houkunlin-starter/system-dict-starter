package test.application.common;

import com.houkunlin.system.dic.starter.notice.RefreshDicEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author HouKunLin
 */
@Configuration
public class ScheduledRefreshDicEvent {
    private final ApplicationEventPublisher publisher;

    public ScheduledRefreshDicEvent(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    @Scheduled(fixedRate = 30000)
    public void refreshDicEvent() {
        publisher.publishEvent(new RefreshDicEvent("定时刷新字典：" + System.currentTimeMillis(), true, true));
    }
}
