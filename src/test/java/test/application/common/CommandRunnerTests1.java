package test.application.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import test.application.common.bean.Bean1;
import test.application.common.bean.Bean2;
import test.application.common.bean.PeopleType;

/**
 * 启动完成后执行一段代码
 *
 * @author HouKunLin
 */
@Component
public class CommandRunnerTests1 implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CommandRunnerTests1.class);
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    public CommandRunnerTests1(final ObjectMapper objectMapper, final ApplicationEventPublisher publisher) {
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    @Override
    public void run(final String... args) throws Exception {
        logger.info("默认的 Bean2 输出：{}", toJson(new Bean2()));
        final Bean1 bean1 = new Bean1();
        logger.info("默认的 Bean1 输出：{}", toJson(bean1));
        logger.info("从字典存储中读取 PeopleType ：{}", DictUtil.getDictType(PeopleType.class.getSimpleName()));
        logger.info("从字典存储中读取 PeopleType JSON：{}", toJson(DictUtil.getDictType(PeopleType.class.getSimpleName())));
        publisher.publishEvent(new RefreshDictEvent("CommandRunnerTests1 刷新", true, false));
    }

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }
}
