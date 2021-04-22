package com.system.dic.starter.store;

import com.system.dic.starter.bean.DicTypeVo;
import com.system.dic.starter.bean.DicValueVo;

import java.io.Serializable;
import java.util.Iterator;

/**
 * @author HouKunLin
 */
public interface DicStore {
    void store(DicTypeVo dicType);

    /**
     * @param iterator
     */
    void store(Iterator<DicValueVo<? extends Serializable>> iterator);

    DicTypeVo getDicType(String type);

    Object getDicValueTitle(String type, String value);
}
