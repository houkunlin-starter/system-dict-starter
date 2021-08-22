package com.houkunlin.system.dict.starter.dic;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import test.application.common.bean.Bean1;
import test.application.common.bean.Bean2;
import test.application.common.bean.PeopleType;

/**
 * @author HouKunLin
 */
@SpringBootTest
class DictApplicationTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher publisher;

    @Test
    void testBean1() throws JsonProcessingException {
        System.out.println(toJson(new Bean2()));
        final Bean1 bean1 = new Bean1();
        System.out.println(toJson(bean1));
        System.out.println(toJson(new Bean2()));
        System.out.println(DictUtil.getDicType(PeopleType.class.getSimpleName()));
        System.out.println(toJson(DictUtil.getDicType(PeopleType.class.getSimpleName())));
    }

    @Test
    void testRefresh() throws InterruptedException {
        publisher.publishEvent(new RefreshDictEvent("test", true));
        Thread.sleep(20 * 1000);
    }

    private String toJson(Object o) throws JsonProcessingException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }
}
