package com.sunny.consistenthash;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * 不带虚拟节点的一致性Hash算法 
 * 重点：1.如何造一个hash环，2.如何在哈希环上映射服务器节点，3.如何找到对应的节点
 */
public class ConsistentHashingWithoutVirtualNode {

    //待添加入Hash环的服务器列表  
    private static String[] servers = {"192.168.0.0:111", "192.168.0.1:111", "192.168.0.2:111", "192.168.0.3:111", "192.168.0.4:111"};

    //key表示服务器的hash值，value表示服务器  
    private static SortedMap<Integer, String> sortedMap = new TreeMap<Integer, String>();

    //程序初始化，将所有的服务器放入sortedMap中  
    static {
        for (int i = 0; i < servers.length; i++) {
            int hash = getHash(servers[i]);
            System.out.println("[" + servers[i] + "]加入集合中, 其Hash值为" + hash);
            sortedMap.put(hash, servers[i]);
        }
        System.out.println();
    }

    //得到应当路由到的结点  
    private static String getServer(String key) {
        //得到该key的hash值  
        int hash = getHash(key);
        //得到大于该Hash值的所有Map  
        SortedMap<Integer, String> subMap = sortedMap.tailMap(hash);
        if (subMap.isEmpty()) {
            //如果没有比该key的hash值大的，则从第一个node开始  
            Integer i = sortedMap.firstKey();
            //返回对应的服务器  
            return sortedMap.get(i);
        } else {
            //第一个Key就是顺时针过去离node最近的那个结点  
            Integer i = subMap.firstKey();
            //返回对应的服务器
            return subMap.get(i);
        }
    }

    /**
     * @Description: 使用FNV1_32_HASH算法计算服务器的Hash值, 这里不使用重写hashCode的方法，最终效果没区别
     * @Author: yangzhimeng
     * @Date: 2019/8/22 10:41
     */
    private static int getHash(String str) {
        final int p = 16777619;
        int hash = (int) 2166136261L;
        for (int i = 0; i < str.length(); i++) {
            hash = (hash ^ str.charAt(i)) * p;
        }
        hash += hash << 13;
        hash ^= hash >> 7;
        hash += hash << 3;
        hash ^= hash >> 17;
        hash += hash << 5;

        // 如果算出来的值为负数则取其绝对值  
        if (hash < 0) {
            hash = Math.abs(hash);
        }
        return hash;
    }

    public static void main(String[] args) {
        String[] keys = {"太阳", "月亮", "星星"};
        for (int i = 0; i < keys.length; i++) {
            System.out.println("[" + keys[i] + "]的hash值为" + getHash(keys[i]) + ", 被路由到结点[" + getServer(keys[i]) + "]");
        }
    }
}