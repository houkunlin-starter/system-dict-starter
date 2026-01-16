package com.houkunlin.system.dict.starter.common;

import com.houkunlin.system.dict.starter.bean.DictValue;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.ArrayList;
import java.util.List;

/**
 * @author HouKunLin
 */
@ConditionalOnProperty("system.dict.test.scheduled")
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
        publisher.publishEvent(new RefreshDictEvent("定时刷新字典：" + System.currentTimeMillis(), true, false));
        logger.info("结束定时刷新事件");


        long startTime = System.nanoTime();
        for (int i = 0; i < 1000; i++) {
            publisher.publishEvent(new RefreshDictValueEvent(DictValue.builder().dictType("DictUser").value("" + i).title("昵称" + i).build(), false));
        }
        logger.info("单条数据刷新1000次耗时：{} ms", (System.nanoTime() - startTime) / 100_0000.0);


        startTime = System.nanoTime();
        List<DictValue> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            list.add(DictValue.builder().dictType("DictUser").value("" + i).title("昵称" + i).build());
        }
        publisher.publishEvent(new RefreshDictValueEvent(list, false));
        logger.info("批量数据刷新1000次耗时：{} ms", (System.nanoTime() - startTime) / 100_0000.0);


        list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(DictValue.builder().dictType("DictUser").value("" + i).title("昵称" + i).build());
        }
        startTime = System.nanoTime();
        int x = 1000 / list.size() + 1;
        for (int i = 0; i < x; i++) {
            publisher.publishEvent(new RefreshDictValueEvent(list, false));
        }
        logger.info("批量数据刷新 {} 条数据耗时（默认）：{} ms", list.size(), (System.nanoTime() - startTime) / 100_0000.0);


        for (int i = 10; i < 20; i++) {
            list.add(DictValue.builder().dictType("DictUser").value("" + i).title("昵称" + i).build());
        }
        startTime = System.nanoTime();
        for (int i = 0; i < x; i++) {
            publisher.publishEvent(new RefreshDictValueEvent(list, false));
        }
        logger.info("批量数据刷新 {} 条数据耗时（批量）：{} ms", list.size(), (System.nanoTime() - startTime) / 100_0000.0);

        logger.info("结束定时刷新事件");
    }
}
