package com.houkunlin.system.dict.starter;

import com.github.benmanes.caffeine.cache.Cache;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.bytecode.DictChildrenObjectGenerate;
import com.houkunlin.system.dict.starter.cache.DictCacheFactory;
import com.houkunlin.system.dict.starter.json.DictText;
import com.houkunlin.system.dict.starter.json.DictTextJsonSerializer;
import com.houkunlin.system.dict.starter.json.DictTextJsonSerializerBasic;
import com.houkunlin.system.dict.starter.notice.RefreshDictEvent;
import com.houkunlin.system.dict.starter.properties.DictPropertiesStorePrefixKey;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import com.houkunlin.system.dict.starter.store.DictStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 系统字典工具
 *
 * @author HouKunLin
 */
@SuppressWarnings("all")
public class DictUtil {
    private static final Logger logger = LoggerFactory.getLogger(DictUtil.class);
    /**
     * 手动处理含有字典注解的对象，对象可能需要动态生成字典，缓存对应的子类对象信息
     */
    private static final Map<Class<?>, Class<?>> TRANSFORM_CACHE = new ConcurrentHashMap<>();
    public static String TYPE_PREFIX = "dict:t:";
    public static String TYPE_SYSTEM_PREFIX = "dict:t_system:";
    public static String VALUE_PREFIX = "dict:v:";
    public static String PARENT_PREFIX = "dict:p:";
    private static DictRegistrar dictRegistrar;

    private static DictStore store;
    /**
     * 字典值缓存
     */
    private static Cache<String, String> cache;
    private static Cache<String, AtomicInteger> missCache;
    private static int missNum = Integer.MAX_VALUE;

    public DictUtil(final DictRegistrar dictRegistrar, final DictStore store, final DictCacheFactory cacheFactory) {
        DictUtil.dictRegistrar = dictRegistrar;
        DictUtil.store = store;
        cache = cacheFactory.build("dict-text");
        missCache = cacheFactory.build("dict-number-of-miss");
        missNum = cacheFactory.getDictProperties().getCache().getMissNum();
        cacheFactory.callbackCache("dict-text", cache);
        cacheFactory.callbackCache("dict-number-of-miss", missCache);
    }

    /**
     * 设置字典数据存储对象。对外提供在运行期间更改 DictStore 存储的方法。
     * 注意：
     * <ul>
     *     <li>1. 调用此接口后请发起 {@link RefreshDictEvent} 事件刷新字典数据，把系统的字典信息写入到新的 {@link DictStore} 存储对象中</li>
     *     <li>2. （推荐）或者在调用此接口前，请先调用 {@link DictUtil#forEachAllDict(Set, Consumer, Consumer, Consumer)} 此接口把所有的字典数据写入新的 {@link DictStore} 存储对象中</li>
     * </ul>
     *
     * @param store 字典数据存储对象
     * @since 1.4.11
     */
    public static void setDictStore(final DictStore store) {
        DictUtil.store = store;
    }

    /**
     * 循环获取所有 {@link DictProvider} 字典提供者提供的所有字典数据信息，把获取到的字典对象和字典值数据存入到 {@link DictStore} 存储对象中
     *
     * @param store 字典存储 {@link DictStore} 对象
     * @see DictRegistrar#forEachAllDict(Set, Consumer, Consumer, Consumer)
     * @since 1.5.0
     */
    public static void forEachAllDict(final Set<String> dictProviderClasses, final DictStore store) {
        if (dictRegistrar != null) {
            dictRegistrar.forEachAllDict(dictProviderClasses, store::store, store::storeSystemDict, store::store);
        }
    }

    /**
     * 循环获取所有 {@link DictProvider} 字典提供者提供的所有字典数据信息，把获取到的字典对象和字典值数据存入到 {@link DictStore} 存储对象中
     *
     * @param dictProviderClasses 只获取特定的 {@link DictProvider} 数据，会调用 {@link DictProvider#supportRefresh(Set)} 来判断
     * @param dictTypeConsumer    保存字典类型的方法
     * @param dictValueConsumer   保存字典值数据的方法
     * @see DictRegistrar#forEachAllDict(Set, Consumer, Consumer, Consumer)
     * @since 1.4.11
     */
    public static void forEachAllDict(final Set<String> dictProviderClasses, final Consumer<DictTypeVo> dictTypeConsumer, final Consumer<DictTypeVo> systemDictTypeConsumer, final Consumer<Iterator<DictValueVo>> dictValueConsumer) {
        if (dictRegistrar != null) {
            dictRegistrar.forEachAllDict(dictProviderClasses, dictTypeConsumer, systemDictTypeConsumer, dictValueConsumer);
        }
    }

    /**
     * 初始化 缓存键 前缀信息
     *
     * @param properties 配置信息
     * @since 1.4.7
     */
    public static void initPrefix(final DictPropertiesStorePrefixKey properties) {
        TYPE_PREFIX = properties.getTypePrefix();
        VALUE_PREFIX = properties.getValuePrefix();
        PARENT_PREFIX = properties.getParentPrefix();
        TYPE_SYSTEM_PREFIX = properties.getTypeSystemPrefix();
    }

