package com.xiaoxun.xun.utils;

/**
 * Created by huangyouyang on 2016/8/17.
 */
public class MathUtils {

    public static int[] prefectNumber() {

        int[] arr = new int[4];
        int sum = 0;
        int index = 0;
        for (int i = 2; i < 500; i++) {
            for (int j = 1; j < i; j++) {
                if (i % j == 0) {
                    sum += j;
                }
            }
            if (i == sum) {
                arr[index++] = sum;
            }
            sum = 0;
        }
        arr[3] = 8128;
        return arr;
    }
}
