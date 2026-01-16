package com.houkunlin.system.dict.starter.json;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.annotation.DictText;

/**
 * 自定义字典类型代码处理器。
 * 需求场景：实体类Bean有字段A和B，字段A的字典值需要根据字段B的值来决定。
 * <pre>
 *  class Bean {
 *      @ DictText(dictType = BeanDictTypeKeyHandler.class)
 *      private int key;
 *      private int keyType;
 *  }
 *  public class BeanDictTypeKeyHandler implements DictTypeKeyHandler&lt;Bean&gt; {
 *      @ Override
 *      public String getDictType(final Bean bean, final String fieldName, final String fieldValue, final DictText dictText) {
 *          if (!"key".equals(fieldName)) {
 *              // 只有 BeanDictTypeKeyHandler 被重复使用时才需要判断 fieldName， 否则的话 fieldName 就是注解所在的字段
 *              return null;
 *          }
 *          if (bean.getKeyType() == 1) {
 *              return "dictUserNickname";
 *          } else if (bean.getKeyType() == 2) {
 *              return "dictDeptName";
 *          } else if (bean.getKeyType() == 3) {
 *              return "dictOrgName";
 *          }
 *          return null;
 *      }
 *  }
 * </pre>
 *
 * @author HouKunLin
 * @since 1.4.7
 */
public interface DictTypeKeyHandler<T> {
    /**
     * 获取字典类型代码
     *
     * @param bean       实体类对象
     * @param fieldName  字段名称
     * @param fieldValueItem 字段值，或者集合字段的单项值，或者分割字符串的单项值
     * @param dictText   字段上的注解对象
     * @return 字典类型代码
     */
    String getDictType(final T bean, final String fieldName, final String fieldValueItem, final DictText dictText);

    /**
     * 获取字典文本
     *
     * @param bean           实体类对象
     * @param fieldName      字段名称
     * @param fieldValueItem 字段值，或者集合字段的单项值
     * @param dictText       字段上的注解对象
     * @param dictType       字典类型 {@link DictTypeKeyHandler#getDictType(Object, String, String, DictText)} 返回值
     * @param dictValue      字典值：字段值，或者集合字段的单项值，或者分割字符串的单项值，或者父级字典值
     * @return 字典文本
     */
    default String getDictText(final T bean, final String fieldName, final String fieldValueItem, final DictText dictText, final String dictType, final String dictValue) {
        return DictUtil.getDictText(dictType, dictValue);
    }

    /**
     * 获取父级字典值
     *
     * @param bean           实体类对象
     * @param fieldName      字段名称
     * @param fieldValueItem 字段值，或者集合字段的单项值
     * @param dictText       字段上的注解对象
     * @param dictType       字典类型 {@link DictTypeKeyHandler#getDictType(Object, String, String, DictText)} 返回值
     * @param dictValue      字典值：字段值，或者集合字段的单项值，或者分割字符串的单项值，或者父级字典值
     * @return 父级字典值
     */
    default String getDictParentValue(final T bean, final String fieldName, final String fieldValueItem, final DictText dictText, final String dictType, final String dictValue) {
        return DictUtil.getDictParentValue(dictType, dictValue);
    }
}
