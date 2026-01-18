package com.houkunlin.dict;

import com.github.benmanes.caffeine.cache.Cache;
import com.houkunlin.dict.annotation.DictText;
import com.houkunlin.dict.bean.DictType;
import com.houkunlin.dict.bean.DictValue;
import com.houkunlin.dict.bytecode.DictChildrenObjectGenerate;
import com.houkunlin.dict.cache.DictCacheFactory;
import com.houkunlin.dict.jackson.DictValueSerializer;
import com.houkunlin.dict.jackson.DictValueSerializerUtil;
import com.houkunlin.dict.notice.RefreshDictEvent;
import com.houkunlin.dict.properties.DictPropertiesStorePrefixKey;
import com.houkunlin.dict.store.DictStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 系统字典工具类
 * <p>
 * 该类是数据字典系统的核心工具类，提供了字典操作的各种实用方法，包括：
 * <ul>
 * <li>字典数据的存储和获取</li>
 * <li>字典文本的查询和缓存</li>
 * <li>字典键的构建和管理</li>
 * <li>字典对象的转换和处理</li>
 * <li>字典事件的处理和响应</li>
 * </ul>
 * 该类使用了缓存机制来提高字典查询性能，同时支持动态生成字典相关的类。
 * </p>
 *
 * @author HouKunLin
 * @since 1.0.0
 */
@SuppressWarnings("all")
public class DictUtil {
    private static final Logger logger = LoggerFactory.getLogger(DictUtil.class);
    /**
     * 手动处理含有字典注解的对象，对象可能需要动态生成字典，缓存对应的子类对象信息
     */
    private static final Map<Class<?>, Class<?>> TRANSFORM_CACHE = new ConcurrentHashMap<>();
    /**
     * 字典类型缓存键前缀
     * <p>用于构建字典类型在缓存中的键，格式为：dict:t:{type}
     */
    public static String TYPE_PREFIX = "dict:t:";
    /**
     * 系统字典类型缓存键前缀
     * <p>用于构建系统字典类型在缓存中的键，格式为：dict:t_system:{type}
     */
    public static String TYPE_SYSTEM_PREFIX = "dict:t_system:";
    /**
     * 字典值缓存键前缀
     * <p>用于构建字典值在缓存中的键，格式为：dict:v:{type}:{value}
     */
    public static String VALUE_PREFIX = "dict:v:";
    /**
     * 字典父级值缓存键前缀
     * <p>用于构建字典父级值在缓存中的键，格式为：dict:p:{type}:{value}
     *
     * @since 1.4.6
     */
    public static String PARENT_PREFIX = "dict:p:";
    /**
     * 字典注册器
     * <p>负责管理字典提供者和刷新字典数据
     */
    private static DictRegistrar dictRegistrar;

    /**
     * 字典存储
     * <p>负责字典数据的实际存储和读取操作
     */
    private static DictStore store;
    /**
     * 字典值缓存
     * <p>缓存字典值对应的文本信息，提高查询性能
     */
    private static Cache<String, String> cache;
    /**
     * 字典值未命中缓存
     * <p>缓存字典值的未命中次数，用于防止频繁查询不存在的字典值
     */
    private static Cache<String, AtomicInteger> missCache;
    /**
     * 字典值未命中阈值
     * <p>在有效期内同一个字典值未命中指定次数将快速返回，不再重复请求获取数据字典信息
     */
    private static int missNum = Integer.MAX_VALUE;

