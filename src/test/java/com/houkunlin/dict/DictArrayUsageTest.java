package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.enums.DictBoolType;
import com.houkunlin.dict.enums.NullStrategy;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.List;

/**
 * 数组形式使用
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class DictArrayUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testString1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(split = ",")
            @DictText(value = DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":"0,1","userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    @Test
    void testString2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(split = "\\|")
            @DictText(value = DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean("0|1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0|1)
        System.out.println(value); // {"userType":"0|1","userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":\"0|1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    @Test
    void testString3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",")
            @DictText(value = DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":"0,1","userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testString4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",")
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testStringNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(value = DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testStringNull11() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testStringNull111() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE, nullable = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":null}", value);
    }

    @Test
    void testStringNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(split = "\\|")
            @DictText(value = DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testStringNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    @Test
    void testStringNull4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        Assertions.assertEquals("{\"userType\":[]}", value);
    }

    @Test
    void testArray1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["0","1"])
        System.out.println(value); // {"userType":["0","1"],"userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"0\",\"1\"],\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    @Test
    void testArray11() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["0","1"])
        System.out.println(value); // {"userType":["0","1"],"userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(nullStrategy = NullStrategy.NULL)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":\"null、系统管理、普通用户\"}", value);
    }

    @Test
    void testArray22() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray222() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.IGNORE)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray2222() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.NULL)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[null,\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray22222() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.EMPTY)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"\",\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray33() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",")
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    @Test
    void testArray333() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",", nullStrategy = NullStrategy.IGNORE)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    @Test
    void testArray3333() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",", nullStrategy = NullStrategy.NULL)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[null],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    @Test
    void testArray33333() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",", nullStrategy = NullStrategy.EMPTY)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[\"\"],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    @Test
    void testArray4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray44() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.IGNORE)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray444() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.NULL)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[null,\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray4444() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.EMPTY)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"\",\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray44444() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",", nullStrategy = NullStrategy.IGNORE)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[[],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    @Test
    void testArray444444() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",", nullStrategy = NullStrategy.NULL)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[[null],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    @Test
    void testArray4444444() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",", nullStrategy = NullStrategy.EMPTY)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[[\"\"],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    @Test
    void testArrayNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testArrayNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(nullStrategy = NullStrategy.NULL)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    @Test
    void testArrayNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        Assertions.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    @Test
    void testArrayNull4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        Assertions.assertEquals("{\"userType\":[]}", value);
    }
}
