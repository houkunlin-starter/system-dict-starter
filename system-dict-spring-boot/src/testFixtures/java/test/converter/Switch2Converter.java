package test.converter;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.common.bean.Switch2;
import org.springframework.core.convert.converter.Converter;

/**
 * {@link Switch2} 字典枚举转换器（字典值类型是 String 且 {@code onlyDictValue = true} 的参考实现）
 * <p>
 * 只支持通过字典值进行转换，不进行枚举名称转换。
 * </p>
 *
 * @author HouKunLin
 */
public class Switch2Converter implements Converter<String, Switch2> {
    @Override
    public Switch2 convert(String text) {
        return (Switch2) DictEnum.valueOf(Switch2.values(), text);
    }
}
