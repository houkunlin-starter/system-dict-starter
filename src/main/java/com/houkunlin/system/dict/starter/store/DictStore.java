package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.provider.DictProvider;

import java.util.Iterator;
import java.util.Set;

/**
 * 系统字典存储对象。程序扫描到的系统字典信息都将会调用该对象来存储字典信息，同时该对象也将负责读取获取系统字典信息
 *
 * @author HouKunLin
 */
public interface DictStore {
    /**
     * 存储一个完整的数据字典信息
     *
     * @param dictType 数据字典对象
     */
    void store(DictTypeVo dictType);

    /**
     * 存储一个字典值列表数据
     *
     * @param iterator 字典值列表
     */
    void store(Iterator<DictValueVo> iterator);

    /**
     * 字典类型代码列表。
     * <p>仅能够得到 {@link DictProvider#isStoreDictType()} 返回 true 的字典类型代码信息。</p>
     * <p>当 {@link DictProvider#isStoreDictType()} 返回 false 时对应的 {@link DictProvider} 提供的字典类型信息对象将不会被存储，也就是当前方法无法获得该字典类型代码</p>
     *
     * @return 字典类型代码列表
     * @since 1.4.0
     */
    Set<String> dictTypeKeys();

    /**
     * 通过字典类型获取完整的字典信息
     * <p>仅能够得到 {@link DictProvider#isStoreDictType()} 返回 true 的字典类型代码信息。</p>
     * <p>当 {@link DictProvider#isStoreDictType()} 返回 false 时对应的 {@link DictProvider} 提供的字典类型信息对象将不会被存储，也就是当前方法无法获得该字典类型代码</p>
     *
     * @param type 字典类型
     * @return 字典对象信息
     */
    DictTypeVo getDictType(String type);

    /**
     * 通过字典值获取字典文本信息
     *
     * @param type  字典所属类型
     * @param value 字典值
     * @return 字典文本
     */
    String getDictText(String type, String value);
}
