package com.houkunlin.system.dic.starter;

import com.houkunlin.system.dic.starter.provider.DicProvider;
import com.houkunlin.system.dic.starter.provider.SystemDicProvider;
import com.houkunlin.system.dic.starter.bean.DicTypeVo;
import com.houkunlin.system.dic.starter.bean.DicValueVo;
import com.houkunlin.system.dic.starter.notice.RefreshDicEvent;
import com.houkunlin.system.dic.starter.store.DicStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * 字典注册，把字典发送到缓存中
 *
 * @author HouKunLin
 */
@Configuration
public class DicRegistrar implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(DicRegistrar.class);
    private final List<DicProvider> providers;
    private final DicStore store;

    public DicRegistrar(final List<DicProvider> providers, final DicStore store) {
        this.providers = providers;
        this.store = store;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        for (final DicProvider provider : providers) {
            if (provider instanceof SystemDicProvider) {
                // 系统字典特殊处理
                final Collection<DicTypeVo> dicTypes = provider.getDicTypes();
                for (final DicTypeVo dicType : dicTypes) {
                    // 系统字典直接写入完整的对象，因为在给前端做字典选择的时候需要完整的列表
                    storeDic(dicType);
                    // 同时系统字典的字典值列表也写入缓存
                    storeDic(dicType.getChildren().iterator());
                }
            } else {
                storeDic(provider.iterator());
            }
        }
    }

    /**
     * 处理系统内部发起的刷新数据字典事件
     */
    @EventListener
    public void refreshDic(RefreshDicEvent event) throws Exception {
        logger.info("[start] 应用内部通知刷新字典事件。事件内容：{}", event.getSource());
        afterPropertiesSet();
        logger.info("[finish] 应用内部通知刷新字典事件");
    }

    private void storeDic(Iterator<DicValueVo<? extends Serializable>> iterator) {
        store.store(iterator);
    }

    private void storeDic(DicTypeVo dicType) {
        store.store(dicType);
    }
}
