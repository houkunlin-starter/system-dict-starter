package com.houkunlin.system.dict.starter.common.provider;

import com.houkunlin.system.dict.starter.bean.DictValueVo;
import com.houkunlin.system.dict.starter.provider.DictProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 假设有一个字典的数量很多，不方便一次性从数据库读取出来
 *
 * @author HouKunLin
 */
@Component
public class UserDictProvider implements DictProvider {
    public static final String DICT_TYPE = "dictUserId";

    @Override
    public boolean isStoreDictType() {
        /** 可能字典的数量非常大，不适合存入到缓存对象中 {@link com.houkunlin.system.dict.starter.store.DictStore} */
        return false;
    }

    @Override
    public Iterator<DictValueVo> dictValueIterator() {
        return new Iterator<DictValueVo>() {
            private int page = 0;
            private int index = 0;
            private List<UserEntity> currentPage = null;

            @Override
            public boolean hasNext() {
                if (currentPage == null || index >= currentPage.size()) {
                    index = 0;
                    currentPage = getUserPage(page++);
                }
                return currentPage != null && index < currentPage.size();
            }

            @Override
            public DictValueVo next() {
                if (index >= currentPage.size()) {
                    throw new NoSuchElementException("没有更多的字典值对象");
                }
                final UserEntity userEntity = currentPage.get(index++);
                return DictValueVo.builder()
                    .dictType(DICT_TYPE)
                    .value(userEntity.getId())
                    .title(userEntity.getName())
                    .build();
            }
        };
    }

    public List<UserEntity> getUserPage(int page) {
        if (page > 5) {
            // 已经加载完所有的用户信息
            return Collections.emptyList();
        }
        final List<UserEntity> entities = new ArrayList<>();
        for (int i = page * 10; i < page * 10 + 10; i++) {
            entities.add(new UserEntity(i, "用户姓名 - " + i));
        }
        return entities;
    }

    @Data
    @AllArgsConstructor
    static class UserEntity {
        private int id;
        private String name;
    }
}
