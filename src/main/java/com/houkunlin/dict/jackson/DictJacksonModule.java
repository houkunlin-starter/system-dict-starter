package com.houkunlin.dict.jackson;

import tools.jackson.core.Version;
import tools.jackson.databind.module.SimpleModule;

/**
 * DictJacksonModule
 *
 * @author HouKunLin
 * @since 1.6.3
 */
public class DictJacksonModule extends SimpleModule {
    public DictJacksonModule() {
        super("dictJacksonModule", new Version(1, 0, 0, null, "com.houkunlin.system.dict.starter.jackson", "dictJacksonModule"));
    }

    @Override
    public void setupModule(SetupContext context) {
        context.addSerializerModifier(new DictBeanSerializerModifier());
    }
}
