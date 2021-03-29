package me.wcy.code.trees;

import java.util.ArrayList;
import java.util.List;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-medium/108/trees-and-graphs/790/
 * Created by wcy on 2021/3/3.
 */
class Kth_Smallest_Element_in_a_BST {

    public int kthSmallest(TreeNode root, int k) {
        List<Integer> list = new ArrayList<>();
        middleTravel(root, list);
        int index = k - 1;
        if (index < 0 || index >= list.size()) {
            return -1;
        }
        return list.get(index);
    }

    public void middleTravel(TreeNode node, List<Integer> list) {
        if (node == null) return;
        middleTravel(node.left, list);
        list.add(node.val);
        middleTravel(node.right, list);
    }
}
