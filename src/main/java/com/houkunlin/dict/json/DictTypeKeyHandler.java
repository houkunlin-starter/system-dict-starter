package com.houkunlin.dict.json;

import com.houkunlin.dict.DictUtil;
import com.houkunlin.dict.annotation.DictText;

/**
 * 自定义字典类型代码处理器。
 * <p>
 * 需求场景：实体类Bean有字段A和B，字段A的字典值需要根据字段B的值来决定。
 * 通过实现此接口，可以根据Bean对象的其他字段值动态计算字典类型。
 * </p>
 * <pre>
 *  class Bean {
 *      @ DictText(dictType = BeanDictTypeKeyHandler.class)
 *      private int key;
 *      private int keyType;
 *  }
 *  public class BeanDictTypeKeyHandler implements DictTypeKeyHandler&lt;Bean&gt; {
 *      @ Override
 *      public String getDictType(final Bean bean, final String fieldName, final DictText dictText) {
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
 * @param <T> 实体类类型
 * @author HouKunLin
 * @since 1.4.7
 */
public interface DictTypeKeyHandler<T> {
    /**
     * 获取字典类型代码
     * <p>
     * 根据实体类对象和字段信息，动态计算并返回字典类型代码。
     * 当返回 null 时，表示使用默认的字典类型处理逻辑。
     * </p>
     *
     * @param bean       实体类对象
     * @param fieldName  字段名称
     * @param dictText   字段上的注解对象
     * @return 字典类型代码，如果返回 null 则使用默认的字典类型处理逻辑
     */
    String getDictType(final T bean, final String fieldName, final DictText dictText);

    /**
     * 获取字典文本
     * <p>
     * 根据字典类型和字典值，获取对应的字典文本。
     * 默认实现使用 DictUtil.getDictText 方法获取字典文本。
     * </p>
     *
     * @param bean           实体类对象
     * @param fieldName      字段名称
     * @param value          字段值，或者集合字段的单项值
     * @param dictText       字段上的注解对象
     * @param dictType       字典类型，由 getDictType 方法返回
     * @param arrayItemValue 字典值：字段值，或者集合字段的单项值，或者分割字符串的单项值，或者父级字典值
     * @return 字典文本
     */
    default String getDictText(final T bean, final String fieldName, final Object value, final DictText dictText, final String dictType, final String arrayItemValue) {
        return DictUtil.getDictText(dictType, arrayItemValue);
    }

    /**
     * 获取父级字典值
     * <p>
     * 根据字典类型和字典值，获取对应的父级字典值。
     * 默认实现使用 DictUtil.getDictParentValue 方法获取父级字典值。
     * </p>
     *
     * @param bean           实体类对象
     * @param fieldName      字段名称
     * @param value          字段值，或者集合字段的单项值
     * @param dictText       字段上的注解对象
     * @param dictType       字典类型，由 getDictType 方法返回
     * @param arrayItemValue 字典值：字段值，或者集合字段的单项值，或者分割字符串的单项值，或者父级字典值
     * @return 父级字典值
     */
    default String getDictParentValue(final T bean, final String fieldName, final Object value, final DictText dictText, final String dictType, final String arrayItemValue) {
        return DictUtil.getDictParentValue(dictType, arrayItemValue);
    }
}
