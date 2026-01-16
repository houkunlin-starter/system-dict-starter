package com.houkunlin.dict.common.provider;

import com.houkunlin.dict.store.DictStore;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.provider.DictProvider;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author HouKunLin
 */
@Component
public class DbDictProvider implements DictProvider {
    @Override
    public boolean isStoreDictType() {
        /** 返回的字典类型信息需要存入 {@link DictStore} */
        return true;
    }

    @Override
    public Iterator<DictType> dictTypeIterator() {
        final List<DictType> typeVos = new ArrayList<>();
        // 模拟从数据库读取数据
        typeVos.add(getHobby());
        typeVos.add(getNation());
        return typeVos.iterator();
    }

    private DictType getHobby() {
        return DictType.newBuilder("dictHobby", "爱好")
            .add(1, "打篮球")
            .add(2, "踢足球")
            .add(3, "打羽毛球")
            .add(4, "打排球")
            .add(5, "玩手机")
            .add(6, "看电影")
            .build();
    }

    private DictType getNation() {
        return DictType.newBuilder("dictNation", "民族")
            .add(1, "汉族")
            .add(2, "回族")
            .add(3, "瑶族")
            .add(4, "壮族")
            .add(5, "藏族")
            .add(6, "蒙古族")
            .build();
    }
}
