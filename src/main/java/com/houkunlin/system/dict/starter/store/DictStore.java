package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;

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
     * 字典类型代码列表
     *
     * @return 字典类型代码列表
     */
    Set<String> dictTypeKeys();

    /**
     * 通过字典类型获取完整的字典信息
     *
     * @param type 字典雷系
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
