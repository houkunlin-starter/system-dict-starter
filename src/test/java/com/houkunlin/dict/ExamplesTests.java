package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictArray;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.common.bean.User;
import com.houkunlin.dict.common.bean.User1;
import com.houkunlin.dict.common.bean.UserType;
import com.houkunlin.dict.notice.RefreshDictEvent;
import com.houkunlin.dict.notice.RefreshDictTypeEvent;
import com.houkunlin.dict.notice.RefreshDictValueEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 把元使用示例的代码移到单元测试中
 *
 * @author HouKunLin
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@SystemDictScan
class ExamplesTests {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ApplicationEventPublisher publisher;
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testBasic() throws Exception {
        final String expect = "{\"id\":1," +
            "\"gender\":0,\"genderText\":\"男\"," +
            "\"sex\":1,\"sexName\":\"女\"," +
            "\"type\":0,\"typeText\":\"系统管理员\"," +
            "\"typeArrays0\":\"0,1\",\"typeArrays0Text\":\"系统管理员、普通用户\"," +
            "\"typeArrays1\":\"0,1\",\"typeArrays1Text\":[\"系统管理员\",\"普通用户\"]," +
            "\"typeArrays2\":[\"系统管理员\",\"普通用户\"]," +
            "\"typeEnum0\":\"0\",\"typeEnum0Text\":\"系统管理员\"," +
            "\"typeEnum1\":\"系统管理员\"," +
            "\"name\":\"用户姓名\"}";
        final User user = User.builder().id(1)
            .gender(0)
            .sex(1)
            .type(0)
            .typeArrays0("0,1")
            .typeArrays1("0,1")
            .typeArrays2("0,1")
            .typeEnum0("0")
            .typeEnum1("0")
            .name("用户姓名").build();
        final String userJson = objectMapper.writeValueAsString(user);
        assertEquals(expect, userJson);
        mockMvc.perform(get("/test/user-get?id=1&gender=0&sex=1&type=0&typeArrays0=0,1&typeArrays1=0,1&typeArrays2=0,1&typeEnum0=0&typeEnum1=0&name=用户姓名"))
            .andDo(log())
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(content().string(expect));
        // mockMvc.perform(post("/test/user-post").characterEncoding(StandardCharsets.UTF_8).contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(expect))
        //     .andDo(log())
        //     .andExpect(status().isOk())
        //     .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
        //     .andExpect(content().string(expect));
    }

    @Test
    void testDbTree() throws Exception {
        final Bean bean = Bean.builder()
            .treeData1("2")
            .treeData2("2-2")
            .city1("110101")
            .city2("130102")
            .city3("130203,130204,130205")
            .build();
        String json = objectMapper.writeValueAsString(bean);
        assertEquals("{\"treeData1\":\"2\",\"treeData1Text\":\"节点2\",\"treeData2\":\"2-2\",\"treeData2Text\":\"节点2-2\",\"city1\":\"110101\",\"city1Text\":\"东城区\",\"city2\":\"130102\",\"city2Text\":\"长安区\",\"city3\":\"130203,130204,130205\",\"city3Text\":[\"路北区\",\"古冶区\",\"开平区\"]}", json);
    }

    @Test
    void testProvider() throws Exception {
        final String expect = "\"hobby\":\"1\",\"hobbyText\":\"打篮球\",\"nation\":\"2\",\"nationText\":\"回族\",\"createdBy\":\"12\",\"createdByText\":\"用户姓名 - 12\"";
        final User1 user = User1.builderUser1()
            .hobby("1")
            .createdBy("12")
            .nation("2")
            .build();
        final String userJson = objectMapper.writeValueAsString(user);
        assertTrue(userJson.contains(expect));
    }

