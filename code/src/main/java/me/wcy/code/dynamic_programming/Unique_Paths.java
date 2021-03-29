package me.wcy.code.dynamic_programming;

/**
 * 和爬梯子一样
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/111/dynamic-programming/808/
 * Created by wcy on 2021/3/10.
 */
class Unique_Paths {
    public static void main(String[] args) {
        int res = new Unique_Paths().uniquePaths(3, 7);
        System.out.println(res);
    }

    public int uniquePaths(int m, int n) {
        if (m <= 0 || n <= 0) return 0;
        if (m <= 1 || n <= 1) return 1;

        int[][] dp = new int[m][n];
        for (int i = 0; i < m; i++) {
            dp[i][0] = 1;
        }
        for (int i = 0; i < n; i++) {
            dp[0][i] = 1;
        }
        for (int i = 1; i < m; i++) {
            for (int j = 1; j < n; j++) {
                dp[i][j] = dp[i - 1][j] + dp[i][j - 1];
            }
        }
        return dp[m - 1][n - 1];
    }
}
