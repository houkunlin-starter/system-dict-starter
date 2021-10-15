package test.application.common;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Iterator;

/**
 * @author HouKunLin
 */
@Component
public class MyProvider implements DictProvider {
    @Override
    public boolean isStoreDictType() {
        return true;
    }

    @Override
    public Iterator<DictTypeVo> dictTypeIterator() {
        final DictTypeVo typeVo = DictTypeVo.newBuilder("name", "测试字典")
            .add("1", "测试1")
            .add("2", "测试2")
            .build();
        return Collections.singletonList(typeVo).iterator();
    }
}