    public static DictTypeVo getDictType(String type) {
        if (type == null || store == null) {
            return null;
        }
        return store.getDictType(type);
    }

    /**
     * 获取字典文本
     *
     * @param type  字典类型
     * @param value 字典值
     * @return 字典文本
     */
    public static String getDictText(String type, String value) {
        if (type == null || value == null || store == null) {
            return null;
        }
        if (cache == null || missCache == null) {
            return store.getDictText(type, value);
        }
        final String dictKey = dictKey(type, value);
        final String result = cache.getIfPresent(dictKey);
        if (result != null) {
            return result;
        }
        final AtomicInteger integer = missCache.get(dictKey, s -> new AtomicInteger(1));
        if (integer.get() > missNum) {
            return null;
        }

        final String dictText = store.getDictText(type, value);
        if (dictText == null) {
            // 未命中数据
            integer.incrementAndGet();
        } else {
            cache.put(dictKey, dictText);
        }
        return dictText;
    }

    /**
     * 获取字典父级值
     *
     * @param type  字典类型
     * @param value 字典值
     * @return 字典父级值
     * @since 1.4.6
     */
    public static String getDictParentValue(String type, String value) {
        if (type == null || value == null || store == null) {
            return null;
        }
        if (cache == null || missCache == null) {
            return store.getDictParentValue(type, value);
        }
        final String dictParentKey = dictParentKey(type, value);
        final String result = cache.getIfPresent(dictParentKey);
        if (result != null) {
            return result;
        }
        final AtomicInteger integer = missCache.get(dictParentKey, s -> new AtomicInteger(1));
        if (integer.get() > missNum) {
            return null;
        }

        final String parentValue = store.getDictParentValue(type, value);
        if (parentValue == null) {
            // 未命中数据
            integer.incrementAndGet();
        } else {
            cache.put(dictParentKey, parentValue);
        }
        return parentValue;
    }

    public static String dictKey(String type) {
        return TYPE_PREFIX + type;
    }

    public static String dictSystemKey(String type) {
        return TYPE_SYSTEM_PREFIX + type;
    }

    public static String dictKey(DictValueVo value) {
        return VALUE_PREFIX + value.getDictType() + ":" + value.getValue();
    }

    /**
     * 构建字典父级值缓存 KEY
     *
     * @param value 字典值对象
     * @return 字典父级值缓存 KEY
     * @since 1.4.6
     */
    public static String dictParentKey(DictValueVo value) {
        return PARENT_PREFIX + value.getDictType() + ":" + value.getValue();
    }

    public static String dictKey(String type, Object value) {
        return VALUE_PREFIX + type + ":" + value;
    }

    /**
     * 构建字典父级值缓存 KEY
     *
     * @param type  字典类型
     * @param value 字典值
     * @return 字典父级值缓存 KEY
     * @since 1.4.6
     */
    public static String dictParentKey(String type, Object value) {
        return PARENT_PREFIX + type + ":" + value;
    }

    /**
     * Redis Dict Value Hash Key
     *
     * @param value
     * @return key
     * @since 1.5.0
     */
    public static String dictKeyHash(DictValueVo value) {
        return VALUE_PREFIX + value.getDictType();
    }

    /**
     * 构建字典父级值缓存 KEY
     * Redis Dict Value Hash Key
     *
     * @param value 字典值对象
     * @return 字典父级值缓存 KEY
     * @since 1.5.0
     */
    public static String dictParentKeyHash(DictValueVo value) {
        return PARENT_PREFIX + value.getDictType();
    }

    /**
     * Redis Dict Value Hash Key
     *
     * @return key
     * @since 1.5.0
     */
    public static String dictKeyHash(String type) {
        return VALUE_PREFIX + type;
    }

    /**
     * 构建字典父级值缓存 KEY
     *
     * @param type  字典类型
     * @return 字典父级值缓存 KEY
     * @since 1.5.0
     */
    public static String dictParentKeyHash(String type) {
        return PARENT_PREFIX + type;
    }

    /**
     * 转换列表的对象中含有字典文本翻译注解的字段信息
     *
     * @param objects 对象集合
     * @param <T>     对象类型
     * @return 字典处理后的对象
     * @see DictUtil#transform(Object)
     * @since 1.4.9
     */
    public static <T> List<T> transform(List<T> objects) {
        final List<T> result = new ArrayList<>();
        for (final T object : objects) {
            result.add(transform(object));
        }
        return result;
    }

