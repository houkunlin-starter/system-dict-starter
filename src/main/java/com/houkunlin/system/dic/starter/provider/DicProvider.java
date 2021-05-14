package com.houkunlin.system.dic.starter.provider;

import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import com.houkunlin.system.dic.starter.store.DicStore;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * 系统字典提供者。系统扫描到的数据字典信息会先存储到 {@link SystemDicProvider} 来进行一个本地缓存，之后 {@link DicProvider} 中的字典信息会被被保存到 {@link DicStore} 对象中
 *
 * @author HouKunLin
 */
public interface DicProvider {
    /**
     * 实现一个迭代器，可以通过迭代器直接获取到列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行。
     *
     * @return 迭代器对象
     */
    default Iterator<DicTypeVo> dicTypeIterator() {
        return Collections.emptyIterator();
    }

    /**
     * 实现一个迭代器，可以通过迭代器直接获取到列表信息。
     * 建议在进行超大数据量的时候手动实现此方法，虽然有可能不存在这种使用场景。
     * 该方法不会并发执行。
     *
     * @return 迭代器对象
     */
    default Iterator<DicValueVo<? extends Serializable>> dicValueIterator() {
        final Iterator<DicTypeVo> iterator = dicTypeIterator();
        return new Iterator<DicValueVo<? extends Serializable>>() {
            List<DicValueVo<? extends Serializable>> valueVos = null;
            int index = 0;
            int size = 0;

            @Override
            public boolean hasNext() {
                if (index == size && iterator.hasNext()) {
                    valueVos = iterator.next().getChildren();
                    index = 0;
                    size = valueVos.size();
                }
                return valueVos != null && index < size;
            }

            @Override
            public DicValueVo<? extends Serializable> next() {
                return valueVos.get(index++);
            }
        };
    }
}
