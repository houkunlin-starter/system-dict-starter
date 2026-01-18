package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.common.bean.Bean1;
import com.houkunlin.dict.common.bean.PeopleType;
import com.houkunlin.dict.enums.DictBoolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.*;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.test.annotation.DirtiesContext;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class DictUtilTransformTest {
    private static final Logger logger = LoggerFactory.getLogger(DictUtilTransformTest.class);
    @Autowired
    private ObjectMapper objectMapper;
    private final ParserContext parserContext = new TemplateParserContext();
    private final ExpressionParser parser = new SpelExpressionParser();

    @Test
    void testA() throws JacksonException {
        final Bean1 bean = new Bean1();
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户"}
        final Bean1 transform = DictUtil.transform(bean);
        System.out.println(transform);
        Assertions.assertEquals(transform.getClass().getName(), bean.getClass().getName() + "$DictChildren");

        final StandardEvaluationContext context = new StandardEvaluationContext(null);
        context.setVariable("bean1", bean);
        context.setVariable("bean2", transform);

        Assertions.assertEquals("系统管理、普通用户", parseExpression("#{#bean2.userTypeText}", context));
        Assertions.assertEquals("系统管理", parseExpression("#{#bean2.userType32Text}", context));
        Assertions.assertNull(parseExpression("#{#bean2.accidentType21Text}", context));
    }

    /**
     * 解析SPEL
     *
     * @param message SpEL表达式
     * @return 解析结果
     */
    private String parseExpression(String message, EvaluationContext context) {
        if (message.length() < 5 || !message.contains(parserContext.getExpressionPrefix())) {
            return message;
        }

        try {
            return parser.parseExpression(message, parserContext).getValue(context, String.class);
        } catch (EvaluationException | ParseException e) {
            if (logger.isErrorEnabled()) {
                logger.error("应用日志 SpEL 解析错误：" + message, e);
            }
            return message;
        }
    }

    /**
     * 解析SPEL
     *
     * @param message SpEL表达式
     * @return 解析结果
     */
    private String parseExpression(String message, Object bean) {
        if (message.length() < 5 || !message.contains(parserContext.getExpressionPrefix())) {
            return message;
        }
        final StandardEvaluationContext context = new StandardEvaluationContext(null);
        context.setVariable("bean", bean);

        try {
            return parser.parseExpression(message, parserContext).getValue(context, String.class);
        } catch (EvaluationException | ParseException e) {
            if (logger.isErrorEnabled()) {
                logger.error("应用日志 SpEL 解析错误：" + message, e);
            }
            return message;
        }
    }

    @Test
    void testB() throws Exception {
        // 动态创建子类
        Bean01 bean0101 = new Bean01(null); // Bean01 { userType = null }
        Bean01 bean0101transform = DictUtil.transform(bean0101); // Bean01$DictChildren { userType = null, userTypeText = "" }
        Assertions.assertNotSame(bean0101, bean0101transform);
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", objectMapper.writeValueAsString(bean0101));
        Assertions.assertEquals(bean0101transform.getClass().getName(), bean0101.getClass().getName() + "$DictChildren");
        // Jackson 序列化时是动态插入json字段的，因此导致多了一个 userTypeText 字段内容，但又不报错
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\",\"userTypeText\":\"\"}", objectMapper.writeValueAsString(bean0101transform));
        Assertions.assertEquals("", parseExpression("#{#bean.userTypeText}", bean0101transform));

        // 动态创建子类
        Bean01 bean0102 = new Bean01("1"); // Bean01 { userType = "1" }
        Bean01 bean0102transform = DictUtil.transform(bean0102); // Bean01$DictChildren { userType = "1", userTypeText = "普通用户" }
        Assertions.assertNotSame(bean0102, bean0102transform);
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\"}", objectMapper.writeValueAsString(bean0102));
        Assertions.assertEquals(bean0102transform.getClass().getName(), bean0102.getClass().getName() + "$DictChildren");
        // Jackson 序列化时是动态插入json字段的，因此导致多了一个 userTypeText 字段内容，但又不报错
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\",\"userTypeText\":\"普通用户\"}", objectMapper.writeValueAsString(bean0102transform));
        Assertions.assertEquals("普通用户", parseExpression("#{#bean.userTypeText}", bean0102transform));


        // 不创建子类，直接替换本身字段值
        Bean02 bean0201 = new Bean02(null); // Bean02 { userType = null }
        Assertions.assertEquals("{\"userType\":\"\"}", objectMapper.writeValueAsString(bean0201));
        Bean02 bean0201transform = DictUtil.transform(bean0201); // Bean02 { userType = "" }
        Assertions.assertSame(bean0201, bean0201transform);
        Assertions.assertEquals(bean0201transform.getClass().getName(), bean0201.getClass().getName());
        Assertions.assertEquals("{\"userType\":\"\"}", objectMapper.writeValueAsString(bean0201transform));
        Assertions.assertEquals("", parseExpression("#{#bean.userType}", bean0201));
        Assertions.assertEquals("", parseExpression("#{#bean.userType}", bean0201transform));
        Assertions.assertEquals("", bean0201transform.getUserType());
        Assertions.assertEquals("", bean0201.getUserType());

        // 不创建子类，直接替换本身字段值
        Bean02 bean0202 = new Bean02("1"); // Bean02 { userType = "1" }
        Assertions.assertEquals("{\"userType\":\"普通用户\"}", objectMapper.writeValueAsString(bean0202));
        Bean02 bean0202transform = DictUtil.transform(bean0202); // Bean02 { userType = "普通用户" }
        Assertions.assertSame(bean0202, bean0202transform);
        Assertions.assertEquals(bean0202transform.getClass().getName(), bean0202.getClass().getName());
        // 因为 userType 值已经变为字典文本了（code -> text == text string），但是注解还在字段上，此时再用 Jackson 序列化，就会找不到字典文本（code[but is code dict text] -> text == empty string）
        Assertions.assertEquals("{\"userType\":\"\"}", objectMapper.writeValueAsString(bean0202transform));
        Assertions.assertEquals("普通用户", parseExpression("#{#bean.userType}", bean0202));
        Assertions.assertEquals("普通用户", parseExpression("#{#bean.userType}", bean0202transform));
        Assertions.assertEquals("普通用户", bean0202.getUserType());
        Assertions.assertEquals("普通用户", bean0202transform.getUserType());


        // 不创建子类，把值写入另外一个字段
        Bean03 bean0301 = new Bean03(null, null); // Bean03 { userType = null, userTypeText = null }
        Assertions.assertNull(bean0301.getUserTypeText());
        Bean03 bean0301transform = DictUtil.transform(bean0301); // Bean03 { userType = null, userTypeText = "" }
        Assertions.assertSame(bean0301, bean0301transform);
        // Jackson 序列化时是动态插入json字段的，因此导致多了一个 userTypeText 字段内容，但又不报错
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\",\"userTypeText\":\"\"}", objectMapper.writeValueAsString(bean0301));
        Assertions.assertEquals(bean0301transform.getClass().getName(), bean0301.getClass().getName());
        // Jackson 序列化时是动态插入json字段的，因此导致多了一个 userTypeText 字段内容，但又不报错
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\",\"userTypeText\":\"\"}", objectMapper.writeValueAsString(bean0301transform));
        Assertions.assertEquals("", parseExpression("#{#bean.userTypeText}", bean0301));
        Assertions.assertEquals("", parseExpression("#{#bean.userTypeText}", bean0301transform));
        Assertions.assertEquals("", bean0301.getUserTypeText());
        Assertions.assertEquals("", bean0301transform.getUserTypeText());

        // 不创建子类，把值写入另外一个字段
        Bean03 bean0302 = new Bean03("1", null); // Bean03 { userType = "1", userTypeText = null }
        Assertions.assertNull(bean0302.getUserTypeText());
        Bean03 bean0302transform = DictUtil.transform(bean0302); // Bean03 { userType = "1", userType = "普通用户" }
        Assertions.assertSame(bean0302, bean0302transform);
        // Jackson 序列化时是动态插入json字段的，因此导致多了一个 userTypeText 字段内容，但又不报错
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\",\"userTypeText\":\"普通用户\"}", objectMapper.writeValueAsString(bean0302));
        Assertions.assertEquals(bean0302transform.getClass().getName(), bean0302.getClass().getName());
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\",\"userTypeText\":\"普通用户\"}", objectMapper.writeValueAsString(bean0302transform));
        Assertions.assertEquals("普通用户", parseExpression("#{#bean.userTypeText}", bean0302));
        Assertions.assertEquals("普通用户", parseExpression("#{#bean.userTypeText}", bean0302transform));
        Assertions.assertEquals("普通用户", bean0302.getUserTypeText());
        Assertions.assertEquals("普通用户", bean0302transform.getUserTypeText());
    }

    /**
     * 动态创建子类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bean01 {
        @DictText(enums = PeopleType.class)
        private String userType;
    }

    /**
     * 不创建子类，直接替换本身字段值
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bean02 {
        @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
        private Object userType;
    }

    /**
     * 不创建子类，把值写入另外一个字段
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Bean03 {
        @DictText(enums = PeopleType.class, replace = DictBoolType.NO)
        private String userType;
        private String userTypeText;
    }
}
