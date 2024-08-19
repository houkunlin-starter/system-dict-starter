package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.common.bean.Bean1;
import com.houkunlin.system.dict.starter.common.bean.PeopleType;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SystemDictScan
class DictUtilTransformTest {
    private static final Logger logger = LoggerFactory.getLogger(DictUtilTransformTest.class);
    @Autowired
    private ObjectMapper objectMapper;
    private final ParserContext parserContext = new TemplateParserContext();
    private final ExpressionParser parser = new SpelExpressionParser();

    @Test
    void testA() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class)
            private String userType;
        }
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
}
