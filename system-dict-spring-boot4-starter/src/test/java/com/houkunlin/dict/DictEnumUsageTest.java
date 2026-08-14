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
 * 枚举数组形式字典转换测试
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class DictEnumUsageTest {
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
     * 测试枚举类型默认字典转换
     */
    @Test
    void testBasic1() throws JacksonException {
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
        JsonAssertUtil.assertEquals("{\"userType\":0,\"userTypeText\":\"系统管理\"}", value);
    }

    /**
     * 测试枚举类型自定义字段名输出字典文本
     */
    @Test
    void testBasic2() throws JacksonException {
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
        JsonAssertUtil.assertEquals("{\"userType\":0,\"userTypeTitle\":\"系统管理\"}", value);
    }

    /**
     * 测试枚举类型 replace 替换输出字典文本
     */
    @Test
    void testBasic3() throws JacksonException {
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
        JsonAssertUtil.assertEquals("{\"userType\":\"系统管理\"}", value);
    }

    /**
     * 测试枚举为 null 时输出空字符串
     */
    @Test
    void testBasicNull1() throws JacksonException {
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举为 null 且自定义字段名时输出空字符串
     */
    @Test
    void testBasicNull2() throws JacksonException {
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
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeTitle\":\"\"}", value);
    }

    /**
     * 测试枚举为 null 且 replace 时输出空字符串
     */
    @Test
    void testBasicNull3() throws JacksonException {
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
        JsonAssertUtil.assertEquals("{\"userType\":\"\"}", value);
    }

    /**
     * 测试枚举数组字典转换输出拼接文本
     */
    @Test
    void testArray1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[0,1])
        System.out.println(value); // {"userType":[0,1],"userTypeText":"系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[0,1],\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试枚举数组字典转换输出文本数组
     */
    @Test
    void testArray11() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[0,1])
        System.out.println(value); // {"userType":[0,1],"userTypeText":"系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[0,1],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举数组含 null 值时拼接文本输出
     */
    @Test
    void testArray2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":[null,0,1],"userTypeText":"null、系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[null,0,1],\"userTypeText\":\"null、系统管理、普通用户\"}", value);
    }

    /**
     * 测试枚举数组 NULL 策略时输出文本数组
     */
    @Test
    void testArray22() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":[null,0,1],"userTypeText":"null、系统管理、普通用户"}
        JsonAssertUtil.assertEquals("{\"userType\":[null,0,1],\"userTypeText\":[null,\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举数组输出文本数组（忽略 null 值）
     */
    @Test
    void testArray3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":[null,0,1],"userTypeText":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[null,0,1],\"userTypeText\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举数组 NULL 策略时输出文本数组（保留 null）
     */
    @Test
    void testArray33() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":[null,0,1],"userTypeText":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[null,0,1],\"userTypeText\":[null,\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举数组拼接文本输出（toText=true 忽略 null）
     */
    @Test
    void testArray333() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = true)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":[null,0,1],"userTypeText":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[null,0,1],\"userTypeText\":\"系统管理、普通用户\"}", value);
    }

    /**
     * 测试枚举数组 replace + NULL 策略时输出文本数组
     */
    @Test
    void testArray44() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[null,\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举数组 replace 输出文本数组
     */
    @Test
    void testArray4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(Arrays.asList(null, PeopleType.ADMIN, PeopleType.USER));
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=[null,0,1])
        System.out.println(value); // {"userType":["系统管理","普通用户"]}
        JsonAssertUtil.assertEquals("{\"userType\":[\"系统管理\",\"普通用户\"]}", value);
    }

    /**
     * 测试枚举数组为 null 时输出空字符串
     */
    @Test
    void testArrayNull1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举数组为 null 时输出空文本数组
     */
    @Test
    void testArrayNull11() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    /**
     * 测试枚举数组为 null + NULL 策略时输出空字符串
     */
    @Test
    void testArrayNull2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(nullStrategy = NullStrategy.NULL)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":""}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举数组为 null 时输出空文本数组
     */
    @Test
    void testArrayNull3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":[]}", value);
    }

    /**
     * 测试枚举数组为 null 时输出空字符串（默认策略）
     */
    @Test
    void testArrayNull31() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray
            @DictText(enums = PeopleType.class)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":null,"userTypeText":[]}
        JsonAssertUtil.assertEquals("{\"userType\":null,\"userTypeText\":\"\"}", value);
    }

    /**
     * 测试枚举数组为 null 时 replace 输出空数组
     */
    @Test
    void testArrayNull4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        JsonAssertUtil.assertEquals("{\"userType\":[]}", value);
    }

    /**
     * 测试枚举数组为 null 时 replace + toText 输出空字符串
     */
    @Test
    void testArrayNull41() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = true)
            @DictText(enums = PeopleType.class, replace = DictBoolType.YES)
            private List<PeopleType> userType;
        }
        final Bean bean = new Bean(null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean); // Bean(userType=null)
        System.out.println(value); // {"userType":[]}
        JsonAssertUtil.assertEquals("{\"userType\":\"\"}", value);
    }
}
