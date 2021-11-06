package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.json.Array;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

/**
 * 数组形式使用
 *
 * @author HouKunLin
 */
@SpringBootTest
class ArrayUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testString1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":"0,1","userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    @Test
    void testString2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(split = "|"))
            private String userType;
        }
        final Bean bean = new Bean("0|1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0|1)
        System.out.println(value); // {"userType":"0|1","userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":\"0|1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    @Test
    void testString3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false))
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":"0,1","userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testString4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false), replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testStringNull1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testStringNull2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(split = "|"))
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testStringNull3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false))
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    @Test
    void testStringNull4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false), replace = DictText.Type.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        Assertions.assertEquals("{\"userType\":[]}", value);
    }

    @Test
    void testArray1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["0","1"])
        System.out.println(value); // {"userType":["0","1"],"userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"0\",\"1\"],\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    @Test
    void testArray2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(ignoreNull = false))
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":\"null、系统管理、普通用户\"}", value);
    }

    @Test
    void testArray3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false))
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false), replace = DictText.Type.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArrayNull1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testArrayNull2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(ignoreNull = false))
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testArrayNull3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false))
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    @Test
    void testArrayNull4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, array = @Array(toText = false), replace = DictText.Type.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        Assertions.assertEquals("{\"userType\":[]}", value);
    }
}
