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
            int len = Math.max(len1, len2);
            if (right - left + 1 < len) {
                left = i - (len - 1) / 2;
                right = i + len / 2;
            }
        }
        return s.substring(left, right + 1);
    }

    public int maxRange(String s, int left, int right) {
        while (left >= 0 && right < s.length() && s.charAt(left) == s.charAt(right)) {
            left--;
            right++;
        }
        return right - left - 2 + 1;
    }
}
