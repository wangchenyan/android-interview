package me.wcy.code.sort;

import java.util.Arrays;

/**
 * Created by wcy on 2018/1/5.
 */
public class QuickSort {

    public static void main(String[] args) {
        int[] array = {9, 3, 7, 12, 4, 0, 11, 6};
        quickSort(array, 0, array.length - 1);
        System.out.println(Arrays.toString(array));
    }

    private static void quickSort(int[] array, int start, int end) {
        if (start >= end) {
            return;
        }

        int left = start;
        int right = end;
        int key = array[left];

        while (left < right) {
            while (left < right && array[right] >= key) {
                right--;
            }
            if (left < right) {
                array[left] = array[right];
                left++;
            }

            while (left < right && array[left] <= key) {
                left++;
            }
            if (left < right) {
                array[right] = array[left];
                right--;
            }
        }

        array[left] = key;

        quickSort(array, start, left - 1);
        quickSort(array, right + 1, end);
    }
}
