package com.houkunlin.dict;

import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.json.DictTypeKeyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author HouKunLin
 */
public class DictTypeKeyHandlerImpl implements DictTypeKeyHandler<Object> {
    private static final Logger logger = LoggerFactory.getLogger(DictTypeKeyHandlerImpl.class);

    public DictTypeKeyHandlerImpl() {
    }

    @Override
    public String getDictType(final Object bean, final String fieldName, final DictText dictText) {
        logger.info("对象 {} 字段 {} 注解 {}", bean, fieldName, dictText);
        return "PeopleType";
    }
}
