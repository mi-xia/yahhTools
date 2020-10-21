package com.yahhTool.cache.impl;

import com.yahhTool.cache.yahhCache;

import java.util.Map;
import java.util.concurrent.locks.StampedLock;

/**
 * @author 邹磊
 * @version 1.0
 * @description:
 * @date 2020/10/20 20:20
 */
public abstract class AbstractCache<K,V> implements yahhCache<K,V> {

    private static final long serialVersionUID = -3295830666555010122L;

    private final StampedLock lock = new StampedLock();

    protected Map<K,CacheObjcet<K,V>> cacheObjcetMap;

    /**
     * 失效时长
     */
    protected long timeout;

    /**
     * 缓存容量
     */
    protected int capcity;

    //----------------------------------------------------------- put star

    @Override
    public void put(K key, V object) {
        put(key, object, timeout);
    }

    @Override
    public void put(K key, V object, Long timeout) {
        final long stamp = lock.writeLock();

        try {
            putWithoutLock(key, object, timeout);
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    private void putWithoutLock(K key, V object, Long timeout){
        CacheObjcet<K,V> cacheObjcet = new CacheObjcet<>(key,object,timeout);

        if (ifFull()){
            pruneCache();
        }
        cacheObjcetMap.put(key,cacheObjcet);
    }

    //----------------------------------------------------------- put end


    //----------------------------------------------------------- 缓存清理相关

    @Override
    public boolean ifFull() {
        return (capcity > 0 ) && (cacheObjcetMap.size() >= capcity);
    }

    @Override
    public int prune() {

        final long stamp = lock.writeLock();

        try {
            return pruneCache();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    /**
     * 清缓存具体实现
     * @return
     */
    protected abstract int pruneCache();





}
