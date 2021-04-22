package com.system.dic.starter;

import com.system.dic.starter.bean.DicTypeVo;
import com.system.dic.starter.bean.DicValueVo;
import com.system.dic.starter.store.DicStore;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
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
        if (type == null) {
            return null;
        }
        final DicTypeVo dicType = store.getDicType(type);
        if (dicType != null) {
            return dicType;
        }
        return null;
    }

    public static String getDicValueTitle(String type, String value) {
        if (type == null || value == null) {
            return null;
        }
        final String title = store.getDicValueTitle(type, value);
        if (title != null) {
            return title;
        }
        return null;
    }

    public static String dicKey(String type) {
        return "dic:" + type;
    }

    public static String dicKey(DicValueVo<? extends Serializable> value) {
        return "dic:" + value.getDicType() + ":" + value.getValue();
    }

    public static String dicKey(String type, Object value) {
        return "dic:" + type + ":" + value;
    }
}
