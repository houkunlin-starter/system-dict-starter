package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.common.bean.PeopleType;
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
 * 枚举（enums 属性）使用测试
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class EnumUsageTest {
    public static final String DICT_TYPE = "PeopleType";
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 测试当前 Starter 模块路径是否匹配
     */
    @Test
    void testJavaAtPath() {
        TestStarterAssertions.assertCurrentStarterModule("4");
    }

    /**
     * 测试 enums 属性指定枚举的基础字典转换
     */
    @Test
    void testBasic1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeText":"普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"普通用户\"}", value);
    }

    /**
     * 测试 enums 属性指定枚举时自定义字段名输出
     */
    @Test
    void testBasic2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, fieldName = "userTypeTitle")
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"1","userTypeTitle":"普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"1\",\"userTypeTitle\":\"普通用户\"}", value);
    }

    /**
     * 测试 enums 属性指定枚举时 replace 替换输出
     */
    @Test
    void testBasic3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=1)
        System.out.println(value); // {"userType":"普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"普通用户\"}", value);
    }

    /**
     * 测试 enums 属性指定枚举时字段为 null 输出空字符串
     */
    @Test
    void testBasicNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试 enums 属性指定枚举时字段为 null 且自定义字段名输出空字符串
     */
    @Test
    void testBasicNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, fieldName = "userTypeTitle")
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeTitle":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeTitle\":\"\"}", value);
    }

    /**
     * 测试 enums 属性指定枚举时字段为 null 且 replace 输出空字符串
     */
    @Test
    void testBasicNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = PeopleType.class, fieldName = "userTypeTitle", replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":""}
        JsonAssertUtil.assertEquals("{\"userType\":\"\"}", value);
    }

    /**
     * 测试枚举字符串以逗号分割的数组字典转换
     */
    @Test
    void testString1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(split = ",")
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":"0,1","userTypeText":"系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试枚举字符串以竖线分割的数组字典转换
     */
    @Test
    void testString2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(split = "\\|")
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean("0|1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0|1)
        System.out.println(value); // {"userType":"0|1","userTypeText":"系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":\"0|1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试枚举字符串数组转换输出文本数组
     */
    @Test
    void testString3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",")
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":"0,1","userTypeText":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举字符串数组转换并替换原字段值为文本数组
     */
    @Test
    void testString4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",")
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean("0,1");
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=0,1)
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举字符串为 null 时数组字典转换输出空字符串
     */
    @Test
    void testStringNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举字符串为 null 且带分割符时输出空字符串
     */
    @Test
    void testStringNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(split = "\\|")
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举字符串为 null 时输出空文本数组
     */
    @Test
    void testStringNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    /**
     * 测试枚举字符串为 null 时替换输出空数组
     */
    @Test
    void testStringNull4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private String userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        JsonAssertUtil.assertEquals("{\"userType\":[]}", value);
    }

    /**
     * 测试枚举 List 数组字典转换输出拼接文本
     */
    @Test
    void testArray1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["0","1"])
        System.out.println(value); // {"userType":["0","1"],"userTypeText":"系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[\"0\",\"1\"],\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试枚举 List 数组字典转换输出文本数组
     */
    @Test
    void testArray11() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["0","1"])
        System.out.println(value); // {"userType":["0","1"],"userTypeText":"系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举 List 数组含未匹配值时拼接文本输出
     */
    @Test
    void testArray2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":\"null、系统管理、普通用户\"}", value);
    }

    /**
     * 测试枚举 List 数组 EMPTY 策略时输出文本数组
     */
    @Test
    void testArray22() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.EMPTY)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=["-1","0","1"])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":"null、系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"\",\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举 List 数组字典转换输出文本数组
     */
    @Test
    void testArray3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举 List 数组分割 + NULL 策略时输出嵌套文本数组
     */
    @Test
    void testArray33() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",", nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["-1","0","1"],"userTypeText":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[null],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试枚举 List 数组替换原字段值为文本数组
     */
    @Test
    void testArray4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举 List 数组替换 + NULL 策略时输出嵌套文本数组
     */
    @Test
    void testArray44() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.NULL, split = ",")
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(Arrays.asList("-1", "0", "1"));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[-1, 0, 1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[[null],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试枚举 List 数组为 null 时输出空字符串
     */
    @Test
    void testArrayNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举 List 数组为 null 时输出空文本数组
     */
    @Test
    void testArrayNull11() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    /**
     * 测试枚举 List 数组为 null + NULL 策略时输出空字符串
     */
    @Test
    void testArrayNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举 List 数组为 null + IGNORE 策略时输出空字符串
     */
    @Test
    void testArrayNull22() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(nullStrategy = NullStrategy.IGNORE)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举 List 数组为 null 时输出空文本数组
     */
    @Test
    void testArrayNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    /**
     * 测试枚举 List 数组为 null 时替换输出空数组
     */
    @Test
    void testArrayNull4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private List<String> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        JsonAssertUtil.assertEquals("{\"userType\":[]}", value);
    }
}
