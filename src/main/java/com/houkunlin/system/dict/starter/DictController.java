package com.houkunlin.system.dict.starter;

import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 系统字典控制器
 *
 * @author HouKunLin
 * @since 1.4.1
 */
@Api(tags = "系统字典")
@RestController
@RequestMapping("${system.dict.controller.prefix:/dict}")
@ConditionalOnProperty(prefix = "system.dict.controller", name = "enabled", matchIfMissing = true)
public class DictController {
    /**
     * 获取字典类型信息
     *
     * @param dict 字典类型代码
     * @return 字典类型对象
     */
    @ApiOperation("获取字典类型[PATH]")
    @ApiImplicitParam(name = "dict", value = "字典类型代码", required = true, paramType = "path", dataTypeClass = String.class)
    @GetMapping("{dict}")
    public DictTypeVo dictType(@PathVariable String dict) {
        return DictUtil.getDictType(dict);
    }

    /**
     * 获取字典类型信息
     *
     * @param dict 字典类型代码
     * @return 字典类型对象
     */
    @ApiOperation("获取字典类型[QUERY]")
    @ApiImplicitParam(name = "dict", value = "字典类型代码", required = true, paramType = "query", dataTypeClass = String.class)
    @GetMapping(params = {"dict"})
    public DictTypeVo dictTypeQuery(String dict) {
        return DictUtil.getDictType(dict);
    }

    /**
     * 获取字典值文本信息
     *
     * @param dict  字典类型代码
     * @param value 字典值代码
     * @return 字典值文本信息
     */
    @ApiOperation("获取字典值文本[PATH]")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dict", value = "字典类型代码", required = true, paramType = "path", dataTypeClass = String.class),
        @ApiImplicitParam(name = "value", value = "字典值代码", required = true, paramType = "path", dataTypeClass = String.class)
    })
    @GetMapping("{dict}/{value}")
    public String dictText(@PathVariable String dict, @PathVariable String value) {
        return DictUtil.getDictText(dict, value);
    }

    /**
     * 获取字典值文本信息
     *
     * @param dict  字典类型代码
     * @param value 字典值代码
     * @return 字典值文本信息
     */
    @ApiOperation("获取字典值文本[QUERY]")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "dict", value = "字典类型代码", required = true, paramType = "query", dataTypeClass = String.class),
        @ApiImplicitParam(name = "value", value = "字典值代码", required = true, paramType = "query", dataTypeClass = String.class)
    })
    @GetMapping(params = {"dict", "value"})
    public String dictTextQuery(String dict, String value) {
        return DictUtil.getDictText(dict, value);
    }
}
