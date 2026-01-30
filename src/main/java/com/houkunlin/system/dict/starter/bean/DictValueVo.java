package com.houkunlin.system.dict.starter.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.houkunlin.system.dict.starter.notice.RefreshDictTypeEvent;
import com.houkunlin.system.dict.starter.notice.RefreshDictValueEvent;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 字典值信息对象
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
@ApiModel("字典值信息")
@Schema(name = "字典值信息")
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class DictValueVo implements Serializable {
    /**
     * 字典类型代码
     */
    @ApiModelProperty(value = "字典类型代码", hidden = true)
    @Schema(title = "字典类型代码", hidden = true)
    @JsonIgnore
    private String dictType;
    /**
     * 父级字典值，由父级字典值可以组成一个类似树形结构数据的字典信息。
     * 构建树形结构字典数据所需要的一个父级值；
     *
     * @since 1.4.6
     */
    @ApiModelProperty("父级字典值")
    @Schema(title = "父级字典值")
    private Object parentValue;
    /**
     * 字典值
     */
    @ApiModelProperty("字典值")
    @Schema(title = "字典值")
    private Object value;
    /**
     * 字典名称。
     * <p>在使用 {@link RefreshDictValueEvent} 事件时，此值为 null 意为删除这个字典值文本信息</p>
     */
    @ApiModelProperty("字典名称")
    @Schema(title = "字典名称")
    private String title;
    /**
     * 数据字典值列表排序值（系统不会使用此字段进行排序和维护排序，需要前端根据此字段来自行排序展示）
     */
    @ApiModelProperty(value = "排序值", notes = "系统不会执行排序后再返回给前端，系统不会使用此字段进行排序和维护排序，需要前端根据此字段来自行排序展示")
    @Schema(title = "排序值", description = "系统不会执行排序后再返回给前端，系统不会使用此字段进行排序和维护排序，需要前端根据此字段来自行排序展示")
    private int sorted;
    /**
     * 是否禁用，禁用的字典文本不会从缓存中删除，因为禁用的字典文本可能在之前的数据中被使用。
     * 并且禁用的字典文本也不会从缓存的字典类型对象中删除，需要在调用 {@link RefreshDictTypeEvent} 时明确的把字典文本移除才能从字典类型缓存中删除。
     *
     * @since 1.4.9
     */
    @ApiModelProperty("是否禁用")
    @Schema(title = "是否禁用")
    private boolean disabled;

    /**
     * 扩展数据
     * <p>
     * 用于存储字典项的额外信息，可根据业务需求自由扩展
     * </p>
     * <p>
     * 使用场景示例：
     * <ul>
     * <li>1. 存储字典项的颜色值，如状态字典中不同状态对应不同颜色</li>
     * <li>2. 存储字典项的图标信息，如菜单类型字典中不同类型对应不同图标</li>
     * <li>3. 存储字典项的业务属性，如用户类型字典中不同类型对应的权限标识</li>
     * <li>4. 存储字典项的国际化信息，如多语言环境下的不同语言文本</li>
     * <li>5. 存储字典项的关联信息，如部门字典中关联的上级部门详情</li>
     * </ul>
     * </p>
     * <p>
     * 注意事项：
     * <ul>
     * <li>1. 扩展数据的键值对可以根据业务需求自定义，不强制要求使用固定的键名</li>
     * <li>2. 扩展数据的键值对可以为空，即返回 null 或空 Map</li>
     * </ul>
     * </p>
     *
     * @since 仅 1.6.3 和 1.7.1 和 2.0.1 版本开始有的特性
     */
    @ApiModelProperty("扩展数据")
    @Schema(title = "扩展数据")
    private Map<String, Object> data;
    /**
     * 子字典值列表
     *
     * @since 1.4.9
     */
    @ApiModelProperty("子字典值列表")
    @Schema(title = "子字典值列表")
    private List<DictValueVo> children;

    public DictValueVo(final String dictType, final Object value, final String title, final int sorted) {
        this.dictType = dictType;
        this.value = value;
        this.title = title;
        this.sorted = sorted;
    }

    public DictValueVo(final String dictType, final Object parentValue, final Object value, final String title, final int sorted) {
        this.dictType = dictType;
        this.parentValue = parentValue;
        this.value = value;
        this.title = title;
        this.sorted = sorted;
    }

    public DictValueVo(final String dictType, final Object parentValue, final Object value, final String title, final int sorted, Map<String, Object> data) {
        this.dictType = dictType;
        this.parentValue = parentValue;
        this.value = value;
        this.title = title;
        this.sorted = sorted;
        this.data = data;
    }
}
