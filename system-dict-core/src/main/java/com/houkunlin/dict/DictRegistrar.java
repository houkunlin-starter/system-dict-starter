package com.houkunlin.dict;

import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.store.DictStore;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public interface DictRegistrar {
    void forEachAllDict(Set<String> dictProviderClasses, Consumer<DictType> dictTypeConsumer, Consumer<DictType> systemDictTypeConsumer, Consumer<Iterator<DictValue>> dictValueConsumer);

    /**
     * 刷新数据字典信息
     * <p>
     * 该方法用于刷新数据字典信息，首先检查距离上一次刷新的时间间隔，
     * 如果小于配置的刷新间隔，则跳过本次刷新操作。
     * 然后更新最后刷新时间，并调用 {@link #forEachAllDict} 方法获取所有字典数据，
     * 最后将获取到的字典数据存储到 {@link DictStore} 中。
     * </p>
     *
     * @param dictProviderClasses 需要刷新的数据字典提供商类限定名，null 表示刷新所有
     */
    void refreshDict(Set<String> dictProviderClasses);
}
