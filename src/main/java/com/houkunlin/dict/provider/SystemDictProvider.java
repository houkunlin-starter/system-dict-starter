package com.houkunlin.dict.provider;

import com.houkunlin.dict.SystemDictScan;
import com.houkunlin.dict.bean.DictType;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 系统字典提供者。{@link SystemDictScan} 注解扫描到的系统字典都会写入到该对象中。
 * 然后由指定的程序获取系统中所有 {@link DictProvider} 对象列表对所有字典进行注册
 *
 * @author HouKunLin
 */
@Getter
public class SystemDictProvider implements DictProvider {
    private static final Logger logger = LoggerFactory.getLogger(SystemDictProvider.class);
    private final Map<String, DictType> cache = new HashMap<>();

    /**
     * 系统字典的字典类型信息（含字典值列表）一定要存储到缓存中，否则无法给前端页面提供完整可用字典值列表数据
     *
     * @return true
     * @see DictProvider#isStoreDictType()
     */
    @Override
    public boolean isStoreDictType() {
        return true;
    }

    /**
     * 获取一个字典类型对象
     *
     * @param dictType 字典类型代码
     * @param creator  创建一个新的字典类型对象
     * @since 1.4.7
     */
    public DictType getDict(final String dictType, final Supplier<DictType> creator) {
        return cache.computeIfAbsent(dictType, s -> creator.get());
    }

    @Override
    public Iterator<DictType> dictTypeIterator() {
        final Collection<DictType> values = cache.values();
        if (logger.isDebugEnabled()) {
            logger.debug("当前系统共有 {} 个系统字典类型信息", values.size());
        }
        return values.iterator();
    }
}
