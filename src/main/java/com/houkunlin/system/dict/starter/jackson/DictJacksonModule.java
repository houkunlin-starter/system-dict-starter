package com.houkunlin.system.dict.starter.jackson;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * DictJacksonModule
 *
 * @author HouKunLin
 * @since 1.6.3
 */
public class DictJacksonModule extends SimpleModule {
    public DictJacksonModule() {
        super("com.houkunlin.dict.jackson.DictJacksonModule", new Version(1, 0, 0, null, "com.houkunlin", "system-dict-starter"));
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        context.addBeanSerializerModifier(new DictBeanSerializerModifier());
    }
}
