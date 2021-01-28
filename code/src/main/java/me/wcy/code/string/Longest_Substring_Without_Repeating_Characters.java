package me.wcy.code.string;

import java.util.HashSet;
import java.util.Set;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/103/array-and-strings/779/
 * <p>
 * 基本思路是维护一个窗口，每次关注窗口中的字符串，在每次判断中，左窗口和右窗口选择其一向前移动。
 * 同样是维护一个HashSet，正常情况下移动右窗口，如果没有出现重复则继续移动右窗口，如果发现重复字符，
 * 则说明当前窗口中的串已经不满足要求，继续移动有窗口不可能得到更好的结果，此时移动左窗口，直到不再有重复字符为止，
 * 中间跳过的这些串中不会有更好的结果，因为他们不是重复就是更短。
 * 因为左窗口和右窗口都只向前，所以两个窗口都对每个元素访问不超过一遍，因此时间复杂度为O(2*n)=O(n)，是线性算法。
 * 空间复杂度为HashSet的size，也是O(n)。
 * <p>
 * Created by wcy on 2020/10/27.
 */
public class Longest_Substring_Without_Repeating_Characters {

    public int lengthOfLongestSubstring(String s) {
        int max = Integer.MIN_VALUE;
        int subStart = 0;
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < s.length(); i++) {
            // 当前字符
            char c = s.charAt(i);
            if (set.contains(c)) {
                max = Math.max(max, set.size());
                // 移除相同字符前面的字符
                while (s.charAt(subStart) != c) {
                    set.remove(s.charAt(subStart));
                    subStart++;
                }
                // 移除相同字符
                set.remove(s.charAt(subStart));
                subStart++;
            }
            set.add(c);
        }

        max = Math.max(max, set.size());
        return max;
    }
}
