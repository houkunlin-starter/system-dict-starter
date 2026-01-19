package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.notice.RefreshDictTypeEvent;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.annotation.DirtiesContext;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 注解使用测试
 *
 * @author HouKunLin
 * @since 2.0.0
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class FieldMapTypeTest {
    public static final String DICT_TYPE = "PeopleType";
    public static final String DICT_TYPE2 = "TreeData2";
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    public void setPublisher(final ApplicationEventPublisher publisher) {
        final DictType typeVo = DictType.newBuilder(DICT_TYPE2, "树形结构数据测试")
            .add("", "1", "节点1")
            .add("", "2", "节点2")
            .add("", "3", "节点3")
            .add("1", "1-1", "节点1-1")
            .add("1", "1-2", "节点1-2")
            .add("1", "1-3", "节点1-3")
            .add("2", "2-1", "节点2-1")
            .add("2", "2-2", "节点2-2")
            .add("2", "2-3", "节点2-3")
            .add("3", "3-1", "节点3-1")
            .add("3", "3-2", "节点3-2")
            .add("3", "3-3", "节点3-3")
            .build();
        publisher.publishEvent(new RefreshDictTypeEvent(typeVo));
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(value = DICT_TYPE)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("0", "测试0");
        map.put("1", "测试1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"0":"测试0","1":"测试1"},"userTypeText":{"0":"系统管理","1":"普通用户"}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType2() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = true)
            @DictText(value = DICT_TYPE)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("0", "测试0");
        map.put("1", "测试1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"0":"测试0","1":"测试1"},"userTypeText":{"0":"系统管理","1":"普通用户"}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType3() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("0", "测试0");
        map.put("1", "测试1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"0":"测试0","1":"测试1"},"userTypeText":{"0":["系统管理"],"1":["普通用户"]}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType4() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = true, split = ",")
            @DictText(value = DICT_TYPE)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("0", "测试0");
        map.put("0,1", "测试1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"0":"测试0","0,1":"测试1"},"userTypeText":{"0":"系统管理","0,1":"系统管理、普通用户"}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType5() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",")
            @DictText(value = DICT_TYPE)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("0", "测试0");
        map.put("0,1", "测试0和测试1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"0":"测试0","0,1":"测试0和测试1"},"userTypeText":{"0":["系统管理"],"0,1":["系统管理","普通用户"]}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType6() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictTree(toText = false)
            @DictText(value = DICT_TYPE2)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "测试1");
        map.put("1-1", "测试1-1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"1":"测试1","1-1":"测试1-1"},"userTypeText":{"1":["节点1"],"1-1":["节点1","节点1-1"]}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType7() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictTree(toText = true)
            @DictText(value = DICT_TYPE2)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "测试1");
        map.put("1-1", "测试1-1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"1":"测试1","1-1":"测试1-1"},"userTypeText":{"1":"节点1","1-1":"节点1/节点1-1"}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType8() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = true, split = ",")
            @DictTree(toText = false)
            @DictText(value = DICT_TYPE2)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "测试1");
        map.put("1-1,2", "测试1-1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"1":"测试1","1-1,2":"测试1-1"},"userTypeText":{"1":"节点1","1-1,2":"节点1/节点1-1、节点2"}}""", value);
    }

    /**
     * 基础测试
     *
     * @throws JacksonException 序列化异常
     * @since 2.0.0
     */
    @Test
    void testFieldMapType9() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictArray(toText = false, split = ",")
            @DictTree(toText = true)
            @DictText(value = DICT_TYPE2)
            private Map<String, String> userType;
        }
        Map<String, String> map = new LinkedHashMap<>();
        map.put("1", "测试1");
        map.put("1-1,2", "测试1-1");
        final Bean bean = new Bean(map);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("""
            {"userType":{"1":"测试1","1-1,2":"测试1-1"},"userTypeText":{"1":["节点1"],"1-1,2":["节点1/节点1-1","节点2"]}}""", value);
    }
}
