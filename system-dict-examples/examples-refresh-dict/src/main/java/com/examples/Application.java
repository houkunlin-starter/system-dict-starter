package com.examples;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.SystemDictScan;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SystemDictScan 是必须加的一个注解
 *
 * @author HouKunLin
 */
@SpringBootApplication
@SystemDictScan
@RestController
@RequestMapping
@Component
@RequiredArgsConstructor
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    private final ApplicationEventPublisher publisher;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(final String... args) throws Exception {
        final String dictType = UserType.class.getSimpleName();
        logger.info("用户类型：{}", DictUtil.getDictType(dictType));
        logger.info("用户类型：UserType(1) = {}", DictUtil.getDictText(dictType, "1"));
        System.out.println();

        publisher.publishEvent(new RefreshDictValueEvent(DictValueVo.builder()
            .dictType(dictType)
            .value(1)
            .title("刷新单个字典值")
            .build()));
        logger.info("用户类型：{}", DictUtil.getDictType(dictType));
        logger.info("用户类型：UserType(1) = {}", DictUtil.getDictText(dictType, "1"));
        System.out.println();

        publisher.publishEvent(new RefreshDictTypeEvent(DictTypeVo.newBuilder(dictType, "刷新一个完整的字典类型")
            .add(0, "刷新一个完整的字典类型0")
            .add(1, "刷新一个完整的字典类型1")
            .add(2, "刷新一个完整的字典类型2")
            .build()));
        logger.info("用户类型：{}", DictUtil.getDictType(dictType));
        logger.info("用户类型：UserType(1) = {}", DictUtil.getDictText(dictType, "1"));
        System.out.println();

        publisher.publishEvent(new RefreshDictEvent("从系统中重新读取字典信息，所有系统字典信息将被还原成系统启动时的最初版本", true, true));
        logger.info("用户类型：{}", DictUtil.getDictType(dictType));
        logger.info("用户类型：UserType(1) = {}", DictUtil.getDictText(dictType, "1"));
        System.out.println();
    }
}
