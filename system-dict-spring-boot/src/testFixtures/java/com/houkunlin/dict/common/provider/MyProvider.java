package com.houkunlin.dict.common.provider;

import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.provider.DictProvider;
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
    public Iterator<DictType> dictTypeIterator() {
        long startTime = System.nanoTime();
        List<DictUser> users = new ArrayList<>();
        for (int i = 0; i < 1586; i++) {
            users.add(new DictUser(i, "名称" + i));
        }
        System.out.println("加载用户数据耗时：" + (System.nanoTime() - startTime) / 100_0000.0 + "ms");
        startTime = System.nanoTime();
        final DictType typeVo = DictType.newBuilder("DictUser", "DictUser")
            .build();
        List<DictValue> children = typeVo.getChildren();
        users.forEach(dictUser -> {
            DictValue valueVo = new DictValue("DictUser", dictUser.getId(), dictUser.getName(), 0);
            children.add(valueVo);
        });
        System.out.println("转换用户数据耗时：" + (System.nanoTime() - startTime) / 100_0000.0 + "ms");
        return Collections.singletonList(typeVo).iterator();
    }
}

