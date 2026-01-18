package com.houkunlin.dict.jackson;

import tools.jackson.core.Version;
import tools.jackson.databind.module.SimpleModule;

/**
 * 字典 Jackson 模块，用于注册字典值序列化器修饰器。
 * <p>
 * 该模块是 Jackson 的扩展模块，用于集成字典值序列化功能到 Jackson 的序列化流程中。
 * 通过注册 DictValueSerializerModifier，实现对带有 DictText 注解字段的自动序列化处理。
 *</p>
 *
 * @author HouKunLin
 * @since 1.7.0
 */
public class DictJacksonModule extends SimpleModule {
    /**
     * 构造方法
     * <p>
     * 创建一个新的字典 Jackson 模块，设置模块名称和版本信息。
     *</p>
     */
    public DictJacksonModule() {
        super("dictJacksonModule", new Version(1, 0, 0, null, "com.houkunlin.system.dict.starter.jackson", "dictJacksonModule"));
    }

    /**
     * 设置模块，注册字典值序列化器修饰器。
     * <p>
     * 在模块设置阶段，向 Jackson 注册 DictValueSerializerModifier，用于修改 Bean 属性的序列化器。
     *</p>
     *
     * @param context 模块设置上下文
     */
    @Override
    public void setupModule(SetupContext context) {
        context.addSerializerModifier(new DictValueSerializerModifier());
    }
}
