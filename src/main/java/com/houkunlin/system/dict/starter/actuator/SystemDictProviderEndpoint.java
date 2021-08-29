package com.houkunlin.system.dict.starter.actuator;

import com.houkunlin.system.dict.starter.provider.SystemDictProvider;
import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统字典提供者端点
 *
 * @author HouKunLin
 * @since 1.3.0
 */
@Component
@Endpoint(id = "dict-system")
@AllArgsConstructor
public class SystemDictProviderEndpoint {
    private final SystemDictProvider provider;

    /**
     * 默认端点，返回系统字典类型代码列表
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
     * 获取字典类型信息
     *
     * @param dictType 字典类型代码
     * @return 字典类型信息
     */
    @ReadOperation
    public Object type(@Selector String dictType) {
        return provider.getCache().get(dictType);
    }
}
