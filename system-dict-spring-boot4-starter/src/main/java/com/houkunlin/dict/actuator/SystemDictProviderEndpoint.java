package com.houkunlin.dict.actuator;

import com.houkunlin.dict.provider.SystemDictProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统字典提供者端点，用于查询系统字典提供者的缓存信息。
 *
 * @author HouKunLin
 * @since 1.3.0
 */
@Endpoint(id = "dictSystem")
@RequiredArgsConstructor
public class SystemDictProviderEndpoint {
    /**
     * 系统字典提供者
     */
    private final SystemDictProvider provider;

    /**
     * 默认端点，返回系统字典类型代码列表。
     *
     * @return 系统字典类型代码列表
     */
    @ReadOperation
    public Object index() {
        final Map<String, Object> result = new HashMap<>();
        result.put("types", provider.getCache().keySet());
        return result;
    }

    /**
     * 获取字典类型信息。
     *
     * @param dictType 字典类型代码
     * @return 字典类型信息
     */
    @ReadOperation
    public Object type(@Selector String dictType) {
        return provider.getCache().get(dictType);
    }
}
