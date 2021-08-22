package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;

/**
 * 用来获取不存在系统字典的字典数据。例如从远程获取数据字典数据。当从 DictStore 对象中获取不到数据的时候，尝试由 RemoteDict 发起远程请求获取数据字典数据。
 * 例如除了需要系统字典的数据（已经内置在 DictStore 中），还需要获取一些其他的用户字典数据，此时可以使用该对象来处理
 *
 * @author HouKunLin
 */
public interface RemoteDict<V> {
    /**
     * 通过字典类型获取完整的字典信息
     *
     * @param type 字典雷系
     * @return 字典对象信息
     */
    DictTypeVo<V> getDicType(String type);

    /**
     * 通过字典值获取字典文本信息
     *
     * @param type  字典所属类型
     * @param value 字典值
     * @return 字典文本
     */
    String getDicValueTitle(String type, String value);
}
