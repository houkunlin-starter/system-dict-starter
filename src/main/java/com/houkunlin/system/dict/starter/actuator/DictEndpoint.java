package com.houkunlin.system.dict.starter.actuator;

import com.houkunlin.system.dict.starter.DictUtil;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import com.houkunlin.system.dict.starter.store.DictStore;
import com.houkunlin.system.dict.starter.store.RemoteDict;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 字典端点
 *
 * @author HouKunLin
 * @since 1.3.0
 */
@Component
@Endpoint(id = "dict")
@AllArgsConstructor
public class DictEndpoint {
    private final List<DictProvider> providers;
    private final DictStore store;
    private final RemoteDict remoteDict;

    /**
     * 默认端点接口信息
     *
     * @return 返回系统一些class对象名称
     */
    @ReadOperation
    public Object index() {
        final Map<String, Object> map = new HashMap<>();
        map.put("providers", providers.stream().map(Object::getClass).map(Class::getName).collect(Collectors.toList()));
        map.put("stores", store.getClass().getName());
        map.put("remoteDict", remoteDict.getClass().getName());

        final Map<String, Object> result = new HashMap<>();
        result.put("dict-classes", map);
        result.put("dict-types", store.dictTypeKeys());
        return result;
    }

    /**
     * 获得字典类型信息
     *
     * @param dictType 字典类型代码
     * @return 字典类型信息
     */
    @ReadOperation
    public Object type(@Selector String dictType) {
        return DictUtil.getDictType(dictType);
    }

    /**
     * 获得字典值文本信息
     *
     * @param dictType  字典类型代码
     * @param dictValue 字典值
     * @return 字典值文本
     */
    @ReadOperation
    public Object title(@Selector String dictType, @Selector String dictValue) {
        return DictUtil.getDictText(dictType, dictValue);
    }
}
