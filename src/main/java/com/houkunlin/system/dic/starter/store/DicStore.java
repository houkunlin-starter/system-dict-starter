package com.houkunlin.system.dic.starter.store;

import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;

import java.io.Serializable;
import java.util.Iterator;

/**
 * 系统字典存储对象。程序扫描到的系统字典信息都将会调用该对象来存储字典信息，同时该对象也将负责读取获取系统字典信息
 *
 * @author HouKunLin
 */
public interface DicStore {
    /**
     * 存储一个完整的数据字典信息
     *
     * @param dicType 数据字典对象
     */
    void store(DicTypeVo dicType);

    /**
     * 存储一个字典值列表数据
     *
     * @param iterator 字典值列表
     */
    void store(Iterator<DicValueVo<? extends Serializable>> iterator);

    /**
     * 通过字典类型获取完整的字典信息
     *
     * @param type 字典雷系
     * @return 字典对象信息
     */
    DicTypeVo getDicType(String type);

    /**
     * 通过字典值获取字典文本信息
     *
     * @param type  字典所属类型
     * @param value 字典值
     * @return 字典文本
     */
    String getDicValueTitle(String type, String value);
}
