package com.yahhTool.cache;

import java.io.Serializable;
import java.security.Key;

/**
 * @author 邹磊
 * @version 1.0
 * @description:  缓存抽象接口
 * @date 2020/10/20 19:42
 */
public interface yahhCache<K,V> extends Iterable<V>, Serializable {

    /**
     * @return 缓存容量，0代表无限制
     */
    int capcity();

    /**
     * @return 单位毫秒，缓存失效时间， 0代表无限制
     */
    int timeout();

    /**
     * 放入缓存，使用默认失效时长
     * @param key
     * @param object
     * @return
     * @see yahhCache#put(Object, Object, Long)
     */
    boolean put(K key, V object);

    /**
     * 自定义失效时长
     * @param key
     * @param object
     * @param timeout 失效时长
     * @return
     */
    boolean put(K key, V object, Long timeout);


    /**
     * 缓存中获取object，不存在或已过期返回 null
     * 命中成功后重置失效时间
     * @param key
     * @return
     * @see yahhCache#get(Object, Boolean) 
     */
    V get(K key);


    /**
     * 缓存中获取object，不存在或已过期返回 null
     * 命中成功后 根据 isUpdateLastAccess 重置失效时间
     * @param key
     * @param isUpdateLastAccess = true 重置失效时间
     * @return
     */
    V get(K key, Boolean isUpdateLastAccess);

    /**
     * 缓存中移除duixiang
     * @param key
     * @return
     */
    boolean remove(K key);

    /**
     * 清空缓存
     * @return
     */
    boolean clear();

    /**
     * 缓存对象的数量
     * @return
     */
    int size();

    /**
     * 检查缓存是否为空
     */
    boolean isEmpty();

    /**
     * 检查key是否存在
     */
    boolean containsKey(K key);

    /**
     * 清理过期对象
     * @return
     */
    int prune();


}
