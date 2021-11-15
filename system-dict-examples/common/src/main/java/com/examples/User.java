package com.examples;

import com.houkunlin.system.dict.starter.json.Array;
import com.houkunlin.system.dict.starter.json.DictText;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户信息。
 * 关于 `@DictText` 注解的更多使用示例代码请查看项目路径下 `${project.dir}/src/test/java/com.houkunlin.system.dict.starter` 的单元测试代码
 *
 * @author HouKunLin
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    /** 用户主键 */
    private int id;
    /** 用户性别 */
    @DictText("UserGender")
    private int gender;
    /** 用户性别 */
    @DictText(value = "UserGender", fieldName = "sexName")
    private int sex;
    /** 用户类型 */
    @DictText("UserType")
    private Integer type;
    /** 用户类型 */
    @DictText(value = "UserType", array = @Array)
    private String typeArrays0;
    /** 用户类型 */
    @DictText(value = "UserType", array = @Array(toText = false))
    private String typeArrays1;
    /** 用户类型 */
    @DictText(value = "UserType", array = @Array(toText = false), replace = DictText.Type.YES)
    private String typeArrays2;
    /** 用户类型 */
    @DictText(enums = UserType.class)
    private String typeEnum0;
    /** 用户类型 */
    @DictText(enums = UserType.class, replace = DictText.Type.YES)
    private String typeEnum1;
    /** 用户姓名 */
    private String name;
}
