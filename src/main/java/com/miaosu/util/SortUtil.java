package com.miaosu.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 权重排序工具类
 *
 * @author cq
 */
public class SortUtil {
    
    private SortUtil() {
        
    }

    private static final Random RANDOM = new Random();

    /**
     * * 利用百分比匹配的方式，将所有可用的供货商按照权重比进行排序，<br/>
     * 使排在第一位的供货商在list中的下标出现的几率和其权重成正比。
     * 
     * @param weightList
     *            权重信息集合
     * @return int[] 顺序索引数组
     */
    public static int[] sortWeightIndex(List<Integer> weightList) {
        int size = weightList.size();
        int[] sortedIndex = new int[size];
        // 初始化为-1
        initArray(sortedIndex);
        List<Integer> asList = new ArrayList<Integer>(weightList);

        for (int i = 0; i < size; i++) {
            int selected = select(asList);
            if (selected == -1) {
                // 没有选择到容器，将未排序的容器加入到列表中
                for (int l = 0; l < size; l++) {
                    if (notInArray(sortedIndex, l)) {
                        selected = l;
                        break;
                    }
                }
            } else {
                asList.remove(selected);
                asList.add(selected, 0);
            }
            sortedIndex[i] = selected;

        }

        return sortedIndex;
    }
    
    public static int select(List<Integer> weights) {
        // 总个数
        int length = weights.size(); 
        // 总权重
        int totalWeight = 0; 
        // 权重是否都一样
        boolean sameWeight = true; 
        int i = 0;
        int lastWeight = 0;
        for (Integer weight : weights) {
            // 累计总权重
            totalWeight += weight; 
            if (sameWeight && i > 0 && weight != lastWeight) {
                // 计算所有权重是否一样
                sameWeight = false; 
            }
            lastWeight = weight;
            i++;
        }
        if (totalWeight > 0 && !sameWeight) {
            // 如果权重不相同且权重大于0则按总权重数随机
            int offset = RANDOM.nextInt(totalWeight);
            // 并确定随机值落在哪个片断上
            int j = 0;
            for (Integer weight : weights) {
                offset -= weight;
                if (offset < 0) {
                    return j;
                }
                j++;
            }
        }
        // 如果权重相同或权重为0则均等随机
        return RANDOM.nextInt(length);
    }

    /**
     * 判断val是不是在指定数组当中，在其中证明该索引已经被选择，否则该索引尚未被选择
     * 
     * @param arr
     *            已分配索引数组
     * @param val
     *            待确认索引
     * @return true-->待确认索引尚未选择 false-->待确认索引已经被选择到
     */
    private static boolean notInArray(int arr[], int val) {
        for (int i = 0; i < arr.length; i++) {
            if (val == arr[i]) {
                return false;
            } else {
                continue;
            }
        }
        return true;
    }

    /**
     * 初始化排序索引数组，默认每一个索引值初始化为-1
     * 
     * @param arr
     *            索引数组
     */
    private static void initArray(int arr[]) {
        for (int i = 0; i < arr.length; i++) {
            arr[i] = -1;
        }
    }
}
