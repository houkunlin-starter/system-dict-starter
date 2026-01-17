package com.houkunlin.dict.jackson;

import com.houkunlin.dict.SystemDictAutoConfiguration;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.annotation.DictTree;
import com.houkunlin.dict.enums.NullStrategy;
import com.houkunlin.dict.properties.DictProperties;

import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;

public interface IDictValueSerializerTree extends IDictValueSerializer {

    default Collection<String> getTreeDictTextList(Object bean, String fieldName, Object value, DictText dictText, DictTree dictTree, final String dictType, String arrayItemValue) {
        if (dictTree == null) {
            return Collections.singleton(getDictText(bean, fieldName, value, dictText, dictType, arrayItemValue));
        }
        int depth = dictTree.maxDepth();
        if (depth <= 0) {
            // 使用全局配置
            depth = SystemDictAutoConfiguration.get(DictProperties::getTreeDepth).orElse(-1);
        }
        final Deque<String> values = new LinkedList<>();
        String itemValue = arrayItemValue;
        do {
            final String text = getDictText(bean, fieldName, value, dictText, dictType, itemValue);
            if (text != null) {
                values.addFirst(text);
            } else if (dictTree.nullStrategy() != NullStrategy.IGNORE) {
                if (dictTree.nullStrategy() == NullStrategy.NULL) {
                    values.addFirst(null);
                } else if (dictTree.nullStrategy() == NullStrategy.EMPTY) {
                    values.addFirst("");
                }
            }
            itemValue = getDictParentValue(bean, fieldName, value, dictText, dictType, itemValue);
        } while (itemValue != null && (depth <= 0 || --depth > 0));
        return values;
    }

    /**
     * 获取树形结构的字典文本值
     *
     * @param bean           包含字典值的 Bean 对象
     * @param value          字典值
     * @param dictType       字典类型
     * @param arrayItemValue 数组项值（如果是数组类型的字典值，则为数组项值，否则为 null）
     * @return 树形结构的字典文本值
     */
    default String getTreeDictTextString(Object bean, String fieldName, Object value, DictText dictText, DictTree dictTree, final String dictType, String arrayItemValue) {
        if (dictTree == null) {
            return getDictText(bean, fieldName, value, dictText, dictType, arrayItemValue);
        }
        final Collection<String> values = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, arrayItemValue);
        return String.join(dictTree.delimiter(), values);
    }
}
