package com.houkunlin.system.dic.starter;

import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import com.houkunlin.system.dic.starter.store.DicStore;
import org.springframework.stereotype.Component;

/**
 * 系统字典工具
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Component
public class DicUtil {

    private static DicStore store;

    public DicUtil(final DicStore store) {
        DicUtil.store = store;
    }

    public static DicTypeVo getDicType(String type) {
        if (type == null || store == null) {
            return null;
        }
        return store.getDicType(type);
    }

    public static String getDicValueTitle(String type, String value) {
        if (type == null || value == null || store == null) {
            return null;
        }
        return store.getDicValueTitle(type, value);
    }

    public static String dicKey(String type) {
        return "dic:" + type;
    }

    public static String dicKey(DicValueVo value) {
        return "dic:" + value.getDicType() + ":" + value.getValue();
    }

    public static String dicKey(String type, Object value) {
        return "dic:" + type + ":" + value;
    }
}
