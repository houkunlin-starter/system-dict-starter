package test.converter;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.common.bean.PeopleType2;
import org.springframework.core.convert.converter.Converter;

/**
 * {@link PeopleType2} 字典枚举转换器（字典值类型非 String 且 {@code onlyDictValue = true} 的参考实现）
 * <p>
 * 只支持通过字典值进行转换，不进行枚举名称转换。
 * </p>
 *
 * @author HouKunLin
 */
public class PeopleType2Converter implements Converter<String, PeopleType2> {
    @Override
    public PeopleType2 convert(String text) {
        return (PeopleType2) DictEnum.valueOf(PeopleType2.values(), Integer.valueOf(text));
    }
}
