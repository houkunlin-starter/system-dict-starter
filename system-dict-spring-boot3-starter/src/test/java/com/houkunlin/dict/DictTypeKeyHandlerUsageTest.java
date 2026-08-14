package com.houkunlin.dict;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.enums.DictBoolType;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;


/**
 * 自定义字典类型处理器（DictTypeKeyHandler）使用测试
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class DictTypeKeyHandlerUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    private static final Logger logger = LoggerFactory.getLogger(DictTypeKeyHandlerUsageTest.class);
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试当前 Starter 模块路径是否匹配
     */
    @Test
    void testJavaAtPath() {
        TestStarterAssertions.assertCurrentStarterModule("3");
    }

    /**
     * 测试自定义字典类型处理器默认输出字典文本字段
     */
    @Test
    void testBasic1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class)
            private String userType;
            private String userType1;
        }
        final Bean bean = new Bean("1", null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1,userType1=null)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户","userType1":null}
        JsonAssertUtil.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\",\"userType1\":null}", value);
    }

    /**
     * 测试自定义字典类型处理器自定义字段名输出字典文本
     */
    @Test
    void testBasic2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, fieldName = "userTypeTitle")
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeTitle":"普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"1\",\"userTypeTitle\":\"普通用户\"}", value);
    }

    /**
     * 测试自定义字典类型处理器 replace 替换原字段值为字典文本
     */
    @Test
    void testBasic3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"普通用户\"}", value);
    }

    /**
     * 测试字段值为 null 时输出空字符串
     */
    @Test
    void testBasicNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试字段值为 null 且自定义字段名时输出空字符串
     */
    @Test
    void testBasicNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, fieldName = "userTypeTitle")
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeTitle":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeTitle\":\"\"}", value);
    }

    /**
     * 测试字段值为 null 且 replace 时输出空字符串
     */
    @Test
    void testBasicNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":""}
        JsonAssertUtil.assertEquals("{\"userType\":\"\"}", value);
    }

    /**
     * 测试 nullable=YES 时未匹配字典值输出 null
     */
    @Test
    void testBasicNullable1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, nullable = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","userTypeText":null}
        JsonAssertUtil.assertEquals("{\"userType\":\"-1\",\"userTypeText\":null}", value);
    }

    /**
     * 测试 nullable=NO 时未匹配字典值输出空字符串
     */
    @Test
    void testBasicNullable2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, nullable = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":\"-1\",\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试 nullable=NO 且 replace 时未匹配字典值输出空字符串
     */
    @Test
    void testBasicNullable3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":""}
        JsonAssertUtil.assertEquals("{\"userType\":\"\"}", value);
    }

    /**
     * 测试 mapValue=NO 时输出普通字典文本字段
     */
    @Test
    void testMapValue1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\"}", value);
    }

    /**
     * 测试 mapValue=YES 且 replace 时输出 value/text 结构
     */
    @Test
    void testMapValue2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":{"text":"普通用户","value":"1"}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":\"1\",\"text\":\"普通用户\"}}", value);
    }

    /**
     * 测试 mapValue=YES 时未匹配字典值输出 value/text 结构
     */
    @Test
    void testMapValue3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    /**
     * 测试 mapValue=YES 时输出到自定义字段 map
     */
    @Test
    void testMapValue4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map")
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","map":{"text":"普通用户","value":"1"}}
        JsonAssertUtil.assertEquals("{\"userType\":\"1\",\"map\":{\"value\":\"1\",\"text\":\"普通用户\"}}", value);
    }

    /**
     * 测试 mapValue=YES 时未匹配字典值输出到自定义字段 map
     */
    @Test
    void testMapValue5() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map")
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":\"-1\",\"map\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    /**
     * 测试 mapValue=YES 且 replace 时未匹配字典值输出 value/text 结构
     */
    @Test
    void testMapValue6() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    /**
     * 测试 mapValue=YES、nullable=YES 时未匹配字典值 text 为 null
     */
    @Test
    void testMapValueNullable1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, nullable = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":null,"value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":null}}", value);
    }

    /**
     * 测试 mapValue=YES、nullable=NO 时未匹配字典值 text 为空字符串
     */
    @Test
    void testMapValueNullable2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    /**
     * 测试 mapValue=YES、nullable=YES 且自定义字段名时 text 为 null
     */
    @Test
    void testMapValueNullable3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.YES, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":null,"value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":\"-1\",\"map\":{\"value\":\"-1\",\"text\":null}}", value);
    }

    /**
     * 测试 mapValue=YES、nullable=NO 且自定义字段名时 text 为空字符串
     */
    @Test
    void testMapValueNullable4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":\"-1\",\"map\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    /**
     * 测试 mapValue=YES、nullable=NO 且 replace 时输出 value/text 结构
     */
    @Test
    void testMapValueNullable5() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    /**
     * 测试字段值为 null、mapValue=YES、nullable=YES 时输出 value/text 结构
     */
    @Test
    void testMapValueNullableNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, nullable = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":null,"value":null}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":null,\"text\":null}}", value);
    }

    /**
     * 测试字段值为 null、mapValue=YES、nullable=NO 时输出 value/text 结构
     */
    @Test
    void testMapValueNullableNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":"","value":null}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":null,\"text\":\"\"}}", value);
    }

    /**
     * 测试字段值为 null、mapValue=YES、nullable=YES 且自定义字段名时输出 value/text 结构
     */
    @Test
    void testMapValueNullableNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.YES, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"map":{"text":null,"value":null}}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"map\":{\"value\":null,\"text\":null}}", value);
    }

    /**
     * 测试字段值为 null、mapValue=YES、nullable=NO 且自定义字段名时输出 value/text 结构
     */
    @Test
    void testMapValueNullableNull4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"map":{"text":"","value":null}}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"map\":{\"value\":null,\"text\":\"\"}}", value);
    }

    /**
     * 测试字段值为 null、mapValue=YES、nullable=NO 且 replace 时输出 value/text 结构
     */
    @Test
    void testMapValueNullableNull5() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":"","value":null}}
        JsonAssertUtil.assertEquals("{\"userType\":{\"value\":null,\"text\":\"\"}}", value);
    }
}
