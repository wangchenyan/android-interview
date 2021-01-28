package me.wcy.code.trees;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * https://leetcode.com/explore/interview/card/top-interview-questions-easy/94/trees/628/
 * Created by wcy on 2021/1/22.
 */
class Binary_Tree_Level_Order_Traversal {

    List<List<Integer>> levelOrder(TreeNode root) {
        List<List<Integer>> res = new ArrayList<>();
        if (root == null) {
            return res;
        }
        Queue<TreeNode> queue = new ArrayDeque<>();
        queue.add(root);
        while (queue.size() > 0) {
            int len = queue.size();
            List<Integer> level = new ArrayList<>(len);
            while (len > 0) {
                TreeNode node = queue.poll();
                level.add(node.val);
                if (node.left != null) queue.add(node.left);
                if (node.right != null) queue.add(node.right);
                len--;
            }
            res.add(level);
        }
        return res;
    }
}
