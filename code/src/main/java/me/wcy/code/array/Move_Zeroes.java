package me.wcy.code.array;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-easy/92/array/567/
 * Created by wcy on 2021/2/20.
 */
class Move_Zeroes {
    public void moveZeroes(int[] nums) {
        if (nums == null || nums.length == 0) {
            return;
        }
        int zero = -1;
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] != 0) {
                if (zero >= 0) {
                    nums[zero] = nums[i];
                    nums[i] = 0;
                    zero++;
                }
            } else if (zero < 0) {
                zero = i;
            }
        }
    }
}
