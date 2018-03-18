package me.wcy.code;

import java.util.Locale;
import java.util.Scanner;

/**
 * 请20分钟内完成以下题目（以文本形式完成，请勿使用IDE等开发工具）
 * 1、请写出实际代码（可以使用任意熟悉的编码语言）
 * 2、需要考虑时间、空间复杂度
 * 百词斩的Robin和Lily非常喜欢开车，他们经常讨论谁开的更好更快。现在有一
 * 条公路，起点是0公里，终点是100公里。这条公路被划分为N段，每一段有不
 * 同的限速。现在他们从A公里处开始，到B公里处结束。请帮他们计算在不超过
 * 限速的情况下，最少需要多少时间完成这段路程。
 * 输入：
 * 1、第一行为公路划分的段数N
 * 2、接下来N行，每行三个正整数，分别是起始点，终止点（前后两段一定保证
 * 是连续的），和限速值（单位：公里/小时）
 * 3、紧接是要计算的起始点A，和终止点B
 * 输出：
 * 1、输出为一行，即从A到B需要的最少时间（单位：小时）,精确到小数点后两
 * 位
 * Sample Input:
 * 4
 * 0 30 10
 * 30 40 20
 * 40 80 20
 * 80 100 5
 * 20 60
 * Sample Output:
 * 2.50
 */
public class BCZ_Racing {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int n = scanner.nextInt();
        int[][] inputs = new int[n][3];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < 3; j++) {
                inputs[i][j] = scanner.nextInt();
            }
        }
        int start = scanner.nextInt();
        int end = scanner.nextInt();

        float time = racing(inputs, start, end);
        System.out.println(String.format(Locale.getDefault(), "%.2f", time));
    }

    private static float racing(int[][] inputs, int start, int end) {
        float time = 0;
        for (int[] input : inputs) {
            int p1 = input[0];
            int p2 = input[1];
            float speed = input[2];
            if (p2 <= start) {
                continue;
            }
            if (start >= p1 && start < p2 && end >= p2) { // --p1--start--p2--end--
                time += (p2 - start) / speed;
            } else if (start <= p1 && end > p1 && end <= p2) { // --start--p1--end--p2--
                time += (end - p1) / speed;
                return time;
            } else if (start <= p1 && end >= p2) { // --start--p1--p2--end--
                time += (p2 - p1) / speed;
            } else if (start >= p1 && end <= p2) { // --p1--start--end--p2--
                time += (end - start) / speed;
                return time;
            }
        }
        return time;
    }
}
