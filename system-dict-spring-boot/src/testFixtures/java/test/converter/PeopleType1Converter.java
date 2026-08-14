package test.converter;

import com.houkunlin.dict.DictEnum;
import com.houkunlin.dict.common.bean.PeopleType;
import org.springframework.core.convert.converter.Converter;

/**
 * {@link PeopleType} 字典枚举转换器（字典值类型非 String 且 {@code onlyDictValue = false} 的参考实现）
 * <p>
 * 优先使用枚举名称转换，枚举名称转换失败时再使用字典值转换。
 * </p>
 *
 * @author HouKunLin
 */
public class PeopleType1Converter implements Converter<String, PeopleType> {

    @Override
    public PeopleType convert(String text) {
        try {
            return PeopleType.valueOf(text);
        } catch (Exception var3) {
            return DictEnum.valueOf(PeopleType.values(), Integer.valueOf(text));
        }
    }

}
