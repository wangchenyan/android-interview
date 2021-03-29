package me.wcy.code.dynamic_programming;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/111/dynamic-programming/810/
 * Created by wcy on 2021/3/10.
 */
class Longest_Increasing_Subsequence {
    public int lengthOfLIS(int[] nums) {
        if (nums == null) return 0;
        if (nums.length == 1) return 1;
        int[] dp = new int[nums.length];
        for (int i = 0; i< dp.length; i++) {
            dp[i] = 1;
        }
        for (int i = 1; i < nums.length; i++) {
            for (int j = 0; j < i; j++) {
                if (nums[j] < nums[i]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                }
            }
        }
        int max = 0;
        for (int n : dp) {
            max = Math.max(max, n);
        }
        return max;
    }
}
