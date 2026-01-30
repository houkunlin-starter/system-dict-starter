package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.common.bean.ACLStatusEnum;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hamcrest.core.StringContains;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 枚举扩展数据测试
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@AutoConfigureMockMvc
@SystemDictScan
class DictExtendDataMapTests {
    public static final String DICT_TYPE = "ACLStatusEnum";
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testJackson() throws JacksonException {
        @Data
        @AllArgsConstructor
        class Bean {
            @DictText(enums = ACLStatusEnum.class)
            private String userType;
            private String userType1;
        }
        final Bean bean = new Bean("1", null);
        final String value = objectMapper.writeValueAsString(bean);
        System.out.println(bean);
        System.out.println(value);
        Assertions.assertEquals("{\"userType\":\"1\",\"userTypeText\":\"不可读写\",\"userType1\":null}", value);

        DictTypeVo dictType = DictUtil.getDictType(DICT_TYPE);
        System.out.println(dictType);
        Assertions.assertNotNull(dictType);
        Assertions.assertNotNull(dictType.getChildren());
        Assertions.assertEquals(3, dictType.getChildren().size());
        Assertions.assertNotNull(dictType.getChildren().get(0).getData());
        Assertions.assertTrue(dictType.getChildren().get(0).getData().containsKey("read"));
        Assertions.assertTrue(dictType.getChildren().get(0).getData().containsKey("write"));
    }

    @Test
    void testWeb() throws Exception {
        mockMvc.perform(get("/dict/" + DICT_TYPE))
            .andDo(log())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(content().string(objectMapper.writeValueAsString(DictUtil.getDictType(DICT_TYPE))))
            .andExpect(content().string(StringContains.containsString("\"read\":true")))
            .andExpect(content().string(StringContains.containsString("\"read\":false")))
            .andExpect(content().string(StringContains.containsString("\"write\":true")))
            .andExpect(content().string(StringContains.containsString("\"write\":false")))
        ;

    }

}
