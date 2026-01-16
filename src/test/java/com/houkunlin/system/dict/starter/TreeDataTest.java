package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictType;
import org.springframework.test.annotation.DirtiesContext;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.annotation.Array;
import com.houkunlin.system.dict.starter.annotation.DictText;
import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

/**
 * 默认注解使用测试
 *
 * @author HouKunLin
 * @since 1.4.6
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class TreeDataTest {
    public static final String DICT_TYPE = "TreeData";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private static ApplicationEventPublisher publisher;

    @Autowired
    public void setPublisher(final ApplicationEventPublisher publisher) {
        final DictType typeVo = DictType.newBuilder(DICT_TYPE, "树形结构数据测试")
            .add("", "1", "节点1")
            .add("", "2", "节点2")
            .add("", "3", "节点3")
            .add("1", "1-1", "节点1-1")
            .add("1", "1-2", "节点1-2")
            .add("1", "1-3", "节点1-3")
            .add("2", "2-1", "节点2-1")
            .add("2", "2-2", "节点2-2")
            .add("2", "2-3", "节点2-3")
            .add("3", "3-1", "节点3-1")
            .add("3", "3-2", "节点3-2")
            .add("3", "3-3", "节点3-3")
            .build();
        publisher.publishEvent(new RefreshDictTypeEvent(typeVo));
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 1.4.6
     */
    @Test
    void testBasic1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, tree = true)
            private String userType;
            @DictText(value = DICT_TYPE, tree = true)
            private String userType1;
            @DictText(value = DICT_TYPE, tree = true, array = @Array(toText = false))
            private String userType3;
        }
        final Bean bean = new Bean("1", "3-3", "1-1,1-2,1-3,2-1,2-2,2-3,3-1,3-2,3-3,3-4");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
    }

}
