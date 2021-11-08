package com.houkunlin.system.dict.starter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
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
import test.application.common.bean.PeopleType;

import java.util.Objects;
import java.util.Optional;

/**
 * 刷新单个字典值 和 刷新单个字典类型对象 的 测试代码，需要关闭缓存，关闭异步执行功能才能得到正确的测试结果。
 * （因为是串行执行，后面的步骤严重依赖前面的执行完整性，因此需要关闭异步执行才能测出结果）
 * 此单元测试还需要把 system.dict.refresh-dict-interval 配置设为 0 ，因为两次单测之间要恢复所有完整的数据。
 *
 * @author HouKunLin
 */
@SpringBootTest
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
        } catch (JsonProcessingException e) {
            return "";
        }
    }

    @Test
    void testRefreshDictValue() {
        System.out.println(toJson(DictUtil.getDictType(dictType)));

        long start = System.nanoTime();
        publisher.publishEvent(new RefreshDictValueEvent(DictValueVo.builder().dictType(dictType).value(2).title("修改后的名称").build()));
        logger.info("修改了一个值 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictTypeVo dictTypeVo = DictUtil.getDictType(dictType);
        System.out.println(toJson(dictTypeVo));

        final Optional<DictValueVo> valueVo = dictTypeVo.getChildren().stream().filter(vo -> Objects.equals(vo.getValue(), 2)).findFirst();
        Assertions.assertTrue(valueVo.isPresent());
        Assertions.assertEquals("修改后的名称", valueVo.get().getTitle());
        Assertions.assertEquals("修改后的名称", DictUtil.getDictText(dictType, "2"));
    }

    @Test
    void testRefreshDictValueRemove() {
        System.out.println(toJson(DictUtil.getDictType(dictType)));

        long start = System.nanoTime();
        publisher.publishEvent(new RefreshDictValueEvent(DictValueVo.builder().dictType(dictType).value(2).title(null).build()));
        logger.info("修改了一个值 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictTypeVo dictTypeVo = DictUtil.getDictType(dictType);
        System.out.println(toJson(dictTypeVo));

        final Optional<DictValueVo> valueVo = dictTypeVo.getChildren().stream().filter(vo -> Objects.equals(vo.getValue(), 2)).findFirst();
        Assertions.assertFalse(valueVo.isPresent());
        Assertions.assertEquals(2, dictTypeVo.getChildren().size());
        Assertions.assertNull(DictUtil.getDictText(dictType, "2"));
    }

    @Test
    void testRefreshDictType() {
        System.out.println(toJson(DictUtil.getDictType(dictType)));

        long start = System.nanoTime();
        final DictTypeVo newDictType = DictTypeVo.newBuilder(dictType, "")
            .add(1, "新的值1")
            .add(2, "新的值2").build();
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType));
        logger.info("更新了一个完整的对象 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictTypeVo dictTypeVo = DictUtil.getDictType(dictType);
        System.out.println(toJson(dictTypeVo));

        Assertions.assertEquals(2, dictTypeVo.getChildren().size());
        Assertions.assertEquals("新的值1", dictTypeVo.getChildren().get(0).getTitle());
        Assertions.assertEquals("新的值2", dictTypeVo.getChildren().get(1).getTitle());
        Assertions.assertEquals("新的值1", DictUtil.getDictText(dictType, "1"));
        Assertions.assertEquals("新的值2", DictUtil.getDictText(dictType, "2"));
    }

    @Test
    void testRefreshDictTypeRemove() {
        System.out.println(toJson(DictUtil.getDictType(dictType)));

        long start = System.nanoTime();
        final DictTypeVo newDictType = new DictTypeVo("", dictType, "", null);
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType));
        logger.info("删除一个对象 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictTypeVo dictTypeVo = DictUtil.getDictType(dictType);
        System.out.println(toJson(dictTypeVo));

        Assertions.assertNull(dictTypeVo);
    }
}
