package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 默认注解使用测试
 *
 * @author HouKunLin
 */
@SpringBootTest
class DictTypeKeyHandlerUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    private static final Logger logger = LoggerFactory.getLogger(DictTypeKeyHandlerUsageTest.class);
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBasic1() throws JsonProcessingException {
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
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\",\"userType1\":null}", value);
    }

    @Test
    void testBasic2() throws JsonProcessingException {
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
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeTitle\":\"普通用户\"}", value);
    }

    @Test
    void testBasic3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, fieldName = "userTypeTitle", replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"普通用户"}
        Assertions.assertEquals("{\"userType\":\"普通用户\"}", value);
    }

    @Test
    void testBasicNull1() throws JsonProcessingException {
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
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testBasicNull2() throws JsonProcessingException {
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
        Assertions.assertEquals("{\"userType\":null,\"userTypeTitle\":\"\"}", value);
    }

    @Test
    void testBasicNull3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, fieldName = "userTypeTitle", replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":""}
        Assertions.assertEquals("{\"userType\":\"\"}", value);
    }

    @Test
    void testBasicNullable1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, nullable = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","userTypeText":null}
        Assertions.assertEquals("{\"userType\":\"-1\",\"userTypeText\":null}", value);
    }

    @Test
    void testBasicNullable2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, nullable = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","userTypeText":""}
        Assertions.assertEquals("{\"userType\":\"-1\",\"userTypeText\":\"\"}", value);
    }

    @Test
    void testBasicNullable3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, nullable = DictText.Type.NO, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":""}
        Assertions.assertEquals("{\"userType\":\"\"}", value);
    }

    @Test
    void testMapValue1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户"}
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\"}", value);
    }

    @Test
    void testMapValue2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":{"text":"普通用户","value":"1"}}
        Assertions.assertEquals("{\"userType\":{\"text\":\"普通用户\",\"value\":\"1\"}}", value);
    }

    @Test
    void testMapValue3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValue4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map")
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","map":{"text":"普通用户","value":"1"}}
        Assertions.assertEquals("{\"userType\":\"1\",\"map\":{\"text\":\"普通用户\",\"value\":\"1\"}}", value);
    }

    @Test
    void testMapValue5() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map")
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValue6() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map", replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullable1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, nullable = DictText.Type.YES, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":null,"value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"text\":null,\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullable2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, nullable = DictText.Type.NO, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullable3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.YES, replace = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":null,"value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"text\":null,\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullable4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.NO, replace = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullable5() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.NO, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullableNull1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, nullable = DictText.Type.YES, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":null,"value":null}}
        Assertions.assertEquals("{\"userType\":{\"text\":null,\"value\":null}}", value);
    }

    @Test
    void testMapValueNullableNull2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, nullable = DictText.Type.NO, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":"","value":null}}
        Assertions.assertEquals("{\"userType\":{\"text\":\"\",\"value\":null}}", value);
    }

    @Test
    void testMapValueNullableNull3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.YES, replace = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"map":{"text":null,"value":null}}
        Assertions.assertEquals("{\"userType\":null,\"map\":{\"text\":null,\"value\":null}}", value);
    }

    @Test
    void testMapValueNullableNull4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.NO, replace = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"map":{"text":"","value":null}}
        Assertions.assertEquals("{\"userType\":null,\"map\":{\"text\":\"\",\"value\":null}}", value);
    }

    @Test
    void testMapValueNullableNull5() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(dictTypeHandler = DictTypeKeyHandlerImpl.class, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.NO, replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":{"text":"","value":null}}
        Assertions.assertEquals("{\"userType\":{\"text\":\"\",\"value\":null}}", value);
    }
}