    /**
     * <p>手动对一个实体类对象进行字典处理。</p>
     * <p>为了与 Jackson 的序列化效果保持一致，返回的对象可能是通过 ASM 字节码技术动态生成的子类对象。</p>
     * <p>默认场景下 Jackson 的表现效果是不需要增加一个字典文本字段，例如：</p>
     * <p>字典值 userType 字段的字典文本为 userTypeText 字段。</p>
     * <p>如果传入的 object 中不存在 userTypeText 字段，则会使用 ASM 动态创建一个子类，然后在子类中实现这个 userTypeText 字段，并把字典文本存入此字段。</p>
     * <p>此方式经过 Spring SpEL 表达式验证，在 SpEL 表达式中可以取到 userTypeText 字段内容（请查看 DictUtilTransformTest 测试类）。</p>
     * <p>
     * 如果需要在 Java 代码中硬编码获取 userTypeText 字典文本信息，可采取以下方式：
     * <ul>
     *     <li>方式一：字段使用 Object 类型，然后注解设置 {@link DictText#replace()} 替换字段值</li>
     *     <li>方式二：明确硬编码所有字典文本字段</li>
     * </ul>
     * </p>
     *
     * @param object
     * @param <T>
     * @return 字典处理后的对象
     * @since 1.4.9
     */
    public static <T> T transform(T object) {
        final Class<?> objectClass = object.getClass();
        final Field[] fields = objectClass.getDeclaredFields();
        final Map<Field, DictText> cache = new HashMap<>();
        final Map<String, Object> newFields = new HashMap<>();
        for (final Field field : fields) {
            ReflectionUtils.makeAccessible(field);
            final DictTextJsonSerializerBasic jsonSerializer = DictTextJsonSerializer.getJsonSerializer(objectClass, field);
            if (jsonSerializer == null) {
                continue;
            }
            final Object serialize;
            try {
                serialize = jsonSerializer.serialize(object, field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("无法获取对象字段值", e);
            }
            if (jsonSerializer.isReplaceValue()) {
                setFieldValue(object, field, serialize);
            } else {
                final String outFieldName = jsonSerializer.getOutFieldName();
                if (!setDictText(object, fields, outFieldName, serialize)) {
                    // 设置字典文本值失败，这个类需要建立子类，然后在子类中加入此字段
                    // 动态生成 T 对象继承类，在继承类中添加 outFieldName 字段
                    newFields.put(outFieldName, serialize);
                }
            }
        }

        if (newFields.isEmpty()) {
            return object;
        }
        return transformChild(objectClass, fields, object, newFields);
    }

    /**
     * 设置子类对象的字典文本值
     *
     * @param objectClass 父类对象类型
     * @param fields      父类对象字段
     * @param object      父类对象
     * @param newFields   子类对象字段
     * @param <T>         父类对象类型
     * @return 子类对象实例
     * @since 1.4.9
     */
    private static <T> T transformChild(final Class<?> objectClass, final Field[] fields, final T object, final Map<String, Object> newFields) {
        final T newObject;
        final Class<?> newObjectClass;
        try {
            if (TRANSFORM_CACHE.containsKey(objectClass)) {
                // 从缓存中获取子类对象
                newObjectClass = TRANSFORM_CACHE.get(objectClass);
            } else {
                // 动态生成 T 对象继承类，在继承类中添加 outFieldName 字段
                newObjectClass = DictChildrenObjectGenerate.newClass(objectClass, newFields.keySet());
                TRANSFORM_CACHE.put(objectClass, newObjectClass);
            }
            newObject = (T) newObjectClass.getConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("转换字典文本失败，无法创建对象子类", e);
        }
        // 从原始对象复制属性值到新对象（子类对象）
        for (final Field field : fields) {
            try {
                field.set(newObject, field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("无法给子类设置字段值", e);
            }
        }
        // 设置新对象的字典文本值
        newFields.forEach((fieldName, value) -> {
            try {
                final Field field = newObjectClass.getDeclaredField(fieldName);
                ReflectionUtils.makeAccessible(field);
                field.set(newObject, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });

        return newObject;
    }

    /**
     * 通过反射设置字典文本值
     *
     * @param object    对象
     * @param fields    对象字段列表
     * @param fieldName 字典文本字段名称
     * @param value     字典文本（可能是字符串，也有可能是数组）
     * @return 是否设置成功
     * @since 1.4.9
     */
    private static boolean setDictText(final Object object, final Field[] fields, final String fieldName, final Object value) {
        for (final Field field : fields) {
            if (field.getName().equals(fieldName)) {
                return setFieldValue(object, field, value);
            }
        }
        return false;
    }

    /**
     * 通过反射设置字段值
     *
     * @param object 对象
     * @param field  字段
     * @param value  字段值
     * @return 是否设置成功
     * @since 1.4.9
     */
    private static boolean setFieldValue(final Object object, final Field field, final Object value) {
        ReflectionUtils.makeAccessible(field);
        try {
            field.set(object, value);
        } catch (IllegalAccessException e) {
            logger.warn("给 {}#{} 字段设置字典文本值失败", object.getClass().getName(), field.getName());
            logger.error("设置字典文本值失败", e);
            return false;
        }
        return true;
    }
}
