package com.system.dic.starter;

import com.system.dic.starter.bean.DicTypeVo;
import com.system.dic.starter.bean.DicValueVo;
import com.system.dic.starter.store.DicStore;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

/**
 * @author HouKunLin
 */
@SuppressWarnings("all")
@Component
public class DicUtil {

    private static List<DicStore> stores;

    public DicUtil(final List<DicStore> stores) {
        DicUtil.stores = stores;
    }

    public static DicTypeVo getDicType(String type) {
        if (type == null) {
            return null;
        }
        for (final DicStore store : stores) {
            final DicTypeVo dicType = store.getDicType(type);
            if (dicType != null) {
                return dicType;
            }
        }
        return null;
    }

    public static Object getDicValueTitle(String type, String value) {
        if (type == null || value == null) {
            return null;
        }
        for (final DicStore store : stores) {
            final Object title = store.getDicValueTitle(type, value);
            if (title != null) {
                return title;
            }
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
