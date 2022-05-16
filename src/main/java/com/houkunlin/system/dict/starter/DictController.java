package com.houkunlin.system.dict.starter;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.houkunlin.system.dict.starter.bean.DictTypeVo;
import com.houkunlin.system.dict.starter.bean.DictValueVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

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
     * @param tree v1.4.9 以字典文本值的代码长度来截取成树结构数据，此值传入分隔长度（树形结构的KEY长度，按照这个长度去分隔字典值，由字典值拿到父级字典值）
     * @return 字典类型对象
     */
    @ApiOperation("获取字典类型[PATH]")
    @ApiImplicitParam(name = "dict", value = "字典类型代码", required = true, paramType = "path", dataTypeClass = String.class)
    @GetMapping("{dict}")
    public DictTypeVo dictType(@PathVariable final String dict, @RequestParam(required = false) final Integer tree) {
        return transform(dict, tree);
    }

    /**
     * 获取字典类型信息
     *
     * @param dict 字典类型代码
     * @param tree v1.4.9 以字典文本值的代码长度来截取成树结构数据，此值传入分隔长度（树形结构的KEY长度，按照这个长度去分隔字典值，由字典值拿到父级字典值）
     * @return 字典类型对象
     */
    @ApiOperation("获取字典类型[QUERY]")
    @ApiImplicitParam(name = "dict", value = "字典类型代码", required = true, paramType = "query", dataTypeClass = String.class)
    @GetMapping(params = {"dict"})
    public DictTypeVo dictTypeQuery(final String dict, @RequestParam(required = false) final Integer tree) {
        return transform(dict, tree);
    }

    /**
     * 转换字典信息。
     * <ol>
     *     <li>10   分类1</li>
     *     <li>1010 分类1-1</li>
     *     <li>1011 分类1-2</li>
     *     <li>20   分类2</li>
     *     <li>2010 分类2-1</li>
     *     <li>2011 分类2-2</li>
     * </ol>
     *
     * @param dict 字典类型代码
     * @param tree 树形结构的KEY长度，按照这个长度去分隔字典值，由字典值拿到父级字典值
     * @return 字典信息
     * @since 1.4.9
     */
    private DictTypeVo transform(final String dict, final Integer tree) {
        final DictTypeVo dictType = DictUtil.getDictType(dict);
        if (tree == null || tree <= 0) {
            return dictType;
        }
        final List<DictValueVo> children = dictType.getChildren();

        final ListMultimap<String, DictValueVo> multimap = handlerTreeDatasource(children,
            "",
            this::getKey,
            vo -> {
                final Object object = vo.getValue();
                if (object instanceof String) {
                    final String value = (String) object;
                    final int length = value.length();
                    final int num = length / tree;
                    final int index = num == 0 ? 0 : num - 1;
                    return value.substring(0, index * tree);
                }
                return "";
            },
            DictValueVo::getChildren,
            DictValueVo::setChildren
        );
        dictType.setChildren(new ArrayList<>(multimap.values()));
        return dictType;
    }

    private String getKey(final DictValueVo vo) {
        final Object object = vo.getValue();
        if (object instanceof String) {
            return (String) object;
        }
        return "";
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

    /**
     * 自动整理、归类、收集树形结构数据，使 multimap 的 key 只保留所有数据中的顶级数据。
     *
     * @param list             原始树形结构单节点数据
     * @param defaultParentKey 默认的顶级父级ID
     * @param getId            从 Entity 获取当前ID的方法
     * @param getParentId      从 Entity 获取父级ID的方法
     * @param getChildren      从 Entity 获取子级列表的方法
     * @param setChildren      给 Entity 设置子级列表的方法
     * @param <E>              Entity 实体类对象
     * @param <K>              当前对象ID 与 父级对象ID 关联关系的类型，比如：String 、 Integer 类型数据来做关联
     * @return <p>
     * 返回处理后的树形结构信息，返回数据的结构为：<br/>
     * 键-值：list中所有顶级父级ID -> 该顶级父级ID的子级列表，一般情况下顶级父级ID为defaultParentKey
     * </p>
     * @since 1.4.9
     */
    public static <E, K> ListMultimap<K, E> handlerTreeDatasource(@NonNull List<? extends E> list,
                                                                  K defaultParentKey,
                                                                  Function<E, @Nullable K> getId,
                                                                  Function<E, @Nullable K> getParentId,
                                                                  Function<E, @Nullable List<E>> getChildren,
                                                                  BiConsumer<E, List<E>> setChildren) {
        ListMultimap<K, E> multimap = ArrayListMultimap.create();
        // 获取父级ID，因为顶级id可能为null，因此需要提供一个默认的父级ID处理方式
        UnaryOperator<K> getParentKey = key -> {
            if (key == null) {
                return defaultParentKey;
            }
            return key;
        };
        // 初步合并树形结构，把所有数据中，同一个父级的数据归类到一个List中存储，存储方式（键-值）：父级ID -> 子级列表
        list.forEach(entity -> multimap.put(getParentKey.apply(getParentId.apply(entity)), entity));
        // 这里必须把所有的key取出来，因为在 handlerTreeChildren 中涉及到 multimap 对象的 key 移除
        HashSet<K> strings = new HashSet<>(multimap.keySet());
        strings.forEach(key -> {
            // 获得父节点 key 的子节点列表
            if (multimap.containsKey(key)) {
                multimap.get(key)
                    .forEach(entity -> handlerTreeChildren(multimap, entity, getId, getParentId, getChildren, setChildren));
            }
        });
        return multimap;
    }

    /**
     * <p>处理树形结构数据的子节点和孙子节点信息。</p>
     * <p>从 multimap 中移除当前节点的子级数据，</p>
     * <p>把 multimap 中当前节点的子级信息存到当前节点对象中，</p>
     * <p>继续处理当前节点的子节点的子节点数据，也就是继续处理当前节点的孙子节点数据。</p>
     *
     * @param multimap    完整的树形结构数据信息
     * @param parent      需要处理的父节点信息
     * @param getId       从 Entity 获取当前ID的方法
     * @param getParentId 从 Entity 获取父级ID的方法
     * @param getChildren 从 Entity 获取子级列表的方法
     * @param setChildren 给 Entity 设置子级列表的方法
     * @param <E>         Entity 实体类对象
     * @param <K>         当前对象ID 与 父级对象ID 关联关系的类型，比如：String 、 Integer 类型数据来做关联
     * @since 1.4.9
     */
    private static <E, K> void handlerTreeChildren(@NonNull ListMultimap<K, E> multimap,
                                                   E parent,
                                                   Function<E, @Nullable K> getId,
                                                   Function<E, @Nullable K> getParentId,
                                                   Function<E, @Nullable List<E>> getChildren,
                                                   BiConsumer<E, List<E>> setChildren) {
        if (getChildren.apply(parent) != null) {
            // 当前对象的子级列表不为null，表示已经处理过，不需要再次处理
            return;
        }
        K id = getId.apply(parent);
        K pid = getParentId.apply(parent);
        if (id != null && id.equals(pid)) {
            // 不允许当前节点的父级是自己，当前节点的父级是自己将会造成死循环
            return;
        }
        // 从 multimap 中移除当前节点的子级数据
        List<E> entities = multimap.removeAll(id);
        // 把 multimap 中当前节点的子级信息存到当前节点对象中
        if (entities.isEmpty()) {
            setChildren.accept(parent, null);
        } else {
            setChildren.accept(parent, entities);
        }
        // 继续处理当前节点子级节点的子级数据，也就是处理当前节点的孙子信息
        entities.forEach(entity -> handlerTreeChildren(multimap, entity, getId, getParentId, getChildren, setChildren));
    }
}