    @Test
    void testRefreshDict() throws Exception {
        final String dictType = UserType.class.getSimpleName();
        DictType dictType1 = DictUtil.getDictType(dictType);
        assertEquals(2, dictType1.getChildren().size());
        assertEquals(0, dictType1.getChildren().get(0).getValue());
        assertEquals(1, dictType1.getChildren().get(1).getValue());
        assertEquals("系统管理员", dictType1.getChildren().get(0).getTitle());
        assertEquals("普通用户", dictType1.getChildren().get(1).getTitle());
        assertEquals("普通用户", DictUtil.getDictText(dictType, "1"));

        // 枚举字典不允许通过刷新变更数据
        publisher.publishEvent(new RefreshDictValueEvent(DictValue.builder()
            .dictType(dictType)
            .value(1)
            .title("刷新单个字典值")
            .build()));
        dictType1 = DictUtil.getDictType(dictType);
        assertEquals(2, dictType1.getChildren().size());
        assertEquals(0, dictType1.getChildren().get(0).getValue());
        assertEquals(1, dictType1.getChildren().get(1).getValue());
        assertEquals("系统管理员", dictType1.getChildren().get(0).getTitle());
        assertEquals("普通用户", dictType1.getChildren().get(1).getTitle());
        assertEquals("普通用户", DictUtil.getDictText(dictType, "1"));

        // 枚举字典不允许通过刷新变更数据
        publisher.publishEvent(new RefreshDictTypeEvent(DictType.newBuilder(dictType, "刷新一个完整的字典类型")
            .add(0, "刷新一个完整的字典类型0")
            .add(1, "刷新一个完整的字典类型1")
            .add(2, "刷新一个完整的字典类型2")
            .build()));
        dictType1 = DictUtil.getDictType(dictType);
        assertEquals(2, dictType1.getChildren().size());
        assertEquals(0, dictType1.getChildren().get(0).getValue());
        assertEquals(1, dictType1.getChildren().get(1).getValue());
        assertEquals("系统管理员", dictType1.getChildren().get(0).getTitle());
        assertEquals("普通用户", dictType1.getChildren().get(1).getTitle());
        assertEquals("普通用户", DictUtil.getDictText(dictType, "1"));

        DictType dictType4 = DictUtil.getDictType("dictNation");
        assertEquals(6, dictType4.getChildren().size());
        assertEquals(1, dictType4.getChildren().get(0).getValue());
        assertEquals(2, dictType4.getChildren().get(1).getValue());
        assertEquals(3, dictType4.getChildren().get(2).getValue());
        assertEquals(4, dictType4.getChildren().get(3).getValue());
        assertEquals(5, dictType4.getChildren().get(4).getValue());
        assertEquals(6, dictType4.getChildren().get(5).getValue());
        assertEquals("汉族", dictType4.getChildren().get(0).getTitle());
        assertEquals("回族", dictType4.getChildren().get(1).getTitle());
        assertEquals("瑶族", dictType4.getChildren().get(2).getTitle());
        assertEquals("壮族", dictType4.getChildren().get(3).getTitle());
        assertEquals("藏族", dictType4.getChildren().get(4).getTitle());
        assertEquals("蒙古族", dictType4.getChildren().get(5).getTitle());
        assertEquals("汉族", DictUtil.getDictText("dictNation", "1"));

        // 非枚举字典可以通过刷新变更字典数据
        publisher.publishEvent(new RefreshDictValueEvent(DictValue.builder()
            .dictType("dictNation")
            .value(1)
            .title("刷新单个字典值")
            .build()));
        dictType4 = DictUtil.getDictType("dictNation");
        assertEquals(6, dictType4.getChildren().size());
        assertEquals(1, dictType4.getChildren().get(0).getValue());
        assertEquals(2, dictType4.getChildren().get(1).getValue());
        assertEquals(3, dictType4.getChildren().get(2).getValue());
        assertEquals(4, dictType4.getChildren().get(3).getValue());
        assertEquals(5, dictType4.getChildren().get(4).getValue());
        assertEquals(6, dictType4.getChildren().get(5).getValue());
        assertEquals("刷新单个字典值", dictType4.getChildren().get(0).getTitle());
        assertEquals("回族", dictType4.getChildren().get(1).getTitle());
        assertEquals("瑶族", dictType4.getChildren().get(2).getTitle());
        assertEquals("壮族", dictType4.getChildren().get(3).getTitle());
        assertEquals("藏族", dictType4.getChildren().get(4).getTitle());
        assertEquals("蒙古族", dictType4.getChildren().get(5).getTitle());
        assertEquals("刷新单个字典值", DictUtil.getDictText("dictNation", "1"));

        publisher.publishEvent(new RefreshDictTypeEvent(DictType.newBuilder("dictNation", "民族")
            .add(1, "汉族1")
            .add(2, "回族1")
            .add(3, "瑶族1")
            .add(4, "壮族1")
            .add(5, "藏族1")
            .add(6, "蒙古族1")
            .build()));
        dictType4 = DictUtil.getDictType("dictNation");
        assertEquals(6, dictType4.getChildren().size());
        assertEquals(1, dictType4.getChildren().get(0).getValue());
        assertEquals(2, dictType4.getChildren().get(1).getValue());
        assertEquals(3, dictType4.getChildren().get(2).getValue());
        assertEquals(4, dictType4.getChildren().get(3).getValue());
        assertEquals(5, dictType4.getChildren().get(4).getValue());
        assertEquals(6, dictType4.getChildren().get(5).getValue());
        assertEquals("汉族1", dictType4.getChildren().get(0).getTitle());
        assertEquals("回族1", dictType4.getChildren().get(1).getTitle());
        assertEquals("瑶族1", dictType4.getChildren().get(2).getTitle());
        assertEquals("壮族1", dictType4.getChildren().get(3).getTitle());
        assertEquals("藏族1", dictType4.getChildren().get(4).getTitle());
        assertEquals("蒙古族1", dictType4.getChildren().get(5).getTitle());
        assertEquals("汉族1", DictUtil.getDictText("dictNation", "1"));

        publisher.publishEvent(new RefreshDictEvent("从系统中重新读取字典信息，所有系统字典信息将被还原成系统启动时的最初版本", true, true));
        dictType4 = DictUtil.getDictType("dictNation");
        assertEquals(6, dictType4.getChildren().size());
        assertEquals(1, dictType4.getChildren().get(0).getValue());
        assertEquals(2, dictType4.getChildren().get(1).getValue());
        assertEquals(3, dictType4.getChildren().get(2).getValue());
        assertEquals(4, dictType4.getChildren().get(3).getValue());
        assertEquals(5, dictType4.getChildren().get(4).getValue());
        assertEquals(6, dictType4.getChildren().get(5).getValue());
        assertEquals("汉族", dictType4.getChildren().get(0).getTitle());
        assertEquals("回族", dictType4.getChildren().get(1).getTitle());
        assertEquals("瑶族", dictType4.getChildren().get(2).getTitle());
        assertEquals("壮族", dictType4.getChildren().get(3).getTitle());
        assertEquals("藏族", dictType4.getChildren().get(4).getTitle());
        assertEquals("蒙古族", dictType4.getChildren().get(5).getTitle());
        assertEquals("汉族", DictUtil.getDictText("dictNation", "1"));
    }

    @Data
    @Builder
    @AllArgsConstructor
    static class Bean {
        @DictTree
        @DictText(value = "TreeData")
        private String treeData1;
        @DictTree
        @DictText(value = "TreeData")
        private String treeData2;
        @DictTree
        @DictText(value = "City")
        private String city1;
        @DictTree
        @DictText(value = "City")
        private String city2;
        @DictTree
        @DictArray(toText = false)
        @DictText(value = "City")
        private String city3;
    }
}
