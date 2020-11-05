package com.yahhTool.core.map;

import com.sun.scenario.effect.impl.prism.PrImage;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author 邹磊
 * @version 1.0
 * @description: 基于linkedHashMap实现一个LRU算法的固定大小的hashmap
 * @date 2020/10/21 20:29
 */
public class FixedLinkedHashMap<K,V> extends LinkedHashMap<K,V> {

    private static final long serialVersionUID = -460082107602986108L;

    /**
     * 容量
     */
    private int capacity;

    /**
     * 构造函数
     * @param capacity
     */
    public FixedLinkedHashMap(int capacity){
        super(capacity + 1, 1.0f, true);

        this.capacity = capacity;
    }


    /**
     * 连表元素大于容量，移除最久未使用的元素
     * @param eldest
     * @return
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > this.capacity;
    }

    /**
     *
     * @return 容量
     */
    public int getCapacity() {
        return this.capacity;
    }


    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
