package test.application.common;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

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
        final List<DictValueVo> valueVos = new ArrayList<>();
        valueVos.add(DictValueVo.builder().dictType("name")
            .value("1").title("测试1")
            .build());
        valueVos.add(DictValueVo.builder().dictType("name")
            .value("2").title("测试2")
            .build());
        final DictTypeVo typeVo = DictTypeVo.builder().type("name")
            .title("测试字典")
            .children(valueVos).build();
        return Collections.singletonList(typeVo).iterator();
    }
}
