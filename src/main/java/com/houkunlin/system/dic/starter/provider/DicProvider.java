package com.houkunlin.system.dic.starter.provider;

import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Collectors;

/**
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
