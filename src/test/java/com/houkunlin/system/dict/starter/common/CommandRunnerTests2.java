package com.houkunlin.system.dict.starter.common;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.common.bean.PeopleType;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 刷新单个字典值 和 刷新单个字典类型对象 的 测试代码，需要关闭缓存，关闭异步执行功能才能得到正确的测试结果。
 * （因为是串行执行，后面的步骤严重依赖前面的执行完整性，因此需要关闭异步执行才能测出结果）
 *
 * @author HouKunLin
 */
@Component
public class CommandRunnerTests2 implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CommandRunnerTests2.class);
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher publisher;

    public CommandRunnerTests2(final ObjectMapper objectMapper, final ApplicationEventPublisher publisher) {
        this.objectMapper = objectMapper;
        this.publisher = publisher;
    }

    @Override
    public void run(final String... args) throws Exception {
        long start = 0;
        System.out.println();
        System.out.println();
        System.out.println("========================[START] 测试刷新单个字典文本值 和 测试刷新完整字典文本值 ========================");
        final String dictType = PeopleType.class.getSimpleName();
        final DictTypeVo dictType1 = DictUtil.getDictType(dictType);
        final String raw1 = toJson(dictType1);
        logger.info("初始的完整类型：{}", dictType1);
        logger.info("初始的完整类型 Json：{}", raw1);

        start = System.nanoTime();
        publisher.publishEvent(new RefreshDictValueEvent(DictValueVo.builder().dictType(dictType).value(2).title("修改后的名称").build()));
        logger.info("修改了一个值 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictTypeVo dictType2 = DictUtil.getDictType(dictType);
        final String raw2 = toJson(dictType2);
        logger.info("修改了一个值后：{}", dictType2);
        logger.info("修改了一个值后 Json：{}", raw2);
        logger.info("修改了一个值后 Json Boolean：{}", Objects.equals(raw1, raw2));
        logger.info("修改了一个值后 确认更新成功：{}", Objects.equals("修改后的名称", DictUtil.getDictText(dictType, "2")));
        logger.info("修改了一个值后 确认更新成功：{} == {}", "修改后的名称", DictUtil.getDictText(dictType, "2"));

        start = System.nanoTime();
        final DictTypeVo newDictType = DictTypeVo.newBuilder(dictType, dictType1.getTitle(), dictType1.getRemark())
            .add(1, "新的值1")
            .add(2, "新的值2").build();
        publisher.publishEvent(new RefreshDictTypeEvent(newDictType));
        logger.info("更新了一个完整的对象 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictTypeVo dictType3 = DictUtil.getDictType(dictType);
        final String raw3 = toJson(dictType3);
        logger.info("更新了一个完整的对象后：{}", dictType3);
        logger.info("更新了一个完整的对象后 Json：{}", raw3);
        logger.info("更新了一个完整的对象后 确认更新成功 1：{}", Objects.equals("新的值1", DictUtil.getDictText(dictType, "1")));
        logger.info("更新了一个完整的对象后 确认更新成功 1：{} == {}", "新的值1", DictUtil.getDictText(dictType, "1"));
        logger.info("更新了一个完整的对象后 确认更新成功 2：{}", Objects.equals("新的值2", DictUtil.getDictText(dictType, "2")));
        logger.info("更新了一个完整的对象后 确认更新成功 2：{} == {}", "新的值2", DictUtil.getDictText(dictType, "2"));

        start = System.nanoTime();
        publisher.publishEvent(new RefreshDictEvent("CommandRunnerTests2 刷新完整的"));
        logger.info("全部重新刷新后 耗时 {} ms", (System.nanoTime() - start) / 1000_000.0);
        final DictTypeVo dictType4 = DictUtil.getDictType(dictType);
        final String raw4 = toJson(dictType4);
        logger.info("全部重新刷新后：{}", dictType4);
        logger.info("全部重新刷新后 Json：{}", raw4);
        logger.info("全部重新刷新后 恢复初始化（原始枚举值）：{}", Objects.equals(raw1, raw4));
        System.out.println("========================[END] 测试刷新单个字典文本值 和 测试刷新完整字典文本值 ========================");
        System.out.println();
        System.out.println();
    }

    private String toJson(Object o) throws JacksonException {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(o);
    }
}
