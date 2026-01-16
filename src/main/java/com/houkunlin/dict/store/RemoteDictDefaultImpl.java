package com.houkunlin.dict.store;

import com.houkunlin.dict.bean.DictType;

/**
 * RemoteDict 默认实现
 *
 * @author HouKunLin
 * @since 1.4.4
 */
public class RemoteDictDefaultImpl implements RemoteDict {
    @Override
    public DictType getDictType(final String type) {
        return null;
    }

    @Override
    public String getDictText(final String type, final String value) {
        return null;
    }
}
