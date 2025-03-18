package com.wufeiqun.zeus.common.utils;

import cn.hutool.core.collection.CollectionUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author wufeiqun
 * @date 2022-07-18
 */
public class DifferenceUtil {
    public static  <T> List<T> differenceObject(List<T> subtrahend, List<T> minuend) {
        Set<T> subtrahendSet = new HashSet<>(subtrahend);
        Set<T> minuendSet = new HashSet<>(minuend);
        subtrahendSet.removeAll(minuendSet);
        return new ArrayList<>(subtrahendSet);
    }

    /**
     * 对比两个集合是否相等, 相等返回True, 不相等返回False
     */
    public static <T> boolean compareSet(Set<T> set1, Set<T> set2){
        // 比较两个空集合, 没意义
        if(CollectionUtil.isEmpty(set1) || CollectionUtil.isEmpty(set2)){
            return false;
        }

        if(set1.size() != set2.size()){
            return false;
        }

        return set1.containsAll(set2);
    }
}
