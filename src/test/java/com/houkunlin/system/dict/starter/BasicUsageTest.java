package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * 默认注解使用测试
 *
 * @author HouKunLin
 */
@SpringBootTest
@SystemDictScan(basePackages = "test.application")
class BasicUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBasic1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户"}
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\"}", value);
    }

    @Test
    void testBasic2() throws JsonProcessingException {
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
    void testBasicNull1() throws JsonProcessingException {
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
    void testBasicNull2() throws JsonProcessingException {
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
    void testBasicNullable1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, nullable = DictText.Type.YES)
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
            @DictText(value = DICT_TYPE, nullable = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","userTypeText":""}
        Assertions.assertEquals("{\"userType\":\"-1\",\"userTypeText\":\"\"}", value);
    }

    @Test
    void testMapValue1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.NO)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, fieldName = "map")
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, fieldName = "map")
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullable1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, nullable = DictText.Type.YES)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, nullable = DictText.Type.NO)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.YES)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean("-1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=-1)
        System.out.println(value); // {"userType":"-1","map":{"text":"","value":"-1"}}
        Assertions.assertEquals("{\"userType\":\"-1\",\"map\":{\"text\":\"\",\"value\":\"-1\"}}", value);
    }

    @Test
    void testMapValueNullableNull1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, nullable = DictText.Type.YES)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, nullable = DictText.Type.NO)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.YES)
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
            @DictText(value = DICT_TYPE, mapValue = DictText.Type.YES, fieldName = "map", nullable = DictText.Type.NO)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"map":{"text":"","value":null}}
        Assertions.assertEquals("{\"userType\":null,\"map\":{\"text\":\"\",\"value\":null}}", value);
    }
}