    /**
     * DictUtil 构造方法
     * <p>
     * 初始化 DictUtil 类的静态字段，包括字典注册器、字典存储和缓存。
     * 该构造方法由 Spring 容器调用，用于依赖注入。
     * </p>
     *
     * @param dictRegistrar 字典注册器，负责管理字典提供者和刷新字典数据
     * @param store 字典存储，负责字典数据的实际存储和读取操作
     * @param cacheFactory 缓存工厂，用于创建和管理字典缓存
     */
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
     * 设置字典数据存储对象
     * <p>
     * 对外提供在运行期间更改 DictStore 存储的方法。
     * 调用此方法后，系统将使用新的存储对象进行字典操作。
     * 注意：
     * <ul>
     *     <li>1. 调用此接口后请发起 {@link RefreshDictEvent} 事件刷新字典数据，把系统的字典信息写入到新的 {@link DictStore} 存储对象中</li>
     *     <li>2. （推荐）或者在调用此接口前，请先调用 {@link DictUtil#forEachAllDict(Set, Consumer, Consumer, Consumer)} 此接口把所有的字典数据写入新的 {@link DictStore} 存储对象中</li>
     * </ul>
     * </p>
     *
     * @param store 字典数据存储对象
     * @since 1.4.11
     */
    public static void setDictStore(final DictStore store) {
        DictUtil.store = store;
    }

    /**
     * 循环获取所有字典提供者提供的字典数据并存储
     * <p>
     * 该方法调用 DictRegistrar 的 forEachAllDict 方法，循环获取所有 DictProvider 提供的字典数据，
     * 并将获取到的字典类型和字典值数据存储到指定的 DictStore 对象中。
     * 该方法用于在切换字典存储实现时，将所有字典数据迁移到新的存储中。
     * </p>
     *
     * @param dictProviderClasses 需要刷新的字典提供商类限定名，null 表示刷新所有
     * @param store 字典存储对象，用于存储字典数据
     * @see DictRegistrar#forEachAllDict(Set, Consumer, Consumer, Consumer)
     * @since 1.5.0
     */
    public static void forEachAllDict(final Set<String> dictProviderClasses, final DictStore store) {
        if (dictRegistrar != null) {
            dictRegistrar.forEachAllDict(dictProviderClasses, store::store, store::storeSystemDict, store::store);
        }
    }

    /**
     * 循环获取所有字典提供者提供的字典数据并通过消费者处理
     * <p>
     * 该方法调用 DictRegistrar 的 forEachAllDict 方法，循环获取所有 DictProvider 提供的字典数据，
     * 并通过提供的消费者函数处理获取到的字典类型和字典值数据。
     * 该方法提供了更灵活的字典数据处理方式，可以根据需要自定义处理逻辑。
     * </p>
     *
     * @param dictProviderClasses 需要刷新的字典提供商类限定名，null 表示刷新所有
     * @param dictTypeConsumer    保存普通字典类型的消费者函数
     * @param systemDictTypeConsumer 保存系统字典类型的消费者函数
     * @param dictValueConsumer   保存字典值数据的消费者函数
     * @see DictRegistrar#forEachAllDict(Set, Consumer, Consumer, Consumer)
     * @since 1.4.11
     */
    public static void forEachAllDict(final Set<String> dictProviderClasses, final Consumer<DictType> dictTypeConsumer, final Consumer<DictType> systemDictTypeConsumer, final Consumer<Iterator<DictValue>> dictValueConsumer) {
        if (dictRegistrar != null) {
            dictRegistrar.forEachAllDict(dictProviderClasses, dictTypeConsumer, systemDictTypeConsumer, dictValueConsumer);
        }
    }

    /**
     * 初始化缓存键前缀信息
     * <p>
     * 该方法根据配置信息初始化字典类型、系统字典类型、字典值和父级字典值的缓存键前缀。
     * 这些前缀用于构建字典数据在缓存中的键，确保不同类型的字典数据不会冲突。
     * </p>
     *
     * @param properties 缓存键前缀配置信息
     * @since 1.4.7
     */
    public static void initPrefix(final DictPropertiesStorePrefixKey properties) {
        TYPE_PREFIX = properties.getTypePrefix();
        VALUE_PREFIX = properties.getValuePrefix();
        PARENT_PREFIX = properties.getParentPrefix();
        TYPE_SYSTEM_PREFIX = properties.getTypeSystemPrefix();
    }

    /**
     * 通过字典类型代码获取一个字典类型对象
     * <p>
     * 该方法通过字典类型代码从字典存储中获取对应的字典类型对象。
     * 首先检查类型代码是否为 null，以及字典存储是否初始化，
     * 然后调用 store.getDictType 方法获取字典类型对象。
     * </p>
     *
     * @param type 字典类型代码
     * @return 字典类型对象，如果类型代码为 null 或存储未初始化则返回 null
     */
    public static DictType getDictType(String type) {
        if (type == null || store == null) {
            return null;
        }
        return store.getDictType(type);
    }

    /**
     * 获取字典文本
     * <p>
     * 该方法通过字典类型代码和字典值获取对应的字典文本。
     * 实现逻辑如下：
     * 1. 检查类型和值是否为 null，以及字典存储是否初始化
     * 2. 如果缓存未初始化，直接从存储中获取
     * 3. 否则，尝试从缓存中获取字典文本
     * 4. 如果缓存未命中，检查未命中次数是否超过阈值
     * 5. 如果未超过阈值，从存储中获取并更新缓存
     * 6. 返回获取到的字典文本或 null
     * </p>
     *
     * @param type  字典类型代码
     * @param value 字典值
     * @return 字典文本，如果类型或值为 null、存储未初始化或未找到则返回 null
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
     * <p>
     * 该方法通过字典类型代码和字典值获取对应的父级字典值。
     * 实现逻辑与 getDictText 方法类似，但获取的是父级值而不是文本：
     * 1. 检查类型和值是否为 null，以及字典存储是否初始化
     * 2. 如果缓存未初始化，直接从存储中获取
     * 3. 否则，尝试从缓存中获取父级值
     * 4. 如果缓存未命中，检查未命中次数是否超过阈值
     * 5. 如果未超过阈值，从存储中获取并更新缓存
     * 6. 返回获取到的父级值或 null
     * </p>
     *
     * @param type  字典类型代码
     * @param value 字典值
     * @return 字典父级值，如果类型或值为 null、存储未初始化或未找到则返回 null
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

    /**
     * 构建字典类型缓存键
     * <p>
     * 该方法使用 TYPE_PREFIX 前缀和字典类型代码构建字典类型在缓存中的键。
     * 格式为：dict:t:{type}
     * </p>
     *
     * @param type 字典类型代码
     * @return 字典类型缓存键
     */
    public static String dictKey(String type) {
        return TYPE_PREFIX + type;
    }

    /**
     * 构建系统字典类型缓存键
     * <p>
     * 该方法使用 TYPE_SYSTEM_PREFIX 前缀和字典类型代码构建系统字典类型在缓存中的键。
     * 格式为：dict:t_system:{type}
     * </p>
     *
     * @param type 字典类型代码
     * @return 系统字典类型缓存键
     */
    public static String dictSystemKey(String type) {
        return TYPE_SYSTEM_PREFIX + type;
    }

    /**
     * 构建字典值缓存键
     * <p>
     * 该方法使用 VALUE_PREFIX 前缀、字典类型代码和字典值构建字典值在缓存中的键。
     * 格式为：dict:v:{type}:{value}
     * </p>
     *
     * @param value 字典值对象
     * @return 字典值缓存键
     */
    public static String dictKey(DictValue value) {
        return VALUE_PREFIX + value.getDictType() + ":" + value.getValue();
    }

    /**
     * 构建字典父级值缓存 KEY
     * <p>
     * 该方法使用 PARENT_PREFIX 前缀、字典类型代码和字典值构建字典父级值在缓存中的键。
     * 格式为：dict:p:{type}:{value}
     * </p>
     *
     * @param value 字典值对象
     * @return 字典父级值缓存 KEY
     * @since 1.4.6
     */
    public static String dictParentKey(DictValue value) {
        return PARENT_PREFIX + value.getDictType() + ":" + value.getValue();
    }

    /**
     * 构建字典值缓存键
     * <p>
     * 该方法使用 VALUE_PREFIX 前缀、字典类型代码和字典值构建字典值在缓存中的键。
     * 格式为：dict:v:{type}:{value}
     * </p>
     *
     * @param type 字典类型代码
     * @param value 字典值
     * @return 字典值缓存键
     */
    public static String dictKey(String type, Object value) {
        return VALUE_PREFIX + type + ":" + value;
    }

    /**
     * 构建字典父级值缓存 KEY
     * <p>
     * 该方法使用 PARENT_PREFIX 前缀、字典类型代码和字典值构建字典父级值在缓存中的键。
     * 格式为：dict:p:{type}:{value}
     * </p>
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
     * 构建字典值哈希缓存键
     * <p>
     * 该方法使用 VALUE_PREFIX 前缀和字典类型代码构建字典值哈希在缓存中的键。
     * 格式为：dict:v:{type}
     * 该键用于 Redis 哈希结构，存储同一字典类型下的所有字典值。
     * </p>
     *
     * @param value 字典值对象
     * @return 字典值哈希缓存键
     * @since 1.5.0
     */
    public static String dictKeyHash(DictValue value) {
        return VALUE_PREFIX + value.getDictType();
    }

    /**
     * 构建字典父级值哈希缓存 KEY
     * <p>
     * 该方法使用 PARENT_PREFIX 前缀和字典类型代码构建字典父级值哈希在缓存中的键。
     * 格式为：dict:p:{type}
     * 该键用于 Redis 哈希结构，存储同一字典类型下的所有字典父级值。
     * </p>
     *
     * @param value 字典值对象
     * @return 字典父级值哈希缓存 KEY
     * @since 1.5.0
     */
    public static String dictParentKeyHash(DictValue value) {
        return PARENT_PREFIX + value.getDictType();
    }

    /**
     * 构建字典值哈希缓存键
     * <p>
     * 该方法使用 VALUE_PREFIX 前缀和字典类型代码构建字典值哈希在缓存中的键。
     * 格式为：dict:v:{type}
     * 该键用于 Redis 哈希结构，存储同一字典类型下的所有字典值。
     * </p>
     *
     * @param type 字典类型
     * @return 字典值哈希缓存键
     * @since 1.5.0
     */
    public static String dictKeyHash(String type) {
        return VALUE_PREFIX + type;
    }

    /**
     * 构建字典父级值哈希缓存 KEY
     * <p>
     * 该方法使用 PARENT_PREFIX 前缀和字典类型代码构建字典父级值哈希在缓存中的键。
     * 格式为：dict:p:{type}
     * 该键用于 Redis 哈希结构，存储同一字典类型下的所有字典父级值。
     * </p>
     *
     * @param type  字典类型
     * @return 字典父级值哈希缓存 KEY
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
     * </p>
     * <ul>
     *     <li>方式一：字段使用 Object 类型，然后注解设置 {@link DictText#replace()} 替换字段值</li>
     *     <li>方式二：明确硬编码所有字典文本字段</li>
     * </ul>
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
            final DictValueSerializer jsonSerializer = DictValueSerializerUtil.getDictTextValueSerializer(objectClass, field);
            if (jsonSerializer == null) {
                continue;
            }
            final Object serialize;
            try {
                serialize = jsonSerializer.transform(object, field.get(object));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("无法获取对象字段值", e);
            }
            if (jsonSerializer.isUseReplaceFieldValue()) {
                setFieldValue(object, field, serialize);
            } else {
                final String outFieldName = jsonSerializer.getOutputFieldName();
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
