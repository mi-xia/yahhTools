package com.yahhTool.cache.impl;

import java.io.Serializable;

/**
 * @author 邹磊
 * @version 1.0
 * @description:
 * @date 2020/10/21 10:42
 */
public class CacheObjcet<K,V> implements Serializable {

    private static final long serialVersionUID = -3108773886437360865L;

    protected final K key;
    protected final V obj;

    /**
     * 上次命中时间
     */
    private long lastAccsess;

    /**
     * 失效时长，单位毫秒  0 代表无限制
     */
    private long ttl;

    /**
     * 命中次数
     */
    protected long accessCount;

    protected CacheObjcet(K key, V obj, long ttl) {
        this.key = key;
        this.obj = obj;
        this.lastAccsess = System.currentTimeMillis();
        this.ttl = ttl;
    }


    /**
     * 判断是否过期
     * @return true 过期
     */
    boolean isExpired() {

        if (ttl > 0){
            final long expiredTime = this.lastAccsess + this.ttl;
            // expired > 0 避免long类型溢出为负数
            return expiredTime > 0 && expiredTime < System.currentTimeMillis();
        }

        return false;
    }


    /**
     * 获取obj并更新最后命中时间
     */
    V getObj(boolean isUpdateLastAccess){

        if (isUpdateLastAccess){
            this.lastAccsess = System.currentTimeMillis();
        }
        this.accessCount ++;
        return this.obj;
    }

    V getObj(){ return this.obj; }

    /**
     * 获取key
     * @return
     */
    K getKey(){ return this.key; }

    @Override
    public String toString() {
        return "CacheObjcet{" +
                "key=" + key +
                ", obj=" + obj +
                ", lastAccsess=" + lastAccsess +
                ", ttl=" + ttl +
                ", accessCount=" + accessCount +
                '}';
    }
}
