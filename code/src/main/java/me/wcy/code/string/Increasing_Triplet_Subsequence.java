package me.wcy.code.string;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/103/array-and-strings/781/
 * Created by wcy on 2020/10/28.
 */
public class Increasing_Triplet_Subsequence {

    public boolean increasingTriplet(int[] nums) {
        int m1 = Integer.MAX_VALUE;
        int m2 = Integer.MAX_VALUE;
        for (int n : nums) {
            if (n <= m1) {
                m1 = n;
            } else if (n <= m2) {
                m2 = n;
            } else {
                return true;
            }
        }
        return false;
    }
}
