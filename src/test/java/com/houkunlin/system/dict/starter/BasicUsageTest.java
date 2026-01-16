package com.houkunlin.system.dict.starter;

import tools.jackson.core.JacksonException;
import com.houkunlin.system.dict.starter.json.DictBoolType;
import com.houkunlin.system.dict.starter.annotation.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tools.jackson.databind.ObjectMapper;

/**
 * 默认注解使用测试
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SystemDictScan
class BasicUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBasic1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(DICT_TYPE)
            private String userType;
            private String userType1;
        }
        final Bean bean = new Bean("1", null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1,userType1=null)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户","userType1":null}
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\",\"userType1\":null}", value);
    }

    @Test
    void testBasic2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, fieldName = "userTypeTitle")
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeTitle":"普通用户"}
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeTitle\":\"普通用户\"}", value);
    }

    @Test
    void testBasic3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"普通用户"}
        Assertions.assertEquals("{\"userType\":\"普通用户\"}", value);
    }

    @Test
    void testBasicNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testBasicNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, fieldName = "userTypeTitle")
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeTitle":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeTitle\":\"\"}", value);
    }

    @Test
    void testBasicNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":""}
        Assertions.assertEquals("{\"userType\":\"\"}", value);
    }

    @Test
    void testBasicNullable1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, nullable = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","userTypeText":null}
        Assertions.assertEquals("{\"userType\":\"-1\",\"userTypeText\":null}", value);
    }

    @Test
    void testBasicNullable2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, nullable = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","userTypeText":""}
        Assertions.assertEquals("{\"userType\":\"-1\",\"userTypeText\":\"\"}", value);
    }

    @Test
    void testBasicNullable3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":""}
        Assertions.assertEquals("{\"userType\":\"\"}", value);
    }

    @Test
    void testMapValue1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户"}
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\"}", value);
    }

    @Test
    void testMapValue2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":{"text":"普通用户","value":"1"}}
        Assertions.assertEquals("{\"userType\":{\"value\":\"1\",\"text\":\"普通用户\"}}", value);
    }

    @Test
    void testMapValue3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    @Test
    void testMapValue4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map")
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","map":{"text":"普通用户","value":"1"}}
        Assertions.assertEquals("{\"userType\":\"1\",\"map\":{\"value\":\"1\",\"text\":\"普通用户\"}}", value);
    }

    @Test
    void testMapValue5() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map")
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    @Test
    void testMapValue6() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    @Test
    void testMapValueNullable1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, nullable = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":null,"value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":null}}", value);
    }

    @Test
    void testMapValueNullable2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    @Test
    void testMapValueNullable3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.YES, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":null,"value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"value\":\"-1\",\"text\":null}}", value);
    }

    @Test
    void testMapValueNullable4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    @Test
    void testMapValueNullable5() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"value\":\"-1\",\"text\":\"\"}}", value);
    }

    @Test
    void testMapValueNullableNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, nullable = DictBoolType.YES, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":null,"value":null}}
        Assertions.assertEquals("{\"userType\":{\"value\":null,\"text\":null}}", value);
    }

    @Test
    void testMapValueNullableNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":"","value":null}}
        Assertions.assertEquals("{\"userType\":{\"value\":null,\"text\":\"\"}}", value);
    }

    @Test
    void testMapValueNullableNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.YES, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"map":{"text":null,"value":null}}
        Assertions.assertEquals("{\"userType\":null,\"map\":{\"value\":null,\"text\":null}}", value);
    }

    @Test
    void testMapValueNullableNull4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.NO)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"map":{"text":"","value":null}}
        Assertions.assertEquals("{\"userType\":null,\"map\":{\"value\":null,\"text\":\"\"}}", value);
    }

    @Test
    void testMapValueNullableNull5() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictBoolType.YES, fieldName = "map", nullable = DictBoolType.NO, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":"","value":null}}
        Assertions.assertEquals("{\"userType\":{\"value\":null,\"text\":\"\"}}", value);
    }
}
