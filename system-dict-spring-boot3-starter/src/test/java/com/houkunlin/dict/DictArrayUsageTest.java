package com.houkunlin.dict;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

import java.util.Arrays;
import java.util.List;

/**
 * 数组形式字典转换测试
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

    /**
     * 测试当前 Starter 模块路径是否匹配
     */
    @Test
    void testJavaAtPath() {
        TestStarterAssertions.assertCurrentStarterModule("3");
    }

    /**
     * 测试字符串以逗号分割的数组字典转换
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试字符串以竖线分割的数组字典转换
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":\"0|1\",\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试字符串数组转换输出文本数组（toText=false）
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":\"0,1\",\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试字符串数组转换并替换原字段值为文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试字符串为 null 时数组字典转换输出空字符串
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试无 @DictArray 注解时字符串为 null 的输出
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试 nullable=YES 时字符串为 null 的输出
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":null}", value);
    }

    /**
     * 测试字符串为 null 且带分割符时输出空字符串
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试字符串为 null 时输出空文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    /**
     * 测试字符串为 null 时替换原字段值输出空数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[]}", value);
    }

    /**
     * 测试 List 数组字典转换输出拼接文本
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"0\",\"1\"],\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试 List 数组字典转换输出文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 List 数组含未匹配值时拼接文本输出
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":\"null、系统管理、普通用户\"}", value);
    }

    /**
     * 测试 List 数组含未匹配值时输出文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 nullStrategy=IGNORE 时忽略未匹配值
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 nullStrategy=NULL 时保留 null 值
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[null,\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 nullStrategy=EMPTY 时输出空字符串
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"\",\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 List 数组字典转换输出文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 List 数组带分割符时输出嵌套文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试 List 数组带分割符 + IGNORE 时输出嵌套文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试 List 数组带分割符 + NULL 时输出嵌套文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[null],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试 List 数组带分割符 + EMPTY 时输出嵌套文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"-1\",\"0\",\"1\"],\"userTypeText\":[[\"\"],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试 List 数组替换原字段值为文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 List 数组替换 + IGNORE 时输出文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 List 数组替换 + NULL 时输出文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[null,\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 List 数组替换 + EMPTY 时输出文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[\"\",\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试 List 数组替换 + 分割符 + IGNORE 时输出嵌套文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[[],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试 List 数组替换 + 分割符 + NULL 时输出嵌套文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[[null],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试 List 数组替换 + 分割符 + EMPTY 时输出嵌套文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[[\"\"],[\"系统管理\"],[\"普通用户\"]]}", value);
    }

    /**
     * 测试 List 数组为 null 时输出空字符串
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试 List 数组为 null + NULL 策略时输出空字符串
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试 List 数组为 null 时输出空文本数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    /**
     * 测试 List 数组为 null 时替换输出空数组
     */
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
        JsonAssertUtil.assertEquals("{\"userType\":[]}", value);
    }
}
