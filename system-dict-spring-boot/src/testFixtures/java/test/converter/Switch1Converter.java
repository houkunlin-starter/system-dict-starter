package test.converter;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.common.bean.Switch;
import org.springframework.core.convert.converter.Converter;

/**
 * {@link Switch} 字典枚举转换器（字典值类型是 String 且 {@code onlyDictValue = false} 的参考实现）
 * <p>
 * 优先使用枚举名称转换，枚举名称转换失败时再使用字典值转换。
 * </p>
 *
 * @author HouKunLin
 */
public class Switch1Converter implements Converter<String, Switch> {

    @Override
    public Switch convert(String text) {
        try {
            return Switch.valueOf(text);
        } catch (Exception var3) {
            return DictEnum.valueOf(Switch.values(), text);
        }
    }

}
