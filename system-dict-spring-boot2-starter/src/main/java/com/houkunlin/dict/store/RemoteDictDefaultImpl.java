package com.houkunlin.dict.store;

import com.houkunlin.dict.bean.DictType;

/**
 * 远程字典获取接口的默认实现
 * <p>
 * 该类是 {@link RemoteDict} 接口的默认实现，提供了空实现方法。
 * 当系统中没有自定义的远程字典实现时，会使用此默认实现，
 * 所有方法均返回 {@code null}，表示无法从远程获取字典数据。
 * </p>
 *
 * @author HouKunLin
 * @since 1.4.4
 */
public class RemoteDictDefaultImpl implements RemoteDict {
    /**
     * 通过字典类型获取完整的字典信息
     * <p>
     * 默认实现，始终返回 {@code null}，表示无法从远程获取字典数据。
     * 当系统中没有自定义的远程字典实现时，会使用此方法。
     * </p>
     *
     * @param type 字典类型代码，标识要获取的字典类型
     * @return 始终返回 {@code null}
     */
    @Override
    public DictType getDictType(final String type) {
        return null;
    }

    /**
     * 通过字典类型和字典值获取字典文本信息
     * <p>
     * 默认实现，始终返回 {@code null}，表示无法从远程获取字典文本数据。
     * 当系统中没有自定义的远程字典实现时，会使用此方法。
     * </p>
     *
     * @param type  字典类型代码，标识字典所属的类型
     * @param value 字典值，需要查询文本的具体值
     * @return 始终返回 {@code null}
     */
    @Override
    public String getDictText(final String type, final String value) {
        return null;
    }
}
