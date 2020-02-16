package com.zm.project_template.components.business;

import com.google.common.cache.Cache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Describle This Class Is GuavaCache
 * @Author ZengMin
 * @Date 2019/7/17 17:02
 */
@Component
public class GuavaCacheUtil {

    @Autowired
    Cache cache;

    public Object get(String key) {
        return StringUtils.isNotEmpty(key) ? cache.getIfPresent(key) : null;
    }

    @Async
    public void put(String key, Object value) {
        if (StringUtils.isNotEmpty(key) && value != null) {
            cache.put(key, value);
        }
    }

    @Async
    public void remove(String key) {
        if (StringUtils.isNotEmpty(key)) {
            cache.invalidate(key);
        }
    }

    @Async
    public void remove(List<String> keys) {
        if (keys != null && keys.size() > 0) {
            cache.invalidateAll(keys);
        }
    }

    public void resetCache() {
        cache.invalidateAll();
    }

}
