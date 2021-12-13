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
public class DbTreeDictProvider implements DictProvider {

    @Override
    public Iterator<DictTypeVo> dictTypeIterator() {
        // 模拟从数据库读取数据
        final List<DictTypeVo> list = new ArrayList<>();
        final DictTypeVo typeVo1 = DictTypeVo.newBuilder("TreeData", "树形结构数据测试")
            .add("", "1", "节点1")
            .add("", "2", "节点2")
            .add("", "3", "节点3")
            .add("1", "1-1", "节点1-1")
            .add("1", "1-2", "节点1-2")
            .add("1", "1-3", "节点1-3")
            .add("2", "2-1", "节点2-1")
            .add("2", "2-2", "节点2-2")
            .add("2", "2-3", "节点2-3")
            .add("3", "3-1", "节点3-1")
            .add("3", "3-2", "节点3-2")
            .add("3", "3-3", "节点3-3")
            .build();
        // http://www.mca.gov.cn/article/sj/xzqh/2020/20201201.html
        final DictTypeVo typeVo2 = DictTypeVo.newBuilder("City", "城市")
            .add("", "110000", "北京市")
            .add("110000", "110101", "东城区")
            .add("110000", "110102", "西城区")
            .add("110000", "110105", "朝阳区")
            .add("110000", "110106", "丰台区")
            .add("110000", "110107", "石景山区")

            .add("", "130000", "河北省")
            .add("130000", "130100", "石家庄市")
            .add("130100", "130102", "长安区")
            .add("130100", "130104", "桥西区")
            .add("130100", "130105", "新华区")
            .add("130100", "130107", "井陉矿区")

            .add("130000", "130200", "唐山市")
            .add("130200", "130202", "路南区")
            .add("130200", "130203", "路北区")
            .add("130200", "130204", "古冶区")
            .add("130200", "130205", "开平区")
            .build();

        list.add(typeVo1);
        list.add(typeVo2);
        return list.iterator();
    }
}
