package me.wcy.code.array;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-easy/92/array/770/
 * Created by wcy on 2021/2/20.
 */
class Rotate_Image {
    public void rotate(int[][] matrix) {
        if (matrix == null || matrix.length == 0) {
            return;
        }
        int rowLeft = 0;
        int rowRight = matrix.length - 1;
        int columnTop = 0;
        int columnBottom = matrix.length - 1;
        int step = matrix.length - 1;
        while (step > 0) {
            for (int i = 0; i <= step - 1; i++) {
                int temp = matrix[columnTop][rowLeft + i];
                matrix[columnTop][rowLeft + i] = matrix[columnBottom - i][rowLeft];
                matrix[columnBottom - i][rowLeft] = matrix[columnBottom][rowRight - i];
                matrix[columnBottom][rowRight - i] = matrix[columnTop + i][rowRight];
                matrix[columnTop + i][rowRight] = temp;
            }
            rowLeft++;
            rowRight--;
            columnTop++;
            columnBottom--;
            step -= 2;
        }
    }
}
