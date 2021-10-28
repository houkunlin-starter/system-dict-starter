package com.houkunlin.system.dict.starter.store;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;

/**
 * RemoteDict 默认实现
 *
 * @author HouKunLin
 * @since 1.4.4
 */
public class RemoteDictDefaultImpl implements RemoteDict {
    @Override
    public DictTypeVo getDictType(final String type) {
        return null;
    }

    @Override
    public String getDictText(final String type, final String value) {
        return null;
    }
}
