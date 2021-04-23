package test.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dic.starter.DicUtil;
import com.houkunlin.system.dic.starter.notice.RefreshDicEvent;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import test.application.bean.Bean1;
import test.application.bean.Bean2;
import test.application.bean.PeopleType;

/**
 * 启动完成后执行一段代码
 *
 * @author HouKunLin
 */
@Component
public class CommandRunnerTests implements CommandLineRunner {
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    public CommandRunnerTests(final ObjectMapper objectMapper, final ApplicationEventPublisher publisher) {
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    @Override
    public void run(final String... args) throws Exception {
        System.out.println(toJson(new Bean2()));
        final Bean1 bean1 = new Bean1();
        System.out.println(toJson(bean1));
        System.out.println(toJson(new Bean2()));
        System.out.println(DicUtil.getDicType(PeopleType.class.getSimpleName()));
        System.out.println(toJson(DicUtil.getDicType(PeopleType.class.getSimpleName())));
        publisher.publishEvent(new RefreshDicEvent("test", true, true));
    }

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }
}
