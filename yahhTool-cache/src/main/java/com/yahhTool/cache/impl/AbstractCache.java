package com.yahhTool.cache.impl;

import com.yahhTool.cache.yahhCache;

import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.concurrent.locks.StampedLock;
import java.util.function.Consumer;

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

    /**
     * 命中数
     */
    protected int hitCount;
    /**
     * 丢失数
     */
    protected int missCount;

    /**
     * 每个对象是否有单独的失效时长
     */
    protected boolean existCustomTimeout;

    /************************************************************** put star ****/

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

        if (timeout != 0){
            existCustomTimeout = true;
        }

        if (ifFull()){
            pruneCache();
        }
        cacheObjcetMap.put(key,cacheObjcet);
    }

    /************************************************************** put end ****/


    /************************************************************** get start ****/

    @Override
    public V get(K key) {
        return get(key,true);
    }

    @Override
    public V get(K key, Boolean isUpdateLastAccess) {

        final long stamp = lock.readLock();

        try {
            final CacheObjcet<K,V> cacheObjcet = cacheObjcetMap.get(key);

            if (null == cacheObjcet){
                this.missCount++;
                return null;
            }
            if (cacheObjcet.isExpired()){
                this.missCount++;
                remove(key,true);
                return null;
            } else {
                hitCount++;
                return cacheObjcet.getObj(isUpdateLastAccess);
            }
        } finally {
            lock.unlockRead(stamp);
        }
    }

    @Override
    public boolean containsKey(K key) {

        final long stamp = lock.readLock();

        try {
            final CacheObjcet cacheObjcet = cacheObjcetMap.get(key);
            if (null == cacheObjcet){
                // 未命中
                return false;
            }
            if (!cacheObjcet.isExpired()){
                // 命中
                return true;
            }
        } finally {
            lock.unlockRead(stamp);
        }

        // 已过期的key需要删除
        remove(key,true);
        return false;
    }

    /************************************************************** get end ****/


    /************************************************************** 缓存清理相关 ****/

    @Override
    public void clear() {
        final long stamp = lock.writeLock();
        try {
            cacheObjcetMap.clear();
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    @Override
    public void remove(K key, boolean withMissCount) {
        final long stamp = lock.writeLock();
        CacheObjcet<K,V> cacheObjcet;
        try {
            cacheObjcet = removeWithoutLock(key, withMissCount);
        } finally {
            lock.unlockWrite(stamp);
        }

        if (null != cacheObjcet){
            removeRollback(key,cacheObjcet.obj);
        }

    }

    protected CacheObjcet<K,V> removeWithoutLock(K key, boolean withMissCount){

        final CacheObjcet<K,V> cacheObjcet = cacheObjcetMap.remove(key);
        if (withMissCount){
            this.missCount++;
        }

        return cacheObjcet;
    }

    private void removeRollback(K key, V cacheObjcet){

    }

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

    /**
     * 是否清理过期对象
     * @return
     */
    protected boolean isPruneExpiredActive() {
        return (timeout != 0) || existCustomTimeout;
    }


    /************************************************************** other ****/

    /**
     * @return 命中数
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * @return 丢失数
     */
    public int getMissCount() {
        return missCount;
    }

    @Override
    public int capcity() { return capcity; }

    @Override
    public long timeout() { return timeout; }

    @Override
    public int size() { return cacheObjcetMap.size(); }

    @Override
    public boolean isEmpty() { return cacheObjcetMap.isEmpty(); }

    @Override
    public String toString() {
        return this.cacheObjcetMap.toString();
    }

    /************************************************************** iterator相关 ****/

    @Override
    public Iterator<V> iterator() {
        return null;
    }

    @Override
    public void forEach(Consumer<? super V> action) {

    }

    @Override
    public Spliterator<V> spliterator() {
        return null;
    }

}
