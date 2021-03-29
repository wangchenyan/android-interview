package me.wcy.code.string;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/103/array-and-strings/780/
 * Created by wcy on 2020/10/28.
 */
public class Longest_Palindromic_Substring {

    public String longestPalindrome(String s) {
        if (s == null || s.length() == 0) return "";
        int left = 0;
        int right = 0;
        for (int i = 0; i < s.length(); i++) {
            int len1 = maxRange(s, i, i);
            int len2 = maxRange(s, i, i + 1);
            if (len1 > right - left + 1) {
                left = i - len1 / 2;
                right = i + len1 / 2;
            }
            if (len2 > right - left + 1) {
                left = i - (len2 / 2 - 1);
                right = i + len2 / 2;
            }
        }
        return s.substring(left, right + 1);
    }

    public int maxRange(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        // 最后一次无效
        left++;
        right--;
        return right - left + 1;
    }
}
