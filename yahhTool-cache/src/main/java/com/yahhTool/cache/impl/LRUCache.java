package com.yahhTool.cache.impl;

import com.yahhTool.core.map.FixedLinkedHashMap;

import java.util.Iterator;


/**
 * @author 邹磊
 * @version 1.0
 * @description:
 * @date 2020/10/21 20:24
 */
public class LRUCache<K,V> extends AbstractCache<K,V> {

    private static final long serialVersionUID = -2460052018128382089L;

    public LRUCache(int capcity, long timeout){

        if (Integer.MAX_VALUE == capcity) {
            capcity -= 1;
        }

        this.capcity = capcity;
        this.timeout = timeout;

        cacheObjcetMap = new FixedLinkedHashMap<>(capcity);

    }


    /**
     * 清理过时的对象
     * @return
     */
    @Override
    protected int pruneCache() {

        if (!isPruneExpiredActive()){
            return 0;
        }

        int count = 0;
        Iterator<CacheObjcet<K,V>> values = cacheObjcetMap.values().iterator();
        CacheObjcet<K,V> cacheObjcet;
        while (values.hasNext()) {
            cacheObjcet = values.next();
            if (cacheObjcet.isExpired()) {
                values.remove();
                count++;
            }
        }
        return count;
    }


}
