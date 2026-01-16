package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictType;
import org.springframework.test.annotation.DirtiesContext;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.bean.DictValue;
import com.houkunlin.system.dict.starter.common.bean.PeopleType;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Objects;
import java.util.Optional;

/**
 * 刷新单个字典值 和 刷新单个字典类型对象 的 测试代码，需要关闭缓存，关闭异步执行功能才能得到正确的测试结果。
 * （因为是串行执行，后面的步骤严重依赖前面的执行完整性，因此需要关闭异步执行才能测出结果）
 * 此单元测试还需要把 system.dict.refresh-dict-interval 配置设为 0 ，因为两次单测之间要恢复所有完整的数据。
 *
 * @author HouKunLin
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@SystemDictScan
class RefreshTest {
    private static final Logger logger = LoggerFactory.getLogger(RefreshTest.class);
    @Autowired
    private static ApplicationEventPublisher publisher;
    @Autowired
    private ObjectMapper objectMapper;
    private final String dictType = PeopleType.class.getSimpleName();

    @Autowired
    public void setPublisher(final ApplicationEventPublisher publisher) {
        RefreshTest.publisher = publisher;
    }

    @BeforeEach
    void beforeAll() {
        publisher.publishEvent(new RefreshDictEvent("刷新完整的"));
    }

    private String toJson(Object o) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
        } catch (JacksonException e) {
            return "";
        }
    }

    @Test
    void testRefreshDictValue() {
        System.out.println(toJson(DictUtil.getDictType(this.dictType)));

        long start = System.nanoTime();
        publisher.publishEvent(new RefreshDictValueEvent(DictValue.builder().dictType(this.dictType).value(2).title("修改后的名称").build()));
        logger.info("修改了一个值 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictType dictType = DictUtil.getDictType(this.dictType);
        System.out.println(toJson(dictType));

        final Optional<DictValue> valueVo = dictType.getChildren().stream().filter(vo -> Objects.equals(vo.getValue(), 2)).findFirst();
        Assertions.assertTrue(valueVo.isPresent());
        // 无法通过事件修改系统类型的字典文本，应当直接修改枚举对象重新编译程序
        Assertions.assertEquals("其他用户", valueVo.get().getTitle());
        Assertions.assertEquals("其他用户", DictUtil.getDictText(this.dictType, "2"));
    }

    @Test
    void testRefreshDictValueRemove() {
        System.out.println(toJson(DictUtil.getDictType(this.dictType)));

        long start = System.nanoTime();
        publisher.publishEvent(new RefreshDictValueEvent(DictValue.builder().dictType(this.dictType).value(2).title(null).build()));
        logger.info("修改了一个值 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictType dictType = DictUtil.getDictType(this.dictType);
        System.out.println(toJson(dictType));

        final Optional<DictValue> valueVo = dictType.getChildren().stream().filter(vo -> Objects.equals(vo.getValue(), 2)).findFirst();
        // 不允许通过事件的方式变更系统字典数据
        Assertions.assertTrue(valueVo.isPresent());
        Assertions.assertEquals(3, dictType.getChildren().size());
        Assertions.assertNotNull(DictUtil.getDictText(this.dictType, "2"));
    }

    @Test
    void testRefreshDictType() {
        System.out.println(toJson(DictUtil.getDictType(this.dictType)));

        long start = System.nanoTime();
        final DictType newDictType = DictType.newBuilder(this.dictType, "")
            .add(1, "新的值1")
            .add(2, "新的值2").build();
        // 不允许通过事件刷新覆盖系统枚举字典内容
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType));
        logger.info("更新了一个完整的对象 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictType dictType = DictUtil.getDictType(this.dictType);
        System.out.println(toJson(dictType));

        // 不允许通过事件刷新覆盖系统枚举字典内容
        Assertions.assertEquals(3, dictType.getChildren().size());
        Assertions.assertEquals("系统管理", dictType.getChildren().get(0).getTitle());
        Assertions.assertEquals("普通用户", dictType.getChildren().get(1).getTitle());
        Assertions.assertEquals("其他用户", dictType.getChildren().get(2).getTitle());
        Assertions.assertEquals("系统管理", DictUtil.getDictText(this.dictType, "0"));
        Assertions.assertEquals("普通用户", DictUtil.getDictText(this.dictType, "1"));
        Assertions.assertEquals("其他用户", DictUtil.getDictText(this.dictType, "2"));

        // 但是可以刷新覆盖自定义的字典数据
        final DictType newDictType1 = DictType.newBuilder("testRefreshDictType", "")
            .add(1, "新的值1")
            .add(2, "新的值2").build();
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType1));
        final DictType dictType1 = DictUtil.getDictType("testRefreshDictType");
        Assertions.assertEquals(2, dictType1.getChildren().size());
        Assertions.assertEquals("新的值1", dictType1.getChildren().get(0).getTitle());
        Assertions.assertEquals("新的值2", dictType1.getChildren().get(1).getTitle());
        Assertions.assertEquals("新的值1", DictUtil.getDictText("testRefreshDictType", "1"));
        Assertions.assertEquals("新的值2", DictUtil.getDictText("testRefreshDictType", "2"));
    }

    @Test
    void testRefreshDictTypeRemove() {
        System.out.println(toJson(DictUtil.getDictType(this.dictType)));

        long start = System.nanoTime();
        final DictType newDictType = new DictType("", this.dictType, "", null);
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType));
        logger.info("删除一个对象 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictType dictType = DictUtil.getDictType(this.dictType);
        System.out.println(toJson(dictType));

        // 不允许通过事件刷新覆盖系统枚举字典内容
        Assertions.assertNotNull(dictType);

        // 但是可以刷新覆盖自定义的字典数据
        final DictType newDictType1 = DictType.newBuilder("testRefreshDictType", "")
            .add(1, "新的值1")
            .add(2, "新的值2").build();
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType1));
        final DictType dictType1 = DictUtil.getDictType("testRefreshDictType");
        Assertions.assertNotNull(dictType1);
        Assertions.assertEquals(2, dictType1.getChildren().size());
        Assertions.assertEquals("新的值1", dictType1.getChildren().get(0).getTitle());
        Assertions.assertEquals("新的值2", dictType1.getChildren().get(1).getTitle());
        Assertions.assertEquals("新的值1", DictUtil.getDictText("testRefreshDictType", "1"));
        Assertions.assertEquals("新的值2", DictUtil.getDictText("testRefreshDictType", "2"));

        final DictType newDictType2 = new DictType("", "testRefreshDictType", "", null);
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType2));
        Assertions.assertNull(DictUtil.getDictType("testRefreshDictType"));
    }
}
