package me.wcy.code.dynamic_programming;

/**
 * 动态规划
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/111/dynamic-programming/807/
 * Created by wcy on 2021/3/10.
 */
class Jump_Game {

    public boolean canJump(int[] nums) {
        if (nums == null || nums.length <= 1) return true;
        if (nums.length == 2) {
            return nums[0] > 0;
        }
        if (nums[0] == 0) return false;
        int[] dp = new int[nums.length];
        dp[0] = nums[0];
        for (int i = 1; i < nums.length - 1; i++) {
            dp[i] = Math.max(dp[i - 1] - 1, nums[i]);
            if (dp[i] <= 0) return false;
        }
        return true;
    }
}
