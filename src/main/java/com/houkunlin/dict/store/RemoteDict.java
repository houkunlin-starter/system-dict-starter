package com.houkunlin.dict.store;

import com.houkunlin.dict.bean.DictType;

/**
 * 远程字典获取接口
 * <p>
 * 该接口用于获取不存在于本地存储的字典数据。当从 {@link DictStore} 对象中获取不到数据时，
 * 会尝试由实现此接口的类发起远程请求获取字典数据。
 * 例如除了需要系统字典的数据（已经内置在 DictStore 中），还需要获取一些其他的用户字典数据，
 * 此时可以通过实现此接口来处理远程字典数据的获取。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
public interface RemoteDict {
    /**
     * 通过字典类型获取完整的字典信息
     * <p>
     * 根据字典类型代码从远程数据源获取完整的字典类型对象，包括该类型的所有字典值信息。
     * 当本地存储中不存在指定的字典类型时，会调用此方法尝试从远程获取。
     * </p>
     *
     * @param type 字典类型代码，标识要获取的字典类型
     * @return 完整的字典类型对象，包含字典类型代码和字典值列表；如果不存在则返回 {@code null}
     */
    DictType getDictType(String type);

    /**
     * 通过字典类型和字典值获取字典文本信息
     * <p>
     * 根据字典类型代码和字典值从远程数据源获取对应的字典文本（标题）。
     * 当本地存储中不存在指定的字典值时，会调用此方法尝试从远程获取。
     * 这是数据字典系统最常用的方法之一，用于将字典值转换为可读的文本显示。
     * </p>
     *
     * @param type  字典类型代码，标识字典所属的类型
     * @param value 字典值，需要查询文本的具体值
     * @return 字典文本（标题）；如果不存在则返回 {@code null}
     */
    String getDictText(String type, String value);
}
