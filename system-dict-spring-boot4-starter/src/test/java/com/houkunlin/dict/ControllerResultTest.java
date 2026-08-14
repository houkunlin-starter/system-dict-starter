package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;

/**
 * Controller 返回结果包装类序列化测试
 *
 * @author HouKunLin
 * @since 1.4.6
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class ControllerResultTest {
    public static final String DICT_TYPE = "TreeData";
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
     * 测试 Controller 返回结果（R 包装类）中的字典字段序列化
     *
     * @throws JacksonException 序列化异常
     * @since 1.4.6
     */
    @Test
    void testBasic1() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictTree
            @DictText(value = DICT_TYPE)
            private String userType;
            @DictTree
            @DictText(value = DICT_TYPE)
            private String userType1;
            @DictTree
            @DictArray(toText = false)
            @DictText(value = DICT_TYPE)
            private String userType3;
        }
        final Bean bean = new Bean("1", "3-3", "1-1,1-2,1-3,2-1,2-2,2-3,3-1,3-2,3-3,3-4");
        final String value = objectMapper.writeValueAsString(R.success(Collections.singleton(bean)));
        System.out.println(bean);
        System.out.println(value);
    }

    @Data
    @AllArgsConstructor
    static class R {
        private boolean success;
        private String code;
        private String message;
        private Object data;

        public static R success() {
            return new R(true, "OK", "OK", null);
        }

        public static R success(Object data) {
            return new R(true, "OK", "OK", data);
        }
    }
}
