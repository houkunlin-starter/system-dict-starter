package com.houkunlin.system.dic.starter.provider;

import com.houkunlin.system.dic.starter.SystemDicScan;
import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.*;

/**
 * 系统字典提供者。{@link SystemDicScan} 注解扫描到的系统字典都会写入到该对象中。
 * 然后由指定的程序获取系统中所有 {@link DicProvider} 对象列表对所有字典进行注册
 *
 * @author HouKunLin
 */
@Component
public class SystemDicProvider implements DicProvider {
    private static final Logger logger = LoggerFactory.getLogger(SystemDicProvider.class);
    private final Map<String, DicTypeVo> cache = new HashMap<>();

    /**
     * 增加一个字典类型对象（含字典值列表）
     *
     * @param vo 字典类型对象
     */
    public void addDic(final DicTypeVo vo) {
        final List<? extends DicValueVo<? extends Serializable>> children = vo.getChildren();
        vo.setChildren(new ArrayList<>(children));
        cache.put(vo.getType(), vo);
    }

    @Override
    public Iterator<DicTypeVo> dicTypeIterator() {
        final Collection<DicTypeVo> values = cache.values();
        if (logger.isDebugEnabled()) {
            logger.debug("当前系统共有 {} 个系统字典类型信息", values.size());
        }
        return values.iterator();
    }
}
