package com.examples;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.provider.DictProvider;
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
        /** 返回的字典类型信息需要存入 {@link com.houkunlin.system.dict.starter.store.DictStore} */
        return true;
    }

    @Override
    public Iterator<DictTypeVo> dictTypeIterator() {
        final List<DictTypeVo> typeVos = new ArrayList<>();
        // 模拟从数据库读取数据
        typeVos.add(getHobby());
        typeVos.add(getNation());
        return typeVos.iterator();
    }

    private DictTypeVo getHobby() {
        return DictTypeVo.newBuilder("dictHobby", "爱好")
            .add(1, "打篮球")
            .add(2, "踢足球")
            .add(3, "打羽毛球")
            .add(4, "打排球")
            .add(5, "玩手机")
            .add(6, "看电影")
            .build();
    }

    private DictTypeVo getNation() {
        return DictTypeVo.newBuilder("dictNation", "民族")
            .add(1, "汉族")
            .add(2, "回族")
            .add(3, "瑶族")
            .add(4, "壮族")
            .add(5, "藏族")
            .add(6, "蒙古族")
            .build();
    }
}
