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

/**
 * 字典树值序列化接口，用于处理字典树结构的序列化逻辑。
 * <p>
 * 该接口提供了获取字典树文本值的方法，支持将字典树结构转换为文本列表或连接的文本字符串。
 * </p>
 *
 * @author HouKunLin
 * @since 2.0.0
 */
public interface IDictValueSerializerTree extends IDictValueSerializer {

    /**
     * 获取树形结构的字典文本值列表。
     * <p>
     * 从字典值开始，递归获取其父节点的文本值，直到达到最大深度或没有父节点为止。
     * 结果以列表形式返回，顺序为从根节点到当前节点。
     * </p>
     *
     * @param bean           包含字典值的 Bean 对象
     * @param fieldName      字段名称
     * @param value          字段值
     * @param dictText       字典文本注解配置
     * @param dictTree       字典树注解配置
     * @param dictType       字典类型
     * @param arrayItemValue 数组项值（如果是数组类型的字典值，则为数组项值）
     * @return 树形结构的字典文本值列表
     */
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
     * 获取树形结构的字典文本值字符串。
     * <p>
     * 从字典值开始，递归获取其父节点的文本值，然后使用配置的分隔符将它们连接成一个字符串。
     * 例如："根节点/父节点/子节点"
     * </p>
     *
     * @param bean           包含字典值的 Bean 对象
     * @param fieldName      字段名称
     * @param value          字段值
     * @param dictText       字典文本注解配置
     * @param dictTree       字典树注解配置
     * @param dictType       字典类型
     * @param arrayItemValue 数组项值（如果是数组类型的字典值，则为数组项值）
     * @return 树形结构的字典文本值字符串
     */
    default String getTreeDictTextString(Object bean, String fieldName, Object value, DictText dictText, DictTree dictTree, final String dictType, String arrayItemValue) {
        if (dictTree == null) {
            return getDictText(bean, fieldName, value, dictText, dictType, arrayItemValue);
        }
        final Collection<String> values = getTreeDictTextList(bean, fieldName, value, dictText, dictTree, dictType, arrayItemValue);
        return String.join(dictTree.delimiter(), values);
    }
}
