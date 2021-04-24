package com.houkunlin.system.dic.starter.provider;

import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import com.houkunlin.system.dic.starter.store.DicStore;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * 系统字典提供者。系统扫描到的数据字典信息会先存储到 {@link SystemDicProvider} 来进行一个本地缓存，之后 {@link DicProvider} 中的字典信息会被被保存到 {@link DicStore} 对象中
 *
 * @author HouKunLin
 */
public interface DicProvider {
    /**
     * 获取所有字典类型 + 字典值 列表信息
     *
     * @return 字典列表
     */
    Collection<DicTypeVo> getDicTypes();

    /**
     * 获取所有字典值列表信息
     *
     * @return 字典值列表
     */
    default Collection<DicValueVo<? extends Serializable>> getDicValues() {
        return getDicTypes().stream().flatMap(vo -> vo.getChildren().stream()).collect(Collectors.toList());
    }

    /**
     * 实现一个迭代器，可以通过迭代器直接获取到列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     *
     * @return 迭代器对象
     */
    default Iterator<DicValueVo<? extends Serializable>> iterator() {
        return getDicValues().iterator();
    }
}
