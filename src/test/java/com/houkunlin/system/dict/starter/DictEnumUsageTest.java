package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.common.bean.PeopleType;
import com.houkunlin.system.dict.starter.json.Array;
import com.houkunlin.system.dict.starter.json.DictBoolType;
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
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@SystemDictScan
class DictEnumUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testBasic1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText
            private PeopleType userType;
        }
        final Bean bean = new Bean(PeopleType.ADMIN);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0)
        System.out.println(value); // {"userType":0,"userTypeText":"系统管理"}
        Assertions.assertEquals("{\"userType\":0,\"userTypeText\":\"系统管理\"}", value);
    }

    @Test
    void testBasic2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(fieldName = "userTypeTitle")
            private PeopleType userType;
        }
        final Bean bean = new Bean(PeopleType.ADMIN);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0)
        System.out.println(value); // {"userType":0,"userTypeTitle":"系统管理"}
        Assertions.assertEquals("{\"userType\":0,\"userTypeTitle\":\"系统管理\"}", value);
    }

    @Test
    void testBasic3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private PeopleType userType;
        }
        final Bean bean = new Bean(PeopleType.ADMIN);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0)
        System.out.println(value); // {"userType":"系统管理"}
        Assertions.assertEquals("{\"userType\":\"系统管理\"}", value);
    }

    @Test
    void testBasicNull1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText
            private PeopleType userType;
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
            @DictText(fieldName = "userTypeTitle")
            private PeopleType userType;
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
            @DictText(fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private PeopleType userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":""}
        Assertions.assertEquals("{\"userType\":\"\"}", value);
    }

    @Test
    void testArray1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, array = @Array)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[0,1])
        System.out.println(value); // {"userType":[0,1],"userTypeText":"系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[0,1],\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    @Test
    void testArray2() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, array = @Array(ignoreNull = false))
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":[null,0,1],"userTypeText":"null、系统管理、普通用户"}
        Assertions.assertEquals("{\"userType\":[null,0,1],\"userTypeText\":\"null、系统管理、普通用户\"}", value);
    }

    @Test
    void testArray3() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, array = @Array(toText = false))
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":[null,0,1],"userTypeText":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[null,0,1],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArray4() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, array = @Array(toText = false), replace = DictBoolType.YES)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        Assertions.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    @Test
    void testArrayNull1() throws JsonProcessingException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, array = @Array)
            private List<PeopleType> userType;
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
            @DictText(enums = PeopleType.class, array = @Array(ignoreNull = false))
            private List<PeopleType> userType;
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
            @DictText(enums = PeopleType.class, array = @Array(toText = false))
            private List<PeopleType> userType;
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
            @DictText(enums = PeopleType.class, array = @Array(toText = false), replace = DictBoolType.YES)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        Assertions.assertEquals("{\"userType\":[]}", value);
    }
}
