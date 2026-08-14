package com.houkunlin.dict;

import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;

import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public interface IDictRegistrar {
    void forEachAllDict(Set<String> dictProviderClasses, Consumer<DictType> dictTypeConsumer, Consumer<DictType> systemDictTypeConsumer, Consumer<Iterator<DictValue>> dictValueConsumer);
}
